package com.mwiacek.straz;

import android.os.Bundle;

public class OgolneActivity extends OneTabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyActivity = this;

        mSpinnerListaId = R.array.ogolnelista;
        Db_Number_Of_Selection = DBClass.DB_ZAKLADKA1;

        fileNames = new String[12];
        fileNames[0] = "ogolne/wielkosc.htm";
        fileNames[1] = "ogolne/kategorie.htm";
        fileNames[2] = "ogolne/podmioty.htm";
        fileNames[3] = "ogolne/poziomy.htm";
        fileNames[4] = "ogolne/kier_int.htm";
        fileNames[5] = "ogolne/kier_tak.htm";
        fileNames[6] = "ogolne/kier_str.htm";
        fileNames[7] = "ogolne/rodz_dzi.htm";
        fileNames[8] = "ogolne/teren.htm";
        fileNames[9] = "ogolne/skala boforta.htm";
        fileNames[10] = "ogolne/uprawnienia.htm";
        fileNames[11] = "ogolne/akty_prawne.htm";

        LayoutId = R.layout.l1ogolne;
        setContentView(LayoutId);

        webView = findViewById(R.id.webView1);
        b1 = findViewById(R.id.button1lt);
        b2 = findViewById(R.id.button1rt);
        textView = findViewById(R.id.autoCompleteTextView1);
        spinner = findViewById(R.id.spinner1);
        scroller = findViewById(R.id.view1);

        onCreateTab();
    }
}
