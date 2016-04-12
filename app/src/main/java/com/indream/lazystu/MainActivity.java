package com.indream.lazystu;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private SharedPreferences sharedPref;
    private Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String pref = getString(R.string.ergwave_pref);
        sharedPref = this.getSharedPreferences(pref, Context.MODE_PRIVATE);
        activity = this;
        setupDeptSpinner();
        getSavedData();
        setupSaveButton();
        setupConnectButton();
    }

    private void getSavedData() {
        String username = sharedPref.getString(getString(R.string.username), "");
        String password = sharedPref.getString(getString(R.string.password), "");
        String sid = sharedPref.getString(getString(R.string.sid), "");
        String cwem = sharedPref.getString(getString(R.string.cwem), "");
//        try {
//            username = SimpleCrypto.decrypt(username);
//            password = SimpleCrypto.decrypt(password);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        int dept = sharedPref.getInt(getString(R.string.dept), 0);
        EditText editUsername = (EditText) findViewById(R.id.editUsername);
        EditText editPassword = (EditText) findViewById(R.id.editPassword);
        EditText editSid = (EditText) findViewById(R.id.editSid);
        EditText editCwem = (EditText) findViewById(R.id.editCwem);
        Spinner deptSpinner = (Spinner) findViewById(R.id.deptSpinner);
        editUsername.setText(username);
        editPassword.setText(password);
        editSid.setText(sid);
        editCwem.setText(cwem);
        deptSpinner.setSelection(dept);
    }

    private void setupDeptSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.deptSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.dept_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupSaveButton() {
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editUsername = (EditText) findViewById(R.id.editUsername);
                EditText editPassword = (EditText) findViewById(R.id.editPassword);
                EditText editSid = (EditText) findViewById(R.id.editSid);
                EditText editCwem = (EditText) findViewById(R.id.editCwem);
                Spinner deptSpinner = (Spinner) findViewById(R.id.deptSpinner);
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();
                String sid = editSid.getText().toString();
                String cwem = editCwem.getText().toString();
                String fqdn =  deptSpinner.getSelectedItem().toString();
                int dept = deptSpinner.getSelectedItemPosition();
//                try {
//                    username = SimpleCrypto.encrypt(username);
//                    password = SimpleCrypto.encrypt(password);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.username), username);
                editor.putString(getString(R.string.password), password);
                editor.putString(getString(R.string.sid), sid);
                editor.putString(getString(R.string.cwem), cwem);
                editor.putString(getString(R.string.fqdn), fqdn);
                editor.putInt(getString(R.string.dept), dept);
                editor.apply();
                Toast.makeText(activity, getString(R.string.saved_setting), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupConnectButton() {
        Button connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ConnectivityReceiver().handleWiFi(activity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
