package com.example.welcome.journalapp.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.welcome.journalapp.MainActivity;
import com.example.welcome.journalapp.R;
import com.example.welcome.journalapp.data.JournalContract;
import com.example.welcome.journalapp.utils.JournalTimeUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.welcome.journalapp.data.JournalContract.JournalEntry.CONTENT_URI;
import static com.example.welcome.journalapp.ui.NoteDetails.EXTRA_CATEGORY_TEXT;
import static com.example.welcome.journalapp.ui.NoteDetails.EXTRA_ID;
import static com.example.welcome.journalapp.ui.NoteDetails.EXTRA_NOTE_DESC;
import static com.example.welcome.journalapp.ui.NoteDetails.EXTRA_TIME;
import static com.example.welcome.journalapp.ui.NoteDetails.EXTRA_TIME_IN_REDABLE_FORMAT;
import static com.example.welcome.journalapp.ui.NoteDetails.NOTE_UNIQUE_ID;

public class AddNewNote extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView mDayCreated;
    private TextView mDayCreatedInWord;
    private TextView mMonthCreated;
    private TextView mYearCreated;
    private TextView mTimeCreated;
    private EditText mNoteDescription;
    private String mDayNum;
    private String mDayInWord;
    private String mMonth;
    private String mYear;
    private String mTime;
    private String mCategory;
    private String mNote;
    private long mUtcDate;
    private String uniqueId;
    private long mUtcDateWthoutTime;
    private int year, month, day;
    private long mTimeFromPickerInMillis;
    private static final int DIALOG_DATE = 999;
    private boolean fromNoteDetails =false;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        intent = getIntent();
        if (intent.hasExtra(EXTRA_TIME)) {
            fromNoteDetails = true;
            uniqueId = intent.getStringExtra(NOTE_UNIQUE_ID);
            setTitle(getString(R.string.edit_note));
        }
        setTitle(getString(R.string.add_new_note));
        setUpSpinner();
        mDayCreated = findViewById(R.id.createdDayNum);
        mDayCreatedInWord = findViewById(R.id.createdDayWord);
        mMonthCreated = findViewById(R.id.createdMonth);
        mYearCreated = findViewById(R.id.createdYear);
        mTimeCreated = findViewById(R.id.createdTime);
        mNoteDescription = findViewById(R.id.taskDescription);

        setDefaultDataInVIews();
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpSpinner() {
        TypedArray array = getResources().obtainTypedArray(R.array.categories);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(arrayAdapter);
        spinner.setPrompt(array.getString(0));
        spinner.setOnItemSelectedListener(this);
        array.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_notes, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.chooseDate){
            showDialog(DIALOG_DATE);

        }else if (id == R.id.chooseTime){
            showTimePickerDialog();
        }else if (id == R.id.discard){
            finish();
        }else if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }else if (id == R.id.save){
            saveTaskDataToDatabase();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        mCategory = String.valueOf(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void saveTaskDataToDatabase(){
        mNote = mNoteDescription.getText().toString();
        String uniqueId = JournalTimeUtils.generateUniqueId();
        long createdTime = mUtcDate + mTimeFromPickerInMillis;
        ContentValues values = new ContentValues();

        values.put(JournalContract.JournalEntry.CREATED_TIME, createdTime);
        values.put(JournalContract.JournalEntry.CREATED_DATE_WITHOUT_TIME, mUtcDate);
        values.put(JournalContract.JournalEntry.CATEGORY, mCategory);
        values.put(JournalContract.JournalEntry.UNIQUE_ID, uniqueId);
        values.put(JournalContract.JournalEntry.NOTES_DESCRIPTION, mNote);
        if (!fromNoteDetails) {
            getContentResolver().insert(CONTENT_URI, values);
        }else{
            getContentResolver().update(CONTENT_URI,
                    values,
                    JournalContract.JournalEntry.UNIQUE_ID+"=?",
                    new String[]{ intent.getStringExtra(EXTRA_ID)});
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    public void OnDateViewClicked(View view) {
        showDialog(DIALOG_DATE);
    }
    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Process the date set by the user in order to add to database
            Calendar calendar = new GregorianCalendar(year, month, day);
            mUtcDate = calendar.getTimeInMillis();
            //update all views
            displayDateInViews(year, month+1, day);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE){
            return new DatePickerDialog(this,
                    R.style.DialogTheme,
                    startDateListener, year, month, day);
        }
        return super.onCreateDialog(id);
    }

    private void displayDateInViews(int year, int month, int day) {
        mDayInWord = JournalTimeUtils.getDayName(this, mUtcDate);
        mDayNum = String.valueOf(day);
        mMonth = JournalTimeUtils.getMonthName(mUtcDate);
        mYear = String.valueOf(year);
        mDayCreated.setText(mDayNum);
        mMonthCreated.setText(mMonth);
        mYearCreated.setText(mYear);
        mDayCreatedInWord.setText(mDayInWord);
    }

    public void OnTimeViewClicked(View view) {
        showTimePickerDialog();
    }
    private void showTimePickerDialog() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timepicker = new TimePickerDialog(new ContextThemeWrapper(
                this, R.style.DialogTheme),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourHere, int minuteHere) {
                        mTime = JournalTimeUtils.getTimeInReadableFormat(mTimeFromPickerInMillis-3600000);
                        mTimeCreated.setText(mTime);
                        long date = mUtcDate - mTimeFromPickerInMillis;
                        mTimeFromPickerInMillis = (((hourHere * 60) + minuteHere) * 60) * 1000;

                    }
                }, hour, minute, true);
        timepicker.setTitle(getString(R.string.time_picker_title));
        timepicker.show();
    }
    public void setDefaultDataInVIews(){
        if (!fromNoteDetails) {
            Calendar currentTime = Calendar.getInstance();
            int year = currentTime.get(Calendar.YEAR);
            int month = currentTime.get(Calendar.MONTH);
            int dayNum = currentTime.get(Calendar.DAY_OF_MONTH);
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            Calendar calendar = new GregorianCalendar(year, month, dayNum);
            mUtcDate = calendar.getTimeInMillis();
            mUtcDateWthoutTime = calendar.getTimeInMillis();

            mTimeFromPickerInMillis = (((hour * 60) + minute) * 60) * 1000;
            mDayNum = String.valueOf(dayNum);
            mDayInWord = JournalTimeUtils.getDayName(this, mUtcDate);
            mMonth = JournalTimeUtils.getMonthName(mUtcDate);
            mYear = String.valueOf(year);
            mTime = JournalTimeUtils.getTimeInReadableFormat(mTimeFromPickerInMillis - 3600000);
            mCategory = "0";
            mTimeCreated.setText(mTime);
        }
        else{
            mTimeCreated.setText(intent.getStringExtra(EXTRA_TIME_IN_REDABLE_FORMAT));
            mUtcDate = Long.parseLong(intent.getStringExtra(EXTRA_TIME));
            mNoteDescription.setText(intent.getStringExtra(EXTRA_NOTE_DESC));
            mCategory = intent.getStringExtra(EXTRA_CATEGORY_TEXT);
            mMonth = JournalTimeUtils.getMonthName(Long.parseLong(intent.getStringExtra(EXTRA_TIME)));
            mDayInWord = JournalTimeUtils.getDayName(this,
                    Long.parseLong(intent.getStringExtra(EXTRA_TIME)));
            mYear = JournalTimeUtils.getYear(Long.parseLong(intent.getStringExtra(EXTRA_TIME)));
            mDayNum = JournalTimeUtils.getDayNum(Long.parseLong(intent.getStringExtra(EXTRA_TIME)));
        }
        mYearCreated.setText(mYear);
        mMonthCreated.setText(mMonth);
        mDayCreated.setText(mDayNum);
        mDayCreatedInWord.setText(mDayInWord);

    }
}
