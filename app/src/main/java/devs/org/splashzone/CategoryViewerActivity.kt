package devs.org.splashzone

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devs.org.splashzone.Adapters.RecyclerAdapter
import devs.org.splashzone.Animator.CustomItemAnimator
import devs.org.splashzone.Data.Walls
import devs.org.splashzone.databinding.ActivityCategoryViewerBinding


class CategoryViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryViewerBinding
    var imageModel: ArrayList<Walls?> = ArrayList()
    private var database = FirebaseDatabase.getInstance()
    private var reference = database.reference.child("wallpapers")
    var category: String? = null
    private var adapter: RecyclerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra("category")

        statusBar()
        setRecyclerViewAdapter()
        fetchImageUrls()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun statusBar(){
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        val defaultBackgroundColor = typedValue.data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = defaultBackgroundColor
        }
    }

    private fun fetchImageUrls() {
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                imageModel.clear()
                for (dataSnapshot in snapshot.children) {
                    val image: Walls? = dataSnapshot.getValue(Walls::class.java)
                    if (image != null && image.category.equals(category)) {
                        imageModel.add(image)
                    }
                }
                imageModel.shuffle()
                runOnUiThread {
                    if (binding.recyclerView.adapter != null) {
                        binding.recyclerView.adapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error here if needed
            }
        })
        reference.keepSynced(true)
    }


    private fun setRecyclerViewAdapter() {
        val spanCount = 2
        val layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        val customItemAnimator = CustomItemAnimator()
        binding.recyclerView.itemAnimator = customItemAnimator
        adapter = RecyclerAdapter(imageModel, this)
        binding.recyclerView.adapter = adapter
    }
}