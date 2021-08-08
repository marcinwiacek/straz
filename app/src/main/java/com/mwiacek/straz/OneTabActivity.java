package com.mwiacek.straz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class OneTabActivity extends Activity {
    private static final String[] Tips = new String[]{
            "zakręt", "ostrzegawcze", "zakaz"
    };
    ArrayAdapter<String> adapter;
    ArrayAdapter<CharSequence> adapter1;
    Activity MyActivity;
    AutoCompleteTextView textView;
    ScrollerWebView webView;
    Button b1, b2;
    Spinner spinner;
    ScrollerView scroller;
    ProgressDialog progressDialog;
    Boolean firstLoad = true;
    Boolean Kontrolki, Zawijanie;
    String[] fileNames;
    byte[] DisplayTotal;
    int mSpinnerListaId;
    int SearchCurr, SearchNum;
    int LayoutId;
    String Db_Number_Of_Selection;

    @Override
    protected void onDestroy() {
        ((StrazActivity) getParent()).db.SetSetting(DBClass.DB_SIZE,
                Integer.toString((int) (100 * webView.getScale())));

        super.onDestroy();
    }

    public void DisplayIt() {
        if (progressDialog != null) return;

        progressDialog = ProgressDialog.show(this, "", "Odświeżanie", true, false);

        if (!firstLoad) {
            MyActivity.runOnUiThread(() -> {
                webView.dispatchTouchEvent(MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN,
                        (float) 0.0,
                        (float) 0.0,
                        0));

                webView.dispatchTouchEvent(MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP,
                        (float) 0.0,
                        (float) 0.0,
                        0));
            });
        }

        new Thread(() -> {
            try {
                InputStream stream;
                if (spinner.getSelectedItemPosition() < fileNames.length) {
                    stream = getAssets().open(fileNames[spinner.getSelectedItemPosition()]);
                    DisplayTotal = new byte[stream.available()];
                    stream.read(DisplayTotal, 0, DisplayTotal.length);
                    stream.close();
                } else {
                    stream = null;
                    DisplayTotal = new byte[0];
                }

                scroller.Showed = false;

                MyActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        int scale = (int) (100 * webView.getScale());

                        if (android.os.Build.VERSION.SDK_INT >= 19) {
                            Kontrolki = ((StrazActivity) getParent()).sp.getBoolean("Kontrolki", false);
                            try {
                                Method m;
                                m = webView.getSettings().getClass().getMethod("setDisplayZoomControls", boolean.class);
                                m.invoke(webView.getSettings(), Kontrolki);
                            } catch (SecurityException ignore) {
                            } catch (NoSuchMethodException ignore) {
                            } catch (IllegalArgumentException ignore) {
                            } catch (IllegalAccessException ignore) {
                            } catch (InvocationTargetException ignore) {
                            }
                        }

                        Zawijanie = ((StrazActivity) getParent()).sp.getBoolean("Zawijanie", false);
                        webView.getSettings().setUseWideViewPort(Zawijanie);

                        if (textView.length() != 0) {
                            webView.loadDataWithBaseURL("file:///android_asset/",
                                    (new String(DisplayTotal, 0, DisplayTotal.length))
                                            .replaceAll("(?![^<]+>)((?i:\\Q" + textView.getText().toString().replace("\\E", "\\E\\\\E\\Q").replace("a", "\\E[aąĄ]\\Q").replace("c", "\\E[cćĆ]\\Q").replace("e", "\\E[eęĘ]\\Q").replace("l", "\\E[lłŁ]\\Q").replace("n", "\\E[nńŃ]\\Q").replace("o", "\\E[oóÓ]\\Q").replace("s", "\\E[sśŚ]\\Q").replace("z", "\\E[zźżŻŹ]\\Q") + "\\E))", "<ins style='background-color:yellow'>$1</ins>").replace("</body>", "<script>function GetY (object) {if (!object) {return 0;} else {return object.offsetTop+GetY(object.offsetParent);}}</script></body>"), "text/html", null, null);
                        } else {
                            webView.loadDataWithBaseURL("file:///android_asset/",
                                    new String(DisplayTotal, 0, DisplayTotal.length),
                                    "text/html", null, null);
                        }

                        DisplayTotal = null;

                        if (((StrazActivity) getParent()).sp.getBoolean("Wielkosc2", true)) {
                            if (firstLoad) {
                                if (!((StrazActivity) getParent()).db.GetSetting(DBClass.DB_SIZE, "").equals("")) {
                                    webView.setInitialScale(Integer.parseInt(((StrazActivity) getParent()).db.GetSetting(DBClass.DB_SIZE, "")));
                                }
                            } else {
                                webView.setInitialScale(scale);
                            }
                        }
                    }
                });
            } catch (IOException ignore) {
            }
        }).start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.cancel();
                } catch (Exception ignore) {
                }
            }
            progressDialog = null;
            ((StrazActivity) getParent()).db.SetSetting(Db_Number_Of_Selection,
                    Integer.toString(spinner.getSelectedItemPosition()));
        }
    }

    public void onCreateTab() {
        ViewGroup.LayoutParams params = scroller.getLayoutParams();
        params.width = scroller.myBitmap.getWidth();
        scroller.setLayoutParams(params);
        scroller.webview = webView;
        webView.sv = scroller;

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(((StrazActivity) getParent())
                .sp.getBoolean("Wielkosc", true));

        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        firstLoad = false;

                        b1.setEnabled(false);
                        if (textView.length() != 0) {
                            webView.loadUrl(
                                    "javascript:Android.ShowToast(document.getElementsByTagName('ins').length);");
                        } else {
                            b2.setEnabled(false);
                            webView.loadUrl("javascript:Android.ShowToast0();");
                        }
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if ((webView.ev.getX() != 0.0 ||
                                webView.ev.getY() != 0.0 ||
                                webView.ev.getDownTime() != webView.ev.getEventTime()) &&
                                url.contains("http")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            MyActivity.startActivity(intent);
                        }
                        return true;
                    }
                }
        );

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        adapter1 = new CustomArrayAdapter<>(this,
                new ArrayList<>(Arrays.asList(getResources().getStringArray(mSpinnerListaId))));
        adapter1.setDropDownViewResource(R.layout.sspinner);
        spinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DisplayIt();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner.setSelection(Integer.parseInt(
                ((StrazActivity) getParent()).db.GetSetting(
                        Db_Number_Of_Selection, "0")), false);

        adapter = new ArrayAdapter<>(this, R.layout.tip_item, Tips);
        textView.setAdapter(adapter);
        textView.setImeActionLabel("Szukaj", KeyEvent.KEYCODE_ENTER);
        textView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                DisplayIt();
                return true;
            }
            return false;
        });

        b1.setOnClickListener(v -> {
            SearchCurr--;

            webView.loadUrl("javascript:window.scrollTo(0, GetY (document.getElementsByTagName('ins').item(" + (SearchCurr - 1) + ")));");

            if (SearchCurr == 1) v.setEnabled(false);
            b2.setEnabled(true);
        });

        b2.setOnClickListener(v -> {
            SearchCurr++;

            webView.loadUrl("javascript:window.scrollTo(0, GetY (document.getElementsByTagName('ins').item(" + (SearchCurr - 1) + ")));");

            b1.setEnabled(SearchCurr != 1);
            if (SearchCurr == SearchNum) v.setEnabled(false);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        webView.getSettings().setBuiltInZoomControls(((StrazActivity) getParent())
                .sp.getBoolean("Wielkosc", true));

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            Kontrolki = ((StrazActivity) getParent()).sp.getBoolean("Kontrolki", false);
            try {
                Method m;
                m = webView.getSettings().getClass().getMethod("setDisplayZoomControls", boolean.class);
                m.invoke(webView.getSettings(), Kontrolki);
            } catch (SecurityException ignore) {
            } catch (NoSuchMethodException ignore) {
            } catch (IllegalArgumentException ignore) {
            } catch (IllegalAccessException ignore) {
            } catch (InvocationTargetException ignore) {
            }
        }

        Zawijanie = ((StrazActivity) getParent()).sp.getBoolean("Zawijanie", false);
        webView.getSettings().setUseWideViewPort(Zawijanie);

        if (((StrazActivity) getParent()).sp.getBoolean("No_Lock", false)) {
            (getParent()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        } else {
            (getParent()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }

    class JavaScriptInterface {
        JavaScriptInterface(Context c) {
        }

        @JavascriptInterface
        public void ShowToast0() {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.cancel();
                    } catch (Exception ignore) {
                    }
                }
                progressDialog = null;
                ((StrazActivity) getParent()).db.SetSetting(Db_Number_Of_Selection,
                        Integer.toString(spinner.getSelectedItemPosition()));
            }
        }

        @JavascriptInterface
        public void ShowToast(int Numberr) {
            SearchNum = Numberr;
            SearchCurr = 0;

            Toast.makeText(MyActivity, "Ilość wyników: " + (SearchNum), Toast.LENGTH_SHORT).show();

            MyActivity.runOnUiThread(() -> {
                b2.setEnabled(SearchNum > 0);
            });

            ShowToast0();
        }
    }

    class CustomArrayAdapter<T> extends ArrayAdapter<T> {
        CustomArrayAdapter(Context ctx, ArrayList<T> objects) {
            super(ctx, android.R.layout.simple_spinner_item, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
            }

            View view = super.getView(position, convertView, parent);

            TextView text = (TextView) view.findViewById(android.R.id.text1);
            text.setTypeface(null, Typeface.NORMAL);
            text.setSingleLine(false);

            return view;
        }
    }
}
