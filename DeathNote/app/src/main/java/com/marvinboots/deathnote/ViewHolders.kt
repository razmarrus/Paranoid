package com.marvinboots.deathnote

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

//public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
class ViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView),  View.OnClickListener  {
    //Title: TextView, Content: TextView, Color: String?, CreationDate: String?,
    var title: TextView
    var content: TextView
    var tags: TextView//? = null
    //internal var image: ImageView
    //var color: String? = null
    var creationDate: String? = null
    init {

        super.itemView
        itemView.setOnClickListener(this)

        title = itemView.findViewById(R.id.note_title)
        content = itemView.findViewById(R.id.note_content)
        tags = itemView.findViewById(R.id.note_tags)
        //image = itemView.findViewById(R.id.note_image)
    }

    override fun onClick(view: View) {

        /*Toast.makeText(view.context, "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT)
            .show()*/

        var simpleNoteEditionIntent = Intent(view.context, NoteCreation::class.java)
        simpleNoteEditionIntent.putExtra("title", title.text)
        simpleNoteEditionIntent.putExtra("content", content.text)
        simpleNoteEditionIntent.putExtra("tags", tags.text)
        simpleNoteEditionIntent.putExtra("creationDate", creationDate)
        simpleNoteEditionIntent.putExtra("position", getPosition())
        (view.context as Activity).startActivityForResult(simpleNoteEditionIntent, 1)
    }
}
