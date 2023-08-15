package ru.netology.titov.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.titov.R
import ru.netology.titov.adapter.PostAdapter
import ru.netology.titov.adapter.PostEventListener
import ru.netology.titov.databinding.ActivityMainBinding
import ru.netology.titov.dto.Post
import ru.netology.titov.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val newPostContract = registerForActivityResult(NewPostActivityContract()) { text ->
            text?.let {
                viewModel.changeContent(it)
                viewModel.saveContent()
            }
        }

        val editPostActivityContract =
            registerForActivityResult(EditPostActivityContract()) { text ->
                text?.let {
                    viewModel.changeContent(it)
                    viewModel.saveContent()
                }

            }

        val adapter = PostAdapter(
            object : PostEventListener {

                override fun onEdit(post: Post) {
                    viewModel.editContent(post)
//                    binding.group.visibility = View.VISIBLE
                    editPostActivityContract.launch(post.content)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.shareById(post.id)
                }

                override fun onVideo(post: Post) {
                    val intentVideo = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    startActivity(intentVideo)
                }
            }
        )

        binding.container.adapter = adapter

        viewModel.data.observe(this)
        { posts ->
            val newPost = adapter.itemCount < posts.size
            adapter.submitList(posts) {
                if (newPost) binding.container.smoothScrollToPosition(0)
            }
        }

        binding.addPost.setOnClickListener {
            newPostContract.launch()
        }
    }
}