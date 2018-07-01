package com.example.welcome.journalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welcome.journalapp.fragments.JournalCalendarView;
import com.example.welcome.journalapp.fragments.JournalListView;
import com.example.welcome.journalapp.ui.AddNewNote;
import com.example.welcome.journalapp.ui.NoteDetails;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements
        JournalListView.OnFragmentInteractionListener, JournalCalendarView.OnFragmentInteractionListener, AdapterView.OnItemSelectedListener {
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private static final int PAGES_NUM = 2;
    public static final String PREFS_CATEGORY = "preference-category";
    public static final String PREF_CATEGORY_KEY = "category-key";
    private static final int REQUEST_CODE = 65;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllPermission();
        getSupportActionBar().setElevation(0);
        PagerTabStrip mPagerTabStrip= findViewById(R.id.pager_tab_strip);
        mPagerTabStrip.setTabIndicatorColor(Color.parseColor("#ffffff"));
        mViewPager= findViewById(R.id.pager);
        mPagerAdapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //Set up Spinner
        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((TextView) spinner.getSelectedView()).setTextColor(Color.WHITE);
            }
        });
        spinner.setOnItemSelectedListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings){

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction() {
        Intent intent = new Intent(this, AddNewNote.class);
        startActivity(intent);

    }

    @Override
    public void onListItemCLicked(long id) {
        String uId=String.valueOf(id);
        //Uri currentWorkerUri= ContentUris.withAppendedId(MedicationContract.MedicationEntry.CONTENT_URI, id);
        Intent intent=new Intent(this, NoteDetails.class);
        //intent.setData(currentWorkerUri);
        intent.putExtra(Intent.EXTRA_TEXT, uId);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        SharedPreferences preferences = getSharedPreferences(PREFS_CATEGORY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_CATEGORY_KEY, position);
        editor.commit();
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Intent intent = new Intent(this, AddNewNote.class);
        startActivity(intent);
    }

    @Override
    public void onListItemClickedInCalendarClass(Long id) {
        String uId=String.valueOf(id);
        //Uri currentWorkerUri= ContentUris.withAppendedId(MedicationContract.MedicationEntry.CONTENT_URI, id);
        Intent intent=new Intent(this, NoteDetails.class);
        //intent.setData(currentWorkerUri);
        intent.putExtra(Intent.EXTRA_TEXT, uId);
        startActivity(intent);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0: return JournalListView.newInstance();
                case 1: return JournalCalendarView.newInstance();
                default: return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return Html.fromHtml("<font color=#ffffff><b>HOME</b></font>.");
                case 1: return Html.fromHtml("<font color=#ffffff><b>CALENDAR VIEW</b></font>.");
                default:return null;
            }
        }

        @Override
        public int getCount() {
            return PAGES_NUM;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
    public void requestAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            }else {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }
        }else{
            Toast.makeText(this, "Permission needs to be granted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
