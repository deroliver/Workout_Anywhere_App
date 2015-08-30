package databasetools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoritesDatabaseTools extends SQLiteOpenHelper {
    private static Integer numFavorites = 0;

    public Integer getNumFavorites() {
        return numFavorites;
    }

    public FavoritesDatabaseTools(Context applicationContext) {
        super(applicationContext, "Favorites.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        String query = "CREATE TABLE favorites (id INTEGER PRIMARY KEY, url TEXT, type TEXT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        String query = "DROP TABLE IF EXISTS favorites";

        database.execSQL(query);

        onCreate(database);

    }

    public void insertFavorite(String url, String type) {
        numFavorites++;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("url", url);
        values.put("type", type);

        database.insert("favorites", null, values);

        database.close();

    }

    public int updateFavorite(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("url", queryValues.get("url"));
        values.put("type", queryValues.get("type"));

        return database.update("favorites", values, "ID" + " =?", new String[] {queryValues.get("ID")});
    }

    public void deleteFavorite(String ID) {
        numFavorites--;

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM favorites WHERE ID = '" + ID + "'";

        database.execSQL(deleteQuery);

        database.close();
    }

    public void deleteFavoriteByURL(String url) {
        numFavorites--;

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM favorites WHERE URL = '" + url + "'";

        database.execSQL(deleteQuery);

        database.close();
    }


    public ArrayList<HashMap<String, String>> getAllFavorites() {

        ArrayList<HashMap<String, String>> favoritesArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM favorites";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            do {

                HashMap<String, String> favoriteMap = new HashMap<String, String>();

                favoriteMap.put("ID", cursor.getString(0));
                favoriteMap.put("url", cursor.getString(1));
                favoriteMap.put("type", cursor.getString(2));

                favoritesArrayList.add(favoriteMap);

            } while(cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return favoritesArrayList;
    }

    public HashMap<String, String> getFavoriteInfo(String ID) {

        HashMap<String, String> favoritetMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM favorites WHERE ID ='" + ID + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            do {

                favoritetMap.put("ID", cursor.getString(0));
                favoritetMap.put("url", cursor.getString(1));
                favoritetMap.put("type", cursor.getString(2));

            } while(cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return favoritetMap;

    }
}
