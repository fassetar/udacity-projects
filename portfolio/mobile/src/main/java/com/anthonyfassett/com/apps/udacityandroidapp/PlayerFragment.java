package com.anthonyfassett.com.apps.udacityandroidapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class PlayerFragment extends DialogFragment implements PlayerSubscriber {

    public static final String PLAYER_FRAGMENT_TAG = "player-fragment";
    public SeekBar mSeekBar;
    public ImageView mPlayPause;
    public ImageView mAlbumArt;
    public TextView mTrackName;
    public TextView mArtistName;
    public TextView mAlbumName;
    public TextView mTrackProgress;
    public PlayerService mService;
    int seekProgress = 0;

    public PlayerFragment() {
    }

    public static void showInContext(FragmentActivity context, boolean asDialog) {
        PlayerFragment player = new PlayerFragment();
        FragmentManager fm = context.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(PLAYER_FRAGMENT_TAG);
        if (prev != null) ft.remove(prev);
        ft.commit();

        if (asDialog) player.show(fm, PLAYER_FRAGMENT_TAG);
        else {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, player, PLAYER_FRAGMENT_TAG)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        mSeekBar = (SeekBar) view.findViewById(R.id.playerSeekBar);
        mPlayPause = (ImageView) view.findViewById(R.id.play);
        mAlbumArt = (ImageView) view.findViewById(R.id.playerAlbumArt);
        mTrackName = (TextView) view.findViewById(R.id.playerTrackName);
        mArtistName = (TextView) view.findViewById(R.id.playerArtistName);
        mAlbumName = (TextView) view.findViewById(R.id.playerAlbumName);
        mTrackProgress = (TextView) view.findViewById(R.id.playerCurrentProgress);
        seekProgress = 0;
        mSeekBar.setMax(100);
        mService = PlayerService.getInstance();

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.next();
            }
        });

        view.findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.previous();
            }
        });

        view.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.playPause();
            }
        });

        updateUIForTrack(mService.getCurrentlyPlayingTrack());
        updateUIForPlayStatus(mService);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.setProgressTo(seekProgress);
             }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTrackProgress.setText(String.format("%2d:%02d", progress / 60, progress % 60));
                if (fromUser) {
                    seekProgress = progress;
                }
            }
        });

        return view;
    }

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(300, 500);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        PlayerService service = PlayerService.getInstance();
        service.subscribe(this);
        updateUIForPlayStatus(service);
        updateUIForTrack(service.getCurrentlyPlayingTrack());
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerService.getInstance().unsubscribe(this);
    }

    @Override
    public void onPlayerEvent(String event, PlayerService service) {
        switch (event) {
            case PlayerService.EVENT_SEEK_TRACK:
            case PlayerService.EVENT_PREPARED:
                updateUIForTrack(service.getCurrentlyPlayingTrack());
                break;
            case PlayerService.EVENT_PLAYLIST_END:
                dismiss();
                break;
            case PlayerService.EVENT_PROGRESS:
                updateUIForProgress(service);
                break;
            case PlayerService.EVENT_PLAY:
            case PlayerService.EVENT_PAUSED:
                updateUIForPlayStatus(service);
                break;
        }
    }

    private void updateUIForProgress(PlayerService service) {

         mSeekBar.setProgress(service.getProgress());
    }

    private void updateUIForPlayStatus(PlayerService service) {
        if (service.isPlaying()) {
            mPlayPause.setImageResource(R.drawable.pause);
        } else {
            mPlayPause.setImageResource(R.drawable.play);
        }
    }

    private void updateUIForTrack(TopTrackModel topTrackModel) {
        Picasso.with(getActivity()).load(topTrackModel.albumImageUrl).into(mAlbumArt);
        mTrackName.setText(topTrackModel.name);
        mArtistName.setText(topTrackModel.artistName);
        mAlbumName.setText(topTrackModel.album);
    }
}
