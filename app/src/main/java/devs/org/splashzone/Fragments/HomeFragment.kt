package devs.org.splashzone.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import devs.org.splashzone.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private val imageModel: ArrayList<Walls?> = ArrayList()
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("wallpapers")
    private var binding: FragmentHomeBinding? = null
    private var rn: RequestNetwork? = null
    private var listener: RequestNetwork.RequestListener? = null
    var adapter: RecyclerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView: View = binding!!.root
        binding!!.loadingBg.visibility = View.VISIBLE
        binding!!.noNetBg.visibility = View.GONE
        binding!!.recyclerView.visibility = View.GONE
        fetchImageUrls()
        rn = activity?.let { RequestNetwork(it) }
        initializeRnService()
        setRecyclerViewAdapter()
        setupRecyclerViewAndFetchData()
        return rootView
    }

    private fun setupRecyclerViewAndFetchData() {
        rn?.startRequestNetwork(RequestNetworkController.GET, GOOGLE_URL, "", listener)
    }

    private fun initializeRnService() {
        listener = object : RequestNetwork.RequestListener {
            override fun onResponse(tag: String?, response: String?, responseHeaders: HashMap<String?, Any?>?) {
                if (isAdded && isResumed) {
                    binding?.recyclerView?.visibility = View.VISIBLE
                    binding?.noNetBg?.visibility = View.GONE
                    binding?.loadingBg?.visibility = View.GONE
                }
            }

            override fun onErrorResponse(tag: String?, message: String?) {
                if (isAdded && isResumed) {
                    binding?.recyclerView?.visibility = View.GONE
                    binding?.noNetBg?.visibility = View.VISIBLE
                    binding?.loadingBg?.visibility = View.GONE
                }
            }
        }
    }

    private fun fetchImageUrls() {
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                imageModel.clear()
                for (dataSnapshot in snapshot.children) {
                    val images: Walls? = dataSnapshot.getValue(Walls::class.java)
                    imageModel.add(images)
                }
                adapter?.notifyDataSetChanged()

                // Set visibility after data is loaded
                binding?.recyclerView?.visibility = View.VISIBLE
                binding?.noNetBg?.visibility = View.GONE
                binding?.loadingBg?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        reference.keepSynced(true)
    }


    private fun setRecyclerViewAdapter() {
        val spanCount = 2
        val layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        binding?.recyclerView?.layoutManager = layoutManager
        val customItemAnimator = CustomItemAnimator()
        binding?.recyclerView?.itemAnimator = customItemAnimator
        adapter = RecyclerAdapter(imageModel, requireContext())
        binding?.recyclerView?.adapter = adapter
    }


    companion object {
        private const val GOOGLE_URL = "https://www.google.com/"
    }
}



