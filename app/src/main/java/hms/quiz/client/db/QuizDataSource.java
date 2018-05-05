package hms.quiz.client.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuizDataSource {
    private static final String TAG = "QuizDataSource";
    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mDbOpenHelper;
    private Context context;
    private String mobileOperatorKey = "mobile_operator";
    private String statusKey = "status";
    private String loginTable = "login";
    private String userChoiceKey = "user_choice";
    private String valueKey = "value";
    private String patternKey = "pattern";
    private String languageKey = "language";
    private String languageTable = "language";
    private String userChoiceTable = "user_choice";

    public QuizDataSource(Context context) {
        this.context = context;
        mDbOpenHelper = new QuizOpenDbHelper(context);
    }

    public void open() {
        mDatabase = mDbOpenHelper.getWritableDatabase();
        Log.d(TAG, "Database opened.");
    }

    public void close() {
        mDbOpenHelper.close();
        Log.d(TAG, "Database closed.");
    }

    //--------------------------------------
    public void insertEntryToLoginTable(String mobileOperator, String status) {
        ContentValues newValues = new ContentValues();
        newValues.put(mobileOperatorKey, mobileOperator);
        newValues.put(statusKey, status);
        mDatabase.insertOrThrow(loginTable, null, newValues);
    }

    //--------------------------start
    public void insertEntryToUserChoiceTable(String user_choice, String value) {
        ContentValues newValues = new ContentValues();
        newValues.put(userChoiceKey, user_choice);
        newValues.put(valueKey, value);
        mDatabase.insertOrThrow(userChoiceTable, null, newValues);
    }

    //----------------------
    public void insertEntryToLanguageTable(String pattern, String language) {
        ContentValues newValues = new ContentValues();
        newValues.put(patternKey, pattern);
        newValues.put(languageKey, language);
        mDatabase.insertOrThrow(languageTable, null, newValues);
    }

    //----------------------
    public int deleteEntryFromLoginTable(String UserName) {
        String where = "mobile_operator=?";
        return mDatabase.delete(loginTable, where,
                new String[]{UserName});
    }

    //-------------------------------------------
    public String getSingleEntryFromLoginTable(String mobileOperator) {
        Cursor cursor = mDatabase.query(loginTable, null, " mobile_operator=?",
                new String[]{mobileOperator}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String status = cursor.getString(cursor.getColumnIndex(statusKey));
        cursor.close();
        return status;
    }

    //-----------------------------------------start
    public String getSingleEntryFromUserChoiceTable(String value) {
        Cursor cursor = mDatabase.query(userChoiceTable, null, " value=?",
                new String[]{value}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "Not EXIST";
        }
        cursor.moveToFirst();
        String user_choice = cursor.getString(cursor.getColumnIndex(userChoiceTable));
        cursor.close();
        return user_choice;
    }

    //-----------------------------------
    public String getSingleEntryFroLanguageTable(String pattern) {
        Cursor cursor = mDatabase.query(languageTable, null, " pattern=?",
                new String[]{pattern}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String status = cursor.getString(cursor.getColumnIndex(languageKey));
        cursor.close();
        return status;
    }

    //-----------------------------------
    public int deleteEntryFromLanguageTable(String pattern) {
        String where = "pattern=?";
        return mDatabase.delete(languageTable, where,
                new String[]{pattern});
    }

    //-----------------------------------
    public void updateEntryFromLoginTable(String mobileOperator, String status) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(mobileOperatorKey, mobileOperator);
        updatedValues.put(statusKey, status);
        String where = "mobile_operator = ?";
        mDatabase.update(loginTable, updatedValues, where, new String[]{mobileOperator});
    }
}
