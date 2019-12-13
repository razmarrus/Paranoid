package com.marvinboots.harveydentcalculator.fragments

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.marvinboots.harveydentcalculator.MainActivity
import com.marvinboots.harveydentcalculator.R
import net.objecthunter.exp4j.ExpressionBuilder

class ScientificFragment : Fragment(){

    companion object {

        fun newInstance(): ScientificFragment {
            return ScientificFragment()
        }

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.scientific_fragment, container, false)
    }


}