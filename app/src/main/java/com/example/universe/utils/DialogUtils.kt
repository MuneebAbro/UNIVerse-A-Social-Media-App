package com.example.universe.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.universe.R

object DialogUtils {

    fun showLogoutDialog(context: Context, onLogout: () -> Unit) {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

        // Build the dialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Show the dialog
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        // Handle button clicks in the dialog
        dialogView.findViewById<View>(R.id.cancelBtnDialog).setOnClickListener {
            dialog.dismiss()
        }

        // Handle logout button click
        dialogView.findViewById<View>(R.id.logoutBtnDialog).setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
            onLogout()       // Trigger the logout action
        }
    }
}
