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

package com.example.android.navigationdrawerexample.Controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.navigationdrawerexample.ConfirmProfile;
import com.example.android.navigationdrawerexample.ForumQA;
import com.example.android.navigationdrawerexample.ForumRequest;
import com.example.android.navigationdrawerexample.FragmentOne;
import com.example.android.navigationdrawerexample.MainActivity;
import com.example.android.navigationdrawerexample.Model.Mahasiswa;
import com.example.android.navigationdrawerexample.R;
import com.example.android.navigationdrawerexample.RequestRole;
import com.example.android.navigationdrawerexample.ViewPicture;

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
import java.util.List;

public class PilihanController extends Activity implements ConfirmProfile.ConfirmProfileListener{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList, getAllRole;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    CustomDrawerAdapter adapter;
    Fragment fragment;

    private int count;

    private String username;

    Button fetch;
    TextView text;
    EditText et;

    List<DrawerItem> dataList;

    private ListView GetAllJadwalListView, ListRole;
    private JSONArray jsonArray;
    private View view,rootView;
    Mahasiswa mahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.username = getIntent().getStringExtra("Username");

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        fetch = (Button) findViewById(R.id.fetch);
        text = (TextView) findViewById(R.id.text);
        et = (EditText) findViewById(R.id.et);

        dataList = new ArrayList<DrawerItem>();

        dataList.add(new DrawerItem("Jadwal Asistensi", R.drawable.ic_jadwal));
        dataList.add(new DrawerItem("Profil", R.drawable.ic_person_grey));
        dataList.add(new DrawerItem("Kelas", R.drawable.ic_kelas));
        dataList.add(new DrawerItem("Forum", R.drawable.ic_forum));
        dataList.add(new DrawerItem("Admin", R.drawable.ic_admin));
        dataList.add(new DrawerItem("Logout", R.drawable.ic_logout));

        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
                dataList);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        /*mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));*/
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (fragment.getArguments().getInt("role")==1){
            /*SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.profile_pref),
                    MODE_PRIVATE);

            String defaultS = "";

            String path = sharedPreferences.getString(getString(R.string.path_foto), defaultS);*/
            ProfileController profileController = new ProfileController(username);

            mahasiswa = profileController.getMahasiswa(username);
            if (mahasiswa.getPath() != null)
                ((ImageButton) rootView.findViewById(R.id.foto_profil)).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(mahasiswa.getPath())));
            //new GetAllRole().execute(username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    /**
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        InputStream is = null;
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.profile_pref),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String sNama = ((EditText) rootView.findViewById(R.id.nama_mahasiswa)).getText().toString();
        String sNPM = ((EditText) rootView.findViewById(R.id.npm_mahasiswa)).getText().toString();
        String sEmail = ((EditText) rootView.findViewById(R.id.email_mahasiswa)).getText().toString();
        String sNomor = ((EditText) rootView.findViewById(R.id.nohp_mahasiswa)).getText().toString();

        editor.putString(getString(R.string.nama_pref), sNama);
        editor.putString(getString(R.string.npm_pref), sNPM);
        editor.putString(getString(R.string.email_pref), sEmail);
        editor.putString(getString(R.string.hp_pref), sNomor);
        editor.commit();

        ((EditText) rootView.findViewById(R.id.nama_mahasiswa)).setText(sNama);
        ((EditText) rootView.findViewById(R.id.npm_mahasiswa)).setText(sNPM);
        ((EditText) rootView.findViewById(R.id.email_mahasiswa)).setText(sEmail);
        ((EditText) rootView.findViewById(R.id.nohp_mahasiswa)).setText(sNomor);

        ((TextView) rootView.findViewById(R.id.nama_mahasiswaText)).setText(sNama);
        ((TextView) rootView.findViewById(R.id.npm_mahasiswaText)).setText(sNPM);
        ((TextView) rootView.findViewById(R.id.email_mahasiswaText)).setText(sEmail);
        ((TextView) rootView.findViewById(R.id.nohp_mahasiswaText)).setText(sNomor);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("Nama", sNama));
        nameValuePairs.add(new BasicNameValuePair("Username", username));
        nameValuePairs.add(new BasicNameValuePair("NPM", sNPM));
        nameValuePairs.add(new BasicNameValuePair("Email", sEmail));
        nameValuePairs.add(new BasicNameValuePair("Nomor", sNomor));

        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("http://ppl-a08.cs.ui.ac.id/profile.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            String msg = "Profile Updated";
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

        ((EditText) rootView.findViewById(R.id.nama_mahasiswa)).setVisibility(View.INVISIBLE);
        ((EditText) rootView.findViewById(R.id.npm_mahasiswa)).setVisibility(View.INVISIBLE);
        ((EditText) rootView.findViewById(R.id.email_mahasiswa)).setVisibility(View.INVISIBLE);
        ((EditText) rootView.findViewById(R.id.nohp_mahasiswa)).setVisibility(View.INVISIBLE);
        ((TextView) rootView.findViewById(R.id.nama_mahasiswaText)).setVisibility(View.VISIBLE);
        ((TextView) rootView.findViewById(R.id.npm_mahasiswaText)).setVisibility(View.VISIBLE);
        ((TextView) rootView.findViewById(R.id.email_mahasiswaText)).setVisibility(View.VISIBLE);
        ((TextView) rootView.findViewById(R.id.nohp_mahasiswaText)).setVisibility(View.VISIBLE);
        ((ImageView) rootView.findViewById(R.id.buttonDone)).setVisibility(View.INVISIBLE);
        ((ImageView) rootView.findViewById(R.id.buttonProfile)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        /*Fragment fragment = new PlanetFragment();*/
        fragment = null;
        Bundle args = new Bundle();

        switch (position) {
            case 0:
                ProfileController profileController = new ProfileController(username);
                int status = 0;

                if(profileController.punyaProfile()) {
                    profileController.addMahasiswa(username);
                } else if(profileController.cekAsdos()) {
                    status = 1;
                } else if(profileController.cekAdmin()) {
                    status = 2;
                }
                final int finalStatus = status;
                fragment = new Fragment(){
                    @Override
                    public void onResume() {
                        super.onResume();
                        new GetAllJadwalTask().execute(username);
                    }

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                             Bundle savedInstanceState) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        View view = inflater.inflate(R.layout.list_jadwal, container, false);
                        GetAllJadwalListView = (ListView) view.findViewById(R.id.GetAllJadwalListView);
                        ImageView buat = (ImageView) view.findViewById(R.id.button);
                        //Button pilihan = (Button) view.findViewById(R.id.button9);
                        if(finalStatus == 0)
                            buat.setVisibility(View.GONE);
                        else
                            buat.setVisibility(View.VISIBLE);

                        new GetAllJadwalTask().execute(username);

                        GetAllJadwalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    // GEt the customer which was clicked
                                    JSONObject mahasiswaClicked = jsonArray.getJSONObject(position);
                                    int jadwalp = mahasiswaClicked.getInt("Id");
                                    int kelasp = mahasiswaClicked.getInt("Id_kelas");

//                        Toast.makeText(getApplicationContext(), "-j=" + jadwalp + "-k=" + kelasp
//                                , Toast.LENGTH_LONG).show();

                                    // Send Customer ID
                                    Intent showDetails = new Intent(getActivity(), JadwalController.class);
                                    showDetails.putExtra("JadwalID", jadwalp);
                                    showDetails.putExtra("KelasID", kelasp);
                                    showDetails.putExtra("Username", username);
                                    showDetails.putExtra("View", "detailJadwal");

                                    //Toast.makeText(getApplicationContext(), "seharusnya abis ini ke detail", Toast.LENGTH_LONG).show();

                                    startActivity(showDetails);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        buat.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                Intent showDetails = new Intent(getActivity(), JadwalController.class);
                                //asumsi username gak null
                                showDetails.putExtra("Username", username);
                                showDetails.putExtra("View", "createJadwal");

                                startActivity(showDetails);
                            }
                        });
                        return view;
                    }

//                    public  void setListAdapter(JSONArray jsonArray) {
//                        this.jsonArray = jsonArray;
//                        this.GetAllJadwalListView.setAdapter(new ListJadwalAdapter(jsonArray, getActivity()));
//                    }
                };
                args.putString(FragmentOne.ITEM_NAME, dataList.get(position)
                        .getItemName());
                args.putInt(FragmentOne.IMAGE_RESOURCE_ID, dataList.get(position)
                        .getImgResID());
                args.putInt("role",2);
                break;
            case 1:
                fragment = new RoleFragment();
                args.putInt("role",1);
                break;
            case 2:
                fragment = new Fragment(){
                    @Override
                    public void onResume() {
                        super.onResume();
                        new GetAllEnrollTask().execute(username);
                    }

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                             Bundle savedInstanceState) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        view = inflater.inflate(R.layout.list_enroll, container, false);

                        //GetAllEnrollListView = (ListView) view.findViewById(R.id.GetAllJadwalListView);
                        ImageView enroll = (ImageView) view.findViewById(R.id.button);
                        new GetAllEnrollTask().execute(username);

                        enroll.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                Intent showDetails = new Intent(getActivity(), EnrollController.class);
                                //asumsi username gak null
                                showDetails.putExtra("Username", username);
                                startActivity(showDetails);
                            }
                        });
                        return view;
                    }

                };
                args.putInt("role",0);
                break;
            case 3:
                fragment = new Fragment(){
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                             Bundle savedInstanceState){
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        View view = inflater.inflate(R.layout.choose_forum, container, false);

                        Button buttonForumReply = (Button) view.findViewById(R.id.choose_forum_1);
                        Button buttonForumForum = (Button) view.findViewById(R.id.choose_forum_2);

                        buttonForumReply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ForumQA.class);
                                intent.putExtra("Username", username);
                                startActivity(intent);
                            }
                        });

                        buttonForumForum.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent (getActivity(), ForumRequest.class);
                                intent.putExtra("Username", username);
                                startActivity(intent);
                            }
                        });

                        return view;
                    }

                };
                args.putInt("role", 3);
                break;
            case 4:
                ProfileController profileController2 = new ProfileController(username);
                if(profileController2.cekAdmin()) {
                    fragment = new Fragment() {

                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                 Bundle savedInstanceState) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);

                            view = inflater.inflate(R.layout.admin, container, false);

                            //GetAllEnrollListView = (ListView) view.findViewById(R.id.GetAllJadwalListView);
                            Button kelas = (Button) view.findViewById(R.id.button9);
                            Button role = (Button) view.findViewById(R.id.button10);

                            kelas.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    Intent showDetails = new Intent(getActivity(), KelasController.class);
                                    //asumsi username gak null
                                    showDetails.putExtra("Username", username);
                                    showDetails.putExtra("View", "listKelas");
                                    startActivity(showDetails);
                                }
                            });

                            role.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    Intent showDetails = new Intent(getActivity(), RoleController.class);
                                    //asumsi username gak null
                                    showDetails.putExtra("Username", username);
                                    startActivity(showDetails);
                                }
                            });
                            return view;
                        }
                    };
                } else {
                    fragment = new FragmentOne();
                    Toast.makeText(getApplicationContext(), "Hei Anda Bukan Admin", Toast.LENGTH_LONG).show();
                }
                args.putInt("role", 4);
                break;
            case 5:
                Intent intent = new Intent (this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
        }
        /*args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);*/
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private class GetAllJadwalTask extends AsyncTask<String, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {

            // it is executed on Background thread
            JSONParser jsonParser = new JSONParser();
            String url = "http://ppl-a08.cs.ui.ac.id/jadwal.php?fun=jadwalList&username=" + params[0];
            return (jsonParser.getJSONArrayFromUrl(url));
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray != null)
                setListAdapter(jsonArray);
        }
    }

    public void setListAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.GetAllJadwalListView.setAdapter(new ListJadwalAdapter(jsonArray, this));
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public class RoleFragment extends Fragment {

        public RoleFragment() {
            // Empty constructor required for fragment subclasses
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.view_profile_personal, container, false);

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.profile_pref),
                    MODE_PRIVATE);

            ProfileController profileController= new ProfileController(username);
            mahasiswa = profileController.getMahasiswa(username);

            /*String defaultS = "";
            int defaultI = 0;
            String name = sharedPreferences.getString(getString(R.string.nama_pref), defaultS);
            String np = sharedPreferences.getString(getString(R.string.npm_pref), defaultS);
            String mail = sharedPreferences.getString(getString(R.string.email_pref), defaultS);
            String hp = sharedPreferences.getString(getString(R.string.hp_pref), defaultS);
            String path = sharedPreferences.getString(getString(R.string.path_foto), defaultS);
            int stats = sharedPreferences.getInt(getString(R.string.status_pref), defaultI);*/

            if (mahasiswa.getUsername() != null){
                ((TextView) rootView.findViewById(R.id.nama_mahasiswaText)).setText(mahasiswa.getName());
                ((TextView) rootView.findViewById(R.id.npm_mahasiswaText)).setText(mahasiswa.getNpm());
                ((TextView) rootView.findViewById(R.id.email_mahasiswaText)).setText(mahasiswa.getEmail());
                ((TextView) rootView.findViewById(R.id.nohp_mahasiswaText)).setText(mahasiswa.getHp());

                ((EditText) rootView.findViewById(R.id.nama_mahasiswa)).setText(mahasiswa.getName());
                ((EditText) rootView.findViewById(R.id.npm_mahasiswa)).setText(mahasiswa.getNpm());
                ((EditText) rootView.findViewById(R.id.email_mahasiswa)).setText(mahasiswa.getEmail());
                ((EditText) rootView.findViewById(R.id.nohp_mahasiswa)).setText(mahasiswa.getHp());

                if (mahasiswa.getPath() != null)
                    ((ImageButton) rootView.findViewById(R.id.foto_profil)).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(mahasiswa.getPath())));
                ListRole = (ListView) rootView.findViewById(R.id.list_role);

                new GetAllRole().execute(username);
            }else{
                ((TextView) rootView.findViewById(R.id.nama_mahasiswaText)).setText("");
                ((TextView) rootView.findViewById(R.id.npm_mahasiswaText)).setText("");
                ((TextView) rootView.findViewById(R.id.email_mahasiswaText)).setText("");
                ((TextView) rootView.findViewById(R.id.nohp_mahasiswaText)).setText("");

                ((EditText) rootView.findViewById(R.id.nama_mahasiswa)).setText("");
                ((EditText) rootView.findViewById(R.id.npm_mahasiswa)).setText("");
                ((EditText) rootView.findViewById(R.id.email_mahasiswa)).setText("");
                ((EditText) rootView.findViewById(R.id.nohp_mahasiswa)).setText("");
            }


            rootView.findViewById(R.id.foto_profil).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ViewPicture.class);
                    intent.putExtra("Username", username);
                    startActivity(intent);
                }
            });

            rootView.findViewById(R.id.buttonProfile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((EditText) rootView.findViewById(R.id.nama_mahasiswa)).setVisibility(View.VISIBLE);
                    ((EditText) rootView.findViewById(R.id.npm_mahasiswa)).setVisibility(View.VISIBLE);
                    ((EditText) rootView.findViewById(R.id.email_mahasiswa)).setVisibility(View.VISIBLE);
                    ((EditText) rootView.findViewById(R.id.nohp_mahasiswa)).setVisibility(View.VISIBLE);
                    ((TextView) rootView.findViewById(R.id.nama_mahasiswaText)).setVisibility(View.INVISIBLE);
                    ((TextView) rootView.findViewById(R.id.npm_mahasiswaText)).setVisibility(View.INVISIBLE);
                    ((TextView) rootView.findViewById(R.id.email_mahasiswaText)).setVisibility(View.INVISIBLE);
                    ((TextView) rootView.findViewById(R.id.nohp_mahasiswaText)).setVisibility(View.INVISIBLE);
                    ((ImageView) rootView.findViewById(R.id.buttonDone)).setVisibility(View.VISIBLE);
                    ((ImageView) rootView.findViewById(R.id.buttonProfile)).setVisibility(View.INVISIBLE);
                }
            });

            rootView.findViewById(R.id.buttonDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmProfile dialog = ConfirmProfile.newInstance();
                    dialog.show(getFragmentManager(), "ConfirmProfile");
                }

            });

            rootView.findViewById(R.id.buttonRole).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RequestRole.class);
                    startActivity(intent);
                }
            });

            return rootView;
        }
    }

    private class GetAllRole extends AsyncTask<String,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params) {

            // it is executed on Background thread
            JSONParser jsonParser = new JSONParser();
            String url = "http://ppl-a08.cs.ui.ac.id/role.php?fun=listrole&username=" + params[0];
            return (jsonParser.getJSONArrayFromUrl(url));
        } 

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray != null)
                setRoleAdapter(jsonArray);
        }
    }

    public  void setRoleAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        this.ListRole.setAdapter(new ListRoleAdapter(jsonArray, this));
    }


    private class GetAllEnrollTask extends AsyncTask<String,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params) {
            // it is executed on Background thread
            JSONParser jsonParser = new JSONParser();
            String url = "http://ppl-a08.cs.ui.ac.id/enroll.php?fun=enrollList&username=" + params[0];
            jsonArray = (jsonParser.getJSONArrayFromUrl(url));
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.container);
            if(jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        TextView textView = new TextView(getApplicationContext());
                        textView.setId(i);
                        //Toast.makeText(getApplicationContext(), jsonArray.getJSONObject(i).getString("Nama"), Toast.LENGTH_LONG).show();
                        textView.setText(jsonArray.getJSONObject(i).getString("Nama"));
                        /*textView.setTextColor(getResources().getColor(R.color.dim_foreground_material_dark));*/
                        linearLayout.addView(textView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }//Toast.makeText(getApplicationContext(), "Di for", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
