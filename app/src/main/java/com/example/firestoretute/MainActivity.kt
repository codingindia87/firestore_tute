package com.example.firestoretute

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firestoretute.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSave.setOnClickListener {setData()}

        binding.btnRead.setOnClickListener { getData() }

        binding.btnUpdate.setOnClickListener { updateData() }

        binding.btnDelete.setOnClickListener { deleteData() }

        binding.btnList.setOnClickListener {
            startActivity(Intent(this@MainActivity,ListWork::class.java))
        }

        getUpdate()
    }

    private fun setData(){
        val user = User(
            name = binding.editName.text.toString().trim(),
            email = binding.editEmail.text.toString().trim(),
            address = binding.editAddress.text.toString().trim()
        )
        val db = Firebase.firestore
        db.collection("users").document("user1")
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(
                    this@MainActivity,
                    "Data Save.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getData(){
        val db = Firebase.firestore
        db.collection("users").document("user1")
            .get()
            .addOnSuccessListener {
                Toast.makeText(
                    this@MainActivity,
                    "Data Get",
                    Toast.LENGTH_SHORT
                ).show()
                val user = it.toObject<User>()
                binding.editUpdateName.setText(user?.name)
                binding.editUpdateEmail.setText(user?.email)
                binding.editUpdateAddress.setText(user?.address)
            }.addOnFailureListener {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateData(){
        val hashMap = HashMap<String,Any>()
        hashMap["name"] = binding.editUpdateName.text.toString().trim()
        hashMap["email"] = binding.editUpdateEmail.text.toString().trim()
        hashMap["address"] = binding.editUpdateAddress.text.toString().trim()

        val db = Firebase.firestore
        db.collection("users").document("user1")
            .update(hashMap)
            .addOnSuccessListener {
                Toast.makeText(
                    this@MainActivity,
                    "Data update.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun deleteData(){
        val db = Firebase.firestore
        db.collection("users").document("user1")
            .delete().addOnSuccessListener {
                Toast.makeText(
                    this@MainActivity,
                    "Data delete.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getUpdate(){
        val db = Firebase.firestore
        db.collection("users").document("user1")
            .addSnapshotListener { value, error ->
                if (error==null){
                    if (value!!.exists()){
                        val user = value.toObject<User>()
                        binding.txtName.text = user?.name
                        binding.txtEmail.text = user?.email
                        binding.txtAddress.text = user?.address
                    }
                }else{
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}