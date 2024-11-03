package com.example.universe.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.universe.R
import com.example.universe.databinding.FragmentMessageBinding
import com.example.universe.model.SharedViewModel
import com.example.universe.ui.login.LoginActivity
import com.example.universe.utils.DialogUtils.showLogoutDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MessageFragment : Fragment(){

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
