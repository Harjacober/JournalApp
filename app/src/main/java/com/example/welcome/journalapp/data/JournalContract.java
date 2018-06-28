package com.example.welcome.journalapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class JournalContract {
    public static final String CONTENT_AUTHORITY="com.example.welcome.journalapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    private JournalContract(){}

    public static final class JournalEntry implements BaseColumns {
        public final static String _ID=BaseColumns._ID;
        public static final String TABLE_NAME="journal_collection";
        public static final String CREATED_TIME="created_time";
        public static final String CREATED_DATE_WITHOUT_TIME="created_date_without_time";
        public static final String NOTES_DESCRIPTION="task_description";
        public static final String CATEGORY="category";
        public static final String UNIQUE_ID="unique_id";
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();


        public static Uri buildTaskUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }
}
