package com.example.ruben.prototype;

import android.content.Intent;
import android.graphics.Color;
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
    public int cabConections=0;
    public int compscieConections=0;
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

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://sepehr-rpi.crabdance.com/api.php/Complete?");

            //HttpGet httpGet = new HttpGet("http://sepehr-rpi.crabdance.com/api.php/Complete?filter=Building,eq,"+location);

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

                        if (obj.getString(1).equals(Keys.Cab_label))
                            cabConections = cabConections + Integer.parseInt(obj.getString(11));

                        if (obj.getString(1).equals(Keys.CompScie_label))
                            compscieConections = compscieConections + Integer.parseInt(obj.getString(11));

                    }
                   // System.out.println("Total number of conections : " + conections);

                } catch (JSONException e) {e.printStackTrace();}

            }

            if (message.equals(Keys.Cab_label))
            {
                LatLng cameron = new LatLng(Keys.Cab_lat, Keys.Cab_lon);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameron, 17));

                mMap.addMarker(new MarkerOptions()
                        .title("Cameron Library")
                        .snippet("Estimated number of people : " + cabConections)
                        .position(cameron));

                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(Keys.Cab_lat, Keys.Cab_lon))
                        .radius(30) // In meters
                        .strokeColor(Color.BLACK)
                        .strokeWidth(2);

                if (cabConections > 1000)
                    circleOptions.fillColor(0x55cc0000); //Red

                if (cabConections <= 1000 && cabConections >= 500 )
                    circleOptions.fillColor(0x55ffd700); //Yellow

                if (cabConections < 500 )
                    circleOptions.fillColor(0x5500ff00); //Green

                mMap.addCircle(circleOptions);

            }

            if (message.equals(Keys.CompScie_label))
            {
                //System.out.println("N. of conections on CompScie : "+ conections);
                LatLng cmput = new LatLng(Keys.CompScie_Lat, Keys.CompScie_Lon);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cmput, 17));

                mMap.addMarker(new MarkerOptions()
                        .title("Computer Science Building")
                        .snippet("Estimated number of people : " + compscieConections)
                        .position(cmput));

                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(Keys.CompScie_Lat, Keys.CompScie_Lon))
                        .radius(30) // In meters
                        .strokeColor(Color.BLACK)
                        .strokeWidth(2);

                if (compscieConections > 1000)
                    circleOptions.fillColor(0x55cc0000); //Red

                if (compscieConections <= 1000 && compscieConections >= 500 )
                    circleOptions.fillColor(0x55ffd700); //Yellow

                if (compscieConections < 500 )
                    circleOptions.fillColor(0x5500ff00); //Green

                mMap.addCircle(circleOptions);

            }

            //public static final double UofA_Lat = 53.5232189;
            //public static final double UofA_Lon = -113.5263186;

            if (message.equals(Keys.All_Label))
            {

                LatLng uofa = new LatLng(Keys.UofA_Lat, Keys.UofA_Lon);
                LatLng compscie = new LatLng(Keys.CompScie_Lat, Keys.CompScie_Lon);
                LatLng cab = new LatLng(Keys.Cab_lat, Keys.Cab_lon);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uofa, 15));

                mMap.addMarker(new MarkerOptions()
                        .title("Computer Science Building")
                        .snippet("Estimated number of people : " + compscieConections)
                        .position(compscie));

                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(Keys.CompScie_Lat, Keys.CompScie_Lon))
                        .radius(30) // In meters
                        .strokeColor(Color.BLACK)
                        .strokeWidth(2);

                if (compscieConections > 1000)
                    circleOptions.fillColor(0x55cc0000); //Red

                if (compscieConections <= 1000 && compscieConections >= 500 )
                    circleOptions.fillColor(0x55ffd700); //Yellow

                if (compscieConections < 500 )
                    circleOptions.fillColor(0x5500ff00); //Green

                mMap.addCircle(circleOptions);

                mMap.addMarker(new MarkerOptions()
                        .title("Cameron Library")
                        .snippet("Estimated number of people : " + cabConections)
                        .position(cab));

                CircleOptions circleOptions2 = new CircleOptions()
                        .center(new LatLng(Keys.Cab_lat, Keys.Cab_lon))
                        .radius(30) // In meters
                        .strokeColor(Color.BLACK)
                        .strokeWidth(2);

                if (cabConections > 1000)
                    circleOptions2.fillColor(0x55cc0000); //Red

                if (cabConections <= 1000 && cabConections >= 500 )
                    circleOptions2.fillColor(0x55ffd700); //Yellow

                if (cabConections < 500 )
                    circleOptions2.fillColor(0x5500ff00); //Green

                mMap.addCircle(circleOptions2);

            }

        }

    }


}
