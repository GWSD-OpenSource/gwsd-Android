package com.gwsd.open_ptt.bean;

import com.gwsd.bean.GWMsgBean;

import java.io.Serializable;

public class FileSendParam implements Serializable {

    public static final int VIDEO_FILE_TYPE = 0;
    public static final int PHOTO_FILE_TYPE = 1;
    public static final int VOICE_FILE_TYPE = 2;

    private String filepath;
    private int filetype;
    private String filepathThumb = "";
    GWMsgBean gwMsgBean;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getFiletype() {
        return filetype;
    }

    public void setFiletype(int filetype) {
        this.filetype = filetype;
    }

    public String getFilepathThumb() {
        return filepathThumb;
    }

    public void setFilepathThumb(String filepathThumb) {
        this.filepathThumb = filepathThumb;
    }

    public GWMsgBean getGwMsgBean() {
        return gwMsgBean;
    }

    public void setGwMsgBean(GWMsgBean gwMsgBean) {
        this.gwMsgBean = gwMsgBean;
    }
}
