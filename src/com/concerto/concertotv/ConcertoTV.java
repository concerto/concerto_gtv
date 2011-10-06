package com.concerto.concertotv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;

public class ConcertoTV {
  public static String macAddress(Context context){
    WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo wifiInf = wifiMan.getConnectionInfo();
    return wifiInf.getMacAddress();
  }
  
  public static String getURL(Context context, DisplayMetrics metrics){
    String concerto_url;
    concerto_url = ConcertoTV.getBaseURL(context) + "?mac=" + ConcertoTV.getMac(context);
    concerto_url += "&width=" + metrics.widthPixels;
    concerto_url += "&height=" + metrics.heightPixels;
    
    return concerto_url;
  }
  
  public static boolean save(Context context, String url, String mac){
    SharedPreferences settings = ConcertoTV.getSettings(context);
    Editor editor = settings.edit();
    editor.putString("base_url", url);
    editor.putString("mac_address", mac);
    editor.putBoolean("first_run", false);
    return editor.commit();
  }
  
  public static boolean stopFirstRun(Context context){
    SharedPreferences settings = ConcertoTV.getSettings(context);
    Editor editor = settings.edit();
    editor.putBoolean("first_run", false);
    return editor.commit();
  }
  
  public static String getMac(Context context){
    SharedPreferences settings = ConcertoTV.getSettings(context);
    String mac =  settings.getString("mac_address", ConcertoTV.macAddress(context));
    if(mac.length() <= 0){
      return ConcertoTV.macAddress(context);
    }
    return mac;
  }
  
  public static String getBaseURL(Context context){
    SharedPreferences settings = ConcertoTV.getSettings(context);
    String url = settings.getString("base_url", context.getString(R.string.default_base_url));
    if(url.length() <= 0){
      return context.getString(R.string.default_base_url);
    }
    return url;
  }
  
  public static boolean firstRun(Context context){
    SharedPreferences settings = ConcertoTV.getSettings(context);
    return settings.getBoolean("first_run", true);
  }
  
  public static SharedPreferences getSettings(Context context){
    return context.getSharedPreferences(context.getString(R.string.pref_file), 0);
  }
  
}
