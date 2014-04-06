package com.concerto.concertotv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends Activity {

  EditText serverInput;
  EditText identifierInput;
  WebView mWebView;
  AlertDialog serverDialog;
  AlertDialog identifierDialog;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    Window window = this.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    mWebView = (WebView) findViewById(R.id.webview);
    mWebView.setBackgroundColor(android.graphics.Color.BLACK);

    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
    mWebView.getSettings().setBuiltInZoomControls(false);
    mWebView.setInitialScale(100);

    String server = ConcertoTV.getBaseURL(getApplicationContext(), true);
    if (server.length() == 0) {
      server = "Concerto TV Server";
    }
    final ProgressDialog dialog = ProgressDialog.show(this, "Loading",
        "Connecting to:\n" + server);

    // Handle redirects and other URL changes inside the app.
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
      }
    });

    // Track the first loading process in a dialog window.
    mWebView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int progress) {
        dialog.setProgress(progress * 10);
        if (progress >= 100) {
          dialog.dismiss();
        }
      }
    });

    final Context context = this;
    setupServerMenu(context);
    setupIdentifierMenu(context);
  }

  /**
   * Setup the Change Server menu.
   * 
   * @param context
   */
  private void setupServerMenu(Context context) {
    LayoutInflater li = LayoutInflater.from(context);

    View promptsView = li.inflate(R.layout.server_prompt, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    alertDialogBuilder.setView(promptsView);

    serverInput = (EditText) promptsView.findViewById(R.id.editServer);

    alertDialogBuilder.setCancelable(false).setTitle(R.string.server_prompt)
        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            String newServer = serverInput.getText().toString();
            if (!newServer.equals(ConcertoTV.getBaseURL(
                getApplicationContext(), true))) {
              ConcertoTV.saveUrl(getApplicationContext(), newServer);
              loadConcerto();
            }
          }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });

    serverDialog = alertDialogBuilder.create();
  }

  /**
   * Setup the Change Identifier menu.
   * 
   * @param context
   */
  private void setupIdentifierMenu(Context context) {
    LayoutInflater li = LayoutInflater.from(context);

    View promptsView = li.inflate(R.layout.identifier_prompt, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    alertDialogBuilder.setView(promptsView);
    identifierInput = (EditText) promptsView.findViewById(R.id.editIdentifier);

    alertDialogBuilder.setCancelable(false)
        .setTitle(R.string.identifier_prompt)
        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            String newMac = identifierInput.getText().toString();
            if (!newMac.equals(ConcertoTV.getMac(getApplicationContext()))) {
              ConcertoTV.saveMac(getApplicationContext(), newMac);
              loadConcerto();
            }
          }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });

    identifierDialog = alertDialogBuilder.create();
  }

  @Override
  public void onStart() {
    super.onStart();
    Intent intent = getIntent();
    Uri data = intent.getData();
    if (data != null) {
      String server = ConcertoTV.parseBaseURL(data);
      if (server == null) {
        server = ConcertoTV.getBaseURL(getApplicationContext(), false);
      }
      String mac = ConcertoTV.parseMac(data);
      if (mac == null) {
        mac = ConcertoTV.getMac(getApplicationContext());
      }

      loadCustomConcerto(server, mac);
    } else {
      loadConcerto();
    }
  }

  /**
   * Load a non-default concerto server and mac address
   * 
   * @param server
   * @param mac
   */
  public void loadCustomConcerto(String server, String mac) {
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    Log.v("connect",
        ConcertoTV.getCustomURL(getApplicationContext(), server, mac, metrics));
    mWebView.loadUrl(ConcertoTV.getCustomURL(getApplicationContext(), server,
        mac, metrics));
  }

  /**
   * Load the default concerto server.
   */
  public void loadConcerto() {
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    mWebView.loadUrl(ConcertoTV.getURL(getApplicationContext(), metrics));
  }

  @Override
  public void onStop() {
    // Head to about:blank to stop hitting the concerto server
    mWebView.loadUrl("about:blank");
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.server:
        serverDialog();
        return true;
      case R.id.identifier:
        identifierDialog();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void serverDialog() {
    serverInput.setText(ConcertoTV.getBaseURL(getApplicationContext(), true));
    serverDialog.show();
  }

  private void identifierDialog() {
    identifierInput.setText(ConcertoTV.getMac(getApplicationContext()));
    identifierDialog.show();
  }
}