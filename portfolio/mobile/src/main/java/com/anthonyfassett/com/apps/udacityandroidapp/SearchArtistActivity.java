package com.anthonyfassett.com.apps.udacityandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;
//TODO: I need to move the Search edit in the fragment so I can use the paraceble.
public class SearchArtistActivity extends AppCompatActivity {

    static final String QUERY_KEY = "query";
    private static final String FRAGMENT_TAG = "topTracksFragment";
    public MenuItem nowPlayingItem;
    private EditText searchText;
    public SearchArtistFragment mArtistSearchFragment;
    private boolean mTwoPane = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search);
        searchText = (EditText) findViewById(R.id.searchBox);
        final Intent intent = new Intent(this, PlayerService.class);
        this.startService(intent);

        if (this.findViewById(R.id.artistSearch_topTracksContainer) != null) {
            mTwoPane = true;
        }

        final SearchArtistFragment fragment = new SearchArtistFragment();
        if (savedInstanceState != null) {
            searchText.setText(savedInstanceState.getString(QUERY_KEY));
            Bundle args = new Bundle();
            args.putString(QUERY_KEY, intent.getStringExtra(QUERY_KEY));
            fragment.setArguments(args);
        }

        mArtistSearchFragment = (SearchArtistFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_artist_search);

         searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                        mArtistSearchFragment.fetchArtists(editable.toString());
                }
            });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(QUERY_KEY, searchText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void onArtistChosen(SearchArtistModel artist) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putString(TopTrackActivity.EXTRA_ARTIST_ID, artist.id);
            bundle.putString(TopTrackActivity.EXTRA_ARTIST_NAME, artist.name);

            TopTrackFragment fragment = new TopTrackFragment();
            fragment.setDialogMode(true);

            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.artistSearch_topTracksContainer, fragment, FRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, TopTrackActivity.class);
            intent.putExtra(TopTrackActivity.EXTRA_ARTIST_ID, artist.id);
            intent.putExtra(TopTrackActivity.EXTRA_ARTIST_NAME, artist.name);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);

        nowPlayingItem = menu.findItem(R.id.action_now_playing);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        if (item.getItemId() == R.id.action_now_playing) {
            PlayerFragment.showInContext(this, mTwoPane);
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onResume() {
        PlayerService service = PlayerService.getInstance();
        if (service != null && nowPlayingItem != null) {
            nowPlayingItem.setVisible(service.isPlaying());
        }
        super.onResume();
    }
}
