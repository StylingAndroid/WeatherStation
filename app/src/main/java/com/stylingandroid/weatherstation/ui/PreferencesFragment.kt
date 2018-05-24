package com.stylingandroid.weatherstation.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.stylingandroid.weatherstation.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is AppCompatActivity) {
            context.supportActionBar?.apply {
                title = getString(R.string.units)
                setDisplayHomeAsUpEnabled(true)
                setHasOptionsMenu(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    fragmentManager?.popBackStack()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
