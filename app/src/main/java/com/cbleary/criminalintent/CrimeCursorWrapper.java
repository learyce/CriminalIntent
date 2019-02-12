package com.cbleary.criminalintent;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.cbleary.criminalintent.database.CrimeDbSchema;

import java.util.Date;
import java.util.UUID;

/**
 * Created by cbleary on 4/7/16.
 */
public class CrimeCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String titleString = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setDate(new Date(date));
        crime.setSolved((isSolved != 0));
        crime.setTitle(titleString);
        crime.setSuspect(suspect);
        return  crime;
    }
}
