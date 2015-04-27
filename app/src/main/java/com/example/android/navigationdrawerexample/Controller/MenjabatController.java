package com.example.android.navigationdrawerexample.Controller;

import android.os.StrictMode;

import com.example.android.navigationdrawerexample.Model.Kelas;
import com.example.android.navigationdrawerexample.Menjabat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 4/5/2015.
 */
public class MenjabatController {
    private String username;
    private ArrayList<Menjabat> allMenjabat;

    public MenjabatController(String username){
        this.username = username;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void setMenjabat(){
        String url = "http://ppl-a08.cs.ui.ac.id/menjabat.php?fun=all";
        ArrayList<Menjabat> temp = new ArrayList<Menjabat>();

        JSONArray jsonAllMenjabat = (new JSONParser()).getJSONArrayFromUrl(url);
        int length = jsonAllMenjabat.length();

        for(int i=0; i<length; i++){
            try {
                JSONObject jsonObject = jsonAllMenjabat.getJSONObject(i);
                temp.add(new Menjabat(jsonObject.getString("Username"), jsonObject.getInt("Id_Kelas")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } this.allMenjabat = temp;
    }

    public ArrayList<Menjabat> getAllMenjabat(){
        return this.allMenjabat;
    }

    //pake parameter string username kalo udh bisa liat profile orang lain
    public ArrayList<Menjabat> getListMenjabat(){
        String url = "http://ppl-a08.cs.ui.ac.id/menjabat.php?fun=getMenjabat&username=" + this.username;
        JSONArray role = (new JSONParser()).getJSONArrayFromUrl(url);

        int length = role.length();
        ArrayList<Menjabat> menjabat = new ArrayList<Menjabat>();

        for(int i=0; i<length; i++){
            try {
                JSONObject jsonObject = role.getJSONObject(i);
                menjabat.add(new Menjabat(jsonObject.getString("Username"), jsonObject.getInt("Id_Kelas")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } return menjabat;
    }

    //pake parameter string username kalo udh bisa liat profile orang lain
    public ArrayList<Kelas> getMenjabatKelas(){
        ArrayList<Menjabat> listMenjabat = getListMenjabat();

        String listIdKelas = "(";

        for (int i=0; i<listMenjabat.size()-1; i++){
            listIdKelas += listMenjabat.get(i).getIdKelas() + ",";
        } listIdKelas += listMenjabat.get(listMenjabat.size()-1).getIdKelas() + ")";

        String url = "http://ppl-a08.cs.ui.ac.id/menjabat.php?fun=getKelas&Id_Kelas=" + listIdKelas;

        JSONArray jsonkelas = (new JSONParser()).getJSONArrayFromUrl(url);

        int length = jsonkelas.length();
        ArrayList<Kelas> kelas = new ArrayList<Kelas>();

        for(int i=0; i<length; i++){
            try {
                JSONObject jsonObject = jsonkelas.getJSONObject(i);
                kelas.add((new KelasController()).createKelas(jsonObject.getInt("Id"), jsonObject.getInt("Id_Semester"), jsonObject.getString("Nama")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } return kelas;
    }

    //tinggal getAsisten (iterasi 2)
}
