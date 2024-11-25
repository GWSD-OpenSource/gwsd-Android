package com.gwsd.open_ptt.bean;

public class GWPttUserInfo {

    private String account;
    private String password;
    private String name;
    private int id;
    private long defaultGid;
    private boolean message;
    private boolean call;
    private boolean video;
    private boolean silent;
    private CurrentGroup currentGroup;
    private long lastpriority;
    private boolean online;

    public GWPttUserInfo() {
        currentGroup = new CurrentGroup();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDefaultGid() {
        return defaultGid;
    }

    public void setDefaultGid(long defaultGid) {
        this.defaultGid = defaultGid;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public boolean isCall() {
        return call;
    }

    public void setCall(boolean call) {
        this.call = call;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public String getCurrentGroupName() {
        return currentGroup.getGname();
    }

    public void setCurrentGroupName(String gname) {
        currentGroup.setGname(gname);
    }

    public long getCurrentGroupGid() {
        return currentGroup.getGid();
    }

    public void setCurrentGroupGid(long currentGid) {
        currentGroup.setGid(currentGid);
    }

    public void setCurrentGroupPriority(long priority) {
        currentGroup.setPriority(priority);
    }

    public long getCurrentGroupPriority() {
        return currentGroup.getPriority();
    }

    public void setCurrentGroupType(int type) {
        currentGroup.setType(type);
    }

    public int getCurrentGroupType() {
        return currentGroup.getType();
    }

    public long getLastpriority() {
        return lastpriority;
    }

    public void setLastpriority(long lastpriority) {
        this.lastpriority = lastpriority;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public static class CurrentGroup {
        private long gid;
        private String gname;
        private int type;
        private long priority;

        public long getGid() {
            return gid;
        }

        public void setGid(long gid) {
            this.gid = gid;
        }

        public String getGname() {
            return gname;
        }

        public void setGname(String gname) {
            this.gname = gname;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getPriority() {
            return priority;
        }

        public void setPriority(long priority) {
            this.priority = priority;
        }
    }

}
