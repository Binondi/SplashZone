package devs.org.splashzone.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devs.org.splashzone.Adapters.RecyclerAdapter
import devs.org.splashzone.Animator.CustomItemAnimator
import devs.org.splashzone.Data.Walls
import devs.org.splashzone.Network.RequestNetwork
import devs.org.splashzone.Network.RequestNetworkController
import devs.org.splashzone.databinding.FragmentTrendingBinding


class TrendingFragment : Fragment() {
    private val imageModel: ArrayList<Walls?> = ArrayList()
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("wallpapers")
    private var binding: FragmentTrendingBinding? = null
    private var rn: RequestNetwork? = null
    private var adapter: RecyclerAdapter? = null
    private var listener: RequestNetwork.RequestListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingBinding.inflate(inflater, container, false)
        val rootView: View = binding!!.root
        binding!!.recyclerView.visibility = View.VISIBLE
        binding!!.noNetBg.visibility = View.GONE
        rn = RequestNetwork(requireActivity())
        initializeRnService()

        setupRecyclerViewAndFetchData()
        fetchImageUrls()
        setRecyclerViewAdapter()
        return rootView
    }

    private fun setupRecyclerViewAndFetchData() {
        rn!!.startRequestNetwork(RequestNetworkController.GET,"https://www.google.com/", "", listener)
    }

    private fun initializeRnService() {
        listener = object : RequestNetwork.RequestListener {
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
        reference.orderByChild("downloadCount")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    imageModel.clear()
                    for (dataSnapshot in snapshot.children) {
                        val image: Walls? = dataSnapshot.getValue(Walls::class.java)
                        if (image != null) {
                            imageModel.add(image)
                        }
                    }

                    binding?.recyclerView?.visibility = View.VISIBLE
                    binding?.noNetBg?.visibility = View.GONE
                    imageModel.sortWith(compareByDescending { it?.downloads })

                    adapter?.notifyDataSetChanged()


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
        binding?.recyclerView?.layoutManager = layoutManager

        // Create and set the custom item animator
        val customItemAnimator = CustomItemAnimator()
        binding?.recyclerView?.itemAnimator = customItemAnimator

        // Set the adapter
        adapter = RecyclerAdapter(imageModel, requireContext())
        binding?.recyclerView?.adapter = adapter
    }

}




