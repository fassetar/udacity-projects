package com.anthonyfassett.com.apps.udacityandroidapp;

public interface PlayerSubscriber {
    void onPlayerEvent(String event, PlayerService service);
}