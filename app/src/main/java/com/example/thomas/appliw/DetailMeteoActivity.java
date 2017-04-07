package com.example.thomas.appliw;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.thomas.appliw.Common.Common;
import com.example.thomas.appliw.Helper.Helper;
import com.example.thomas.appliw.Model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DetailMeteoActivity extends AppCompatActivity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_meteo);
        mListView = (ListView) findViewById(R.id.listview);

        List<Weather5d> rows = generateWeather();

        Weather5dAdapter adapter = new Weather5dAdapter(DetailMeteoActivity.this, rows);
        mListView.setAdapter(adapter);


        Intent intent = getIntent();
        if(intent != null) {
            //on récupère nos infos
            double lat = intent.getDoubleExtra("lat", 0.0);
            double lon = intent.getDoubleExtra("lon", 0.0);

            //on envoi la requette pour recevoir les previsions meteo sur 5jours
            //new GetWeather(lat, lon).execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lon),5));

            //TODO on traite et affiche sur la vue

        }

    }
    //TODO une row pour chaque heure/jour reçu du json
    private List<Weather5d> generateWeather(){
        List<Weather5d> row = new ArrayList<Weather5d>();
        row.add(new Weather5d(Color.BLACK, "Florent", "Mon premier tweet !"));
        row.add(new Weather5d(Color.BLUE, "Kevin", "C'est ici que ça se passe !"));
        row.add(new Weather5d(Color.GREEN, "Logan", "Que c'est beau..."));
        row.add(new Weather5d(Color.RED, "Mathieu", "Il est quelle heure ??"));
        row.add(new Weather5d(Color.GRAY, "Willy", "On y est presque"));
        return row;
    }

    //traitement
    private class GetWeather extends AsyncTask<String,Void,String> {

        private double lat;
        private double lon;

        GetWeather(double lat, double lon){
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new Gson();
            Type mType5d = new TypeToken<OpenWeatherMap>(){}.getType();
            //TODO creer ma propre class qui sera un objet de la même forme que le json retourné pour que GSON fasse la correspondance
            OpenWeatherMap openWeatherMap5d = gson.fromJson(s,mType5d);

        }

    }
    // ###############


}
