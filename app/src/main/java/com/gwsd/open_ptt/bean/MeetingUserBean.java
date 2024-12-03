package com.gwsd.open_ptt.bean;

public class MeetingUserBean {

    private long userVideoId;
    private long userId;
    private String userName;
    private boolean hasVideo;


    public long getUserVideoId() {
        return userVideoId;
    }

    public void setUserVideoId(long userVideoId) {
        this.userVideoId = userVideoId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }
}
