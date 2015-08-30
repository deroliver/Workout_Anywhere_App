package databasetools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class UserInfoDatabaseTools extends SQLiteOpenHelper {
    private static Integer numFavorites = 0;

    public Integer getNumFavorites() {
        return numFavorites;
    }

    public UserInfoDatabaseTools(Context applicationContext) {
        super(applicationContext, "Users.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        String query = "CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT, userName TEXT, password TEXT, firstTrainerDay TEXT, signedIn TEXT, avatarURL TEXT, paidOrFree TEXT, trainerType TEXT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        String query = "DROP TABLE IF EXISTS users";

        database.execSQL(query);

        onCreate(database);

    }

    public void insertUser(String name, String userName, String password, String firstTrainerDay, String signedIn, String avatarURL, String paidOrFree, String trainerType) {
        numFavorites++;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("userName", userName);
        values.put("password", password);
        values.put("firstTrainerDay", firstTrainerDay);
        values.put("signedIn", signedIn);
        values.put("avatarURL", avatarURL);
        values.put("paidOrFree", paidOrFree);
        values.put("trainerType", trainerType);

        database.insert("users", null, values);

    }

    public int updateUser(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("name"));
        values.put("userName", queryValues.get("userName"));
        values.put("password", queryValues.get("password"));
        values.put("firstTrainerDay", queryValues.get("firstTrainerDay"));
        values.put("signedIn", queryValues.get("signedIn"));
        values.put("avatarURL", queryValues.get("avatarURL"));
        values.put("paidOrFree", queryValues.get("paidOrFree"));
        values.put("trainerType", queryValues.get("trainerType"));

        int i = database.update("users", values, "ID" + " =?", new String[] {queryValues.get("ID")});

        return i;
    }

    public int updateTrainerTypeByUserName(String userName, String trainerType) {

        SQLiteDatabase database = this.getWritableDatabase();

        HashMap<String, String> queryValues = getUserInfoByUserName(userName);

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("name"));
        values.put("userName", queryValues.get("userName"));
        values.put("password", queryValues.get("password"));
        values.put("firstTrainerDay", queryValues.get("firstTrainerDay"));
        values.put("signedIn", queryValues.get("signedIn"));
        values.put("avatarURL", queryValues.get("avatarURL"));
        values.put("paidOrFree", queryValues.get("paidOrFree"));
        values.put("trainerType", trainerType);

        int i  = database.update("users", values, "ID" + " =?", new String[] {queryValues.get("ID")});

        return i;
    }


    public int updateFirstTrainerDayByUserByUserName(String userName, String firstTrainerDay) {

        SQLiteDatabase database = this.getWritableDatabase();

        HashMap<String, String> queryValues = getUserInfoByUserName(userName);

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("name"));
        values.put("userName", queryValues.get("userName"));
        values.put("password", queryValues.get("password"));
        values.put("firstTrainerDay", firstTrainerDay);
        values.put("signedIn", queryValues.get("signedIn"));
        values.put("avatarURL", queryValues.get("avatarURL"));
        values.put("paidOrFree", queryValues.get("paidOrFree"));
        values.put("trainerType", queryValues.get("trainerType"));


        int i  = database.update("users", values, "ID" + " =?", new String[] {queryValues.get("ID")});

        return i;
    }


    public void deleteUser(String ID) {
        numFavorites--;

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM users WHERE ID = '" + ID + "'";

        database.execSQL(deleteQuery);
    }

    public void deleteUserByName(String name) {
        numFavorites--;

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM users WHERE NAME = '" + name + "'";

        database.execSQL(deleteQuery);
    }


    public ArrayList<HashMap<String, String>> getAllUsers() {

        ArrayList<HashMap<String, String>> usersList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM users";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            do {

                HashMap<String, String> userMap = new HashMap<String, String>();

                userMap.put("ID", cursor.getString(0));
                userMap.put("name", cursor.getString(1));
                userMap.put("userName", cursor.getString(2));
                userMap.put("password", cursor.getString(3));
                userMap.put("firstTrainerDay", cursor.getString(4));
                userMap.put("signedIn", cursor.getString(5));
                userMap.put("avatarURL", cursor.getString(6));
                userMap.put("paidOrFree", cursor.getString(7));
                userMap.put("trainerType", cursor.getString(8));

                usersList.add(userMap);

            } while(cursor.moveToNext());
        }
        cursor.close();

        return usersList;
    }

    public HashMap<String, String> getUserInfo(String ID) {

        HashMap<String, String> userMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM users WHERE ID ='" + ID + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            do {

                userMap.put("ID", cursor.getString(0));
                userMap.put("name", cursor.getString(1));
                userMap.put("userName", cursor.getString(2));
                userMap.put("password", cursor.getString(3));
                userMap.put("firstTrainerDay", cursor.getString(4));
                userMap.put("signedIn", cursor.getString(5));
                userMap.put("avatarURL", cursor.getString(6));
                userMap.put("paidOrFree", cursor.getString(7));
                userMap.put("trainerType", cursor.getString(8));

            } while(cursor.moveToNext());
        }
        cursor.close();

        return userMap;
    }

    public HashMap<String, String> getUserInfoByUserName(String userName) {

        HashMap<String, String> userMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM users WHERE userName ='" + userName + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {

            do {
                userMap.put("ID", cursor.getString(0));
                userMap.put("name", cursor.getString(1));
                userMap.put("userName", cursor.getString(2));
                userMap.put("password", cursor.getString(3));
                userMap.put("firstTrainerDay", cursor.getString(4));
                userMap.put("signedIn", cursor.getString(5));
                userMap.put("avatarURL", cursor.getString(6));
                userMap.put("paidOrFree", cursor.getString(7));
                userMap.put("trainerType", cursor.getString(8));

            } while(cursor.moveToNext());
        }
        cursor.close();

        return userMap;
    }
}
