package de.uulm.einhoernchen.flashcardsapp.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Database.DbHelper;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_CARD_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_DRAWER;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_END_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_KNOWLEDGE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_START_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_USER_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.TABLE_STATISTICS;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.allStatisticsColumns;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.01.15
 */

public class Statistics {

    private long id;
    private long userId;
    private long cardId;
    private float knowledge;
    private int drawer;
    private long startDate;
    private long endDate;

    /**
     * Construct with this constructor to generate a new statistic for one user
     * this object will be used over and over
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     */
    public Statistics() {

        this.userId = Globals.getDb().getLoggedInUser().getId();
    }


    public long getUserId() {
        return userId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public long getCardId() {
        return this.cardId;
    }


    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }


    public long getStartDate() {
        return this.startDate;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "userId=" + userId +
                ", cardId=" + cardId +
                ", knowledge=" + knowledge +
                ", drawer=" + drawer +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    /**
     * Saves a statistic to the local database
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @return
     */
    public boolean save(float knowledge) {

        int drawer = getLatestDrawer(this.getCardId(), userId);

        if (knowledge != 100 && drawer > 0) {

            drawer = 0;
        } else if (knowledge == 100 && drawer < Constants.COUNT_DRAWER) {

            drawer += 1;
        }

        SQLiteDatabase database = Globals.getDb().getSQLiteDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATISTICS_USER_ID, this.getUserId());
        values.put(COLUMN_STATISTICS_CARD_ID, this.getCardId());
        values.put(COLUMN_STATISTICS_KNOWLEDGE, knowledge);
        values.put(COLUMN_STATISTICS_DRAWER, drawer);
        values.put(COLUMN_STATISTICS_START_DATE, this.getStartDate());
        values.put(COLUMN_STATISTICS_END_DATE, System.currentTimeMillis());

        database.insert(TABLE_STATISTICS, null, values);

        return true;
    }


    /**
     * Gets the drawer where the card was before
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @param cardId
     * @param userId
     * @return
     */
    private static int getLatestDrawer(long cardId, long userId) {

        SQLiteDatabase database = Globals.getDb().getSQLiteDatabase();

        int drawer = 0;


        Cursor cursor = database.query(
                TABLE_STATISTICS,
                allStatisticsColumns,
                COLUMN_STATISTICS_CARD_ID + " = " + cardId
                        + " AND " + COLUMN_STATISTICS_USER_ID + " = " + userId
                , null, null, null,
                COLUMN_STATISTICS_END_DATE + " DESC ",
                "1");

        if (cursor.moveToFirst()) {

            drawer = cursor.getInt(
                    cursor.getColumnIndex(DbHelper.COLUMN_STATISTICS_DRAWER)
            );

        }

        cursor.close();

        return drawer;
    }



}
