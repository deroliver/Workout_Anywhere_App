package databasetools;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class CompletedDatabaseTools extends SQLiteOpenHelper {
    private static Integer numCompleted = 0;

    public Integer getNumCompleted() {
        return numCompleted;
    }

    public CompletedDatabaseTools(Context applicationContext) {
        super(applicationContext, "Completed.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        String query = "CREATE TABLE completed (id INTEGER PRIMARY KEY, url TEXT, type TEXT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        String query = "DROP TABLE IF EXISTS completed";

        database.execSQL(query);

        onCreate(database);

    }

    public void insertCompletion(String url, String type) {
        numCompleted++;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("url", url);
        values.put("type", type);

        database.insert("completed", null, values);

        database.close();

    }

    public int updateCompletion(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("url", queryValues.get("url"));
        values.put("type", queryValues.get("type"));

        return database.update("completed", values, "ID" + " =?", new String[] {queryValues.get("ID")});

    }

    public void deleteCompletion(String ID) {
        numCompleted--;

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM completed WHERE ID = '" + ID + "'";

        database.execSQL(deleteQuery);

        database.close();

    }

    public void deleteCompletionByURL(String url) {
        numCompleted--;

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM completed WHERE URL = '" + url + "'";

        database.execSQL(deleteQuery);

        database.close();
    }


    public ArrayList<HashMap<String, String>> getAllCompleted() {

        ArrayList<HashMap<String, String>> completedArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM completed";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            do {

                HashMap<String, String> completedMap = new HashMap<String, String>();

                completedMap.put("ID", cursor.getString(0));
                completedMap.put("url", cursor.getString(1));
                completedMap.put("type", cursor.getString(2));

                completedArrayList.add(completedMap);

            } while(cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return completedArrayList;
    }

    public HashMap<String, String> getCompletedInfo(String ID) {

        HashMap<String, String> completedMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM completed WHERE ID ='" + ID + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            do {

                completedMap.put("ID", cursor.getString(0));
                completedMap.put("url", cursor.getString(1));
                completedMap.put("type", cursor.getString(2));

            } while(cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return completedMap;

    }
}
