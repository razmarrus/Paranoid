package com.marvinboots.deathnote

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.recyclerview.widget.SortedList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marvinboots.deathnote.ui.main.ItemObjects

import java.util.List;

class TitleSortAdapter (itemList: kotlin.collections.List<ItemObjects>) : RecyclerView.Adapter<ViewHolders>() { //itemList: kotlin.collections.List<ItemObjects>
    //private List<ItemObjects> _itemList;
    //private ViewGroup _parent;

    private var _itemList2: SortedList<ItemObjects>? = null
    private var _itemList : kotlin.collections.List<ItemObjects>
    private var _parent: ViewGroup? = null

    init {
        _itemList = itemList
    }


    fun compare(o1 : ItemObjects, o2 : ItemObjects) : Int? {
        return o1.getTitle()?.compareTo(o2.getTitle()!!);
    }

    fun onChanged(position : Int, count : Int) {
        notifyItemRangeChanged(position, count);
    }

    fun areContentsTheSame(oldItem : ItemObjects, newItem : ItemObjects) : Boolean {
        return oldItem.getTitle().equals(newItem.getTitle());
    }

    fun areItemsTheSame(item1: ItemObjects, item2 : ItemObjects) : Boolean {
        return (item1.getTitle().equals(item2.getTitle()))
    }

    fun onInserted(position : Int, count : Int) {
        notifyItemRangeInserted(position, count);
    }

    fun onRemoved(position : Int, count : Int) {
        notifyItemRangeRemoved(position, count);
    }

    public fun onMoved(fromPosition : Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition);
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {

        @SuppressLint("InflateParams") val layoutView =
            LayoutInflater.from(parent.context).inflate(R.layout.solvent_list, null)

        _parent = parent
        return ViewHolders(layoutView)
    }

    fun addAll(items: kotlin.collections.List<ItemObjects>) {
        _itemList2?.beginBatchedUpdates()
        for (i in items.indices) {
            _itemList2?.add(items.get(i))
        }
        _itemList2?.endBatchedUpdates()
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
            holder.tags.setTextSize(2f)
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

        return if (_itemList != null) _itemList.size else 0
    }
}