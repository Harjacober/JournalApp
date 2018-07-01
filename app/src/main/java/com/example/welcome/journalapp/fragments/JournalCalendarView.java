package com.example.welcome.journalapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;

import com.example.welcome.journalapp.R;
import com.example.welcome.journalapp.adapters.JournalAdapter;
import com.example.welcome.journalapp.data.JournalContract;

import java.util.Calendar;
import java.util.GregorianCalendar;
public class JournalCalendarView extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int PRODUCT_LOADER = 0;
    private JournalAdapter mAdapter;
    private ListView mListView;
    private CalendarView calendarView;
    private String mUtcDateWithOutTime = null;

    private OnFragmentInteractionListener mListener;

    public JournalCalendarView() {
        // Required empty public constructor
    }

    public static JournalCalendarView newInstance() {
        return new JournalCalendarView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtcDateWithOutTime = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal_calendar_view, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(null);
            }
        });
        mAdapter = new JournalAdapter(getContext(), null);
        mListView = view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        setListViewListener();
        listViewOnTouchListener();
        calendarView = view.findViewById(R.id.calendarView);
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
        setCalendarViewListener();
        return view;
    }

    private void setCalendarViewListener(){
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Calendar calendar = new GregorianCalendar(year, month, day);
                mUtcDateWithOutTime = String.valueOf(calendar.getTimeInMillis());
                restartLoader();
            }
        });
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={JournalContract.JournalEntry._ID,
                JournalContract.JournalEntry.CREATED_TIME,
                JournalContract.JournalEntry.CATEGORY,
                JournalContract.JournalEntry.UNIQUE_ID,
                JournalContract.JournalEntry.CREATED_DATE_WITHOUT_TIME,
                JournalContract.JournalEntry.NOTES_DESCRIPTION };

        String selection;
        String[] selectionArgs;
        String sortOrder = JournalContract.JournalEntry._ID+" DESC";
        if (mUtcDateWithOutTime != null) {
            selection = JournalContract.JournalEntry.CREATED_DATE_WITHOUT_TIME + "=?";
            selectionArgs = new String[]{mUtcDateWithOutTime};
        }else {
            selection = null;
            selectionArgs = null;
        }
        return new CursorLoader(getContext(),
                JournalContract.JournalEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }
    public void restartLoader() {
        getLoaderManager().restartLoader(PRODUCT_LOADER,null,this);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void onListItemClickedInCalendarClass(Long id);
    }
    public void setListViewListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListener.onListItemClickedInCalendarClass(id);
            }
        });
    }
    public void listViewOnTouchListener(){
        mListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });
    }
}
