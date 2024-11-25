package com.gwsd.open_ptt.comm_ui.voice;

/**
 * Created by Nicky on 2017/10/24.
 */

public enum PlayState {
    STATE_PREPARING(1),STATE_PAUSE(2),STATE_PLAYING(3),STATE_STOP(4),STATE_IDLE(5);

    int value;
    PlayState(int value){
        this.value=value;
    }
}
