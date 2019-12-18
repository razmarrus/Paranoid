package com.marvinboots.deathnote


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

//import androidx.core.app.ActivityCompat
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TableLayout
import android.widget.TextView
//import androidx.appcompat.app.ActionBar

//import com.mykeep.r3j3ct3d.mykeep.R;

import com.marvinboots.deathnote.R

import org.json.JSONException
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteCreation : AppCompatActivity()  {
    lateinit var titleEditText: EditText
    lateinit var contentEditText: EditText
    lateinit var lastUpdateDateTextView: TextView
    lateinit var colorPickerRadioGroup: RadioGroup

    lateinit  var noteLayout: LinearLayout
    lateinit  var noteActionsLayout : LinearLayout
    lateinit  var bottomToolbar: TableLayout
    lateinit var actionBar : ActionBar//androidx.appcompat.app.ActionBar
    lateinit  var noteActionsButton: ImageButton

    var noteColor: String? = null
    lateinit var lastUpdateDateString: String
    var creationDateString:String? = null

    var lastTitle: String? = null
    var lastContent:String? = null
    var notePosition: Int = 0


        //override fun onCreate(savedInstanceState: Bundle?) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_note_creation)

            //actionBar.supportActionBar
            //actionBar = getSupportActionBar()
            //actionBar.getSupportActionBar()
        //actionBar.setDisplayHomeAsUpEnabled(true);



        //var actionBar = ActionBar()//supportActionBar //ActionBar//this!!.getSupportActionBar
        //var actionBar : ActionBar//= supportActionBar
            //SupportActionBar(actionBar)
        //var actionBar = ActionBar.getSupportActionBar
            //actionBar = supportActionBar;

        titleEditText = findViewById(R.id.title_edit_text)
        contentEditText = findViewById(R.id.content_edit_text)
        colorPickerRadioGroup = findViewById(R.id.color_picker_radio_group)
        noteLayout = findViewById(R.id.simple_note_creation_linear_layout)
        noteActionsLayout = findViewById(R.id.note_actions_layout)
        bottomToolbar = findViewById(R.id.bottom_toolbar)
        lastUpdateDateTextView = findViewById(R.id.last_modification_date)
        noteActionsButton = findViewById(R.id.note_actions_button)

        val editionIntent = intent
        lastTitle = editionIntent.getStringExtra("title")
        lastContent = editionIntent.getStringExtra("content")
        val color = editionIntent.getStringExtra("color")
        creationDateString = editionIntent.getStringExtra("creationDate")
        notePosition = editionIntent.getIntExtra("position", -1)

        // Set activity default color
        noteLayout.setBackgroundColor(Color.parseColor(color))
        noteActionsLayout.setBackgroundColor(Color.parseColor(color))
        bottomToolbar.setBackgroundColor(Color.parseColor(color))
        noteColor = color
        //actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor(color)))
        window.statusBarColor = darkenNoteColor(Color.parseColor(noteColor), 0.7f)

        // Set title and content if edit
        titleEditText.setText(lastTitle)
        contentEditText.setText(lastContent)

        // Get date
        if (creationDateString!!.isEmpty())
            creationDateString =
                SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(Date())
        lastUpdateDateString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Set "Last Update" field content
        lastUpdateDateTextView.text = "Last update : $lastUpdateDateString"

        // Open keyboard and put focus on the content edit text
        if (lastTitle!!.isEmpty() && lastContent!!.isEmpty()) {
            contentEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(contentEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        // Hide note actions by default
        noteActionsLayout.setVisibility(View.GONE)

        // Hide/Show note actions
        noteActionsButton.setOnClickListener {
            if (noteActionsLayout.getVisibility() == View.GONE) {
                noteActionsLayout.setVisibility(View.VISIBLE)
                noteActionsButton.setBackgroundColor(
                    darkenNoteColor(
                        Color.parseColor(noteColor),
                        0.9f
                    )
                )
            } else if (noteActionsLayout.getVisibility() == View.VISIBLE) {
                noteActionsButton.setBackgroundColor(Color.parseColor(noteColor))
                noteActionsLayout.setVisibility(View.GONE)
            }
        }

        // Check the color picker
        colorPickerRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            /*if (checkedId == R.id.default_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteDefault)

            } else if (checkedId == R.id.red_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteRed)
            } else if (checkedId == R.id.orange_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteOrange)
            } else if (checkedId == R.id.yellow_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteYellow)
            } else if (checkedId == R.id.green_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteGreen)
            } else if (checkedId == R.id.cyan_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteCyan)
            } else if (checkedId == R.id.light_blue_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteLightBlue)
            } else if (checkedId == R.id.dark_blue_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteDarkBlue)
            } else if (checkedId == R.id.purple_color_checkbox) {
                noteColor = resources.getString(R.color.colorNotePurple)
            } else if (checkedId == R.id.pink_color_checkbox) {
                noteColor = resources.getString(R.color.colorNotePink)
            } else if (checkedId == R.id.brown_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteBrow)
            } else if (checkedId == R.id.grey_color_checkbox) {
                noteColor = resources.getString(R.color.colorNoteGrey)*/

            //noteColor = resources.getString(R.color.colorNoteCyan)//R.color.colorNoteDefault)
            //noteColor = getResources().getString(R.color.colorNotePink);
            //Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.redish)))
            Color.parseColor("#"+Integer.toHexString(getColor( R.color.colorNoteDefault)))
        }
        noteLayout.setBackgroundColor(Color.parseColor(noteColor))
        noteActionsLayout.setBackgroundColor(Color.parseColor(noteColor))
        bottomToolbar.setBackgroundColor(Color.parseColor(noteColor))
        //actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor(noteColor)))
        window.statusBarColor = darkenNoteColor(Color.parseColor(noteColor), 0.7f)

        noteActionsButton.setBackgroundColor(darkenNoteColor(Color.parseColor(noteColor), 0.9f))
    }


    fun darkenNoteColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor)
        val g = Math.round(Color.green(color) * factor)
        val b = Math.round(Color.blue(color) * factor)
        return Color.argb(
            a,
            Math.min(r, 255),
            Math.min(g, 255),
            Math.min(b, 255)
        )
    }

// Save note content on back pressed
    override fun onBackPressed() {

        var changed: Boolean? = false
        val titleText = titleEditText.getText().toString()
        val contentText = contentEditText.getText().toString()

        if (titleText != lastTitle || contentText != lastContent)
            changed = true

        // Check if fields are not empty
        if ((!TextUtils.isEmpty(titleText) || !TextUtils.isEmpty(contentText)) && changed!!) {


            val noteJSON = JSONObject()
            try {
                noteJSON.put("noteTitle", titleEditText.getText().toString())
                noteJSON.put("noteContent", contentEditText.getText().toString())
                noteJSON.put("noteColor", noteColor)
                noteJSON.put("noteCreationDate", creationDateString)
                noteJSON.put("noteLastUpdateDate", lastUpdateDateString)
                noteJSON.put("notePosition", notePosition)
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

}