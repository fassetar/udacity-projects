package com.anthonyfassett.com.apps.udacityandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private int[] ids = new int[]{R.id.spotify_btn, R.id.super_btn, R.id.built_btn, R.id.xyz_btn, R.id.capstone_btn};
    private Button[] arrayButton = new Button[ids.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO: Remember to add other Activities but for now make them all do the spotify example.
        final Intent myIntent = new Intent(MainActivity.this, SearchArtistActivity.class);

        for (int i = 0; i < arrayButton.length; i++) {
            arrayButton[i] = (Button) findViewById(R.id.spotify_btn);
            arrayButton[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    MainActivity.this.startActivity(myIntent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}