package com.deneksepeti.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class Card  constructor( var image: String, var title: String,
                         var desc: String, var quota: String, var dayleft: String ) {}