package com.example.firestoretute

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(val context: Context, val productList: ArrayList<Product>):
    RecyclerView.Adapter<ProductAdapter.ProductVH>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.product_layout,
            parent,
            false
        )
        return ProductVH(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        val product = productList[position]
        holder.product_name.text = product.product_name!!
        holder.product_description.text = product.product_description!!
        holder.product_type.text = product.product_type
        holder.product_ab.text = product.product_ab.toString()
    }

    class ProductVH(itemView: View):RecyclerView.ViewHolder(itemView){
        val product_name: TextView = itemView.findViewById(R.id.product_name)!!
        val product_description: TextView = itemView.findViewById(R.id.product_description)!!
        val product_type: TextView = itemView.findViewById(R.id.product_type)!!
        val product_ab: TextView = itemView.findViewById(R.id.product_ab)!!
    }
}