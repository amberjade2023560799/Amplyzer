package com.example.amplyzer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Amplyze.db";
    public static final String TABLE_NAME = "bills";

    public static final String COL_ID = "ID";
    public static final String COL_MONTH = "MONTH";
    public static final String COL_UNIT = "UNIT";
    public static final String COL_TOTAL = "TOTAL";
    public static final String COL_REBATE = "REBATE";
    public static final String COL_FINAL = "FINAL";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_MONTH + " TEXT, " +
                        COL_UNIT + " INTEGER, " +
                        COL_TOTAL + " REAL, " +
                        COL_REBATE + " INTEGER, " +
                        COL_FINAL + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String month, int unit, double total, int rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC", null);
    }

    public Cursor getDataById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = ?", new String[]{id});
    }

    public boolean updateData(String id, String month, int unit, double total, int rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);

        int result = db.update(TABLE_NAME, values, "ID = ?", new String[]{id});
        return result > 0;
    }

    public int deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }
}