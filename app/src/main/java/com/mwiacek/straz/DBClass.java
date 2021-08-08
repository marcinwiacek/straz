package com.mwiacek.straz;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBClass {
    private final Context mCtx;
    private DBClassHelper mDbHelper;
    private SQLiteDatabase mDb;

    static final String DB_TAB_HOST = "3"; //nr zakladki
    static final String DB_SIZE = "19";//wielkosc w kazdej zakladce
    static final String DB_NR_WERSJI = "21";//nr wersji aplikacji
    static final String DB_ZAKLADKA1 = "22";//nr pozycji w zakladce
    static final String DB_ZAKLADKA2 = "23";//---"---
    static final String DB_ZAKLADKA3 = "24";//---"---

    //ostatni 24

    DBClass(Context ctx) {
        this.mCtx = ctx;
        this.open();
    }

    DBClass open() throws SQLException {
        mDbHelper = new DBClassHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    void close() {
        mDbHelper.close();
    }

    void SetSetting(String searchfor, String value) {
        mDb.beginTransaction();
        mDb.execSQL("delete from USTAWIENIA where nam='" + searchfor + "'");
        mDb.execSQL("insert into USTAWIENIA (nam, val) values ('" + searchfor + "','" + value + "')");
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    String GetSetting(String searchfor, String def) {
        Cursor c = mDb.rawQuery("select val from ustawienia where nam='" + searchfor + "'", null);
        String s = def;

        if (c == null) {
            return def;
        } else {
            if (c.moveToFirst()) {
                s = c.getString(0);
                c.close();
                return s;
            } else {
                c.close();
                return def;
            }
        }
    }

    private static class DBClassHelper extends SQLiteOpenHelper {
        DBClassHelper(Context context) {
            super(context, "przep", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table USTAWIENIA (NAM text not null, VAL text not null)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS USTAWIENIA");
            onCreate(db);
        }
    }
}
