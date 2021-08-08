package com.mwiacek.straz;

import android.os.Bundle;

public class ChemiaActivity extends OneTabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyActivity = this;

        mSpinnerListaId = R.array.chemialista;
        Db_Number_Of_Selection = DBClass.DB_ZAKLADKA2;

        fileNames = new String[10];
        fileNames[0] = "chemia/jednostki.htm";
        fileNames[1] = "chemia/adr.htm";
        fileNames[2] = "chemia/adr2.htm";
        fileNames[3] = "chemia/adr3.htm";
        fileNames[4] = "chemia/diament.htm";
        fileNames[5] = "chemia/hazem.htm";
        fileNames[6] = "chemia/butle.htm";
        fileNames[7] = "chemia/sorbenty.htm";
        fileNames[8] = "chemia/strefy.htm";
        fileNames[9] = "chemia/klima.htm";

        LayoutId = R.layout.l2chemia;
        setContentView(LayoutId);

        webView = findViewById(R.id.webView2);
        b1 = findViewById(R.id.button2lt);
        b2 = findViewById(R.id.button2rt);
        textView = findViewById(R.id.autoCompleteTextView2);
        spinner = findViewById(R.id.spinner2);
        scroller = findViewById(R.id.view2);

        onCreateTab();
    }
}
