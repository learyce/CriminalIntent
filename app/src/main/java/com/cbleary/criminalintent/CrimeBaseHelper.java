package com.cbleary.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cbleary.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by cbleary on 4/7/16.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //_id creates an unique row id
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                        CrimeTable.Cols.UUID + ", " +
                        CrimeTable.Cols.TITLE + ", " +
                        CrimeTable.Cols.DATE + ", " +
                        CrimeTable.Cols.SOLVED  + ", " +
                        CrimeTable.Cols.SUSPECT +
                        ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
