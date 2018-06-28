package com.example.welcome.journalapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JournalDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME="JournalApp.db";
    private static final int DATABASE_VERSION=1;

    public JournalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CFI_TABLE="CREATE TABLE "+ JournalContract.JournalEntry.TABLE_NAME+"("
                + JournalContract.JournalEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + JournalContract.JournalEntry.CREATED_TIME+" TEXT NOT NULL, "
                + JournalContract.JournalEntry.CREATED_DATE_WITHOUT_TIME+" TEXT NOT NULL, "
                + JournalContract.JournalEntry.CATEGORY+" TEXT NOT NULL, "
                + JournalContract.JournalEntry.NOTES_DESCRIPTION+" TEXT NOT NULL, "
                + JournalContract.JournalEntry.UNIQUE_ID+" TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_CFI_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE="DROP IF EXIST "+ JournalContract.JournalEntry.TABLE_NAME+";";
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
