package com.deneksepeti.app

import android.view.View

interface ItemClickListener {
    fun onClick (view: View, position: Int)
}