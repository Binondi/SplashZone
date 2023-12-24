package devs.org.splashzone.Network;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestNetworkController {
    public static final String GET = "GET";

    public static final int REQUEST_PARAM = 0;

    private static final int SOCKET_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 25000;

    protected OkHttpClient client;

    private static RequestNetworkController mInstance;

    public static synchronized RequestNetworkController getInstance() {
        if(mInstance == null) {
            mInstance = new RequestNetworkController();
        }
        return mInstance;
    }

    private OkHttpClient getClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            try {
                @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType)
                                    throws CertificateException {
                            }

                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType)
                                    throws CertificateException {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[]{};
                            }
                        }
                };

                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.connectTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
                builder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
                builder.writeTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            client = builder.build();
        }

        return client;
    }

    public void execute(final RequestNetwork requestNetwork, String method, String url, final String tag, final RequestNetwork.RequestListener requestListener) {
        Request.Builder reqBuilder = new Request.Builder();
        Headers.Builder headerBuilder = new Headers.Builder();

        if (requestNetwork.headers.size() > 0) {
            HashMap<String, Object> headers = requestNetwork.headers;

            for (HashMap.Entry<String, Object> header : headers.entrySet()) {
                headerBuilder.add(header.getKey(), String.valueOf(header.getValue()));
            }
        }

        try {
            if (requestNetwork.requestType == REQUEST_PARAM) {
                if (method.equals(GET)) {
                    HttpUrl.Builder httpBuilder;

                    try {
                        httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
                    } catch (NullPointerException ne) {
                        throw new NullPointerException("unexpected url: " + url);
                    }

                    if (requestNetwork.params.size() > 0) {
                        HashMap<String, Object> params = requestNetwork.params;

                        for (HashMap.Entry<String, Object> param : params.entrySet()) {
                            httpBuilder.addQueryParameter(param.getKey(), String.valueOf(param.getValue()));
                        }
                    }

                    reqBuilder.url(httpBuilder.build()).headers(headerBuilder.build()).get();
                } else {
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    if (requestNetwork.params.size() > 0) {
                        HashMap<String, Object> params = requestNetwork.params;

                        for (HashMap.Entry<String, Object> param : params.entrySet()) {
                            formBuilder.add(param.getKey(), String.valueOf(param.getValue()));
                        }
                    }

                    RequestBody reqBody = formBuilder.build();

                    reqBuilder.url(url).headers(headerBuilder.build()).method(method, reqBody);
                }
            } else {
                RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(requestNetwork.params));

                if (method.equals(GET)) {
                    reqBuilder.url(url).headers(headerBuilder.build()).get();
                } else {
                    reqBuilder.url(url).headers(headerBuilder.build()).method(method, reqBody);
                }
            }

            Request req = reqBuilder.build();

            getClient().newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                    requestNetwork.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestListener.onErrorResponse(tag, e.getMessage());
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    assert response.body() != null;
                    final String responseBody = response.body().string().trim();
                    requestNetwork.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Headers b = response.headers();
                            HashMap<String, Object> map = new HashMap<>();
                            for (String s : b.names()) {
                                map.put(s, b.get(s) != null ? b.get(s) : "null");
                            }
                            requestListener.onResponse(tag, responseBody, map);
                        }
                    });
                }
            });
        } catch (Exception e) {
            requestListener.onErrorResponse(tag, e.getMessage());
        }
    }
}
