package com.marvinboots.deathnote

import android.annotation.SuppressLint
import android.graphics.Typeface
//import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marvinboots.deathnote.ui.main.ItemObjects

class RecycleViewAdapter(itemList: kotlin.collections.List<ItemObjects>) : RecyclerView.Adapter<ViewHolders>() {
    //private List<ItemObjects> _itemList;
    //private ViewGroup _parent;

    private var _itemList : kotlin.collections.List<ItemObjects>
    private var _parent: ViewGroup? = null

   init {
        _itemList = itemList
    }

    fun updateList(itemList: kotlin.collections.List<ItemObjects>)
    {
        _itemList = itemList
    }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {

        @SuppressLint("InflateParams") val layoutView =
            LayoutInflater.from(parent.context).inflate(R.layout.solvent_list, null)

        _parent = parent
        return ViewHolders(layoutView)
    }

   override fun onBindViewHolder(holder: ViewHolders, position: Int) {

        holder.title.setText(_itemList!![position].getTitle())
        // If title is empty, hide title edit text
        if (holder.title.getText().toString().isEmpty()) {
            holder.title.setHeight(0)
        }
        holder.content.setText(_itemList!![position].getContent())
        // If content is empty, hide content edit text
        if (holder.content.getText().toString().isEmpty()) {
            holder.content.setHeight(0)
        }

       holder.tags.setText(_itemList!![position].getTags())
       if (holder.tags.getText().toString().isEmpty()) {
           holder.tags.setHeight(0)
       }

        if (holder.content.getText().toString().length > 0 && holder.content.getText().toString().length < 6) {
            var typeface: Typeface? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = _parent!!.context.resources.getFont(R.font.roboto_slab_thin)
            }
            holder.content.setTypeface(typeface)
            //var float buffer = 1f
            holder.content.setTextSize(70f)
        }
        if (holder.content.getText().toString().length >= 6 && holder.content.getText().toString().length < 10) {
            var typeface: Typeface? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = _parent!!.context.resources.getFont(R.font.roboto_slab_light)
            }
            holder.content.setTypeface(typeface)
            holder.content.setTextSize(50f)
        }
        if (holder.content.getText().toString().length >= 10 && holder.content.getText().toString().length < 13) {
            var typeface: Typeface? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = _parent!!.context.resources.getFont(R.font.roboto_slab_light)
            }
            holder.content.setTypeface(typeface)
            holder.content.setTextSize(36f)
        }
        if (holder.content.getText().toString().length >= 13 && holder.content.getText().toString().length < 19) {
            var typeface: Typeface? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = _parent!!.context.resources.getFont(R.font.roboto_slab_light)
            }
            holder.content.setTypeface(typeface)
            holder.content.setTextSize(24f)
        }
        if (holder.content.getText().toString().length >= 19 && holder.content.getText().toString().length < 24) {
            var typeface: Typeface? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = _parent!!.context.resources.getFont(R.font.roboto_slab_regular)
            }
            holder.content.setTypeface(typeface)
            holder.content.setTextSize(18f)
        }
        if (holder.content.getText().toString().length >= 24) {
            var typeface: Typeface? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = _parent!!.context.resources.getFont(R.font.roboto_slab_regular)
            }
            holder.content.setTypeface(typeface)
            holder.content.setTextSize(16f)
        }

        holder.creationDate = _itemList!![position].getCreationDate()
    }

   override fun getItemCount(): Int {

        return if (_itemList != null) _itemList!!.size else 0
    }
}