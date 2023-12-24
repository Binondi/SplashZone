package devs.org.splashzone.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devs.org.splashzone.Adapters.CategoryAdapter
import devs.org.splashzone.Data.Category
import devs.org.splashzone.Network.RequestNetwork
import devs.org.splashzone.Network.RequestNetworkController
import devs.org.splashzone.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment() {
    private val categoryList: MutableList<Category> = ArrayList()
    private var reference: DatabaseReference? = null
    private var binding: FragmentCategoryBinding? = null
    private var adapter: CategoryAdapter? = null
    private var rn: RequestNetwork? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val rootView: View = binding!!.root
        initViews()
        setupRecyclerView()
        setupNetworkRequest()
        fetchImageUrls()
        return rootView
    }

    private fun initViews() {
        binding?.recyclerView?.visibility = View.VISIBLE
        binding?.noNetBg?.visibility = View.GONE
        rn = RequestNetwork(requireActivity())
        val database = FirebaseDatabase.getInstance()
        reference = database.reference.child("category")
        adapter = CategoryAdapter(categoryList, context)
    }

    private fun setupRecyclerView() {
        binding?.recyclerView?.layoutManager = GridLayoutManager(context, 1)
        binding?.recyclerView?.adapter = adapter
    }

    private fun setupNetworkRequest() {
        rn!!.startRequestNetwork(
            RequestNetworkController.GET,
            "https://www.google.com/",
            "",
            createRequestListener()
        )
    }

    private fun createRequestListener(): RequestNetwork.RequestListener {
        return object : RequestNetwork.RequestListener {
            override fun onResponse(
                tag: String?,
                response: String?,
                responseHeaders: HashMap<String?, Any?>?
            ) {
                binding?.recyclerView?.visibility = View.VISIBLE
                binding?.noNetBg?.visibility = View.GONE
            }

            override fun onErrorResponse(tag: String?, message: String?) {
                binding?.recyclerView?.visibility = View.GONE
                binding?.noNetBg?.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchImageUrls() {
        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (dataSnapshot in snapshot.children) {
                    val images: Category? = dataSnapshot.getValue(Category::class.java)
                    if (images != null) {
                        categoryList.add(images)
                    }
                }
                binding?.recyclerView?.visibility = View.VISIBLE
                binding?.noNetBg?.visibility = View.GONE
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        reference!!.keepSynced(true)
    }
}

