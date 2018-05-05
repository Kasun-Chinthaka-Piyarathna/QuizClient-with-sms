package hms.quiz.client.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class QuizOpenDbHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final String DATABASE_NAME = "quiz.client";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "QuizOpenDbHelper";

    public QuizOpenDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //Create Tables in the DB
    private static final String DATABASE_LOGIN_TABLE_CREATE = "create table " + "login" + "( "
            + _ID + " integer primary key autoincrement,"
            + "mobile_operator  text,status text); ";
    //to put completeness of the level
    private static final String DATABASE_USER_CHOICE_TABLE_CREATE = "create table " + "user_choice" + "( "
            + _ID + " integer primary key autoincrement,"
            + "user_choice  text,value text); ";
    //to put completeness of the level
    private static final String DATABASE_LANGUAGE_TABLE_CREATE = "create table " + "language" + "( "
            + _ID + " integer primary key autoincrement,"
            + "pattern  text,language text); ";

    //----------------
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_LANGUAGE_TABLE_CREATE);
        Log.d(TAG, DATABASE_LANGUAGE_TABLE_CREATE);
        //-------------------------
        db.execSQL(DATABASE_LOGIN_TABLE_CREATE);
        Log.d(TAG, DATABASE_LOGIN_TABLE_CREATE);
        //------------------------
        db.execSQL(DATABASE_USER_CHOICE_TABLE_CREATE);
        Log.d(TAG, DATABASE_USER_CHOICE_TABLE_CREATE);
        //------------------------

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'login'; ");
        Log.d(TAG, "DROP TABLE IF EXISTS 'login'; ");
        onCreate(db);

    }

}
