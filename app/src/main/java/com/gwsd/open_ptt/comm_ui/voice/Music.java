package com.gwsd.open_ptt.comm_ui.voice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Nicky on 2017/10/24.
 */

public class Music implements Parcelable {
    public static final  int MusicType_LOCAL=1;
    public static final  int MusicType_ONLINE=2;
    public static final  int MusicType_RAW=3;

    // 歌曲类型:本地/网络
    @MusicTypeEmun  int type;
    // 音乐标题
    // 持续时间
    private long duration;
    // 音乐路径
    private String path;
    // 文件名
    private String fileName;
    // 文件大小
    private long fileSize;
    private int rawId;
    boolean isLoop=false;

    @IntDef({MusicType_LOCAL,MusicType_ONLINE,MusicType_RAW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MusicTypeEmun {
    }

    public Music() {

    }

    protected Music(Parcel in) {
        int typeTemp=in.readInt();
        if(typeTemp== Music.MusicType_LOCAL){
            type= Music.MusicType_LOCAL;
        }else if(typeTemp== Music.MusicType_ONLINE){
            type= Music.MusicType_ONLINE;
        }else if(typeTemp== Music.MusicType_RAW){
            type= Music.MusicType_RAW;
        }
        duration = in.readLong();
        path = in.readString();
        fileName = in.readString();
        fileSize = in.readLong();
        rawId = in.readInt();
        isLoop = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeLong(duration);
        dest.writeString(path);
        dest.writeString(fileName);
        dest.writeLong(fileSize);
        dest.writeInt(rawId);
        dest.writeByte((byte) (isLoop ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public @MusicTypeEmun int getType() {
        return type;
    }

    public void setType(@MusicTypeEmun int type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getRawId() {
        return rawId;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }
}
