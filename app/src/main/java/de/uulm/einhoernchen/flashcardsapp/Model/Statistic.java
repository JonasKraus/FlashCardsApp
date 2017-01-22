package de.uulm.einhoernchen.flashcardsapp.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Database.DbHelper;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SELECTION_CARD_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SELECTION_USER_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_CARD_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_DRAWER;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_END_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_KNOWLEDGE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_START_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_USER_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.TABLE_FLASHCARD;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.TABLE_STATISTICS;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.allStatisticsColumns;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.01.15
 */

public class Statistic {

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
    public Statistic() {

        this.userId = Globals.getDb().getLoggedInUser().getId();
    }


    /**
     * Use this constructor to instantiate a statistic from the db
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-22
     *
     * @param userId
     * @param cardId
     * @param knowledge
     * @param drawer
     * @param startDate
     * @param endDate
     */
    private Statistic(long userId, long cardId, float knowledge, int drawer, long startDate, long endDate) {
        this.userId = userId;
        this.cardId = cardId;
        this.knowledge = knowledge;
        this.drawer = drawer;
        this.startDate = startDate;
        this.endDate = endDate;
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
        return "Statistic{" +
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


    /**
     * Gets the list of all currently selected cards' stats
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-22
     *
     * @return
     */
    public static List<Statistic> getStatistics() {

        Settings settings = Settings.getSettings();
        Constants learnMode = settings.getLearnMode();

        String orderBy = null;

        switch (learnMode) {

            case SETTINGS_LEARN_MODE_KNOWLEDGE:

                orderBy = " ORDER BY " + TABLE_STATISTICS  + "." +
                        COLUMN_STATISTICS_KNOWLEDGE + " ASC,  max(statistics.endDate) ASC";
                // Order by knowledge ASC
                break;

            case SETTINGS_LEARN_MODE_DATE:

                // ORDER by dateEnd ASC
                orderBy = " ORDER BY " + TABLE_STATISTICS  + "." +
                        COLUMN_STATISTICS_END_DATE + " ASC";
                break;

            case SETTINGS_LEARN_MODE_RANDOM:

                // ORDER Random ??
                break;

            case SETTINGS_LEARN_MODE_DRAWER:

                // Order by drawer ASC

                orderBy = " ORDER BY " + TABLE_STATISTICS + "." +
                        COLUMN_STATISTICS_DRAWER + " ASC,  max(statistics.endDate) ASC";
                break;

        }

        List<Statistic> stats = new ArrayList<Statistic>();

        SQLiteDatabase database = Globals.getDb().getSQLiteDatabase();

        Cursor cursor = database.rawQuery("SELECT selection.userId, selection.cardId, knowledge, drawer, startDate, endDate " +
                "FROM selection\n" +
                "    LEFT JOIN statistics ON selection.cardId = statistics.cardId\n" +
                "    JOIN user ON user.isLoggedIn = 1\n" +
                "    WHERE selection.cardId NOT NULL\n" +
                "     GROUP BY (selection.cardId)\n" +
                "    " + orderBy, null);


        if (cursor.moveToFirst()) {
            do {

                long cardId = cursor.getLong(cursor.getColumnIndex(COLUMN_SELECTION_CARD_ID));
                long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_SELECTION_USER_ID));
                float knowledge = cursor.getFloat(cursor.getColumnIndex(COLUMN_STATISTICS_KNOWLEDGE));
                int drawer = cursor.getInt(cursor.getColumnIndex(COLUMN_STATISTICS_DRAWER));
                long startDate = cursor.getLong(cursor.getColumnIndex(COLUMN_STATISTICS_START_DATE));
                long endDate = cursor.getLong(cursor.getColumnIndex(COLUMN_STATISTICS_END_DATE));

                Statistic statistic = new Statistic(userId, cardId, knowledge, drawer, startDate, endDate);

                Log.d("stat", statistic.toString());
                stats.add(statistic);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return stats;
    }


    /**
     * Gets the duration of all selected cards
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-22
     *
     * @return
     */
    public static long getDurationOfSelection() {

        SQLiteDatabase database = Globals.getDb().getSQLiteDatabase();

        long duration = 0;

        Cursor cursor = database.rawQuery(
                "SELECT sum(endDate-startDate) as duration FROM selection " +
                        "    LEFT JOIN statistics ON selection.cardId = statistics.cardId " +
                        "    JOIN user ON user.isLoggedIn = 1"
                , null);

        if (cursor.moveToFirst()) {

            duration = cursor.getLong(0);

        }
        cursor.close();

        return duration;
    }
}
