/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigationdrawerexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.navigationdrawerexample.Controller.PilihanController;

public class MainActivity extends Activity implements View.OnClickListener {
    EditText username, password;
    Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.login);

        username = (EditText) findViewById(R.id.editText8);
        password = (EditText) findViewById(R.id.pass);
        ok = (Button) findViewById(R.id.button3);
        ok.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent showDetails = new Intent(this, PilihanController.class);
        String usernameMahasiswa = username.getText().toString();
        String passwordMahasiswa = password.getText().toString();
        if (passwordMahasiswa.equals("123")){
            showDetails.putExtra("Username", usernameMahasiswa);
            startActivity(showDetails);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_LONG).show();
            return;
        }

    }
}
