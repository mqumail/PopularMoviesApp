package app.com.example.muhammad.popularmoviesapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Muhammad on 9/18/2016.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Set up the background color of appBar and up button
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the main fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.detail_ui_container_a, new DetailFragmentA());
        ft.commit();
    }



    // This method creates options menu from R.menu.menu xml
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // This method handles when items are selected from overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // when the user clicks up button, bring back to the parent activity
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
