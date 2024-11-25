package com.gwsd.open_ptt.comm_ui.video_record.help;

/**
 * Created by Nicky on 2017/12/15.
 */

public class VideoRecoidingResultBean {
    /**
     * -1、错误
     * 200、录制成功
     * 201、开始录制
     */
    public int code;
    public String message;
    public String filePath;
    public long timerCount;

}
