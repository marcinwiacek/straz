package com.mwiacek.straz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PreferencesActivity extends PreferenceActivity {
    final Activity MyActivity5 = this;
    List<String> listItems = new ArrayList<>();
    List<String> listItems2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", "").length() == 0) {
            setTheme(android.R.style.Theme);
        } else if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", "").equals("pusty")) {
            setTheme(android.R.style.Theme);
        } else if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", "").equals("pusty2")) {
            setTheme(android.R.style.Theme_Black);
        } else if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", "").equals("holo")) {
            setTheme(android.R.style.Theme_Holo);
        } else if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", "").equals("holo2")) {
            setTheme(android.R.style.Theme_Holo_Light);
        } else if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", "").equals("domyslnyurzadzenie")) {
            setTheme(android.R.style.Theme_DeviceDefault);
        } else if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", "").equals("domyslnyurzadzenie2")) {
            setTheme(android.R.style.Theme_DeviceDefault_Light);
        }

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sett2);

        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

            setTitle("Straż " + info.versionName);
        } catch (Exception ignore) {
        }

        Intent intent = new Intent();
        intent.putExtra("wyglad", PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", ""));
        setResult(RESULT_OK, intent);

        listItems.add("Android 2.x");
        listItems2.add("pusty");
        listItems.add("Android 2.x (czarny)");
        listItems2.add("pusty2");
        if (android.os.Build.VERSION.SDK_INT > 10) {
            listItems.add("Holo");
            listItems2.add("holo");
            listItems.add("Holo (jasny)");
            listItems2.add("holo2");
        }
        if (android.os.Build.VERSION.SDK_INT > 13) {
            listItems.add("producenta");
            listItems2.add("domyslnyurzadzenie");
            listItems.add("producenta (jasny)");
            listItems2.add("domyslnyurzadzenie2");
        }

        ListPreference customPref2 = (ListPreference) findPreference("wyglad");
        customPref2.setEntries(listItems.toArray(new CharSequence[listItems.size()]));
        customPref2.setEntryValues(listItems2.toArray(new CharSequence[listItems2.size()]));
        Iterator<String> it1, it2;
        it1 = listItems.iterator();
        it2 = listItems2.iterator();
        while (it1.hasNext()) {
            if (it2.next().equals(PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", ""))) {
                customPref2.setSummary("Wybrany wygląd: " + it1.next());
                break;
            } else {
                it1.next();
            }
        }
        customPref2.setOnPreferenceChangeListener((preference, newValue) -> {
            if (!newValue.equals(PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", ""))) {
                MyActivity5.finish();
                MyActivity5.startActivity(new Intent(MyActivity5, MyActivity5.getClass()));
            }

            Iterator<String> it11, it21;
            it11 = listItems.iterator();
            it21 = listItems2.iterator();
            while (it11.hasNext()) {
                if (it21.next().equals(PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("wyglad", ""))) {
                    preference.setSummary("Wybrany wygląd: " + it11.next());
                    break;
                } else {
                    it11.next();
                }
            }

            return true;
        });

        OnSharedPreferenceChangeListener listener = (prefs, key)
                -> setRequestedOrientation(prefs.getBoolean("Obrot", true) ?
                ActivityInfo.SCREEN_ORIENTATION_SENSOR :
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(listener);
        if (!sp.getBoolean("Obrot", true)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        findPreference("Czyszczenie").setOnPreferenceClickListener(preference -> {
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse("http://mwiacek.com/www/?q=node/121"));
            MyActivity5.startActivity(intent1);

            return true;
        });

        if (android.os.Build.VERSION.SDK_INT < 19) {
            findPreference("Kontrolki").setEnabled(false);
        }
    }
}	
    	