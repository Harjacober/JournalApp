package com.example.welcome.journalapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class JournalProvider extends ContentProvider{

    JournalDbHelper journalDbHelper;
    private static final int NOTES =100;
    private static final int NOTES_WITH_ID =101;
    private static final UriMatcher surimatcher=buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(JournalContract.CONTENT_AUTHORITY, JournalContract.JournalEntry.TABLE_NAME, NOTES);
        matcher.addURI(JournalContract.CONTENT_AUTHORITY, JournalContract.JournalEntry.TABLE_NAME+ "/#", NOTES_WITH_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        journalDbHelper =new JournalDbHelper(getContext());

        JournalContract.BASE_CONTENT_URI.buildUpon().appendPath(JournalContract.JournalEntry.TABLE_NAME).build();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortorder) {
        SQLiteDatabase sqLiteDatabase = journalDbHelper.getReadableDatabase();
        int match = surimatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case NOTES:
                cursor= sqLiteDatabase.query(JournalContract.JournalEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortorder);
                break;
            case NOTES_WITH_ID:
                selection= JournalContract.JournalEntry._ID+"=?";
                selectionArgs=new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor= sqLiteDatabase.query(JournalContract.JournalEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortorder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase= journalDbHelper.getWritableDatabase();
        Uri returnUri;
        int match=surimatcher.match(uri);
        switch (match){
            case NOTES: {
                long id = sqLiteDatabase.insert(JournalContract.JournalEntry.TABLE_NAME,

                        null,
                        contentValues);
                if (id > 0) {
                    returnUri = JournalContract.JournalEntry.buildTaskUri(id);
                } else {
                    throw new SQLException("failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase= journalDbHelper.getWritableDatabase();
        int numdeleted;
        int match=surimatcher.match(uri);
        switch (match){
            case NOTES:
                numdeleted=sqLiteDatabase.delete(JournalContract.JournalEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"
                        + JournalContract.JournalEntry.TABLE_NAME+"'");
                break;
            case NOTES_WITH_ID:
                numdeleted=sqLiteDatabase.delete(JournalContract.JournalEntry.TABLE_NAME,
                        JournalContract.JournalEntry._ID+"=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"
                        + JournalContract.JournalEntry.TABLE_NAME+"'");
                break;
            default:
                throw new UnsupportedOperationException("Unlnown uri "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return numdeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase= journalDbHelper.getWritableDatabase();
        int numupdated=0;
        if (contentValues==null){
            throw new IllegalArgumentException("cannot have null content values");
        }
        int match=surimatcher.match(uri);
        switch (match){
            case NOTES:
                numupdated=sqLiteDatabase.update(JournalContract.JournalEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case NOTES_WITH_ID:
                numupdated=sqLiteDatabase.update(JournalContract.JournalEntry.TABLE_NAME,
                        contentValues,
                        JournalContract.JournalEntry._ID+"=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+uri);
        }
        if (numupdated>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return numupdated;
    }
}
