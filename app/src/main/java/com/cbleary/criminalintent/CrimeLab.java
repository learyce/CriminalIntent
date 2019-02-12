package com.cbleary.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.cbleary.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cbleary on 3/23/16.
 */
public class CrimeLab {
    private static final String TAG = CrimeLab.class.getName();
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab getCrimeLab(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();

        //getWritableDatabase() -> Handles the calls to onCreate and onUpgrade as needed.
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public void newCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null); //Return all

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } catch (Error e){
            Log.e(TAG, "getCrimes: " + e.toString());
        }
        finally {
            cursor.close();
        }

        return  crimes;
    }

    public void deleteCrime(Crime crime){
        String uuid = crime.getId().toString();
        ContentValues contentValues= getContentValues(crime);

        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + "= ? ", new String[] {uuid});
    }

    public Crime getCrime(UUID uuid){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + "= ?", new String[]{uuid.toString()});

        try {
            if(cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getCrime();
        } catch (Error e){
            Log.e(TAG, "getCrime: " + e.toString());
        } finally {
            cursor.close();
        }
        return null;
    }

    public int getPosition(UUID crimeId) {
        List<Crime> crimes = getCrimes();
        for (int i = 0; i < crimes.size(); i++) {
            if(crimes.get(i).getId().equals(crimeId))
                return i;
        }
        return 0;
    }

    public void updateCrime(Crime crime){
        String uuid = crime.getId().toString();
        ContentValues contentValues= getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, contentValues,
                CrimeTable.Cols.UUID + "= ? ", new String[] {uuid});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, //Columns - null means select all
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null //orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

    public static ContentValues getContentValues(Crime c){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, c.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, c.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, c.getDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED, c.isSolved() ? 1 : 0);
        contentValues.put(CrimeTable.Cols.SUSPECT, c.getSuspect());
        return contentValues;
    }

    public File getPhotoFile(Crime c){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //Check that file directory exists
        if(externalFilesDir == null){
            return null;
        }
        return new File(externalFilesDir, c.getPhotoFilename());
    }
}
