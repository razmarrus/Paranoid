package com.marvinboots.harveydentcalculator.fragments


import android.os.Bundle
import android.app.Fragment
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import com.marvinboots.harveydentcalculator.Engi
import com.marvinboots.harveydentcalculator.R
import net.objecthunter.exp4j.ExpressionBuilder


class BasicFragment : Fragment(){

    companion object {

        fun newInstance(): BasicFragment {
            return BasicFragment()
        }
    }



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.basic_fragment, container, false)
    }



}