package com.marvinboots.deathnote

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper


import android.view.View
import android.widget.Button

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
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
    lateinit var rcAdapter: RecycleViewAdapter

    lateinit var listViewItems: MutableList<ItemObjects>
    lateinit var recyclerView: RecyclerView
    lateinit var createSimpleNoteButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Check and get storage permissions

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )

            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
        }


        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)

        // Check orientation to put the good amount of columns
        if (resources.configuration.orientation == 2) {//==={
            var column = 3
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(column, 1)
            recyclerView.setLayoutManager(staggeredGridLayoutManager)
        }
        else
        {
            val linearLayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(linearLayoutManager)

        }
        // Prevent the loss of items
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0)

        val scale = resources.displayMetrics.density
        val spacing = (1 * scale + 0.5f).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacing))

        // Load notes from internal storage
        staggeredList = loadNotes()


        //rcAdapter = RecyclerViewAdapter(staggeredList)
        //lateinit var rcAdapter: RecycleViewAdapter
        rcAdapter = RecycleViewAdapter(staggeredList)
        recyclerView.adapter = rcAdapter

        // Drag and drop
        lateinit var ith : ItemTouchHelper
        //val ith = ItemTouchHelper(_ithCallback)
        ith = ItemTouchHelper(_ithCallback)
        ith.attachToRecyclerView(recyclerView)

        // Create a simple note button click listener


        createSimpleNoteButton = findViewById(R.id.create_new_note1)
        createSimpleNoteButton.setOnClickListener(View.OnClickListener {
            val simpleNoteIntent = Intent(applicationContext, NoteCreation::class.java)
            simpleNoteIntent.putExtra("title", "")
            simpleNoteIntent.putExtra("content", "")
            //simpleNoteIntent.putExtra("color", resources.getString(R.color.colorNoteDefault))
            simpleNoteIntent.putExtra("creationDate", "")
            simpleNoteIntent.putExtra("tags", "")
            simpleNoteIntent.putExtra("position", -1)
            // TODO
            startActivityForResult(simpleNoteIntent, 1)
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            val noteJSON = data.getStringExtra("noteJSON")

            try {
                val json = JSONObject(noteJSON!!)

                val noteTitle = json.getString("noteTitle")
                val noteContent = json.getString("noteContent")
                //val noteColor = json.getString("noteColor")
                val noteLastUpdateDate = json.getString("noteLastUpdateDate")
                val noteCreationDate = json.getString("noteCreationDate")
                val notePosition = json.getInt("notePosition")

                val noteTags = json.getString("noteTags")
                /*val JSONnoteTags = json.getJSONArray("tags")

                var noteTags = List(JSONnoteTags.length()) {
                    JSONnoteTags.getString(it)*/
                //}

                saveNote(noteJSON, noteCreationDate)

                if (notePosition > -1) {
                    listViewItems.removeAt(notePosition)
                    listViewItems.add(
                        notePosition,
                        ItemObjects(
                            noteTitle,
                            noteContent,
                            //noteColor,
                            noteLastUpdateDate,
                            noteCreationDate,
                            noteTags
                        )
                    )
                    rcAdapter.notifyItemChanged(notePosition)
                } else {
                    listViewItems.add(
                        ItemObjects(
                            noteTitle,
                            noteContent,
                            //noteColor,
                            noteLastUpdateDate,
                            noteCreationDate,
                            noteTags
                        )
                    )
                    rcAdapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    internal var _ithCallback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {

            Collections.swap(staggeredList, viewHolder.adapterPosition, target.adapterPosition)
            rcAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //TODO
        }

        // Defines the enabled move directions in each state (idle, swiping, dragging).
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

            return makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
            )
        }
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
            var line: String?
            line = bufferedReader!!.readLine()
            try {
                if (line != null) {
                    while ( line != null) {
                        //line = bufferedReader.readLine()
                        sb.append(line)
                        line = bufferedReader.readLine()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

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

            //var noteTags: List<String>? = null
            var noteTags: String? = null
            try {
                if (json != null)
                {
                    noteTags = json.getString("noteTags")
                    /*var JSONnoteTag = json.getJSONArray("noteTags")//("tag1")

                    noteTags = List(JSONnoteTag.length()) {
                        JSONnoteTag.getString(it)
                    }*/
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

            if (secure!!)
                listViewItems.add(
                    ItemObjects(
                        noteTitle!!,
                        noteContent!!,
                        //noteColor!!,
                        noteLastUpdateDate!!,
                        noteCreationDate!!,
                        noteTags!!
                    )
                )
        }
        return listViewItems
    }


    /*@SuppressLint("ResourceType")
    private fun getListItemData(): List<ItemObjects> {

        listViewItems = ArrayList()

        listViewItems.add(
            ItemObjects(
                "",
                "Cat cat cat cat",
                resources.getString(R.color.colorNoteRed)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Lorem",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nullapariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                resources.getString(R.color.colorNoteOrange)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Length length length length",
                resources.getString(R.color.colorNoteYellow)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "String string string string",
                resources.getString(R.color.colorNoteGreen)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Content content content content",
                resources.getString(R.color.colorNoteCyan)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Size size size size size size size size size size size",
                resources.getString(R.color.colorNoteLightBlue)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Char char char char char char char",
                resources.getString(R.color.colorNoteDarkBlue)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Dog dog dog dog dog dog",
                resources.getString(R.color.colorNotePurple)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Int hold dog cat hold hold hold cat hold hold dog hold int hold hold hold hold hold",
                resources.getString(R.color.colorNotePink)
            )
        )

        listViewItems.add(
            ItemObjects(
                "",
                "test test test test test test",
                resources.getString(R.color.colorNoteRed)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Lorem",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nullapariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                resources.getString(R.color.colorNoteOrange)
            )
        )
        /*listViewItems.add(
            ItemObjects(
                "Alkane",
                "Hello Markdown",
                R.drawable.one,
                resources.getString(R.color.colorNoteYellow)
            )
        )*/
        listViewItems.add(
            ItemObjects(
                "Ethane",
                "Hello Markdown",
                resources.getString(R.color.colorNoteGreen)
            )
        )
        /*listViewItems.add(
            ItemObjects(
                "Alkyne",
                "Hello Markdown",
                R.drawable.three,
                resources.getString(R.color.colorNoteCyan)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Benzene",
                "Hello Markdown",
                R.drawable.four,
                resources.getString(R.color.colorNoteLightBlue)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Amide",
                "Hello Markdown",
                R.drawable.one,
                resources.getString(R.color.colorNoteDarkBlue)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Amino Acid",
                "Hello Markdown",
                R.drawable.two,
                resources.getString(R.color.colorNotePurple)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Phenol",
                "Hello Markdown",
                resources.getString(R.color.colorNotePink)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Carbonxylic",
                "Hello Markdown",
                R.drawable.four,
                resources.getString(R.color.colorNoteRed)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Nitril",
                "Hello Markdown",
                R.drawable.one,
                resources.getString(R.color.colorNoteOrange)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Ether",
                "Hello Markdown",
                R.drawable.two,
                resources.getString(R.color.colorNoteYellow)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Ester",
                "Hello Markdown",
                R.drawable.three,
                resources.getString(R.color.colorNoteGreen)
            )
        )*/
        listViewItems.add(
            ItemObjects(
                "Alcohol",
                "Hello Markdown",
                resources.getString(R.color.colorNoteCyan)
            )
        )
        listViewItems.add(
            ItemObjects(
                "Title only",
                "",
                resources.getString(R.color.colorNoteLightBlue)
            )
        )
        listViewItems.add(
            ItemObjects(
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "",
                resources.getString(R.color.colorNoteDarkBlue)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Content only",
                resources.getString(R.color.colorNotePurple)
            )
        )
        listViewItems.add(ItemObjects("", "Kkk", resources.getString(R.color.colorNotePink)))
        listViewItems.add(ItemObjects("", "Kkkkkk", resources.getString(R.color.colorNoteRed)))
        listViewItems.add(
            ItemObjects(
                "",
                "Kkkkkkkkkk",
                resources.getString(R.color.colorNoteOrange)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Kkkkkkkkkkkkk",
                resources.getString(R.color.colorNoteYellow)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Kkkkkkkkkkkkkkkkkkk",
                resources.getString(R.color.colorNoteGreen)
            )
        )
        listViewItems.add(
            ItemObjects(
                "",
                "Kkkkkkkkkkkkkkkkkkkkkkkk",
                resources.getString(R.color.colorNoteCyan)
            )
        )

        return listViewItems
    }*/


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
