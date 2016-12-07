package net.hokiegeek.android.dondeestas;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener
{

    public static final String KEY_SERVER = "pref_server";
    public static final String KEY_USER_ID = "pref_userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SettingsActivity.KEY_SERVER.equals(key)) {
            String dbServer = sharedPreferences.getString(key, "");
            if (!dbServer.startsWith("http")) {
                sharedPreferences.edit().putString(key, "http://" + dbServer).apply();
            }
        }

        // Preference pref = sharedfindPreference(key);
        // pref.setSummary(sharedPreferences.getString(key, ""));
    }
}
