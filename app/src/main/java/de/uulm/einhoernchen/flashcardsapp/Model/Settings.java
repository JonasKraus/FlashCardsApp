package de.uulm.einhoernchen.flashcardsapp.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Database.DbHelper;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_ALLOW_SYNC;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_DATE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_IS_NIGHT_MODE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_LEARN_MODE;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_ORDER_ANSWERS_MULTIPLY_CHOICE_RANDOM;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_SHOW_LAST_DRAWER;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_USER_ID;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.TABLE_SETTINGS;
import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.allSettingsColumns;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.01.15
 */

public class Settings {

    private long id;
    private long userId;
    private boolean allowSync;
    private Constants learnMode;
    private boolean multiplyChoiceAnswerOrderRandom;
    private boolean isNightMode;
    private boolean showLastDrawer;
    private int changeDate;

    private boolean hasCahnges = false;


    /**
     * Constructs a settings object with all params
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @param id
     * @param userId
     * @param allowSync
     * @param learnMode
     * @param multiplyChoiceAnswerOrderRandom
     * @param isNightMode
     * @param showLastDrawer
     * @param changeDate
     */
    public Settings(long id, long userId, boolean allowSync, Constants learnMode, boolean multiplyChoiceAnswerOrderRandom, boolean isNightMode, boolean showLastDrawer, int changeDate) {

        this.id = id;
        this.userId = userId;
        this.allowSync = allowSync;
        this.learnMode = learnMode;
        this.multiplyChoiceAnswerOrderRandom = multiplyChoiceAnswerOrderRandom;
        this.isNightMode = isNightMode;
        this.showLastDrawer = showLastDrawer;
        this.changeDate = changeDate;

        this.hasCahnges = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {

        this.id = id;
        this.hasCahnges = true;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {

        this.hasCahnges = true;
        this.userId = userId;
    }

    public boolean isAllowSync() {
        return allowSync;
    }

    public void setAllowSync(boolean allowSync) {

        this.hasCahnges = true;
        this.allowSync = allowSync;
    }

    public Constants getLearnMode() {
        return learnMode;
    }

    public void setLearnMode(Constants learnMode) {

        this.hasCahnges = true;
        this.learnMode = learnMode;
    }

    public boolean isMultiplyChoiceAnswerOrderRandom() {

        return multiplyChoiceAnswerOrderRandom;
    }

    public void setMultiplyChoiceAnswerOrderRandom(boolean multiplyChoiceAnswerOrderRandom) {

        this.hasCahnges = true;
        this.multiplyChoiceAnswerOrderRandom = multiplyChoiceAnswerOrderRandom;
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    public void setNightMode(boolean nightMode) {

        this.hasCahnges = true;
        isNightMode = nightMode;
    }

    public boolean isHideLastDrawer() {

        return showLastDrawer;
    }

    public void setHideLastDrawer(boolean showLastDrawer) {

        this.hasCahnges = true;
        this.showLastDrawer = showLastDrawer;
    }

    public int getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(int changeDate) {

        this.hasCahnges = true;
        this.changeDate = changeDate;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "id=" + id +
                ", userId=" + userId +
                ", allowSync=" + allowSync +
                ", learnMode=" + learnMode +
                ", multiplyChoiceAnswerOrderRandom=" + multiplyChoiceAnswerOrderRandom +
                ", isNightMode=" + isNightMode +
                ", showLastDrawer=" + showLastDrawer +
                ", changeDate=" + changeDate +
                '}';
    }

    /************************/
    /*** D A T A B A S E ****/
    /************************/

    /**
     * Saves the settings object as a new row
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
        values.put(COLUMN_SETTINGS_USER_ID, this.getUserId());
        values.put(COLUMN_SETTINGS_ALLOW_SYNC, this.isAllowSync());
        values.put(COLUMN_SETTINGS_LEARN_MODE, String.valueOf(this.getLearnMode()));
        values.put(COLUMN_SETTINGS_ORDER_ANSWERS_MULTIPLY_CHOICE_RANDOM, this.isMultiplyChoiceAnswerOrderRandom());
        values.put(COLUMN_SETTINGS_IS_NIGHT_MODE, this.isNightMode());
        values.put(COLUMN_SETTINGS_SHOW_LAST_DRAWER, this.isHideLastDrawer());
        values.put(COLUMN_SETTINGS_DATE, System.currentTimeMillis());

        database.insert(TABLE_SETTINGS, null, values);

        return true;
    }


    /**
     * Get the latest settings of the currently logged in user
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @return
     */
    public static Settings getSettings() {

        SQLiteDatabase database = Globals.getDb().getSQLiteDatabase();

        long id;
        long userId;
        boolean allowSync;
        String learnModeString;
        Constants learnMode;
        boolean multiplyChoiceAnswerOrderRandom;
        boolean isNightMode;
        boolean showLastDrawer;
        int changeDate;

        Settings settings = null;

        Cursor cursor = database.query(
                TABLE_SETTINGS,
                allSettingsColumns,
                COLUMN_SETTINGS_USER_ID + " = " + Globals.getDb().getLoggedInUser().getId()
                , null, null, null,
                COLUMN_SETTINGS_DATE + " DESC ",
                "1");

        if (cursor.moveToFirst()) {

            id = cursor.getLong(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_ID)
            );
            userId = cursor.getLong(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_USER_ID)
            );
            allowSync = cursor.getInt(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_ALLOW_SYNC)
            ) > 0;
            learnModeString = cursor.getString(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_LEARN_MODE)
            );
            learnMode = Constants.equalsEnum(learnModeString);
            multiplyChoiceAnswerOrderRandom = cursor.getInt(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_ORDER_ANSWERS_MULTIPLY_CHOICE_RANDOM)
            ) > 0;
            isNightMode = cursor.getInt(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_IS_NIGHT_MODE)
            ) > 0;
            showLastDrawer = cursor.getInt(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_SHOW_LAST_DRAWER)
            ) > 0;
            changeDate = cursor.getInt(
                    cursor.getColumnIndex(DbHelper.COLUMN_SETTINGS_DATE)
            );

            settings = new Settings(id, userId, allowSync, learnMode, multiplyChoiceAnswerOrderRandom, isNightMode, showLastDrawer, changeDate);

        } else {

            settings = new Settings(0, Globals.getDb().getLoggedInUser().getId(), true, Constants.SETTINGS_LEARN_MODE_DRAWER, false, false, true, 0);

        }

        cursor.close();

        return settings;
    }


}
