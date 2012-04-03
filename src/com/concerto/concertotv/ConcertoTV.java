package com.concerto.concertotv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;

public class ConcertoTV {
  /**
   * Get the mac address, or something that looks like one.
   * 
   * @param context
   * @return String with the device's mac address.
   */
  public static String macAddress(Context context) {
    WifiManager wifiMan = (WifiManager) context
        .getSystemService(Context.WIFI_SERVICE);
    WifiInfo wifiInf = wifiMan.getConnectionInfo();
    String mac = wifiInf.getMacAddress();
    // If the mac address is null, return something that is workable.
    if (mac == null) {
      String uniqueId = Secure.getString(context.getContentResolver(),
          Secure.ANDROID_ID);
      // We hope the last 9 chars are sufficiently unique.
      mac = "001" + uniqueId.substring(0, 9);
    }
    return mac;
  }

  /**
   * Build a URL with the screen information and default server and mac address.
   * 
   * @param context
   * @param metrics
   * @return URL to load.
   */
  public static String getURL(Context context, DisplayMetrics metrics) {
    String concerto_url;
    concerto_url = ConcertoTV.getBaseURL(context, false) + "?mac="
        + ConcertoTV.getMac(context);
    concerto_url += "&width=" + metrics.widthPixels;
    concerto_url += "&height=" + metrics.heightPixels;

    return concerto_url;
  }

  /**
   * Build a URL with the screen information and custom server and mac address.
   * 
   * @param context
   * @param baseURL
   * @param mac
   * @param metrics
   * @return URL to load.
   */
  public static String getCustomURL(Context context, String baseURL,
      String mac, DisplayMetrics metrics) {
    String concerto_url;
    concerto_url = baseURL + "?mac=" + mac;
    concerto_url += "&width=" + metrics.widthPixels;
    concerto_url += "&height=" + metrics.heightPixels;

    return concerto_url;
  }

  /**
   * Save the concerto server URL.
   * 
   * @param context
   * @param url
   * @return Save successful.
   */
  public static boolean saveUrl(Context context, String url) {
    SharedPreferences settings = ConcertoTV.getSettings(context);
    Editor editor = settings.edit();
    editor.putString("base_url", url);
    return editor.commit();
  }

  /**
   * Save the mac address.
   * 
   * @param context
   * @param mac
   * @return Save successful.
   */
  public static boolean saveMac(Context context, String mac) {
    SharedPreferences settings = ConcertoTV.getSettings(context);
    Editor editor = settings.edit();
    editor.putString("mac_address", mac);
    return editor.commit();
  }

  /**
   * Get the save mac address or fall back to the device's current info.
   * 
   * @param context
   * @return Mac address.
   */
  public static String getMac(Context context) {
    SharedPreferences settings = ConcertoTV.getSettings(context);
    String mac = settings.getString("mac_address",
        ConcertoTV.macAddress(context));
    if (mac.length() <= 0) {
      return ConcertoTV.macAddress(context);
    }
    return mac;
  }

  /**
   * Get the current server URL to connect to.
   * 
   * @param context
   * @param hide_default
   *          Don't return the URL if it's the default.
   * @return server url.
   */
  public static String getBaseURL(Context context, Boolean hide_default) {
    SharedPreferences settings = ConcertoTV.getSettings(context);
    String url = settings.getString("base_url",
        context.getString(R.string.default_base_url));
    if (url.length() <= 0) {
      url = context.getString(R.string.default_base_url);
    }
    if (url == context.getString(R.string.default_base_url) && hide_default) {
      return "";
    } else {
      return url;
    }
  }

  public static SharedPreferences getSettings(Context context) {
    return context.getSharedPreferences(context.getString(R.string.pref_file),
        0);
  }

  /**
   * Extract the concerto server from an URL loading the application.
   * 
   * @param data
   * @return Concerto server URL.
   */
  public static String parseBaseURL(Uri data) {
    Uri.Builder builder = data.buildUpon();
    if (data.getScheme().equals("concerto")) {
      builder.scheme("http");
    } else {
      if (data.getPathSegments().size() > 0) {
        // Copy the server to replace goto.your-concerto.com
        String host = data.getPathSegments().get(0);
        builder.authority(host);
        // and remove it from the path.
        builder.path(data.getPath().replace(host, ""));
      } else {
        // There is no server specified so clear everything out.
        builder.authority("");
        builder.path("");
      }
    }
    // Clear out the query string. We don't care about it here.
    builder.query("");
    Uri composed = builder.build();
    if (composed.getPath().length() > 0) {
      return composed.toString();
    } else {
      return null;
    }
  }

  /**
   * Extract the mac address from a URI loading the application.
   * 
   * @param data
   * @return Mac address.
   */
  public static String parseMac(Uri data) {
    String mac = data.getQueryParameter("mac");
    return mac;
  }
}
