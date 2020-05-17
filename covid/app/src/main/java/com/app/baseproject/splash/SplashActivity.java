package com.app.baseproject.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.app.baseproject.R;
import com.app.baseproject.home.HomeActivity;
import com.app.baseproject.splash.versionutils.Const;
import com.app.baseproject.splash.versionutils.VersionListener;
import com.app.baseproject.utils.AppData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity implements VersionListener {
    String currentVersion = "";
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        getLastLocation();

        //recent comment
        //checkUpdatedVersion();

    }

    //https://www.androdocs.com/java/getting-current-location-latitude-longitude-in-android-using-java.html
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    //here your locatiom
                                    gettingCountryName(location.getLatitude(),location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void gettingCountryName(double latitude,double longitude) {
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

            String add = "";
            if (addresses.size() > 0)
            {
                AppData.COUNTRY_CODE = addresses.get(0).getCountryCode();
                gotoNextScreen(1000);
                //code reference: https://stackoverflow.com/questions/12673357/how-to-get-only-city-state-country-from-lat-long-in-android
                //Toast.makeText(this, ""+addresses.get(0).getCountryCode(), Toast.LENGTH_SHORT).show();

            }


        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            //here is your location
            gettingCountryName(mLastLocation.getLatitude(),mLastLocation.getLongitude());

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }


    private void checkUpdatedVersion() {
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv_version = findViewById(R.id.tv_version);
            tv_version.setText("Version " + currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //new GetVersionCode(this).execute();

        gotoNextScreen(2000);
    }

    @Override
    public void onGetResponse(String onlineVersion) {
        Log.d("1111", "Current version " + currentVersion + "......playstore version " + onlineVersion);
        if (currentVersion.compareTo(onlineVersion) >= 0) {
            gotoNextScreen(1000);
        } else {
            showUpdateDialog();
        }

    }

    private void showUpdateDialog() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please update to new version to continue use")
                .setCancelable(false)
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + getPackageName())));
                        dialogInterface.dismiss();
                        finish();
                    }
                }).setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoNextScreen(1000);
                        // finish();
                    }
                }).create();
        dialog.show();
    }


    public static class GetVersionCode extends AsyncTask<Void, String, String> {

        private VersionListener listener;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        GetVersionCode(VersionListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.e("inside", "doInBackground");
            String newVersion = null;

            try {
                Document document = Jsoup.connect(Const.LINK)
                        .timeout(30000)
                        .userAgent(Const.USER_AGENT)
                        .referrer(Const.REFERRER)
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText(Const.CURRENT_VERSION);
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

//Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)
                listener.onGetResponse(onlineVersion);

            }

        }

    }

    @SuppressLint("HandlerLeak")
    private void gotoNextScreen(long delay) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //IntentController.sendIntent(SplashActivity.this, HomeActivity.class);
                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }.sendEmptyMessageDelayed(0, delay);
    }
}
