package com.marvinboots.harveydentcalculator.fragments


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.marvinboots.harveydentcalculator.R


class BasicFragment : Fragment(){
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View viewHierarchy = inflater.inflate(R.layout.button_fragment, container, false);

        return viewHierarchy;
    }*/
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