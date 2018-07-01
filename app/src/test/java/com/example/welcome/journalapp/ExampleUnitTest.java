package com.example.welcome.journalapp;

import com.example.welcome.journalapp.fragments.JournalListView;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {

    @Mock
    private JournalListView.OnFragmentInteractionListener mListener;
    private JournalListView journalListView;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void fabClicked_showsEditor_Activity(){
        journalListView.onButtonPressed();
        verify(mListener).onFragmentInteraction();
    }
}