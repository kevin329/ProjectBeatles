package tw.kevin329.beatles;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Info extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.about));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_clear_material );
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info.this.finish();
            }
        });

        if (savedInstanceState == null) {
            SettingsFragment mSettingsFragment = new SettingsFragment();
            replaceFragment(R.id.settings_container, mSettingsFragment);
        }
    }
    public void replaceFragment(int viewId, android.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }
    public static class SettingsFragment extends PreferenceFragment {
        String server_url;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()) ;
            server_url = prefs.getString("server_url", null);
            ListPreference serverList = (ListPreference) findPreference("server_url");
            serverList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference arg0, Object newValue) {
                    //這裏可以監聽到checkBox中值是否改變了
                    //並且可以拿到新改變的值
                    server_url = newValue.toString();
                    return true;
                }
            });
        }
    }
}
