package com.gwsd.open_ptt.comm_ui.video_record.help;

import java.io.Serializable;

public class VideoRecordParam1 implements Serializable {
    public final int NAVTO_TYPE_Norm=0;
    public final int NAVTO_TYPE_ShortcutKeys=1;

    Integer navToType;
    RecordParam recordParam;
    PlayParam playParam;

    public static class RecordParam implements Serializable {
        Integer maxTime;// second
        public Integer getMaxTime() {
            return maxTime;
        }
        public void setMaxTime(int maxTime) {
            this.maxTime = maxTime;
        }
    }
    public static class PlayParam implements Serializable {
        String filePath;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    public Integer getNavToType() {
        return navToType;
    }

    public void setNavToType(Integer navToType) {
        this.navToType = navToType;
    }

    public RecordParam getRecordParam() {
        return recordParam;
    }

    public void setRecordParam(RecordParam recordParam) {
        this.recordParam = recordParam;
    }

    public PlayParam getPlayParam() {
        return playParam;
    }

    public void setPlayParam(PlayParam playParam) {
        this.playParam = playParam;
    }
}
