package com.jailedbird.wired.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
// @Keep
@Parcelize
data class TestParcelable(val parcelable1: String, val parcelable2: String) : Parcelable