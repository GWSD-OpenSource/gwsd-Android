package com.gwsd.open_ptt.comm_ui.video_record.contracts;

import com.gwsd.open_ptt.comm_ui.video_record.help.EmptyControlVideo;

/**
 * Created by Nicky on 2017/12/15.
 */

public interface VideoPlayContracts {

    interface VideoPlayUI{
        void uiPlay(String path);
        void uiStopPlay();
        void uiAddCallbackPlayTimer(EmptyControlVideo.CallbackPlayTimer callbackPlayTimer);
    }


}
