package ru.netology.titov.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val published: String,
    val content: String,
    var liked: Boolean,
    val video: String? = null,
    val likeNum: Long,
    val shareNum: Long,
    val viewNum: Long,


)