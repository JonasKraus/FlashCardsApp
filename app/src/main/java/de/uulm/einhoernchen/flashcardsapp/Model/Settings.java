package de.uulm.einhoernchen.flashcardsapp.Model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Database.DbHelper;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

import static de.uulm.einhoernchen.flashcardsapp.Database.DbHelper.COLUMN_SETTINGS_DATE;
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
    }

    /**
     * Constructor
     *
     */
    public Settings() {

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

    public boolean isAllowSync() {
        return allowSync;
    }

    public void setAllowSync(boolean allowSync) {
        this.allowSync = allowSync;
    }

    public Constants getLearnMode() {
        return learnMode;
    }

    public void setLearnMode(Constants learnMode) {
        this.learnMode = learnMode;
    }

    public boolean isMultiplyChoiceAnswerOrderRandom() {
        return multiplyChoiceAnswerOrderRandom;
    }

    public void setMultiplyChoiceAnswerOrderRandom(boolean multiplyChoiceAnswerOrderRandom) {
        this.multiplyChoiceAnswerOrderRandom = multiplyChoiceAnswerOrderRandom;
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
    }

    public boolean isShowLastDrawer() {
        return showLastDrawer;
    }

    public void setShowLastDrawer(boolean showLastDrawer) {
        this.showLastDrawer = showLastDrawer;
    }

    public int getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(int changeDate) {
        this.changeDate = changeDate;
    }

    private static boolean saveSettings() {

        SQLiteDatabase sqLiteDatabase = Globals.getDb().getSQLiteDatabase();

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

        }

        cursor.close();

        return settings;
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
}
