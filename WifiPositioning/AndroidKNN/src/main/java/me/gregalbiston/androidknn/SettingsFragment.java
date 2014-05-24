package me.gregalbiston.androidknn;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 28/07/13
 * Time: 12:06
 * Extends Preference Fragment to load the preferences xml file: http://developer.android.com/guide/topics/ui/settings.html#Activity
 */
public class SettingsFragment extends PreferenceFragment {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}