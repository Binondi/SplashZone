package devs.org.splashzone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import devs.org.splashzone.CategoryViewerActivity;
import devs.org.splashzone.Data.Category;
import devs.org.splashzone.R;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    private final ArrayList<Category> list;
    private final Context context;
    public CategoryAdapter(List<Category> list, Context context) {
        this.list = (ArrayList<Category>) list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Category data = list.get(position);
        Glide.with(context)
                .load(data.getCategoryImageUrl())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(10)))
                .into(holder.imageView);

        holder.category.setText(data.category);

        holder.card.setOnClickListener(view -> {
            if (data.category != null) {
                context.startActivity(new Intent(context, CategoryViewerActivity.class)
                        .putExtra("category", data.category));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView category;
        CardView card;
        public Holder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.category);
            card = itemView.findViewById(R.id.card);
        }
    }
}
