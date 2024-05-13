package com.ar.docscanner.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class sql extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserManager.db";
    private static final String TABLE_USER = "user";

    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_ID = "user_id";

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID +" int" + ","
            + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public sql(@Nullable Context context) {
        super(context, DATABASE_NAME,null ,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL("Insert into "+TABLE_USER+"("+COLUMN_USER_ID+","+COLUMN_USER_PASSWORD+")"+"VALUES"+"(1,'12345678910')");
        onCreate(db);
    }

    public boolean updateUser(String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Update "+TABLE_USER+" set "+COLUMN_USER_PASSWORD+"= "+pass+" WHERE "+COLUMN_USER_ID+"=1");
        db.close();
        return true;
    }

    public boolean checkUser(String email) {
        String[] columns = {
                COLUMN_USER_PASSWORD
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_PASSWORD + " = ? ";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }
}
