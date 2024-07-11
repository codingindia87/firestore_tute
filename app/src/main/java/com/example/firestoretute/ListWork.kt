package com.example.firestoretute

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firestoretute.databinding.ActivityListWorkBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ListWork : AppCompatActivity() {

    private lateinit var binding: ActivityListWorkBinding

    private val productList: ArrayList<Product> = ArrayList()

    private lateinit var adapter: ProductAdapter

    private lateinit var query: Query

    private lateinit var lastData: DocumentSnapshot

    private var isLoading = false

    private var dataAvailable = true

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener { createAddDialog() }

        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = linearLayoutManager
        adapter = ProductAdapter(this@ListWork,productList)
        binding.recyclerView.adapter = adapter

        query = Firebase.firestore.collection("product")
            .orderBy("product_ab").limit(10)
        getProducts()

        binding.swLayout.setOnRefreshListener {
            productList.clear()
            adapter.notifyDataSetChanged()
            isLoading = false
            dataAvailable = true
            query = Firebase.firestore.collection("product").orderBy("product_ab").limit(10)
            getProducts()
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading && dataAvailable){
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                        ){
                        isLoading = true
                        binding.lMPBar.visibility = View.VISIBLE
                        query = Firebase.firestore.collection("product")
                            .orderBy("product_ab")
                            .startAfter(lastData)
                            .limit(10)
                        getProducts()
                    }
                }
            }
        })


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getProducts(){
        query.get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    lastData = it.documents[it.size() - 1]
                    for (snap in it.documents){
                        val product = snap.toObject<Product>()
                        productList.add(product!!)
                    }
                }else{
                    Toast.makeText(
                        this@ListWork,
                        "Error: Data not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dataAvailable = false
                }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
                if (binding.swLayout.isRefreshing) binding.swLayout.isRefreshing = false
                binding.lMPBar.visibility = View.GONE
                isLoading = false
            }.addOnFailureListener {
                Toast.makeText(
                    this@ListWork,
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
                if (binding.swLayout.isRefreshing) binding.swLayout.isRefreshing = false
                isLoading = false
                dataAvailable = false
                binding.lMPBar.visibility = View.GONE
            }
    }

    private fun createAddDialog(){
        val addDialog = BottomSheetDialog(this)
        addDialog.setContentView(R.layout.add_product_layout)

        val product_name: EditText = addDialog.findViewById(R.id.product_name)!!
        val product_description: EditText = addDialog.findViewById(R.id.product_description)!!
        val product_type: EditText = addDialog.findViewById(R.id.product_type)!!
        val product_ab: EditText = addDialog.findViewById(R.id.product_ab)!!
        val add_btn : Button = addDialog.findViewById(R.id.btn_add)!!

        add_btn.setOnClickListener {
            val product = Product(
                product_name = product_name.text.toString().trim(),
                product_description = product_description.text.toString().trim(),
                product_type = product_type.text.toString().trim(),
                product_ab = product_ab.text.toString().toInt()
            )
            addProduct(product)
            addDialog.cancel()
        }

        addDialog.show()
    }

    private fun addProduct(product: Product){
        val db = Firebase.firestore
        db.collection("product").document().set(product)
            .addOnSuccessListener {
                Toast.makeText(
                    this@ListWork,
                    "Product Add",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    this@ListWork,
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}