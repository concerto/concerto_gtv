package com.concerto.concertotv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;

public class MainActivity extends Activity {

  WebView mWebView;

  public String macAddress() {
    WifiManager wifiMan = (WifiManager) this
        .getSystemService(Context.WIFI_SERVICE);
    WifiInfo wifiInf = wifiMan.getConnectionInfo();
    return wifiInf.getMacAddress();
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    SharedPreferences settings = getSharedPreferences(
        getString(R.string.pref_file), 0);
    String base_url = settings.getString("base_url",
        getString(R.string.default_base_url));
    String mac_addr = settings.getString("mac_address", macAddress());

    String concerto_url;
    concerto_url = base_url + "?mac=" + mac_addr;
    concerto_url += "&width=" + metrics.widthPixels;
    concerto_url += "&height=" + metrics.heightPixels;

    mWebView = (WebView) findViewById(R.id.webview);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.loadUrl(concerto_url);
  }
}