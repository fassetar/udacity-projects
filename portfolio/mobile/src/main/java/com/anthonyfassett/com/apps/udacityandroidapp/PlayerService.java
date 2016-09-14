package com.anthonyfassett.com.apps.udacityandroidapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


//DOCS: https://github.com/zacharytamas/spotify-sampler
public class PlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    public static final String EVENT_PREPARED = "player-prepared";
    public static final String EVENT_TRACK_COMPLETED = "player-track-completed";
    public static final String EVENT_SEEK_TRACK = "player-seek-track";
    public static final String EVENT_PLAY = "player-play";
    public static final String EVENT_PAUSED = "player-paused";
    public static final String EVENT_PROGRESS = "player-progress";
    public static final String EVENT_PLAYLIST_END = "player-playlist-end";
    public static final String EVENT_SEEK = "progress-seek";
    private static final String LOCK_KEY = "player-wifilock";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = PlayerService.class.getSimpleName();
    private static PlayerService defaultService;
    private Notification mNotification;

    private ArrayList<PlayerSubscriber> mSubscribers;

    private ArrayList<TopTrackModel> mPlaylist;
    private int mTrackIndex = -1;
    private MediaPlayer mPlayer;
    private WifiManager.WifiLock mWifiLock;
    private Timer mTimer;

    public static PlayerService getInstance() {
        return defaultService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Created a new PlayerService.");

        mSubscribers = new ArrayList<>();
        defaultService = this;
        mPlaylist = new ArrayList<>();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, LOCK_KEY);
        mWifiLock.acquire();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        mWifiLock.release();
        super.onDestroy();
    }

    private void seekTrack(TopTrackModel track) {
        Log.i(TAG, "Seeking track: " + track.name);
        try {
            this.fire(EVENT_SEEK_TRACK);
            if (mPlayer.isPlaying()) mPlayer.pause();
            stopTicking();
            mPlayer.reset();
            mPlayer.setDataSource(track.previewUrl);
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seekTrack() {
        seekTrack(mPlaylist.get(mTrackIndex));
    }

    private void createNotification() {
        mNotification = new Notification();
        mNotification.tickerText = "Spotify Streamer";
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, mNotification);
    }

    public void playNewPlaylistAtIndex(ArrayList<TopTrackModel> playlist, int index) {
        Log.i(TAG, "playNewPlaylistAtIndex " + index);
        if (playlist != mPlaylist) {
            Log.i(TAG, "Replaced playlist");
            mPlaylist = playlist;
        }
        mTrackIndex = index;
        seekTrack();
    }

    public void play() {

        if (mNotification == null) {
            createNotification();
        }

        mPlayer.start();
        this.startTicking();
        this.fire(EVENT_PLAY);
    }

    public void pause() {
        mPlayer.pause();
        this.stopTicking();
        this.fire(EVENT_PAUSED);
    }

    public void setProgressTo(int progress) {
        //if(mPlayer.isPlaying())
        this.fire(EVENT_SEEK);
        mPlayer.seekTo(progress);
    }

    public boolean hasNextTrack() {
        return mTrackIndex < (mPlaylist.size() - 1);
    }

    public boolean hasPreviousTrack() {
        return mTrackIndex != 0;
    }

    public void playPause() {
        if (mPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public int getDuration() {
        return mPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    public void next() {
        if (hasNextTrack()) {
            mTrackIndex += 1;
            seekTrack();
        } else {
            mWifiLock.release();
            this.fire(EVENT_PLAYLIST_END);
        }
    }

    public void previous() {
        if (hasPreviousTrack()) {
            mTrackIndex -= 1;
            seekTrack();
        } else {
            mPlayer.seekTo(0);
        }
    }

    public TopTrackModel getCurrentlyPlayingTrack() {
        if (mTrackIndex >= 0) {
            return mPlaylist.get(mTrackIndex);
        }
        return null;
    }

    public int getProgress() {
        int position = getCurrentPosition();
        float duration = getDuration();
        return (int) (position / duration * 100);
    }

    private void startTicking() {
        if (mTimer != null) stopTicking();
        if (mSubscribers.size() == 0) return;
        if (!isPlaying()) return;

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "ProgressTimer: Tick...");
                fire(EVENT_PROGRESS);
            }
        }, 0, 8);
    }

    private void stopTicking() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        Log.i(TAG, "ProgressTimer: Stopped ticking.");
    }

    public void subscribe(PlayerSubscriber subscriber) {
        Log.i(TAG, "New subscriber.");
        mSubscribers.add(subscriber);
        startTicking();
    }

    public void unsubscribe(PlayerSubscriber subscriber) {
        Log.i(TAG, "Lost a subscriber.");
        mSubscribers.remove(subscriber);

        // If no one is watching, don't bother ticking.
        if (mSubscribers.size() == 0) {
            stopTicking();
        }
    }

    private void fire(String event) {
        for (PlayerSubscriber subscriber : mSubscribers) {
            subscriber.onPlayerEvent(event, this);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        this.fire(EVENT_PREPARED);
        play();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        this.fire(EVENT_TRACK_COMPLETED);
        stopTicking();
        //I don't want it to play the next song.
        //next();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
