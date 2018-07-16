package com.stylingandroid.weatherstation.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.stylingandroid.weatherstation.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}
