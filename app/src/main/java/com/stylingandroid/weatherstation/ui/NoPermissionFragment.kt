package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stylingandroid.weatherstation.R

class NoPermissionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_no_permission, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AppCompatActivity) {
            context.supportActionBar?.apply {
                setTitle(R.string.app_name)
            }
        }
    }

}
