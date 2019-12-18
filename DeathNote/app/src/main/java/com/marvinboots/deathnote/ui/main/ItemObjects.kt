package com.marvinboots.deathnote.ui.main

import java.text.SimpleDateFormat
import java.util.*

class ItemObjects(title: String?, content: String?, color: String?) {

    private var _title: String? = null
    private var _content: String? = null
    private var _image: Int = 0
    private var _color: String? = null
    private var _lastUpdateDate: String? = null
    private var _creationDate: String? = null

    init{//(title: String, content: String, color: String): ??? {
        _content = content
        _image = -1
        _color = color
        if (title == null || title == "")
            _title = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())//creationDate
        else
            _title = title
    }
    //internal fun ItemObjects(title: String, content: String, image: Int, color: String): ???
    constructor(title: String, content: String, color: String, image: Int) : this(title, content, color)
    {
        _image = image
    }

    constructor(title: String, content: String, color: String, lastUpdateDate: String, creationDate: String) : this(title, content, color){
        _lastUpdateDate = lastUpdateDate
        _creationDate = creationDate
    }

    constructor(title: String, content: String,
        image: Int, color: String,
        lastUpdateDate: String,
        creationDate: String
    ):  this(title, content, color, image){
        if(_title == null)
            _title = creationDate
        _lastUpdateDate = lastUpdateDate
        _creationDate = creationDate
    }

    // Getters
    fun getTitle(): String? {
        return _title
    }

    internal fun getContent(): String? {
        return _content
    }

    internal fun getColor(): String? {
        return _color
    }

    internal fun getImage(): Int {
        return _image
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

    fun setColor(color: String) {
        _color = color
    }

}