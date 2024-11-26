package com.example.trackademic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TrackademicDB";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PROFILES = "profiles";

    // Common column names
    private static final String COLUMN_ID = "id";

    // Users Table Columns
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Profiles Table Columns
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_MAJOR = "major";
    private static final String COLUMN_GRADUATION_YEAR = "graduation_year";

    // Create Users Table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT"
            + ")";

    // Create Profiles Table with Foreign Key
    private static final String CREATE_TABLE_PROFILES = "CREATE TABLE " + TABLE_PROFILES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_FULL_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_MAJOR + " TEXT,"
            + COLUMN_GRADUATION_YEAR + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PROFILES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    // User Management Methods
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        // Insert user and return the user ID
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean addProfile(long userId, String fullName, String email, String major, int graduationYear) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_MAJOR, major);
        values.put(COLUMN_GRADUATION_YEAR, graduationYear);

        long result = db.insert(TABLE_PROFILES, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    public long getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        long userId = -1;

        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        }
        cursor.close();

        return userId;
    }

    public Cursor getProfile(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_FULL_NAME, COLUMN_EMAIL, COLUMN_MAJOR, COLUMN_GRADUATION_YEAR};
        String selection = COLUMN_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        return db.query(TABLE_PROFILES, columns, selection, selectionArgs, null, null, null);
    }

    public boolean updateProfile(long userId, String fullName, String email, String major, int graduationYear) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_MAJOR, major);
        values.put(COLUMN_GRADUATION_YEAR, graduationYear);

        String whereClause = COLUMN_USER_ID + "=?";
        String[] whereArgs = {String.valueOf(userId)};

        int result = db.update(TABLE_PROFILES, values, whereClause, whereArgs);
        return result > 0;
    }
}