package com.marvinboots.deathnote

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager;

import android.telephony.TelephonyManager;

import com.marvinboots.deathnote.ui.main.MainFragment

import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.main_activity.*

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper

import java.util.*
//StaggeredGridLayoutManager//core.app.Recy.recyclerview:recyclerview
/*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
*/

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button

import android.content.Context
import com.marvinboots.deathnote.ui.main.GridSpacingItemDecoration
import com.marvinboots.deathnote.ui.main.ItemObjects

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Collections


class MainActivity : AppCompatActivity() {

    lateinit var staggeredList: List<ItemObjects>
    //lateinit var rcAdapter: SolventRecyclerViewAdapter

    lateinit var listViewItems: MutableList<ItemObjects>
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }*/
        // Check and get storage permissions

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }


        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)

        // Check orientation to put the good amount of columns
        var column = 2
        if (resources.configuration.orientation == 2) //===
            column = 3

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(column, 1)
        recyclerView.setLayoutManager(staggeredGridLayoutManager)
        // Prevent the loss of items
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0)


        val scale = resources.displayMetrics.density
        val spacing = (1 * scale + 0.5f).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacing))

        // Load data
        //staggeredList = getListItemData();

        // Load notes from internal storage
        staggeredList = loadNotes()

        /*rcAdapter = SolventRecyclerViewAdapter(staggeredList)
        recyclerView.adapter = rcAdapter

        // Drag and drop
        val ith = ItemTouchHelper(_ithCallback)
        ith.attachToRecyclerView(recyclerView)

        // Create a simple note button click listener
        val createSimpleNoteButton = findViewById(R.id.create_new_note)
        createSimpleNoteButton.setOnClickListener(View.OnClickListener {
            val simpleNoteIntent = Intent(applicationContext, SimpleNoteCreation::class.java)
            simpleNoteIntent.putExtra("title", "")
            simpleNoteIntent.putExtra("content", "")
            //simpleNoteIntent.putExtra("color", resources.getString(R.color.colorNoteDefault))
            simpleNoteIntent.putExtra("creationDate", "")
            simpleNoteIntent.putExtra("position", -1)
            // TODO
            startActivityForResult(simpleNoteIntent, 1)
        })*/
    }

    private fun loadNotes(): kotlin.collections.List<ItemObjects> {

        listViewItems = ArrayList<ItemObjects>()
        val allNotes = fileList()

        var secure: Boolean? = false

        for (allNote in allNotes) {

            var fis: FileInputStream? = null
            try {
                fis = baseContext.openFileInput(allNote)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            var isr: InputStreamReader? = null
            if (fis != null) {
                isr = InputStreamReader(fis)
            }
            var bufferedReader: BufferedReader? = null
            if (isr != null) {
                bufferedReader = BufferedReader(isr)
            }
            val sb = StringBuilder()
            var line: String
            /*try {
                if (bufferedReader != null) {
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }*/

            var json: JSONObject? = null
            try {
                if (sb.toString().length > 0)
                    secure = true
                json = JSONObject(sb.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var noteTitle: String? = null
            try {
                if (json != null) {
                    noteTitle = json.getString("noteTitle")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var noteContent: String? = null
            try {
                if (json != null) {
                    noteContent = json.getString("noteContent")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var noteColor: String? = null
            try {
                if (json != null) {
                    noteColor = json.getString("noteColor")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var noteLastUpdateDate: String? = null
            try {
                if (json != null) {
                    noteLastUpdateDate = json.getString("noteLastUpdateDate")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var noteCreationDate: String? = null
            try {
                if (json != null) {
                    noteCreationDate = json.getString("noteCreationDate")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            /*if (secure!!)
                listViewItems.add(
                    ItemObjects(
                        noteTitle,
                        noteContent,
                        noteColor,
                        noteLastUpdateDate,
                        noteCreationDate
                    )
                )*/
        }
        return listViewItems
    }


    // Save notes to internal storage
    fun saveNote(note: String, noteCreationDate: String) {

        // Name file with current date
        val outputStream: FileOutputStream

        try {
            outputStream = openFileOutput(noteCreationDate, Context.MODE_PRIVATE)
            outputStream.write(note.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
