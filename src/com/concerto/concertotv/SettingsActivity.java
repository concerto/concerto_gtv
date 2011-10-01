package com.concerto.concertotv;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class SettingsActivity extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);
    EditText macText = (EditText) findViewById(R.id.macText);
    macText.setText(ConcertoTV.macAddress(this.getApplicationContext()));
  }
}
