package net.hokiegeek.android.dondeestas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_SERVER = "pref_server";
    public static final String KEY_USER_ID = "pref_userid";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        addOnPreferenceChangeListeners();

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        setSummaryIfNoValue(prefs, KEY_SERVER);
        setSummaryIfNoValue(prefs, KEY_USER_ID);
    }

    private void addOnPreferenceChangeListeners() {
        findPreference(KEY_SERVER).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String dbServer = (String)newValue;

                if (!dbServer.startsWith("http")) {
                    getPreferenceManager().getSharedPreferences().edit().putString(KEY_SERVER, "http://" + dbServer).apply();
                }
                preference.setSummary(dbServer);
                return false;
            }
        });

        findPreference(KEY_USER_ID).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return false;
            }
        });
    }

    private void setSummaryIfNoValue(SharedPreferences prefs, String key) {
        String val = prefs.getString(key, "");
        if (!"".equals(val)) {
            findPreference(key).setSummary(val);
        }
    }
}
