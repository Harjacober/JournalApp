package com.example.welcome.journalapp.ui;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.welcome.journalapp.R;
import com.example.welcome.journalapp.adapters.JournalAdapter;
import com.example.welcome.journalapp.data.JournalContract;
import com.example.welcome.journalapp.utils.JournalTimeUtils;

public class NoteDetails extends AppCompatActivity {
    private String mId;
    private Uri mTaskWithId;
    private String uniqueId;
    private TextView mDayCreated;
    private TextView mCreatedDayName;
    private TextView mCreatedMonthYear;
    private TextView createdTime;
    private TextView mNotesDescription;
    private TextView mCategoryText;
    private ImageView mCategoryColor;
    private String categoryHere;
    private String time;
    public static String NOTE_UNIQUE_ID = "note-unique-id";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_ID = "extra-unique-id";
    public static final String EXTRA_NOTE_DESC = "note-desc";
    public static final String EXTRA_CATEGORY_TEXT = "category-text";
    public static final String EXTRA_TIME_IN_REDABLE_FORMAT = "extra-time-in-readable-format";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        final Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            mId = intent.getStringExtra(Intent.EXTRA_TEXT);
            mTaskWithId = JournalContract.JournalEntry.buildTaskUri(Long.parseLong(mId));
        }

        displayTaskDetails();
        mDayCreated = findViewById(R.id.createdDayNum);
        mCreatedDayName = findViewById(R.id.createdDayWord);
        mCreatedMonthYear = findViewById(R.id.createdMonthYear);
        createdTime = findViewById(R.id.createdTime);
        mNotesDescription = findViewById(R.id.noteDescription);
        mCategoryColor = findViewById(R.id.radioButton);
        mCategoryText = findViewById(R.id.category);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditorActivity();
            }
        });

        android.support.v7.app.ActionBar actiobar = getSupportActionBar();
        if (actiobar != null) {
            actiobar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.delete_note) {
            if (mTaskWithId != null) {
                getContentResolver().delete(mTaskWithId,
                        null,
                        null);
                finish();
            }
        }else if (id == R.id.modify_note){
            openEditorActivity();

        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditorActivity() {
        Intent intent = new Intent(this, AddNewNote.class);
        intent.putExtra(EXTRA_TIME, time);
        intent.putExtra(EXTRA_TIME_IN_REDABLE_FORMAT, createdTime.getText().toString());
        intent.putExtra(EXTRA_NOTE_DESC, mNotesDescription.getText().toString());
        intent.putExtra(EXTRA_CATEGORY_TEXT, categoryHere);
        intent.putExtra(EXTRA_ID, uniqueId);

        startActivity(intent);
    }

    private void displayTaskDetails() {
        String[] projection = {JournalContract.JournalEntry._ID,
                JournalContract.JournalEntry.CREATED_TIME,
                JournalContract.JournalEntry.CATEGORY,
                JournalContract.JournalEntry.CREATED_TIME,
                JournalContract.JournalEntry.NOTES_DESCRIPTION,
                JournalContract.JournalEntry.UNIQUE_ID,
                JournalContract.JournalEntry.CREATED_DATE_WITHOUT_TIME};
        new FetchNotesDetails().execute(projection);
    }
    class FetchNotesDetails extends AsyncTask<String[], Void, Cursor> {

        @Override
        protected Cursor doInBackground(String[]... strings) {
            String[] projection = strings[0];
            Cursor cursor;
            cursor = getContentResolver().query(mTaskWithId,
                    projection,
                    null,
                    null,
                    null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            try{
                if (cursor != null){
                    cursor.moveToFirst();
                    time = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.CREATED_TIME));
                    String desc = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.NOTES_DESCRIPTION));
                    categoryHere = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.CATEGORY));
                    uniqueId = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.UNIQUE_ID));

                    TypedArray array = getApplicationContext().getResources().obtainTypedArray(R.array.categories);
                    String pCategory = array.getString(Integer.valueOf(categoryHere));
                    String month = JournalTimeUtils.getMonthName(Long.parseLong(time));
                    String dayName = JournalTimeUtils.getDayName(getApplicationContext(), Long.parseLong(time));
                    String year = JournalTimeUtils.getYear(Long.parseLong(time));
                    String dayNum = JournalTimeUtils.getDayNum(Long.parseLong(time));
                    String monthYearView = month + " " + year;
                    array.recycle();

                    mDayCreated.setText(dayNum);
                    mCreatedDayName.setText(dayName);
                    mCreatedMonthYear.setText(monthYearView);
                    createdTime.setText(JournalTimeUtils.getTimeInReadableFormat(Long.valueOf(time)-3600000));
                    mNotesDescription.setText(desc);
                    mCategoryText.setText(pCategory);
                    JournalAdapter.categoryImage(Integer.valueOf(categoryHere), mCategoryColor);
                }
            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            super.onPostExecute(cursor);
        }
    }
}
