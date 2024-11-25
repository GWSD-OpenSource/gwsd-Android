package com.gwsd.open_ptt.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceConfig {

    public interface DEVICE_KEY_BROADCAST {
        public final String PTT_KEY_DOWN = "android.intent.action.side_key.keydown.PTT";
        public final String PTT_KEY_UP = "android.intent.action.side_key.keyup.PTT";
        // you can add other key broadcast

        static List<String> getBroadcastArray() {
            List<String> actionAll = new ArrayList<>();
            actionAll.addAll(Arrays.asList(PTT_KEY_DOWN, PTT_KEY_UP));
            return actionAll;
        }
    }

    public interface DEVICE_CAMERA_ORIENTATION {
        public int front_camera_orientation = 90;
        public int back_camera_orientation = 90;
    }

    public static String getDeviceImei() {
        // you should call android api get device imei
        return "12345";
    }

    public static String getDeviceIccid() {
        // you should call android api get sim card iccid
        return "54321";
    }

    public static int getDeviceBattery() {
        // you should call android api get battery
        return 100;
    }

    public static String getDeviceNetwork() {
        // you should call android api get network
        return "5G";
    }

}
