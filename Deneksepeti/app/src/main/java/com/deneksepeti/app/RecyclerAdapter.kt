package com.deneksepeti.app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerAdapter(var cards: ArrayList<Card>, var context: Context, var itemClickListener: ItemClickListener): RecyclerView.Adapter<RecyclerAdapter.CardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false ) as View
        return CardHolder(view)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(cards[position].image)
            .into(holder.image)
        holder.title.setText(cards[position].title)
        holder.quota.setText(cards[position].quota)
        holder.dayLeft.setText(cards[position].dayleft)
    }

    inner class CardHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var image: ImageView
        var title : TextView
        var quota : TextView
        var dayLeft : TextView

        init {
            itemView.setOnClickListener {
                itemClickListener.onClick(it, adapterPosition)
            }

            image = itemView.findViewById(R.id.images)
            title = itemView.findViewById(R.id.titleText)
            quota = itemView.findViewById(R.id.quota)
            dayLeft = itemView.findViewById(R.id.deadline)
        }

    }

}



