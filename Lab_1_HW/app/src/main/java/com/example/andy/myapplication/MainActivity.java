package com.example.andy.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TelephonyManager telephonyManager;
    private String deviceID = " ";

    //private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView mVerName;

        setContentView(R.layout.activity_main);
        //int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        mVerName = findViewById(R.id.layoutVersionName);
        mVerName.setText("Current version is:\n " + versionName);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button)
        {
            getID();
            if(deviceID != " ")
                Toast.makeText(MainActivity.this, deviceID,Toast.LENGTH_LONG).show();
        }
    }

    private void getID() {
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
        else {
            deviceID = telephonyManager.getDeviceId();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //f (requestCode == 1) {
            //if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            //{

                //deviceID = telephonyManager.getDeviceId();
            //}
        //else
                Toast.makeText(MainActivity.this, "*Extremely convincing explanation*", Toast.LENGTH_LONG).show();
        //}
    }

}

