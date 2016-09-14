package com.anthonyfassett.com.apps.udacityandroidapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

public class SearchArtistFragment extends Fragment {

    private static final String KEY_ARTISTS = "KEY_ARTISTS";
    private SpotifyApi api = new SpotifyApi();
    private SpotifyService spotifyService = api.getService();
    private SearchArtistAdapter mAdapter;
    private ArrayList<SearchArtistModel> mArtists;
    private FetchArtistsTask mFetchTask;
    private TextView mEmptyTextView;

    public SearchArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ListView mListView;
        LinearLayout mEmptyView;
        mArtists = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_artist_search, container, false);

        mListView = (ListView) view.findViewById(R.id.artistListView);
        mAdapter = new SearchArtistAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty));

        mEmptyView = (LinearLayout) view.findViewById(R.id.empty);
        mEmptyTextView = (TextView) mEmptyView.findViewById(R.id.emptyText);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchArtistModel artist = (SearchArtistModel) adapterView.getAdapter().getItem(i);
                SearchArtistActivity activity = (SearchArtistActivity) getActivity();
                activity.onArtistChosen(artist);
            }
        });


        Bundle args = getArguments();

        if (args != null) {
            String query = args.getString(SearchArtistActivity.QUERY_KEY);

            if (query != null) {
                if (savedInstanceState == null) {
                    this.fetchArtists(query);
                } else {
                    mArtists = savedInstanceState.getParcelableArrayList(KEY_ARTISTS);
                    mAdapter.addAll(mArtists != null ? mArtists : null);
                }

            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ARTISTS, mArtists);
        super.onSaveInstanceState(outState);
    }

    public void fetchArtists(String query) {
        if (mFetchTask != null && mFetchTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchTask.cancel(true);
        }
        updateEmptyView("loading");
        mFetchTask = new FetchArtistsTask();
        mFetchTask.execute(query);
    }

    private void updateEmptyView(String status) {
        mEmptyTextView.setVisibility(View.VISIBLE);
        if (status == "loading") {
            mEmptyTextView.setText("Loading results...");
        } else if (status == "error") {
            mEmptyTextView.setText(getActivity().getString(R.string.no_network));
        } else {
            mEmptyTextView.setText(getActivity().getString(R.string.empty_results));
        }
    }

    private class FetchArtistsTask extends AsyncTask<String, Void, List<Artist>> {

        String mStatus;

        @Override
        protected List doInBackground(String... strings) {

            if (strings.length != 1) {
                return null;
            }

            if (strings[0].length() > 0) {
                try {
                    ArtistsPager artistsPager = spotifyService.searchArtists(strings[0]);
                    if (artistsPager.artists != null) {
                        mStatus = null;
                        return artistsPager.artists.items;
                    }
                } catch (RetrofitError error) {
                    mStatus = "error";
                }
            }

            return new ArrayList();
        }

        @Override
        protected void onPostExecute(List<Artist> list) {
            updateEmptyView(mStatus);
            mAdapter.clear();
            mArtists.clear();

            for (Artist artist : list) {
                mArtists.add(new SearchArtistModel(artist));
            }

            mAdapter.addAll(mArtists);
        }

    }
}
