package ru.netology.titov.repository

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.titov.dto.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PostRepositoryInMemory   (  private val context: Context
) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId = 1L
    private var nextIdKey = "next_id"
    private var nextIdLike = "like_id"
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)
    private val filename = "posts.json"

    //Ввел данные  времени
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    val current = LocalDateTime.now().format(formatter)


    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it, type)
                data.value = posts
            }
            nextId=prefs.getLong(nextIdKey,nextId)

        } else {
            sync()
        }
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))


        }
    }



    override fun get(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                liked = !it.liked,
                likeNum = if (!it.liked) it.likeNum + 1 else it.likeNum - 1


            )

        }
        data.value = posts
        sync()

    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                shareNum = it.shareNum + 1
            )
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filterNot { it.id == id }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            // TODO: remove hardcoded author & published
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    liked = false,
                    published = current
                )
            ) + posts
            data.value = posts
            sync()
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }

}




