package com.example.rekomenapp.models

import java.io.Serializable

data class ReviewModel (
    var reviewId: String? = null,
    var reviewCategory: String? = null,
    var reviewSubCategory: String? = null,
    var reviewJudul: String? = null,
    var reviewDesc: String? = null,
    var reviewRating: Float? = null,
    var reviewHarga: String? = null,
    var reviewImg1: String? = null,
    var reviewImg2: String? = null,
    var reviewImg3: String? = null,
    var reviewImg4: String? = null,
    var reviewDate: String? = null,
    var userId: String? = null,
    var reviewCategorySort: String? = null
) : Serializable {
    init {
        reviewCategorySort = "$reviewCategory-$reviewSubCategory"
    }
}