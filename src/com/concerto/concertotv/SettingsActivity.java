package com.concerto.concertotv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {
  
  ProgressDialog dialog;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);
    if(!ConcertoTV.firstRun(getApplicationContext())){
      dialog = ProgressDialog.show(SettingsActivity.this, "", "Loading Concerto...", true);
      loadMain(findViewById(android.R.id.content));
    }
  }
  public void onStart(){
    super.onStart();
    
    Button saveBtn=(Button)findViewById(R.id.btnSave);
    saveBtn.requestFocus();
  }
  
  public void onResume(){
    super.onResume();
    loadSettings();
  }
  
  public void onPause(){
    if(dialog != null && dialog.isShowing()){
      dialog.dismiss();
    }
    super.onPause();
  }
  
  public void loadSettings(){
    EditText hostText = (EditText) findViewById(R.id.hostText);
    hostText.setText(ConcertoTV.getBaseURL(this.getApplicationContext()));
    
    EditText macText = (EditText) findViewById(R.id.macText);
    macText.setText(ConcertoTV.getMac(this.getApplicationContext()));
    
    Button cancelBtn=(Button)findViewById(R.id.btnCancel);
    if(ConcertoTV.firstRun(getApplicationContext())){
      cancelBtn.setText(getString(R.string.firstrun_cancel_prompt));
    } else {
      cancelBtn.setText(getString(R.string.cancel_prompt));
    }
  }
  public void save(View view){
    EditText hostText = (EditText) findViewById(R.id.hostText);
    EditText macText = (EditText) findViewById(R.id.macText);
    
    ConcertoTV.save(getApplicationContext(), hostText.getText().toString(), macText.getText().toString());
    
    loadMain(view);
  }
  
  public void cancel(View view){
    loadSettings();
    ConcertoTV.stopFirstRun(getApplicationContext());
    loadMain(view);
  }
  public void loadMain(View view){
    Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
    startActivity(mainIntent);
  }
}
