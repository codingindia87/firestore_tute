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
import com.example.firestoretute.databinding.ActivityListWorkBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ListWork : AppCompatActivity() {

    private lateinit var binding: ActivityListWorkBinding

    private val productList: ArrayList<Product> = ArrayList()

    private lateinit var adapter: ProductAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener { createAddDialog() }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(this@ListWork,productList)
        binding.recyclerView.adapter = adapter

        getProducts()

        binding.swLayout.setOnRefreshListener {
            productList.clear()
            adapter.notifyDataSetChanged()
            getProducts()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getProducts(){
        val db = Firebase.firestore
        db.collection("product")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
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
                }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
                if (binding.swLayout.isRefreshing) binding.swLayout.isRefreshing = false
            }.addOnFailureListener {
                Toast.makeText(
                    this@ListWork,
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
                if (binding.swLayout.isRefreshing) binding.swLayout.isRefreshing = false
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