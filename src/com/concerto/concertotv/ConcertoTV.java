package com.concerto.concertotv;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ConcertoTV {
  public static String macAddress(Context context){
    WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo wifiInf = wifiMan.getConnectionInfo();
    return wifiInf.getMacAddress();
  }
  public static String getURL(){
    return "";
  }
  public static String getMac(){
    return "";
  }
  public static String getBaseURL(){
    return "";
  }
}
