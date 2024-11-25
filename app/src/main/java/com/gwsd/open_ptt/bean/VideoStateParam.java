package com.gwsd.open_ptt.bean;

public class VideoStateParam {

    private int videoStatus;
    private String remoteName;
    private String remoteUID;
    private boolean duplex;
    private String ts;

    public int getVideoStatus() {
        return videoStatus;
    }

    public VideoStateParam setVideoStatus(int videoStatus) {
        this.videoStatus = videoStatus;
        return this;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public VideoStateParam setRemoteName(String remoteName) {
        this.remoteName = remoteName;
        return this;
    }

    public String getRemoteUID() {
        return remoteUID;
    }

    public VideoStateParam setRemoteUID(String remoteUID) {
        this.remoteUID = remoteUID;
        return this;
    }

    public boolean isDuplex() {
        return duplex;
    }

    public VideoStateParam setDuplex(boolean duplex) {
        this.duplex = duplex;
        return this;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
