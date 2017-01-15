package de.uulm.einhoernchen.flashcardsapp.Model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.*;
import android.provider.Settings;
import android.util.Log;

import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_ALLOW_SYNC;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_IS_NIGHT_MODE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_LEARN_MODE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_ORDER_ANSWERS_MULTIPLY_CHOICE_RANDOM;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_SHOW_LAST_DRAWER;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_USER_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_CARD_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_DRAWER;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_END_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_KNOWLEDGE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_START_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_STATISTICS_USER_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.TABLE_SETTINGS;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.TABLE_STATISTICS;

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

    private boolean hasCahnges = false;

    /**
     * Construct with this constructor to generate a new statistic
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @param drawer
     * @param knowledge
     * @param cardId
     * @param userId
     */
    public Statistics(int drawer, float knowledge, long cardId, long userId) {
        this.drawer = drawer;
        this.knowledge = knowledge;
        this.cardId = cardId;
        this.userId = userId;
        this.startDate = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public float getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(float knowledge) {
        this.knowledge = knowledge;
    }

    public int getDrawer() {
        return drawer;
    }

    public void setDrawer(int drawer) {
        this.drawer = drawer;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean isHasCahnges() {
        return hasCahnges;
    }

    public void setHasCahnges(boolean hasCahnges) {
        this.hasCahnges = hasCahnges;
    }


    /**
     * Saves a statistic to the local database
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @return
     */
    public boolean save() {

        if (!this.hasCahnges) {

            Log.d("save " + this.getClass().getName(), "no changes");

            return false;
        }

        SQLiteDatabase database = Globals.getDb().getSQLiteDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATISTICS_USER_ID, this.getUserId());
        values.put(COLUMN_STATISTICS_CARD_ID, this.getCardId());
        values.put(COLUMN_STATISTICS_KNOWLEDGE, this.getKnowledge());
        values.put(COLUMN_STATISTICS_DRAWER, this.getDrawer());
        values.put(COLUMN_STATISTICS_START_DATE, this.getStartDate());
        values.put(COLUMN_STATISTICS_END_DATE, System.currentTimeMillis());

        database.insert(TABLE_STATISTICS, null, values);

        return true;
    }
}
