package edu.its.oduattendance.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private DataBaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DataBaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String midas_id,String response_code, String message, String date,String time) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DataBaseHelper.midas_id,midas_id);
        contentValue.put(DataBaseHelper.response_code, response_code);
        contentValue.put(DataBaseHelper.message,message);
        contentValue.put(DataBaseHelper.c_date, date);
        contentValue.put(DataBaseHelper.c_time, time);
        database.insert(DataBaseHelper.TABLE_NAME, null, contentValue);
       dbHelper.close();

    }

    public Cursor fetch(String midas) {
        String[] columns = new String[] { DataBaseHelper._ID,DataBaseHelper.midas_id, DataBaseHelper.response_code, DataBaseHelper.message, DataBaseHelper.c_date ,DataBaseHelper.c_time};
        //Cursor cursor = database.query(DataBaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        Cursor cursor = database.rawQuery("SELECT  * FROM "+DataBaseHelper.TABLE_NAME+" WHERE midas_id='"+midas+"'",null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public List<String> getAllLabels(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT *  FROM " + DataBaseHelper.TABLE_NAME ;   //+" WHERE"+DataBaseHelper.midas_id+ "="+midas;

        //String[] columns = new String[] { DataBaseHelper._ID,DataBaseHelper.midas_id, DataBaseHelper.response_code, DataBaseHelper.message, DataBaseHelper.c_date ,DataBaseHelper.c_time};
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        database.close();

        // returning lables
        return labels;
    }

}
