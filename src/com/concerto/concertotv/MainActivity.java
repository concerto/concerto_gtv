package com.concerto.concertotv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MainActivity extends Activity {

  WebView mWebView;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    String server = ConcertoTV.getBaseURL(getApplicationContext());
    if (server.equals(getString(R.string.default_base_url))) {
      server = "Concerto TV Server";
    }
    final ProgressDialog dialog = ProgressDialog.show(this, "Loading",
        "Connecting to:\n" + server);

    mWebView = (WebView) findViewById(R.id.webview);
    mWebView.setBackgroundColor(android.graphics.Color.BLACK);

    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setBuiltInZoomControls(false);
    mWebView.setInitialScale(100);

    mWebView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int progress) {
        dialog.setProgress(progress * 10);
        if (progress == 100) {
          dialog.dismiss();
        }
      }
    });
    mWebView.loadUrl(ConcertoTV.getURL(getApplicationContext(), metrics));
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
      finish();
    }
    return super.onKeyDown(keyCode, event);
  }
}