package com.example.ruben.prototype;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;



public class MapsPrototypeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public int conections=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_prototype);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra(PrototypeActivity.EXTRA_MESSAGE);

        System.out.print("CALLING SERVICE.....");
        new LongRunningGetIO().execute();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        String message = intent.getStringExtra(PrototypeActivity.EXTRA_MESSAGE);
        //int nconections = 51;
        //message.

        //DRAWING CIRCULES



    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }

            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {

            Intent intent = getIntent();
            String location = intent.getStringExtra(PrototypeActivity.EXTRA_MESSAGE);
            //String location = "";


/*
           location

            if (message.equals("CAB")){
                //System.out.println("Selected values : CAB");
                location = "CAB";
            }

            if (message.equals("CMPUT")){
                //System.out.println("Selected values : CMPUT");
                location = "c";
            }*/


            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://sepehr-rpi.crabdance.com/api.php/Complete?filter=Building,eq,"+location);

            String text = null;
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();
            }

            return text;
        }


        protected void onPostExecute(String results) {

            Intent intent = getIntent();
            String message = intent.getStringExtra(PrototypeActivity.EXTRA_MESSAGE);

            if (results!=null) {
                //EditText et = (EditText)findViewById(R.id.my_edit);
                System.out.println(results);

                try {
                    JSONObject jsonRootObject = new JSONObject(results);

                    JSONObject  jsonRootObject2 = new JSONObject(jsonRootObject.getJSONObject("Complete").toString());
                    //Get the instance of JSONArray that contains JSONObjects
                    JSONArray jsonArray = jsonRootObject2.optJSONArray("records");

                    //Iterate the jsonArray and print the info of JSONObjects
                    for(int i=0; i < jsonArray.length(); i++){

                        JSONArray obj = jsonArray.getJSONArray(i);
                        //System.out.println("values : " + obj.getString(11));
                        conections = conections + Integer.parseInt(obj.getString(11));

                    }
                   // System.out.println("Total number of conections : " + conections);

                } catch (JSONException e) {e.printStackTrace();}

            }


            if (message.equals("CAB"))
            {
                System.out.println("N. of conections on CAB : "+ conections);

                LatLng cameron = new LatLng(53.5269274, -113.523687);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameron, 18));

                mMap.addMarker(new MarkerOptions()
                        .title("Cameron Library")
                        .snippet(message)
                        .position(cameron));

                //mMap.getCameraPosition().

                //     if (nconections > 50)
                //  {
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(53.5269274, -113.523687))
                        .radius(500); // In meters
                // .strokeColor(5)
                // .fillColor(5);

                Circle circle = mMap.addCircle(circleOptions);

// Get back the mutable Circle
                /*
                Circle circle = mMap.addCircle(circleOptions);


                var cityCircle = new google.maps.Circle({
                        strokeColor: '#FF0000',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#FF0000',
                    fillOpacity: 0.35,
                    map: map,
                    center: citymap[city].center,
                    radius: Math.sqrt(citymap[city].population) * 100
                });*/

                //    }

            }

            if (message.equals("CompScie"))
            {
                System.out.println("N. of conections on CompScie : "+ conections);
                LatLng cmput = new LatLng(53.5267106, -113.5271157);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cmput, 18));

                mMap.addMarker(new MarkerOptions()
                        .title("Computer Science Building")
                        .snippet(message)
                        .position(cmput));
            }

            //TextView textView = new TextView(this);
            //textView.setTextSize(40);
            //textView.setText(message);
            //RelativeLayout layout = (RelativeLayout) findViewById(R.id.option1_button);
            //layout.addView(textView);

            // Add a marker in Sydney and move the camera

            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in " + message));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        }

    }


}
