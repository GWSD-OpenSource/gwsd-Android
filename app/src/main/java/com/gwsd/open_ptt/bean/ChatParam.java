package com.gwsd.open_ptt.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatParam implements Parcelable {

    private int convId;
    private int convType;
    private String convName;

    public ChatParam() {

    }

    public ChatParam(int convId, int convType, String convName) {
        this.convId = convId;
        this.convType = convType;
        this.convName = convName;
    }

    protected ChatParam(Parcel in) {
        convId = in.readInt();
        convType = in.readInt();
        convName = in.readString();
    }

    public static final Creator<ChatParam> CREATOR = new Creator<ChatParam>() {
        @Override
        public ChatParam createFromParcel(Parcel in) {
            return new ChatParam(in);
        }

        @Override
        public ChatParam[] newArray(int size) {
            return new ChatParam[size];
        }
    };

    public int getConvId() {
        return convId;
    }

    public void setConvId(int convId) {
        this.convId = convId;
    }

    public int getConvType() {
        return convType;
    }

    public void setConvType(int convType) {
        this.convType = convType;
    }

    public String getConvName() {
        return convName;
    }

    public void setConvName(String convName) {
        this.convName = convName;
    }

    @Override
    public String toString() {
        return "ChatParam{" +
                "convId=" + convId +
                ", convType=" + convType +
                ", convName='" + convName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(convId);
        dest.writeInt(convType);
        dest.writeString(convName);
    }
}

