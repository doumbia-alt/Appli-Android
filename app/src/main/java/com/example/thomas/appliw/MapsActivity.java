package com.example.thomas.appliw;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thomas.appliw.Common.Common;
import com.example.thomas.appliw.Helper.Helper;
import com.example.thomas.appliw.Model.OpenWeatherMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    LayoutInflater inflater = null;
    Marker marker;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private LocationManager locationManager;
    private String provider;
    public static double latitude;
    public static double longitude;
    TextView tvTitle;

    //TODO rotation ecran garder location marker

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("latitude", latitude);
        outState.putDouble("longitude", longitude);
    }

    // ****************************************************************
    // *** Lancement de l'application                               ***
    // ****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*super.onCreate(savedInstanceState);
        if( savedInstanceState != null ) {
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");
        }*/

        //tes si les permissions ne sont pas acceptées
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                //on demande les permissions : appel methode pour afficher popup
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            System.out.println("location not available");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    // ****************************************************************

    public void onLocationChanged(Location location) {
        latitude = (double) (location.getLatitude());
        longitude = (double) (location.getLongitude());
        System.out.println("location changed to : "+latitude +" "+longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "GPS activé " + provider,
                Toast.LENGTH_SHORT).show();
    }


    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "GPS desactivé" + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onMapClick(LatLng latlng) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.######");

        new GetWeather(latlng.latitude, latlng.longitude).execute(Common.apiRequest(String.valueOf(latlng.latitude),String.valueOf(latlng.longitude),1));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(MyOnInfoWindowClickListener);//detection du click sur l'info view
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.######"); //pour n'afficher que # chiffres après la virgule

        LatLng currentloc = new LatLng(latitude, longitude);
        new GetWeather(currentloc.latitude, currentloc.longitude).execute(Common.apiRequest(String.valueOf(currentloc.latitude),String.valueOf(currentloc.longitude),1));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentloc));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) ); //set zoom 2 to 21

        mMap.setOnMapClickListener(this); //attente d'un click sur la map

        //appel de ma custom view
        mMap.setInfoWindowAdapter(new CustomInfoWindow());

    }

    //lors d'un click sur l'info du marker
    GoogleMap.OnInfoWindowClickListener MyOnInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener(){
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent monIntent = new Intent(MapsActivity.this, DetailMeteoActivity.class);

            //on récupère la latitude et longitude pour l'envoyer ) l'intent
            monIntent.putExtra("lat", marker.getPosition().latitude);
            monIntent.putExtra("lon", marker.getPosition().longitude);
            startActivity(monIntent);//on lance l'activity

        }
    };

    //affichage meteo
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
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            OpenWeatherMap openWeatherMap = gson.fromJson(s,mType);


            if (marker != null) {
                marker.remove();//suppression de l'ancien marqueur
            }

            marker= mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(openWeatherMap.getName()));
            marker.setTag(openWeatherMap);

            //on bouge et zoom la camera sur le pointeur
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) ); //set zoom 2 to 21

        }

    }

    //ma custom view
    class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{
        private final View myContentsView;

        CustomInfoWindow(){
            //appel du layout
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_content, null);
        }

        //contenu de la custom info window
        @Override
        public View getInfoContents(Marker marker) {
            OpenWeatherMap openWeatherMap = (OpenWeatherMap) marker.getTag();

            //on defini les labels
            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            TextView tvDate = ((TextView)myContentsView.findViewById(R.id.date));
            TextView tvMeteo = ((TextView)myContentsView.findViewById(R.id.meteo));
            TextView tvHumidite = ((TextView)myContentsView.findViewById(R.id.humidite));
            TextView tvTemp = ((TextView)myContentsView.findViewById(R.id.temp));
            TextView tvTempmin = ((TextView)myContentsView.findViewById(R.id.tempmin));
            TextView tvTempmax = ((TextView)myContentsView.findViewById(R.id.tempmax));
            TextView tvWind = ((TextView)myContentsView.findViewById(R.id.vent));
            ImageView imWeather = ((ImageView)myContentsView.findViewById(R.id.imageWeather));

            //on set les labels
            tvTitle.setText(openWeatherMap.getName());
            tvDate.setText((String.format("%s", Common.getDateNow())));
            tvMeteo.setText("Temps : "+ String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            tvHumidite.setText("Humidité : "+ String.format("%d%%",openWeatherMap.getMain().getHumidity()));
            tvTemp.setText("Température : "+ String.format("%.2f °C",openWeatherMap.getMain().getTemp()));
            tvWind.setText("Vent : "+ String.format("%.2f km/h",openWeatherMap.getWind().getSpeed())+ " "+ openWeatherMap.getWind().getDeg());
            tvTempmin.setText("min : "+ String.format("%.2f °C",openWeatherMap.getMain().getTemp_min()));
            tvTempmax.setText("max : "+ String.format("%.2f °C",openWeatherMap.getMain().getTemp_max()));

            //appel de la methode pour charger nos propres images de meteo
            imWeather.setImageDrawable(getIconFromApi(openWeatherMap.getWeather().get(0).getIcon()));

            TextView tvVoirplus = ((TextView)myContentsView.findViewById(R.id.voirplus));
            tvVoirplus.setText("> Voir les prévisions <");

            return myContentsView; //retourne la vue
        }

        @Override
        public View getInfoWindow(Marker marker) {

            return null;
        }

    }
    // ##################

    //methode pour retourner les images selon le string correspondant à la météo retourné par openWeathermapAPI
    public Drawable getIconFromApi(String icon){
        switch(icon){
            case "01d":
                return getResources().getDrawable(R.drawable.d01);
            case "01n":
                return getResources().getDrawable(R.drawable.n01);
            case "02d":
                return getResources().getDrawable(R.drawable.d02);
            case "02n":
                return getResources().getDrawable(R.drawable.n02);
            case "03d":
                return getResources().getDrawable(R.drawable.d03);
            case "03n":
                return getResources().getDrawable(R.drawable.n03);
            case "04d":
                return getResources().getDrawable(R.drawable.d04);
            case "04n":
                return getResources().getDrawable(R.drawable.n04);
            case "09d":
                return getResources().getDrawable(R.drawable.d09);
            case "09n":
                return getResources().getDrawable(R.drawable.n09);
            case "10d":
                return getResources().getDrawable(R.drawable.d10);
            case "10n":
                return getResources().getDrawable(R.drawable.n10);
            case "11d":
                return getResources().getDrawable(R.drawable.d11);
            case "11n":
                return getResources().getDrawable(R.drawable.n11);
            case "13d":
                return getResources().getDrawable(R.drawable.d13);
            case "13n":
                return getResources().getDrawable(R.drawable.n13);
            case "50d":
                return getResources().getDrawable(R.drawable.d50);
            case "50n":
                return getResources().getDrawable(R.drawable.n50);

        }
        return null;
    }
    // ###################################################################

    //Popup pour demander les permissions
    //TODO faire pour les autres permissions demandées par la meteo
    @Override
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // on a les permissions

                } else {
                    // on a pas les permissions
                }
                return;
            }

        }
    }
}
