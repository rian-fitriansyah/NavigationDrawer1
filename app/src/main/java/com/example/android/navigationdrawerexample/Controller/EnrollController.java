package com.example.android.navigationdrawerexample.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.android.navigationdrawerexample.Model.Kelas;
import com.example.android.navigationdrawerexample.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 4/11/2015.
 */
public class EnrollController extends Activity{
    private LinearLayout linearMain;
    private String username;
    private JSONArray jsonArray;
    private ArrayList<CheckBox> addKelas = new ArrayList<CheckBox>();
    private ArrayList<Kelas> pilihan = new ArrayList<Kelas>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.username = getIntent().getStringExtra("Username");

        setContentView(R.layout.add_enroll);
        linearMain = (LinearLayout) findViewById(R.id.container);
        Button enroll = (Button) findViewById(R.id.button);

        new GetAllKelasTask().execute(linearMain);

        enroll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String kelas = "";
                for (int i = 0; i < addKelas.size(); i++) {
                    if (addKelas.get(i).isChecked()) {
                        kelas += pilihan.get(addKelas.get(i).getId()).getId() + " ";
                        Toast.makeText(getApplicationContext(), ""+ pilihan.get(addKelas.get(i).getId()).getId(), Toast.LENGTH_LONG).show();
                    }
                }
                if (!kelas.equals("")){
                    kelas = kelas.substring(0, kelas.length() - 1);
                    addEnroll(username, kelas);
                    Toast.makeText(getApplicationContext(), "Masuk Enroll", Toast.LENGTH_LONG).show();
                }

                Intent showDetails = new Intent(getApplicationContext(), EnrollController.class);
                //asumsi username gak null
                showDetails.putExtra("Username", username);
                startActivity(showDetails);
                finish();
            }
        });
    }

    private class GetAllKelasTask extends AsyncTask<LinearLayout,Long,LinearLayout>
    {
        ArrayList a;
        @Override
        protected LinearLayout doInBackground(LinearLayout... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(LinearLayout a) {
            ScrollView scrollView = new ScrollView(getApplicationContext());
            scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));

            LinearLayout linearLayout3 = new LinearLayout(getApplicationContext());
            linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout3.setOrientation(LinearLayout.VERTICAL);

            pilihan = (new KelasController()).getAllClass(username);
            if (!pilihan.isEmpty()){
                for (int i = 0; i < pilihan.size(); i++) {
                    CheckBox checkBox = new CheckBox(getApplicationContext());
                    checkBox.setId(i);
                    checkBox.setText(pilihan.get(i).getNama());
                    checkBox.setTextColor(getResources().getColor(R.color.black));
                    linearLayout3.addView(checkBox);
                    addKelas.add(checkBox);
                } scrollView.addView(linearLayout3);
                a.addView(scrollView);
            }
        }
    }

    public void addEnroll(String username, String kelas) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("Kelas", kelas));
        nameValuePairs.add(new BasicNameValuePair("Username", username));

        InputStream is = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("http://ppl-a08.cs.ui.ac.id/createEnroll.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            is = entity.getContent();

            String msg = "Berhasil";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("Client Protocol", "Log_Tag");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Log_Tag", "IOException");
            e.printStackTrace();
        }
    }

}
