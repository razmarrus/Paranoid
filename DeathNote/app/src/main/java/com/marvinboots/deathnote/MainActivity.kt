package com.marvinboots.deathnote

import android.Manifest
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
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.marvinboots.deathnote.ui.main.GridSpacingItemDecoration
import com.marvinboots.deathnote.ui.main.ItemObjects

import org.json.JSONException
import org.json.JSONObject
import java.io.*

import java.util.ArrayList
import java.util.Collections
import java.io.IOException;


class MainActivity : AppCompatActivity() {

    lateinit var staggeredList: List<ItemObjects>
    lateinit var rcAdapter: RecycleViewAdapter
    lateinit var listViewItems: MutableList<ItemObjects>
    lateinit var recyclerView: RecyclerView
    lateinit var createSimpleNoteButton : Button
    lateinit var tagEditText: EditText

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
//96 64 32 48
        tagEditText   = findViewById(R.id.tag_input)
        tagEditText.setHint("input tags");
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
        val spacing = (5 * scale + 0.5f).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacing))

        // Load notes from internal storage
        staggeredList = loadNotes()
        staggeredList = staggeredList.sortedWith(compareBy({ it.getTitle() }))//.sortedBy { it  }

        rcAdapter = RecycleViewAdapter(staggeredList)
        recyclerView.adapter = rcAdapter


        // Drag and drop
        lateinit var ith : ItemTouchHelper
        ith = ItemTouchHelper(_ithCallback)
        ith.attachToRecyclerView(recyclerView)

        // Create a simple note button click listener
        createSimpleNoteButton = findViewById(R.id.create_new_note1)
        createSimpleNoteButton.setOnClickListener(View.OnClickListener {
            val simpleNoteIntent = Intent(applicationContext, NoteCreation::class.java)
            simpleNoteIntent.putExtra("title", "")
            simpleNoteIntent.putExtra("content", "")
            simpleNoteIntent.putExtra("creationDate", "")
            simpleNoteIntent.putExtra("tags", "")
            simpleNoteIntent.putExtra("position", -1)
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
                val noteLastUpdateDate = json.getString("noteLastUpdateDate")
                val noteCreationDate = json.getString("noteCreationDate")
                val notePosition = json.getInt("notePosition")
                val noteTags = json.getString("noteTags")

                saveNote(noteJSON, noteCreationDate)

                if (notePosition > -1) {
                    listViewItems.removeAt(notePosition)
                    listViewItems.add(
                        notePosition,
                        ItemObjects(
                            noteTitle,
                            noteContent,
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
            target: RecyclerView.ViewHolder): Boolean {

            Collections.swap(staggeredList, viewHolder.adapterPosition, target.adapterPosition)
            rcAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            /*Toast.makeText(this@MainActivity, staggeredList.get(target.adapterPosition).getTitle(), Toast.LENGTH_LONG).show()

            val noteJSON = JSONObject()
            JSONObject().remove(staggeredList.get(viewHolder.adapterPosition).getTitle())//getAsJsonObject("accounts").

            noteJSON.remove(staggeredList.get(viewHolder.adapterPosition).getTitle())
            noteJSON.remove(staggeredList.get(viewHolder.adapterPosition).getCreationDate())
            noteJSON.remove(staggeredList.get(viewHolder.adapterPosition).getLastUpdateDate())
            noteJSON.remove(staggeredList.get(viewHolder.adapterPosition).getContent())
            noteJSON.remove(staggeredList.get(viewHolder.adapterPosition).getTags())
            */

            //Toast.makeText(this@MainActivity, "Deleted", Toast.LENGTH_LONG).show()

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //Toast.makeText(this@MainActivity, "Test", Toast.LENGTH_LONG).show()
        }

        // Defines the enabled move directions in each state (idle, swiping, dragging).
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            Toast.makeText(this@MainActivity, "Move to delete", Toast.LENGTH_LONG).show()
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

    fun itemTouchHelperFun()
    {
        lateinit var ith : ItemTouchHelper
        ith = ItemTouchHelper(_ithCallback)
        ith.attachToRecyclerView(recyclerView)

        createSimpleNoteButton = findViewById(R.id.create_new_note1)
        createSimpleNoteButton.setOnClickListener(View.OnClickListener {
            val simpleNoteIntent = Intent(applicationContext, NoteCreation::class.java)
            simpleNoteIntent.putExtra("title", "")
            simpleNoteIntent.putExtra("content", "")
            simpleNoteIntent.putExtra("creationDate", "")
            simpleNoteIntent.putExtra("tags", "")
            simpleNoteIntent.putExtra("position", -1)
            startActivityForResult(simpleNoteIntent, 1)
        })
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


    fun sortByTitle(view: View)
    {
        staggeredList = loadNotes()
        staggeredList = staggeredList.sortedWith(compareBy({ it.getTitle() }))//.sortedBy { it  }
        rcAdapter = RecycleViewAdapter(staggeredList)
        recyclerView.adapter = rcAdapter

        // Drag and drop
        itemTouchHelperFun()
    }

    fun sortByDate(view: View)
    {
        staggeredList = loadNotes()
        staggeredList = staggeredList.sortedWith(compareBy({ it.getCreationDate() }))//.sortedBy { it  }
        rcAdapter = RecycleViewAdapter(staggeredList)
        recyclerView.adapter = rcAdapter

        itemTouchHelperFun()
    }

    fun searchByTag (view: View)
    {
        staggeredList = loadNotes()

        var tagsString = tagEditText.getText().toString()
        tagsString = tagsString.toLowerCase()
        val inputTagList: List<String> = tagsString.split(" ").map { it.trim() }
        var taggedNotes: MutableList<ItemObjects> = mutableListOf<ItemObjects>()//MutableList<ItemObjects>()

        for(note in staggeredList)
        {
            if(note.getTags() != null) {
                val tagList: List<String> = note.getTags()!!.split(" ").map { it.trim() }
                for(tag in tagList)
                {
                    if(inputTagList.contains(tag.toLowerCase()))
                    {
                        taggedNotes.add(note)
                    }
                }
            }
        }
        rcAdapter = RecycleViewAdapter(taggedNotes)
        recyclerView.adapter = rcAdapter
        // Drag and drop
        itemTouchHelperFun()
    }

    override fun onResume() {
        super.onResume()
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
        staggeredList = loadNotes()
        staggeredList = staggeredList.sortedWith(compareBy({ it.getTitle() }))//.sortedBy { it  }
        rcAdapter = RecycleViewAdapter(staggeredList)

        recyclerView.adapter = rcAdapter

        // Drag and drop
        itemTouchHelperFun()
    }

}
