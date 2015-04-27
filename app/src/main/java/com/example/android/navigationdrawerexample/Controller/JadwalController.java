package com.example.android.navigationdrawerexample.Controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.navigationdrawerexample.Model.Jadwal;
import com.example.android.navigationdrawerexample.Model.Kelas;
import com.example.android.navigationdrawerexample.Model.Mahasiswa;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lenovo on 3/31/2015.
 */
public class JadwalController extends Activity {
    private String username;
    private ListView GetAllJadwalListView;
    private JSONArray jsonArray;
    private String op;
    private int status;
    private int jadwalID;
    private int kelasID;

    ImageView ivIcon;
    TextView tvItemName;

    public static final String IMAGE_RESOURCE_ID = "iconResourceID";
    public static final String ITEM_NAME = "itemName";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.op = getIntent().getStringExtra("View");

        if (op.equals("detailJadwal")){
            setContentView(R.layout.detail_jadwal);

            TextView judul = (TextView) this.findViewById(R.id.judul);
            TextView asisten = (TextView) this.findViewById(R.id.asisten);
            TextView hp = (TextView) this.findViewById(R.id.hp);
            TextView tanggal = (TextView) this.findViewById(R.id.tanggal);
            TextView waktu = (TextView) this.findViewById(R.id.waktu);
            TextView ruangan = (TextView) this.findViewById(R.id.ruangan);
            TextView deskripsi = (TextView) this.findViewById(R.id.deskripsi);
            final TextView jumlah = (TextView) this.findViewById(R.id.textView11);

            Button hadir = (Button) this.findViewById(R.id.button5);

            ImageView picture = (ImageView) this.findViewById(R.id.pic);


            // get Customer ID
            final int jadwalID = getIntent().getIntExtra("JadwalID", -1);
            final int kelasID = getIntent().getIntExtra("KelasID", -1);

            JadwalDetail jadwalDetail = new JadwalDetail(judul, asisten, hp, tanggal, waktu, ruangan, deskripsi, jadwalID, kelasID, picture);

            this.username = getIntent().getStringExtra("Username");
            try {
                jumlah.setText(Integer.toString(getJumlahMenghadiri(jadwalID, kelasID)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jadwalID > 0 && kelasID > 0)
            {
                // we have customer ID passed correctly.
                getDetailJadwal(jadwalDetail);
            }

            hadir.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //bernilai true jika ia blm menghadiri
                    if(cekMenghadiri(jadwalID, kelasID)) {
                        addMenghadiri(jadwalID, kelasID);
                        try {
                            jumlah.setText(Integer.toString(getJumlahMenghadiri(jadwalID, kelasID)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } //belum menghadiri

                }
            });
        } else if(op.equals("createJadwal")){
            setContentView(R.layout.form_jadwal);

            //username = (EditText) findViewById(R.id.editText8);
            ImageView ok = (ImageView) findViewById(R.id.button2);
            ImageView bTanggal = (ImageView) findViewById(R.id.button);
            ImageView bMulai = (ImageView) findViewById(R.id.button3);
            ImageView bSelesai = (ImageView) findViewById(R.id.button4);
            final Calendar calendar = Calendar.getInstance();
            final Calendar calendarMulai = Calendar.getInstance();
            final Calendar calendarSelesai = Calendar.getInstance();

            final Spinner id_kelas = (Spinner)findViewById(R.id.spinner);
            final EditText judul = (EditText) findViewById(R.id.editText);
            final TextView tanggal = (TextView) findViewById(R.id.textView8);
            final TextView mulai = (TextView) findViewById(R.id.textView9);
            final TextView selesai = (TextView) findViewById(R.id.textView10);
            final EditText ruangan = (EditText) findViewById(R.id.editText5);
            final EditText deskripsi = (EditText) findViewById(R.id.editText6);


            // asumsikan username gak null
            this.username = getIntent().getStringExtra("Username");

            bTanggal.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener(){

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            //view.setSpinnersShown(false);
                            //view.setCalendarViewShown(true);

                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            tanggal.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                            //Toast.makeText(getApplicationContext(), "Tahun=" + calendar.get(Calendar.YEAR) + "-Bulan=" +
                            // calendar.get(Calendar.MONTH) + "-Tanggal=" + calendar.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();
                        }
                    };
                    DatePickerDialog dialog = new DatePickerDialog(JadwalController.this, d, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setSpinnersShown(false);
                    dialog.getDatePicker().setCalendarViewShown(true);
                    dialog.show();
                }
            });

            bMulai.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener(){

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            view.setIs24HourView(true);
                            calendarMulai.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendarMulai.set(Calendar.MINUTE, minute);

                            String sMinute;

                            if((""+minute).length() == 1){
                                sMinute = "0" + minute;
                            } else {
                                sMinute = "" + minute;
                            }

                            String sHourOfDay;

                            if((""+hourOfDay).length() == 1){
                                sHourOfDay = "0" + hourOfDay;
                            } else {
                                sHourOfDay = "" + hourOfDay;
                            }

                            mulai.setText(sHourOfDay + ":" + sMinute);
                        }
                    };
                    new TimePickerDialog(JadwalController.this, d, calendarMulai.get(Calendar.HOUR_OF_DAY), calendarMulai.get(Calendar.MINUTE), true).show();
                }
            });

            bSelesai.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener(){

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            view.setIs24HourView(true);
                            calendarSelesai.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendarSelesai.set(Calendar.MINUTE, minute);

                            String sMinute;

                            if((""+minute).length() == 1){
                                sMinute = "0" + minute;
                            } else {
                                sMinute = "" + minute;
                            }

                            String sHourOfDay;

                            if((""+hourOfDay).length() == 1){
                                sHourOfDay = "0" + hourOfDay;
                            } else {
                                sHourOfDay = "" + hourOfDay;
                            }

                            selesai.setText(sHourOfDay + ":" + sMinute);
                        }
                    };
                    new TimePickerDialog(JadwalController.this, d, calendarSelesai.get(Calendar.HOUR_OF_DAY), calendarSelesai.get(Calendar.MINUTE), true).show();
                }
            });

            final ArrayList<Kelas> arrayKelas = (new MenjabatController(username)).getMenjabatKelas();
            final ArrayList<Integer> temp_id = new ArrayList<Integer>();
            final String[] daftarKelas = new String[arrayKelas.size()];

            for(int i=0; i<arrayKelas.size(); i++){
                daftarKelas[i] = arrayKelas.get(i).getNama();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, daftarKelas);

            id_kelas.setAdapter(adapter);
            id_kelas.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                   int arg2, long arg3) {
                            int position = id_kelas.getSelectedItemPosition();
                            temp_id.add(arrayKelas.get(position).getId());
                            Toast.makeText(getApplicationContext(),"You have selected "+daftarKelas[position],Toast.LENGTH_LONG).show();
                            // TODO Auto-generated method stub
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    }
            );

            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String sKelas = temp_id.get(0).toString();
                    String sJudul = judul.getText().toString();

                    String sRuangan = ruangan.getText().toString();
                    String sDeskripsi = deskripsi.getText().toString();

                    if(validasi(sKelas, sJudul, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), mulai.getText().toString(), selesai.getText().toString(), sRuangan, sDeskripsi)){
                        String sTanggal = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                        String sMulai = mulai.getText().toString() + ":00";
                        String sSelesai = selesai.getText().toString() + ":00";

                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("Id_Kelas", sKelas));
                        nameValuePairs.add(new BasicNameValuePair("Username", username));
                        nameValuePairs.add(new BasicNameValuePair("Judul", sJudul));
                        nameValuePairs.add(new BasicNameValuePair("Tanggal", sTanggal));
                        nameValuePairs.add(new BasicNameValuePair("W_Mulai", sMulai));
                        nameValuePairs.add(new BasicNameValuePair("W_Akhir", sSelesai));
                        nameValuePairs.add(new BasicNameValuePair("Ruangan", sRuangan));
                        nameValuePairs.add(new BasicNameValuePair("Deskripsi", sDeskripsi));

                        addJadwal(nameValuePairs);

                        Intent showDetails = new Intent(getApplicationContext(), PilihanController.class);
                        showDetails.putExtra("Username", username);
                        startActivity(showDetails);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"Pastikan isian lengkap",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public int getJumlahMenghadiri(int id_jadwal, int id_kelas) throws JSONException {

        String url = "http://ppl-a08.cs.ui.ac.id/jadwal.php?fun=jadwalJumlah&Id="+id_jadwal;

        return (new JSONParser()).getJSONObjectFromUrl(url).getInt("Count");
    }

    public boolean validasi(String kelas, String judul, int tahun, int bulan, int tanggal, String mulai, String selesai, String ruangan, String deskripsi){
        if(kelas.equals("") || kelas == null)
            return false;
        if(judul.equals("") || judul == null)
            return false;
        if(mulai.equals("") || mulai == null)
            return false;
        if(selesai.equals("") || selesai == null)
            return false;
        if((Integer)tahun == null)
            return false;
        if((Integer)bulan == null)
            return false;
        if((Integer)tanggal == null)
            return false;
        if(ruangan.equals("") || ruangan == null)
            return false;
        if(deskripsi.equals("") || deskripsi == null)
            return false;
        return true;
    }
    public boolean cekMenghadiri(int id_jadwal, int id_kelas){
        String url = "http://ppl-a08.cs.ui.ac.id/jadwal.php?fun=cekHadir&Id="+id_jadwal+"&Id_Kelas="+id_kelas+"&Username="+username;
        return (new JSONParser()).getJSONObjectFromUrl(url) == null;
    }

    public void addMenghadiri(int id_jadwal, int id_kelas){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("Id_Kelas", Integer.toString(id_kelas)));
        nameValuePairs.add(new BasicNameValuePair("Username", username));
        nameValuePairs.add(new BasicNameValuePair("Id_Jadwal", Integer.toString(id_jadwal)));

        InputStream is = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("http://ppl-a08.cs.ui.ac.id/createMenghadiri.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            is = entity.getContent();

            String msg = "Berhasil";
            //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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

    public JSONArray getListJadwal(String username){
        //new GetAllJadwalTask().execute(username);
        JSONParser jsonParser = new JSONParser();
        String url = "http://ppl-a08.cs.ui.ac.id/jadwal.php?fun=jadwalList&username=" + username;
        JSONArray jsonArray = jsonParser.getJSONArrayFromUrl(url);
//        if(jsonArray != null)
//            setListAdapter(jsonArray);
        return jsonArray;
    }

    public void getDetailJadwal(JadwalDetail jadwalDetail){
        new GetJadwalDetails().execute(jadwalDetail);
        //return (new ProfileController()).getMahasiswa(jadwalDetail.getU);
    }

    public void addJadwal (List<NameValuePair> nameValuePairs){
        InputStream is = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("http://ppl-a08.cs.ui.ac.id/createJadwal.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            is = entity.getContent();

            String msg = "Berhasil";
            //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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

    private class GetJadwalDetails extends AsyncTask<JadwalDetail,Long,JSONObject>
    {
        private JadwalDetail jadwalDetail;
        @Override
        protected JSONObject doInBackground(JadwalDetail... params) {

            // it is executed on Background thread
            this.jadwalDetail = params[0];
            String url = "http://ppl-a08.cs.ui.ac.id/jadwal.php?fun=jadwalDetail&Id="+jadwalDetail.getJadwalID()+"&Id_Kelas="+jadwalDetail.getKelasID();
            return (new JSONParser()).getJSONObjectFromUrl(url);
        }

        @Override
        protected void onPostExecute(JSONObject jadwal) {
            try {
                Jadwal kJadwal = new Jadwal(jadwal.getInt("Id"), jadwal.getString("Judul"), jadwal.getString("Tanggal"), jadwal.getString("W_Mulai"),
                        jadwal.getString("W_Akhir"), jadwal.getString("Ruangan"), jadwal.getString("Deskripsi"), jadwal.getInt("Id_Kelas"),
                        jadwal.getString("Username"));
                jadwalDetail.getJudul().setText(kJadwal.getJudul());

                String username = kJadwal.getUsername();
                jadwalDetail.getAsisten().setText(username);

                //new GetAsisten(jadwalDetail).execute(username);
                Mahasiswa m = (new ProfileController()).getMahasiswa(username);
                jadwalDetail.getHp().setText(m.getHp());

                jadwalDetail.getTanggal().setText(kJadwal.getTanggal());
                jadwalDetail.getWaktu().setText(kJadwal.getWaktuMulai() + " - " + kJadwal.getWaktuAkhir());
                jadwalDetail.getRuangan().setText(kJadwal.getRuangan());
                jadwalDetail.getDeskripsi().setText(kJadwal.getDeskripsi());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class JadwalDetail{
        private TextView judul;
        private TextView asisten;
        private TextView hp;
        private TextView tanggal;
        private TextView waktu;

        private TextView ruangan;
        private TextView deskripsi;

        private int jadwalID;
        private int kelasID;

        private ImageView picture;

        JadwalDetail(TextView judul, TextView asisten, TextView hp, TextView tanggal, TextView waktu, TextView ruangan, TextView deskripsi, int jadwalID, int kelasID, ImageView picture) {
            this.judul = judul;
            this.asisten = asisten;
            this.hp = hp;
            this.tanggal = tanggal;
            this.waktu = waktu;
            this.ruangan = ruangan;
            this.deskripsi = deskripsi;
            this.jadwalID = jadwalID;
            this.kelasID = kelasID;
            this.picture = picture;
        }

        public TextView getJudul() {
            return judul;
        }

        public void setJudul(TextView judul) {
            this.judul = judul;
        }

        public TextView getAsisten() {
            return asisten;
        }

        public void setAsisten(TextView asisten) {
            this.asisten = asisten;
        }

        public TextView getHp() {
            return hp;
        }

        public void setHp(TextView hp) {
            this.hp = hp;
        }

        public TextView getTanggal() {
            return tanggal;
        }

        public void setTanggal(TextView tanggal) {
            this.tanggal = tanggal;
        }

        public TextView getWaktu() {
            return waktu;
        }

        public void setWaktu(TextView waktu) {
            this.waktu = waktu;
        }

        public TextView getRuangan() {
            return ruangan;
        }

        public void setRuangan(TextView ruangan) {
            this.ruangan = ruangan;
        }

        public TextView getDeskripsi() {
            return deskripsi;
        }

        public void setDeskripsi(TextView deskripsi) {
            this.deskripsi = deskripsi;
        }

        public int getJadwalID() {
            return jadwalID;
        }

        public void setJadwalID(int jadwalID) {
            this.jadwalID = jadwalID;
        }

        public int getKelasID() {
            return kelasID;
        }

        public void setKelasID(int kelasID) {
            this.kelasID = kelasID;
        }

        public ImageView getPicture() {
            return picture;
        }

        public void setPicture(ImageView picture) {
            this.picture = picture;
        }
    }
}
