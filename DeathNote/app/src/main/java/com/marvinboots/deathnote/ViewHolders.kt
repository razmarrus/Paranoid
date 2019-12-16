package com.marvinboots.deathnote

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

//public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
class ViewHolders( itemView: View  ) : RecyclerView.ViewHolder(itemView),  View.OnClickListener  {
    //Title: TextView, Content: TextView, Color: String?, CreationDate: String?,
    var title: TextView
    var content: TextView
    //internal var image: ImageView
    var color: String? = null
    var creationDate: String? = null
    /*init
    {
        this.title = Title
        this.content = Content
        this.creationDate = CreationDate
        this.color = Color
    }*/
    init {

        super.itemView
        itemView.setOnClickListener(this)

        title = itemView.findViewById(R.id.note_title)
        content = itemView.findViewById(R.id.note_content)
        //image = itemView.findViewById(R.id.note_image)
    }

    override fun onClick(view: View) {

        Toast.makeText(view.context, "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT)
            .show()

        val simpleNoteEditionIntent = Intent(view.context, NoteCreation::class.java)
        simpleNoteEditionIntent.putExtra("title", title.text)
        simpleNoteEditionIntent.putExtra("content", content.text)
        simpleNoteEditionIntent.putExtra("color", color)
        simpleNoteEditionIntent.putExtra("creationDate", creationDate)
        simpleNoteEditionIntent.putExtra("position", getPosition())
        (view.context as Activity).startActivityForResult(simpleNoteEditionIntent, 1)
    }
}
