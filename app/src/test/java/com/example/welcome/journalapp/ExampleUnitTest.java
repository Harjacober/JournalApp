package com.example.welcome.journalapp;

import com.example.welcome.journalapp.fragments.JournalListView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {

    @Mock
    private JournalListView.OnFragmentInteractionListener mListener;
    private JournalListView journalListView = new JournalListView();
    private MainActivity mainActivity;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Before
    public void initClass() throws IOException {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
        // Get a reference to the class under test
        journalListView = new JournalListView();
        mainActivity = new MainActivity();

    }
    @Test
    public void fabClicked_showsEditor_Activity(){
        mListener.onFragmentInteraction();

        verify(mainActivity).openAddNoteActivity();
    }
}