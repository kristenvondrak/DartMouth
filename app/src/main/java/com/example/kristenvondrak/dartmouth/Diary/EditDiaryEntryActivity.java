package com.example.kristenvondrak.dartmouth.Diary;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.Stats.ProgressFragment;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Calendar;

public class EditDiaryEntryActivity extends ActionBarActivity {
    private Activity m_Me;

    // Header
    private TextView m_CancelBtn;
    private TextView m_SaveBtn;

    // Fragment
    private Fragment m_Fragment;
    private FrameLayout m_FragmentContent;
    private FragmentManager m_FragmentManager;

    private DiaryEntry m_DiaryEntry;
    private UserMeal m_UserMeal;
    private Calendar m_Calendar;


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary_entry);
        m_Me = this;
        initializeViews();
        initializeListeners();

        Intent intent = getIntent();
        String diaryEntryId = intent.getStringExtra(DiaryFragment.EXTRA_DIARY_ENTRY_ID);
        String userMealId = intent.getStringExtra(DiaryFragment.EXTRA_USER_MEAL_ID);
        m_Calendar = Calendar.getInstance();
        m_Calendar.setTimeInMillis(intent.getLongExtra(DiaryFragment.EXTRA_DATE, m_Calendar.getTimeInMillis()));

        // Get the diary entry
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DiaryEntry");
        query.include("recipe");
        query.getInBackground(diaryEntryId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    m_DiaryEntry = (DiaryEntry) object;
                    m_Fragment = new DiaryEntryFragment();
                } else {
                    m_Fragment = new ProgressFragment();
                    Log.d("Error", e.getMessage());
                }
                updateFragment();
            }
        });

        // Get the user meal -- for deletion purposes
        ParseQuery<ParseObject> userMealQuery = ParseQuery.getQuery("UserMeal");
        userMealQuery.include("entries");
        userMealQuery.getInBackground(userMealId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    m_UserMeal = (UserMeal) object;
                } else {
                    Log.d("Error", e.getMessage());
                }
            }
        });

    }

    private void updateFragment() {
        m_FragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = m_FragmentManager.beginTransaction();
        ft.replace(m_FragmentContent.getId(), m_Fragment).commit();
    }

    private void initializeViews() {
        m_FragmentContent = (FrameLayout) findViewById(R.id.content);
        m_CancelBtn = (TextView) findViewById(R.id.header_cancel_btn);
        m_SaveBtn = (TextView) findViewById(R.id.header_save_btn);
    }

    private void initializeListeners() {
        m_SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("DiaryEntry");
                query.getInBackground(m_DiaryEntry.getObjectId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, com.parse.ParseException e) {
                        if (e == null) {
                            //TODO: for non-DDS items --> update all
                            DiaryEntry entry = (DiaryEntry) object;
                            entry.put("servingsMultiplier", ((DiaryEntryFragment) m_Fragment).getServingsMultiplier());
                            entry.saveInBackground();
                        }
                        m_Me.onBackPressed();
                    }
                });
            }
        });

        m_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Me.onBackPressed();
            }
        });
    }


    public Recipe getRecipe() {
        return m_DiaryEntry.getRecipe();
    }

    public float getServingsMultiplier() {
        return m_DiaryEntry.getServingsMultiplier();
    }

    public void deleteDiaryEntry() {
        // Remove pointer to entry from user meal in Parse
        if (m_UserMeal.getDiaryEntries().size() == 1)
            m_UserMeal.deleteInBackground();
        else {
            m_UserMeal.removeDiaryEntry(m_DiaryEntry);
            m_UserMeal.saveInBackground();
        }

        // Remove diary entry
        m_DiaryEntry.deleteInBackground();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        this.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }


    public void showDeleteDialog() {

        LayoutInflater inflater = m_Me.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(m_Me, R.style.DialogStyle));
        View v = inflater.inflate(R.layout.delete_entry_dialog, null);

        builder .setView(v);
        final AlertDialog dialog = builder.create();

        TextView cancelBtn = (TextView) v.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        TextView deleteBtn = (TextView) v.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiaryEntry();
                Toast.makeText(m_Me, "Deleted from diary!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                m_Me.onBackPressed();
            }
        });

        dialog.show();
    }

}
