package com.example.welcome.journalapp.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.welcome.journalapp.R;
import com.example.welcome.journalapp.data.JournalContract;
import com.example.welcome.journalapp.utils.JournalTimeUtils;

public class JournalAdapter extends CursorAdapter {

    public JournalAdapter(Context mContext, Cursor mCursor) {
        super(mContext, mCursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return  LayoutInflater.from(context).inflate(
                R.layout.item_notes_list_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView mDayCreated;
        TextView mDayCreatedName;
        TextView mCreatedMonthYear;
        TextView mTimeCreated;
        TextView mNoteDescription;
        TextView categoryText;
        ImageView categoryColor = view.findViewById(R.id.radioButton);
        ImageView checkBox = view.findViewById(R.id.checkbox);

        mDayCreated = view.findViewById(R.id.createdDayNum);
        mDayCreatedName = view.findViewById(R.id.createdDayWord);
        mCreatedMonthYear = view.findViewById(R.id.createdMonthYear);
        mTimeCreated = view.findViewById(R.id.createdTime);
        mNoteDescription = view.findViewById(R.id.noteDescription);
        categoryText = view.findViewById(R.id.category);

        if (cursor == null) return;
        String time = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.CREATED_TIME));
        String desc = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.NOTES_DESCRIPTION));
        String categoryHere = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.CATEGORY));
        final String uniqueId = cursor.getString(cursor.getColumnIndex(JournalContract.JournalEntry.UNIQUE_ID));

        TypedArray array = context.getResources().obtainTypedArray(R.array.categories);
        String pCategory = array.getString(Integer.valueOf(categoryHere));
        String month = JournalTimeUtils.getMonthName(Long.parseLong(time));
        String dayName = JournalTimeUtils.getDayName(context, Long.parseLong(time));
        String year = JournalTimeUtils.getYear(Long.parseLong(time));
        String dayNum = JournalTimeUtils.getDayNum(Long.parseLong(time));
        String monthYearView = month + " " + year;

        mDayCreated.setText(dayNum);
        mDayCreatedName.setText(dayName);
        mCreatedMonthYear.setText(monthYearView);
        mTimeCreated.setText(JournalTimeUtils.getTimeInReadableFormat(Long.valueOf(time)-3600000));
        mNoteDescription.setText(getScaledDesc(desc));
        categoryText.setText(pCategory);
        categoryImage(Integer.valueOf(categoryHere), categoryColor);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getContentResolver().delete(JournalContract.JournalEntry.CONTENT_URI,
                        JournalContract.JournalEntry.UNIQUE_ID+"=?",
                        new String[]{uniqueId});
                notifyDataSetChanged();
            }
        });
        array.recycle();

    }
    public static void categoryImage(int index, ImageView imageView){
        switch (index){
            case 0:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_red_24dp);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_yellow_24dp);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_blue_24dp);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_green_24dp);
                break;
        }
    }
    public String getScaledDesc(String mCaskDescription) {
        StringBuilder desc = new StringBuilder();
        if (mCaskDescription.length() > 100){
            for (int i = 0; i < 100; i++){
                desc.append(mCaskDescription.charAt(i));
            }
            return desc.toString()+"...";
        }else{
            return mCaskDescription;
        }
    }
}
