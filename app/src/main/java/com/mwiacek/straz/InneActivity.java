package com.mwiacek.straz;

import android.os.Bundle;

public class InneActivity extends OneTabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyActivity = this;

        mSpinnerListaId = R.array.innelista;
        Db_Number_Of_Selection = DBClass.DB_ZAKLADKA3;

        fileNames = new String[11];
        fileNames[0] = "inne/lpr.htm";
        fileNames[1] = "inne/lacznosc.htm";
        fileNames[2] = "inne/dymy.htm";
        fileNames[3] = "inne/grupy.htm";
        fileNames[4] = "inne/rozne.htm";
        fileNames[5] = "inne/psp.htm";
        fileNames[6] = "inne/osp.htm";
        fileNames[7] = "inne/nr.htm";
        fileNames[8] = "inne/linki.htm";
        fileNames[9] = "inne/tryskacze.htm";
        fileNames[10] = "inne/rm.htm";

        LayoutId = R.layout.l3inne;
        setContentView(LayoutId);

        webView = findViewById(R.id.webView3);
        b1 = findViewById(R.id.button3lt);
        b2 = findViewById(R.id.button3rt);
        textView = findViewById(R.id.autoCompleteTextView3);
        spinner = findViewById(R.id.spinner3);
        scroller = findViewById(R.id.view3);

        onCreateTab();
    }
}
