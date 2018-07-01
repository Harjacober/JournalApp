package com.example.welcome.journalapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.welcome.journalapp.MainActivity;
import com.example.welcome.journalapp.R;
import com.example.welcome.journalapp.adapters.JournalAdapter;
import com.example.welcome.journalapp.data.JournalContract;

import static com.example.welcome.journalapp.MainActivity.PREFS_CATEGORY;
import static com.example.welcome.journalapp.MainActivity.PREF_CATEGORY_KEY;
public class JournalListView extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private JournalAdapter mAdapter;
    private ListView mListView;
    public static final int PRODUCT_LOADER = 0;

    private OnFragmentInteractionListener mListener;

    public JournalListView() {
        // Required empty public constructor
    }

    public static JournalListView newInstance() {
        return  new JournalListView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal_list_view, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed();
            }
        });

        mAdapter = new JournalAdapter(getContext(), null);
        mListView = view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        //This method will be called in the main Activity later
        setListViewListener();
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
        return view;
    }
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
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
                JournalContract.JournalEntry.NOTES_DESCRIPTION };
        String selection;
        String[] selectionArgs;
        SharedPreferences preferences = getContext().getSharedPreferences(PREFS_CATEGORY, 0);
        int selectedType = preferences.getInt(PREF_CATEGORY_KEY, 0);
        if (selectedType == 0) {
            selection = null;
            selectionArgs = null;
        } else {
            selection = JournalContract.JournalEntry.CATEGORY + "=?";
            selectionArgs = new String[]{String.valueOf(selectedType-1)};
        }
        return new CursorLoader(getContext(),
                JournalContract.JournalEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
        void onListItemCLicked(long id);
    }

    public void setListViewListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListener.onListItemCLicked(id);
            }
        });
    }
}
