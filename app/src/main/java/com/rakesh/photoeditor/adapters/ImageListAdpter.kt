package com.rakesh.photoeditor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.rakesh.photoeditor.R


class ImageListAdpter(context: Context, mListImage: ArrayList<String>): RecyclerView.Adapter<ImageListAdpter.ViewHolder>() {

    val mContext=context
    val mImageList=mListImage


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view=LayoutInflater.from(mContext).inflate(
           R.layout.adpterlayout_imagelist,
           parent,
           false
       )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_filename.setText(mImageList.get(position))
        Glide.with(mContext)
            .load(mImageList.get(position))
            .transform(RoundedCorners(80))
            .into(holder.iv_imageListed)

    }

    override fun getItemCount(): Int {
       return mImageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv_filename=itemView.findViewById<TextView>(R.id.tv_filename)
        val iv_imageListed=itemView.findViewById<ImageView>(R.id.iv_imageListed)

    }
}