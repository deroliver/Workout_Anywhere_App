package databasetools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.practice.derikpc.workoutanywhere.HomeScreenButtonsFragment;

import java.util.HashMap;

import user.User;

/**
 * Created by DerikPC on 8/31/2015.
*/
public class StreamDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "streamDatabase" + User.getUserID() + ".db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private final String TABLE_LIKES = "likes";
    private final String TABLE_COMMENTS = "comments";

    // Likes Table Columns
    private final String KEY_LIKE_ID = "id";
    private final String KEY_LIKE_POST_ID = "postID";

    // Comment Table Columns
    private final String KEY_COMMENT_ID = "id";
    private final String KEY_COMMENT_POST_ID = "postID";


    public StreamDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println(DATABASE_NAME);
    }


    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LIKES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LIKES +
                "(" +
                    KEY_LIKE_ID + " INTEGER PRIMARY KEY," +
                    KEY_LIKE_POST_ID + " TEXT" +
                ")";


        String CREATE_COMMENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COMMENTS +
                "(" +
                    KEY_COMMENT_ID + " INTEGER PRIMARY KEY," +
                    KEY_COMMENT_POST_ID + " TEXT" +
                ")";

        db.execSQL(CREATE_LIKES_TABLE);
        db.execSQL(CREATE_COMMENTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
            onCreate(db);
        }
    }

    public void deleteStreamDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_LIKES, null, null);
            db.delete(TABLE_COMMENTS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }


    public void addLike(String id) {
        SQLiteDatabase db = getWritableDatabase();
        System.out.println(id);

        db.beginTransaction();

        try {

            ContentValues values = new ContentValues();
            values.put(KEY_LIKE_POST_ID, id);

            db.insertOrThrow(TABLE_LIKES, null, values);
            db.setTransactionSuccessful();
        } catch(Exception e) {
            System.out.println("Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public void addComment(String id) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            ContentValues values = new ContentValues();
            values.put(KEY_LIKE_POST_ID, id);

            db.insertOrThrow(TABLE_COMMENTS, null, values);
            db.setTransactionSuccessful();
        } catch(Exception e) {
            System.out.println("Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteLike(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LIKES, "postID=?", new String[]{id});

    }

    public void deleteComment(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_COMMENTS, "postID=?", new String[] {id});

    }

    public boolean checkIfLikeExists(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println(id);

        String query = "SELECT * FROM " + TABLE_LIKES + " WHERE postID = " + id;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() <= 0) {
            cursor.close();
            return  false;
        }

        cursor.close();
        return true;
    }

    public void printAllLikes() {

        String query = "SELECT * FROM " + TABLE_LIKES;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {

            do {

                System.out.println("Like ID: " + c.getString(0));
                System.out.println("Like Post ID: " + c.getString(1));

            } while(c.moveToNext());
        }
        c.close();
        db.close();

    }
}

