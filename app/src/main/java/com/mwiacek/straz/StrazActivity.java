package com.mwiacek.straz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.TabHost;
import android.widget.TextView;

public class StrazActivity extends TabActivity {
    final Activity MyActivity5 = this;
    public Intent in;
    DBClass db;
    SharedPreferences sp;
    AlertDialog ad;

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(sp.getBoolean("Obrot", true) ?
                ActivityInfo.SCREEN_ORIENTATION_SENSOR :
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getString("wyglad", "").length() == 0) {
            setTheme(android.R.style.Theme);
            SharedPreferences.Editor editor1 = sp.edit();
            editor1.putString("wyglad", "pusty");
            editor1.commit();
        } else if (sp.getString("wyglad", "").equals("pusty")) {
            setTheme(android.R.style.Theme);
        } else if (sp.getString("wyglad", "").equals("pusty2")) {
            setTheme(android.R.style.Theme_Black);
        } else if (sp.getString("wyglad", "").equals("holo")) {
            setTheme(android.R.style.Theme_Holo);
        } else if (sp.getString("wyglad", "").equals("holo2")) {
            setTheme(android.R.style.Theme_Holo_Light);
        } else if (sp.getString("wyglad", "").equals("domyslnyurzadzenie")) {
            setTheme(android.R.style.Theme_DeviceDefault);
        } else if (sp.getString("wyglad", "").equals("domyslnyurzadzenie2")) {
            setTheme(android.R.style.Theme_DeviceDefault_Light);
        }

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (!sp.getBoolean("Obrot", true)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (sp.getBoolean("No_Lock", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        db = new DBClass(this);

        if (db.GetSetting(DBClass.DB_NR_WERSJI, "0").equals("0") &&
                android.os.Build.VERSION.SDK_INT >= 19) {
            Editor editor = sp.edit();
            editor.putBoolean("Zawijanie", true);
            editor.commit();
        }

        Paint p = new Paint();
        int h = (int) (p.measureText("Marcin")
                * getApplicationContext().getResources().getDisplayMetrics().density);

        setContentView(R.layout.main);

        in = getIntent();

        TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("ogolne")
                .setIndicator("Ogólne", null).setContent(
                        new Intent().setClass(this, OgolneActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("chemia")
                .setIndicator("Chemia", null).setContent(
                        new Intent().setClass(this, ChemiaActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("inne")
                .setIndicator("Inne", null).setContent(
                        new Intent().setClass(this, InneActivity.class)));

        for (int i = 0; i < 3; i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = h;
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        tabHost.setCurrentTab(Integer.parseInt(db.GetSetting(DBClass.DB_TAB_HOST, "2")));

        try {
            if (!db.GetSetting(DBClass.DB_NR_WERSJI, "0").equals(
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName)) {
                ad = new AlertDialog.Builder(this).create();
                ad.setCancelable(false);
                ad.setMessage("Aplikacja została uaktualniona, używając jej zgadzasz się na to, że "
                        + "autor nie ponosi żadnej odpowiedzialności z tytułu jej używania przez Ciebie, "
                        + "w razie znalezienia błędów prośba o kontakt (np. przez \"Kontakt email\")\n\nMożesz "
                        + "przyspieszyć jej rozwój składając datek.");

                ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK (00:07)", (dialog, which) -> dialog.dismiss());

                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "Info o datku", (dialog, which) -> {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("http://www.mwiacek.com/www/?q=node/121"));
                    MyActivity5.startActivity(intent1);
                });

                ad.show();
                ad.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                new CountDownTimer(7000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        ad.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                                "OK (00:0" + millisUntilFinished / 1000 + ")");
                    }

                    @Override
                    public void onFinish() {
                        ad.getButton(DialogInterface.BUTTON_POSITIVE).setText("OK");
                        ad.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                }.start();

                db.SetSetting(DBClass.DB_NR_WERSJI, getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            }
        } catch (NameNotFoundException ignore) {
        }
    }

    @Override
    protected void onDestroy() {
        db.SetSetting(DBClass.DB_TAB_HOST, Integer.toString(getTabHost().getCurrentTab()));
        super.onDestroy();
        if (db != null) db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                ShowAbout();
                return true;
            case R.id.sett:
                ShowSett();
                return true;
            case R.id.report:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                String subject = "";
                try {
                    subject = "Straż "
                            + getPackageManager().getPackageInfo(getPackageName(), 0).versionName
                            + " / Android " + Build.VERSION.RELEASE;
                } catch (Exception ignore) {
                }

                String[] extra = new String[]{"marcin@mwiacek.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, extra);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.setType("message/rfc822");
                try {
                    startActivity(emailIntent);
                } catch (Exception e) {
                    AlertDialog alertDialog;
                    alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Informacja");
                    alertDialog.setMessage("Błąd stworzenia maila");
                    alertDialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void ShowAbout() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.about);
        dialog.setCancelable(true);

        ScrollerWebView webView = dialog.findViewById(R.id.webView5);
        ScrollerView scroller = dialog.findViewById(R.id.view5);

        scroller.webview = webView;
        webView.sv = scroller;
        ViewGroup.LayoutParams params = scroller.getLayoutParams();
        params.width = scroller.myBitmap.getWidth();
        scroller.setLayoutParams(params);

        try {
            dialog.setTitle("Straż "
                    + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception ignore) {
        }

        TextView title = dialog.findViewById(android.R.id.title);
        title.setSingleLine(false);

        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webView.getSettings().setAppCacheEnabled(false);

        webView.loadUrl("file:///android_asset/about.htm");

        dialog.show();
    }

    void ShowSett() {
        Intent intent = new Intent(MyActivity5, PreferencesActivity.class);
        intent.putExtra("wyglad", sp.getString("wyglad", ""));
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (!sp.getString("wyglad", "").equals(data.getStringExtra("wyglad"))) {
                MyActivity5.finish();
                MyActivity5.startActivity(new Intent(MyActivity5, MyActivity5.getClass()));
            }
        }
    }
}
