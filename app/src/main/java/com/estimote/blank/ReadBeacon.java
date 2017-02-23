package com.estimote.blank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.telemetry.EstimoteTelemetry;

import java.util.List;

public class ReadBeacon extends AppCompatActivity {
    private BeaconManager beaconManager;
    private String scanId;
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_beacon);
        tv1 = (TextView)findViewById(R.id.tv1);
        beaconManager = new BeaconManager(this);
        beaconManager.setTelemetryListener(new BeaconManager.TelemetryListener() {
            @Override
            public void onTelemetriesFound(List<EstimoteTelemetry> telemetries) {

                for (EstimoteTelemetry tlm : telemetries) {
                    if(tlm.deviceId == null ||
                            !tlm.deviceId.toString().equals("[fbc7ed741c620f8c4c6e2d4bc234023a]") ||
                            tlm.ambientLight == null) {
                        continue;
                    }
                    tv1.setText(String.format("beaconID:%s,ambientLight:%f°C", tlm.deviceId, tlm.ambientLight));
                }
                ;

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                scanId = beaconManager.startTelemetryDiscovery();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stopTelemetryDiscovery(scanId);
    }
}
