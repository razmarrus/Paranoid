package com.marvinboots.deathnote

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout

import android.widget.TableLayout
import android.widget.TextView

import org.json.JSONException
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteCreation : AppCompatActivity()  {
    lateinit var titleEditText: EditText
    lateinit var contentEditText: EditText
    lateinit var tagsEditText: EditText
    lateinit var lastUpdateDateTextView: TextView

    lateinit  var noteLayout: LinearLayout
    lateinit  var bottomToolbar: TableLayout
    lateinit var lastUpdateDateString: String
    var creationDateString:String? = null

    var lastTitle: String? = null
    var lastContent:String? = null
    var notePosition: Int = 0
    var lastTags: String? = null//List<String>? = null


        //override fun onCreate(savedInstanceState: Bundle?) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_simple_note_creation)

            titleEditText = findViewById(R.id.title_edit_text)
            contentEditText = findViewById(R.id.content_edit_text)
            tagsEditText = findViewById(R.id.tags_edit_text)

            noteLayout = findViewById(R.id.simple_note_creation_linear_layout)
            bottomToolbar = findViewById(R.id.bottom_toolbar)
            lastUpdateDateTextView = findViewById(R.id.last_modification_date)

            val editionIntent = intent
            lastTitle = editionIntent.getStringExtra("title")
            lastContent = editionIntent.getStringExtra("content")
            creationDateString = editionIntent.getStringExtra("creationDate")
            lastTags = editionIntent.getStringExtra("tags")

            titleEditText.setText(lastTitle)
            contentEditText.setText(lastContent)
            tagsEditText.setText(lastTags)

            // Get date
            if (creationDateString!!.isEmpty())
                creationDateString =
                    SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).format(Date())
            lastUpdateDateString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            // Set "Last Update" field content
            lastUpdateDateTextView.text = "Last update : $lastUpdateDateString"

            // Open keyboard and put focus on the content edit text
            if (lastTitle!!.isEmpty() && lastContent!!.isEmpty()) {
                contentEditText.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(contentEditText, InputMethodManager.SHOW_IMPLICIT)
            }


        }
// Save note content on back pressed
    override fun onBackPressed() {

        var changed: Boolean? = false
        val titleText = titleEditText.getText().toString()
        val contentText = contentEditText.getText().toString()
        val tagsText = tagsEditText.getText().toString()//.toList()

        if (titleText != lastTitle || contentText != lastContent || lastTags != tagsText)
            changed = true

        // Check if fields are not empty
        if ((!TextUtils.isEmpty(titleText) || !TextUtils.isEmpty(contentText)) && changed!!) {

            val noteJSON = JSONObject()
            try {
                noteJSON.put("noteTitle", titleEditText.getText().toString())
                noteJSON.put("noteContent", contentEditText.getText().toString())
                noteJSON.put("noteCreationDate", creationDateString)
                noteJSON.put("noteLastUpdateDate", lastUpdateDateString)
                noteJSON.put("notePosition", notePosition)
                noteJSON.put("noteTags", tagsEditText.getText().toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            // Return note JSON to MainActivity
            val resultIntent = Intent()
            resultIntent.putExtra("noteJSON", noteJSON.toString())
            setResult(Activity.RESULT_OK, resultIntent)
        }
        this.finish()
    }

    fun addNewNote(view: View)
    {
        onBackPressed()
    }

}