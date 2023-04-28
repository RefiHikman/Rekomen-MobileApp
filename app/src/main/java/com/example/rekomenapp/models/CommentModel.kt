package com.example.rekomenapp.models

import java.io.Serializable

data class CommentModel (
    var commentId: String? = null,
    var commentText: String? = null,
    var commentDate: String? = null,
    var reviewId: String? = null,
    var userId: String? = null
) : Serializable