package cw.coursework;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by pavlo on 13/03/2018.
 */

public class LocationService implements LocationListener {


    public static LocationService instance;
    private FusedLocationProviderClient mFusedLocationClient;
   private Location loc;
   private double altitude;
   private double longtitude;
   private double latitude;
   public Boolean isAllowed;
   private ArrayList<ILocationServiceEvent> _listeners = new ArrayList<ILocationServiceEvent>();
    Activity act;
    public LocationService(Activity act)
    {
        this.act=act;


    }

    public void askForLocationPermision()
    {


        if (ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

                )

        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                    Manifest.permission.ACCESS_FINE_LOCATION )|| ActivityCompat.shouldShowRequestPermissionRationale(act,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(act,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }



        }
        else
        {

            ActivityCompat.requestPermissions(act,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);

        }

    }



    @SuppressLint("MissingPermission")
    public void getLastKnowLocation()
    {

System.out.println("Hello");
        mFusedLocationClient= getFusedLocationProviderClient(act);


        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(act, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        System.out.println("Test");
                        if (location != null) {
                            altitude = location.getAltitude();
                            longtitude= location.getLongitude();
                            latitude = location.getLatitude();
                            loc = location;
                            System.out.println("Got new location");
                            GetAddress();
                        }
                        System.out.println("End outside if");
                    }
                });


    }

    public String GetAddress()
    {

        String actAddress = null;
        Geocoder geocoder = new Geocoder(act, Locale.getDefault());
        try {
            if (latitude == 0.0 || longtitude ==0.0)
            {
                getLastKnowLocation();
                System.out.println("Should get new address");
                return "";
            }
            List<Address> addresses = geocoder.getFromLocation(latitude, longtitude, 1);
            System.out.println(latitude);
            System.out.println(longtitude);

          Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            for (String s : addressFragments) {
                System.out.println(s);
                   actAddress = actAddress+s;
            }
            return actAddress;

        }catch(IOException e)
        {

        }
        return null;
    }

    public void addListener(ILocationServiceEvent l)
    {

    }

    private void onSuccess()
    {
        for(ILocationServiceEvent e : _listeners)
        {
            e.onLocationServiceAllowed();
        }
    }
    private void onFail()
    {
        for(ILocationServiceEvent e : _listeners)
        {
            e.onLocationServiceDenied();
        }
    }

    public Location getLoc() {
        return loc;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public static LocationService getInstance( Activity act)
    {
        if(instance == null)
        {
            instance = new LocationService(act);
            return instance;
        }else
        {
            return instance;
        }


    }
}
