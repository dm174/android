package ru.netology.titov.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.titov.R
import ru.netology.titov.databinding.CardPostBinding
import ru.netology.titov.dto.Post
import ru.netology.titov.util.FormatLikeShare

interface PostEventListener {
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onVideo(post: Post)


}

class PostAdapter(
    private val listener: PostEventListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(
            binding,
            listener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)

    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostEventListener

) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author

            published.text = post.published
            content.text = post.content

            //likeNum.text = FormatLikeShare.counterDecimal(post.likeNum)
            //shareNum.text = FormatLikeShare.counterDecimal(post.shareNum)

            like.text=FormatLikeShare.counterDecimal(post.likeNum)
            like.isChecked = post.liked //Если like = true, то кнопка считается нажатой
            share.text = FormatLikeShare.counterDecimal(post.shareNum)
            if (post.video == null) {
                binding.playVideoGroup.visibility = View.GONE
            } else {
                binding.playVideoGroup.visibility = View.VISIBLE
            }





            like.setOnClickListener {
             listener.onLike(post)

                }
            share.setOnClickListener { listener.onShare(post) }
            play.setOnClickListener { listener.onVideo(post) }
            backgroundVideo.setOnClickListener { listener.onVideo(post) }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)

                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {

                            R.id.remove -> {
                                listener.onRemove(post)
//                                true
                                return@setOnMenuItemClickListener true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
//                                true
                                return@setOnMenuItemClickListener true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }

}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any = Unit
}