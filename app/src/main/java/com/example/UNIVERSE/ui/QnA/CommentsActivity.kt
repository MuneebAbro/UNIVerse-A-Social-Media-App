package com.example.UNIVERSE.ui.QnA

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.UNIVERSE.R
import com.example.UNIVERSE.adapter.CommentAdapter
import com.example.UNIVERSE.data.Comment
import com.example.UNIVERSE.databinding.ActivityCommentsBinding

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainFullScreenPost)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener { // this is to move the edittext when keyboard is shown
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            // If the keypad height is more than 200, it means the keyboard is visible
            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible, move EditText
                binding.commentInputLayout.translationY = -keypadHeight.toFloat()
            } else {
                // Keyboard is hidden, reset EditText position
                binding.commentInputLayout.translationY = 0f
            }
        }

        val dummyComments = List(10) {      // dummy data
            Comment(
                profileImageResId = R.drawable.dummy_person_img,
                name = "User $it",
                username = "@user$it",
                commentText = "This is a dummy comment #$it"
            )
        }

        val recyclerView: RecyclerView = findViewById(R.id.commentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)  // or in your fragment context
        recyclerView.adapter = CommentAdapter(dummyComments)

    }
}