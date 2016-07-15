package de.uulm.einhoernchen.flashcardsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

import de.uulm.einhoernchen.flashcardsapp.Models.User;

/**
 * Created by Jonas on 02.07.2016.
 */
public class DbManager {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context context;

    /**
     * All Columns of an user
     */
    private String[] allUsersColumns = {
            MySQLiteHelper.COLUMN_USER_USER_ID,     //0
            MySQLiteHelper.COLUMN_USER_NAME,        //1
            MySQLiteHelper.COLUMN_USER_PASSWORD,    //2
            MySQLiteHelper.COLUMN_USER_EMAIL,       //3
            MySQLiteHelper.COLUMN_USER_RATING,      //4
            MySQLiteHelper.COLUMN_USER_GROUP_ID,    //5
            MySQLiteHelper.COLUMN_USER_CREATED,     //6
    };


    /**
     * Constructor
     *
     * @param context
     */
    public DbManager(Context context) {

        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }


    /**
     * Opens a database connection
     *
     * @throws SQLException
     */
    public void open() throws SQLException {

        this.database = dbHelper.getWritableDatabase();
    }


    /**
     * Closes a database connection
     *
     */
    public void close() {

        dbHelper.close();
        Log.d("import db", "closed");
    }


    /**
     * Creates a user with all given values
     * @param user
     */
    public void createUser(User user) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_USER_ID, user.getUserId());
        values.put(MySQLiteHelper.COLUMN_USER_NAME, user.getName());
        values.put(MySQLiteHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(MySQLiteHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put(MySQLiteHelper.COLUMN_USER_RATING, user.getRating());
        values.put(MySQLiteHelper.COLUMN_USER_GROUP_ID, user.getGroup_id());
        values.put(MySQLiteHelper.COLUMN_USER_CREATED, user.getCreated());

        // Executes the query
        database.insert(MySQLiteHelper.TABLE_USER, null, values);
    }


    /**
     * Gets the saved User
     *
     * @return User | null
     */
    public User getUser() {

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER,allUsersColumns, null, null, null, null, null);
        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getLong(5),
                    cursor.getString(6)
            );
        }
        cursor.close();

        return user;
    }


    /**
     * Checks if a user is already existant
     *
     * @return boolean
     */
    public boolean checkIfUserExists() {

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER,allUsersColumns, null, null, null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

}
