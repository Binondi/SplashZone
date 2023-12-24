package devs.org.splashzone.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import devs.org.splashzone.Data.Walls
import devs.org.splashzone.R
import devs.org.splashzone.SetWallActivity

class RecyclerAdapter(private val list: ArrayList<Walls?>, private val context: Context) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageModel = list[position]
        if (imageModel != null) {
            Glide.with(context)
                .load(imageModel.imageUrl)
                .placeholder(R.drawable.click_bg)
                .into(holder.imageView)
        }
        holder.cardView.setOnClickListener {
            if (imageModel != null) {
                if (imageModel.imageUrl != null) {
                    val intent = Intent(context, SetWallActivity::class.java)
                    intent.putExtra("url", imageModel.imageUrl)
                    intent.putExtra("category", imageModel.category)
                    intent.putExtra("name", imageModel.wpName)
                    intent.putExtra("uid", imageModel.uid)
                    intent.putExtra("down", imageModel.downloads)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView
        var imageView: ImageView

        init {
            cardView = itemView.findViewById(R.id.card)
            imageView = itemView.findViewById(R.id.walls)
        }
    }
}
