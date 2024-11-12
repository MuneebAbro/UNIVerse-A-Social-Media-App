package com.example.universe.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universe.adapter.UserAdapter
import com.example.universe.databinding.FragmentSearchBinding
import com.example.universe.model.User
import com.example.universe.ui.profile.UserProfileActivity
import com.example.universe.utils.USER_NODE
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView and Adapter
        // SearchFragment.kt
        userAdapter = UserAdapter { user ->
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("userName", user.name)
            intent.putExtra("userUsername", user.username)
            intent.putExtra("userImageUrl", user.image)
            intent.putExtra("userEmail", user.email)
            startActivity(intent)
        }
        binding.searchRecyclerView.adapter = userAdapter

        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        // Add TextWatcher to EditText for real-time search
        binding.loginEmailEditText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.staticTextSearch.visibility = View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.staticTextSearch.visibility = View.GONE
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchUsers(query)
                } else {
                    updateUserList(emptyList())
                    binding.staticTextSearch.visibility = View.VISIBLE
                    // Clear list if input is empty
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return root
    }

    private fun searchUsers(query: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(USER_NODE)
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    val userList = documents.map { it.toObject(User::class.java) }
                    updateUserList(userList)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show()
                }
    }

    private fun updateUserList(users: List<User>) {
        userAdapter.updateData(users)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
