package com.marvinboots.deathnote.ui.main

import java.text.SimpleDateFormat
import java.util.*

class ItemObjects(title: String?, content: String?) {

    private var _title: String? = null
    private var _content: String? = null
    //private var _image: Int = 0
    //private var _color: String? = null
    private var _lastUpdateDate: String? = null
    private var _creationDate: String? = null
    private var _tags: String? = null
   // private var _tags: List<String>? = null

    init{//(title: String, content: String, color: String): ??? {
        _content = content
        if ((title == null || title == "") && (content!= null || content != ""))
            _title = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())//creationDate
        else
            _title = title
    }
    //internal fun ItemObjects(title: String, content: String, image: Int, color: String): ???

    constructor(title: String, content: String, lastUpdateDate: String, creationDate: String, tags: String) : this(title, content){
        _lastUpdateDate = lastUpdateDate
        _creationDate = creationDate
        _tags = tags
    }

    constructor(title: String, content: String, lastUpdateDate: String, creationDate: String) : this(title, content){
        _lastUpdateDate = lastUpdateDate
        _creationDate = creationDate
    }

    /*constructor(title: String, content: String,lastUpdateDate: String, creationDate: String):
            this(title, content){
        _lastUpdateDate = lastUpdateDate
        _creationDate = creationDate
    }*/

    // Getters
    fun getTitle(): String? {
        return _title
    }

    internal fun getContent(): String? {
        return _content
    }

    internal fun getTags(): String? {
        return _tags
    }

    internal fun getLastUpdateDate(): String? {
        return _lastUpdateDate
    }

    internal fun getCreationDate(): String? {
        return _creationDate
    }

    // Setters
    fun setTitle(title: String) {
        _title = title
    }

    fun setContent(content: String) {
        _content = content
    }

    /*fun setColor(color: String) {
        _color = color
    }*/

}