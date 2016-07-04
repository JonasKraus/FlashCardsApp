package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jonas on 02.07.2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Strings for table user
    public static final String TABLE_USER = "questions";
    public static final String COLUMN_USER_USER_ID = "userId";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_RATING = "rating";
    public static final String COLUMN_USER_GROUP_ID = "group_id";
    public static final String COLUMN_USER_CREATED= "created";

    // Databasename and version - increase when existing table is altered
    private static final String DATABASE_NAME = "flashcardsDb.db";
    private static final int DATABASE_VERSION = 1;


    /**
     * Database creation sql statement for table user
     *
     */
    private static final String USER_CREATE = "create table "
            + TABLE_USER + "("
            + COLUMN_USER_USER_ID
            + " integer primary key autoincrement, "
            + COLUMN_USER_NAME
            + " text not null, "
            + COLUMN_USER_PASSWORD
            + " text not null, "
            + COLUMN_USER_EMAIL
            + " text not null, "
            + COLUMN_USER_RATING
            + " integer DEFAULT 0, "
            + COLUMN_USER_GROUP_ID
            + " integer, "
            + COLUMN_USER_CREATED
            + " text not null "
            + ");";


    /**
     * Costructor
     *
     * @param context
     */
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        onCreate(db);
    }
}

