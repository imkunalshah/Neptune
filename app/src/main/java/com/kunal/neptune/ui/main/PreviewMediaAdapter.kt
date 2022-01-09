package com.kunal.neptune.ui.main

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kunal.neptune.R

class PreviewMediaAdapter(
    val imageList:MutableList<Uri>,
    val map:HashMap<Uri,String>
): RecyclerView.Adapter<PreviewMediaAdapter.PreviewMediaVH>() {

    class PreviewMediaVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image:ImageView = itemView.findViewById(R.id.image)
        val playBtn:ImageView = itemView.findViewById(R.id.playBtn)
        val card:CardView = itemView.findViewById(R.id.card)
        val delete:ImageButton = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreviewMediaVH {
        val rootView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.preview_image,parent, false)
        return PreviewMediaVH(rootView)
    }

    override fun onBindViewHolder(holder: PreviewMediaVH, position: Int) {
        holder.image.setImageURI(imageList[position])
        if (map[imageList[position]] == "video"){
            holder.playBtn.visibility = View.VISIBLE
        }
    }

    override fun getItemCount():Int = imageList.size

    fun addImage(image:Uri,type:String){
        imageList.add(image)
        map[image] = type
        notifyDataSetChanged()
    }
}