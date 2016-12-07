package net.hokiegeek.android.dondeestas;

import android.os.Bundle;
import android.app.Fragment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        findPreference(SettingsActivity.KEY_SERVER).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String dbServer = (String)newValue;

                if (!dbServer.startsWith("http")) {
                    getPreferenceManager().getSharedPreferences().edit().putString(SettingsActivity.KEY_SERVER, "http://" + dbServer).apply();
                } else {
                    preference.setSummary(dbServer);
                }
                return false;
            }
        });

        findPreference(SettingsActivity.KEY_USER_ID).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return false;
            }
        });
    }
}
