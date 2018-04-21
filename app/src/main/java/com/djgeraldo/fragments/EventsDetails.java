package com.djgeraldo.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.EventsData;
import com.djgeraldo.phonemidea.CheckNetwork;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsDetails extends Fragment  implements OnMapReadyCallback {

    Context context;
    EventsData current;
    String title;
    ImageView calImage;
    TextView location,place,dateTimeText,time,description;
    double lat=0,longt=0;
    String latitude,longitude;
    Button map_location;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    String tag;
    GoogleMap gMap;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    ScrollView scrollview;
    LocationListener listener;

    public EventsDetails() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public EventsDetails(Context context,RelativeLayout header,String title, EventsData current,String longitude,String latitude,String tag)
    {
        this.context = context;
        this.header = header;
        this.current = current;
        this.title = title;
        this.longitude = longitude;
        Log.d("Longitude Constructor",longitude);
        Log.d("Latitude Constructor",latitude);
        this.latitude = latitude;
        this.tag = tag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events_details, container, false);
        scrollview = v.findViewById(R.id.scr);
        scrollview.setSmoothScrollingEnabled(true);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(title);
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                MainActivity.getMenuFragment();
//                frag = new Events(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
//            }
//        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
//            }
//        });
        if (CheckNetwork.isInternetAvailable(context))
        {

        }
        else
        {

        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M))
        {
            checkPermission();
        }
        String provider = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("Events2","Android id: "+androidId);
        Log.d("Events2","Net is connected: "+isConnected);
        calImage = v.findViewById(R.id.calImage);
        location = v.findViewById(R.id.location);
        location.setSelected(true);
        place = v.findViewById(R.id.place);
        place.setSelected(true);
        dateTimeText = v.findViewById(R.id.dateTimeText);
        time = v.findViewById(R.id.time);
        map_location = v.findViewById(R.id.map_location);
        description = v.findViewById(R.id.description);
        Glide.with(context)
                .load(current.geteImage())
                .placeholder(R.drawable.dj_gradlo_app_logo)
                .into(calImage);
        location.setText(current.geteTitle());
        place.setText(current.geteAddress());
        dateTimeText.setText(current.geteStart());
        time.setText(current.getTime());
        Spanned htmlAsSpanned = Html.fromHtml(current.geteDescription());
        description.setText(htmlAsSpanned);
//        SupportMapFragment mapFragment1 = (SupportMapFragment) findFragmentById(R.id.map);
//        mapFragment1.getMapAsync(Events2Fragment.this);
        map_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String provider = android.provider.Settings.Secure.getString(getContext().getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if ((Double.valueOf(longitude) == null && Double.valueOf(latitude) == 0) || !provider.contains("gps")) {
                    Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                } else {
                    Log.d("", "ELSE");
                    float destiLat = Float.parseFloat(String.valueOf(latitude));
                    Log.d("", "lat(" + latitude + ")");
                    float destiLong = Float.parseFloat(String.valueOf(longitude));
                    Log.d("", "long(" + longitude + ")");
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", Double.valueOf(latitude), Double.valueOf(longitude), destiLat, destiLong);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }

    public void checkPermission()
    {
        boolean hasPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if (hasPermission)
        {
            //Get the location
            ActivityCompat.requestPermissions(getActivity(),permissions,0);

        }
        else
        {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION},
//          5          0);


            //show the permission request dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("To receive relevant location based notifications you have to allow us access to your location.");
            builder.setTitle("Location Services");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, 0);
                }
            });

            builder.show();

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map ready","");
        gMap = googleMap;
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            listener = new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    lat = location.getLatitude();
//                    longt = location.getLongitude();
//                }
//
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String provider) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String provider) {
//
//                }
//            };
//            return;
//        }

//       lat = Double.valueOf(latitude);
//        longt = Double.valueOf(longitude);
        LatLng latLng = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
        Log.d("LatLong is:",""+latLng);
//        gMap.setMyLocationEnabled(true);
        gMap.addMarker(new MarkerOptions().position(latLng).title(title));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(10));

//        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//        gMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

}
