package edu.its.oduattendance.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.its.oduattendance.MainActivity;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "CHECK_in";

    // Table columns
    public static final String _ID = "_id";
    public static final String midas_id = "midas_id";
    public static final String response_code = "response_code";
    public static final String message = "message";
    public static final String c_date = "checkindate";
    public static final String c_time = "checkintime";

    // Database Information
    static final String DB_NAME = "QR_Check1.db";


    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + midas_id + " TEXT NOT NULL, " + response_code + " TEXT NOT NULL, " + message + " TEXT NOT NULL, " + c_date +" TEXT, " + c_time + " TEXT);";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<String> getAllLabels(String midas){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT checkindate  FROM " + DataBaseHelper.TABLE_NAME + " WHERE midas_id='" + midas+"'";
  //      String selectQuery = "SELECT DISTINCT checkindate  FROM " + DataBaseHelper.TABLE_NAME + " WHERE midas_id=" + "'achan007'";

        //String[] columns = new String[] { DataBaseHelper._ID,DataBaseHelper.midas_id, DataBaseHelper.response_code, DataBaseHelper.message, DataBaseHelper.c_date ,DataBaseHelper.c_time};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }
}