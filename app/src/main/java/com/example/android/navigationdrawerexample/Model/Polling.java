package com.example.android.navigationdrawerexample.Model;


import java.util.List;

/**
 * Created by lenovo on 3/18/2015.
 */
public class Polling {
    private String id;
    private String idKelas;
    private String username;
    private List<Pilihan> pilihan;

    public Polling(String id, String idKelas, String username, List<Pilihan> pilihan) {
        this.id = id;
        this.idKelas = idKelas;
        this.username = username;
        this.pilihan = pilihan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdKelas() {
        return idKelas;
    }

    public void setIdKelas(String idKelas) {
        this.idKelas = idKelas;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Pilihan> getPilihan() {
        return pilihan;
    }

    public void setPilihan(List<Pilihan> pilihan) {
        this.pilihan = pilihan;
    }
}
