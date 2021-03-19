package com.example.countriesinasia;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.countriesinasia.adapter.Adapter;
import com.example.countriesinasia.database.CountryDatabase;
import com.example.countriesinasia.database.CountryEntity;
import com.example.countriesinasia.helper.Country;
import com.example.countriesinasia.helper.Language;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    List<Country> countryList;
    RecyclerView recyclerItem;
    Adapter adapter;
    EditText search;
    ProgressBar progressBar;
    TextView textView;
    boolean connected = false;
    ImageView deleteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        textView=findViewById(R.id.txtNotAvailable);
        progressBar = findViewById(R.id.progressBar);
        deleteData=findViewById(R.id.deleteData);
        progressBar.setVisibility(View.VISIBLE);
        recyclerItem = findViewById(R.id.recyclerItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerItem.setLayoutManager(layoutManager);
        recyclerItem.setHasFixedSize(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        countryList = new ArrayList<>();

        isConnected();
        try {
            countryList= new RetrieveData(this).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if ((countryList.size()!=0 && !connected) || (countryList.size()!=0 && connected)) {
                if (MainActivity.this!=null) {
                    progressBar.setVisibility(GONE);
                    adapter = new Adapter(countryList, MainActivity.this);
                    recyclerItem.setAdapter(adapter);
                    recyclerItem.setLayoutManager(layoutManager);
                }
            if (countryList.size()==0) {
                textView.setVisibility(VISIBLE);
            } else {
                textView.setVisibility(GONE);
            }
        } else if (connected && countryList.size()==0){
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://restcountries.eu/rest/v2/region/asia?fields=name;capital;flag;region;subregion;population;borders;languages";
            JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject countryData = response.getJSONObject(i);
                        List<String> borders = new ArrayList<>();
                        List<Language> languages = new ArrayList<>();
                        JSONArray border = countryData.getJSONArray("borders");
                        for (int j = 0; j < border.length(); j++) {
                            borders.add(border.getString(j));
                        }
                        JSONArray lang = countryData.getJSONArray("languages");
                        for (int k = 0; k < lang.length(); k++) {
                            JSONObject index = lang.getJSONObject(k);
                            if (!index.isNull("iso639_1")) {
                                Language languageObject = new Language(
                                        index.getString("iso639_1"),
                                        index.getString("iso639_2"),
                                        index.getString("name"),
                                        index.getString("nativeName")
                                );
                                languages.add(languageObject);
                            } else {
                                Language languageObject = new Language(
                                        "",
                                        index.getString("iso639_2"),
                                        index.getString("name"),
                                        index.getString("nativeName")
                                );
                                languages.add(languageObject);
                            }

                        }

                        StringBuilder borderString= new StringBuilder();
                        for (int l=0;l<borders.size();l++) {
                            borderString.append(borders.get(l));
                            if (l!=borders.size()-1) {
                                borderString.append(", ");
                            }
                        }

                        //Languages
                        StringBuilder langString= new StringBuilder();
                        for (int m=0;m<languages.size();m++) {
                            langString.append(languages.get(m).getName());
                            if (m!=languages.size()-1) {
                                langString.append(", ");
                            }
                        }

                        Country countryObject = new Country(
                                countryData.getString("name"),
                                countryData.getString("capital"),
                                countryData.getString("flag"),
                                countryData.getString("region"),
                                countryData.getString("subregion"),
                                countryData.getInt("population"),
                                borderString.toString(),
                                langString.toString()
                        );

                        CountryEntity countryEntity = new CountryEntity(
                                countryData.getString("name"),
                                countryData.getString("capital"),
                                countryData.getString("flag"),
                                countryData.getString("region"),
                                countryData.getString("subregion"),
                                countryData.getInt("population"),
                                borderString.toString(),
                                langString.toString()
                        );
                        countryList.add(countryObject);
                        adapter = new Adapter(countryList, MainActivity.this);
                        recyclerItem.setAdapter(adapter);
                        recyclerItem.setLayoutManager(layoutManager);

                        AsyncTask async = new DBAsyncTask(MainActivity.this, countryEntity, 1).execute();
                        Object result = async.get();
                        /*if(Boolean.parseBoolean(String.valueOf(result))) {
                            Toast.makeText(this,"Country Added",Toast.LENGTH_SHORT).show();
                        }*/
                    }
                    progressBar.setVisibility(GONE);
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }, error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-type", "application/json");
                    return headers;
                }
            };
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonRequest);
        } else {
            textView.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
        }

        //Delete Entire Room Data
        deleteData.setOnClickListener(v -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Do yo really want to delete entire data?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                new DBAsyncTask(getApplicationContext(), null, 2).execute();
                try {
                    countryList= new RetrieveData(this).execute().get();
                    adapter = new Adapter(countryList, MainActivity.this);
                    recyclerItem.setAdapter(adapter);
                    recyclerItem.setLayoutManager(layoutManager);
                    textView.setVisibility(VISIBLE);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Data Deleted!", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {
                //Do Nothing
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        //Search for Country
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
                if (adapter.getItemCount() != 0)
                    textView.setVisibility(GONE);
                else
                    textView.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    //FILTER SEARCH

    private void filter(String text) {
        ArrayList<Country> filteredList = new ArrayList<>();

        for (Country country : countryList) {
            if (country.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(country);
            }
        }
        adapter.filterList(filteredList);
    }

    //Insert and Delete data
    class DBAsyncTask extends AsyncTask<Void,Void,Boolean> {

        Context context;
        CountryEntity countryEntity;
        int mode;

        public DBAsyncTask(Context context, CountryEntity countryEntity,int mode) {
            this.context=context;
            this.countryEntity=countryEntity;
            this.mode=mode;
        }

        CountryDatabase db= Room.databaseBuilder(MainActivity.this, CountryDatabase.class,"country-db").build();

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mode) {
                case 1:
                    //save data to room
                    db.countryDao().insertAllData(countryEntity);
                    db.close();
                    return true;
                case 2:
                    //delete data from room
                    db.countryDao().deleteEntireData();
                    db.close();
                    return true;
            }
            return false;
        }
    }

    //Check Connectivity
    private void isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }

    //Retrive entire data
    class RetrieveData extends AsyncTask<Void, Void, List<Country>>{

        Context context;

        public RetrieveData(Context context) {
            this.context=context;
        }

        @Override
        protected List<Country> doInBackground(Void... voids) {
            CountryDatabase db= Room.databaseBuilder(MainActivity.this, CountryDatabase.class,"country-db").build();
            return db.countryDao().retrieveData();
        }
    }

}