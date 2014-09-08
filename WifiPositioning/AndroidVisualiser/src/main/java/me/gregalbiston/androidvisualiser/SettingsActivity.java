package me.gregalbiston.androidvisualiser;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 28/07/13
 * Time: 11:38
 * Extends activity to setup Activity Fragment following Android Developers advice: http://developer.android.com/guide/topics/ui/settings.html#Activity
 */
public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }

    @Override
    public void onPause() {
        super.onPause();

    }
}