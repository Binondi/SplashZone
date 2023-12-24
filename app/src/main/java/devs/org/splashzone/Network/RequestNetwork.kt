package devs.org.splashzone.Network

import android.app.Activity

class RequestNetwork(@JvmField val activity: Activity) {
    @JvmField
    val params = HashMap<String, Any>()
    @JvmField
    val headers = HashMap<String, Any>()
    @JvmField
    val requestType = 0

    fun startRequestNetwork(
        method: String?,
        url: String?,
        tag: String?,
        requestListener: RequestListener?
    ) {
        RequestNetworkController.getInstance().execute(this, method, url, tag, requestListener)
    }

    interface RequestListener {
        fun onResponse(tag: String?, response: String?, responseHeaders: HashMap<String?, Any?>?)
        fun onErrorResponse(tag: String?, message: String?)
    }
}
