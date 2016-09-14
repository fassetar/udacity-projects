package com.anthonyfassett.com.apps.udacityandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class TopTrackActivity extends AppCompatActivity {

    public static final String EXTRA_ARTIST_ID = "artistId";
    public static final String EXTRA_ARTIST_NAME = "artistName";

    public MenuItem nowPlayingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        if (savedInstanceState == null) {

            Intent intent = getIntent();

            Bundle args = new Bundle();
            args.putString(EXTRA_ARTIST_ID, intent.getStringExtra(EXTRA_ARTIST_ID));
            args.putString(EXTRA_ARTIST_NAME, intent.getStringExtra(EXTRA_ARTIST_NAME));

            TopTrackFragment fragment = new TopTrackFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.artistSearch_topTracksContainer, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        nowPlayingItem = menu.findItem(R.id.action_now_playing);

        PlayerService service = PlayerService.getInstance();
        if (service != null && nowPlayingItem != null) {
            nowPlayingItem.setVisible(service.isPlaying());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_now_playing:
                PlayerFragment.showInContext(this, false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        PlayerService service = PlayerService.getInstance();
        if (service != null && nowPlayingItem != null) {
            nowPlayingItem.setVisible(service.isPlaying());
        }
    }
}
