package com.estimote.blank;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.connection.DeviceConnection;
import com.estimote.sdk.connection.DeviceConnectionCallback;
import com.estimote.sdk.connection.DeviceConnectionProvider;
import com.estimote.sdk.connection.exceptions.DeviceConnectionException;
import com.estimote.sdk.connection.scanner.ConfigurableDevice;
import com.estimote.sdk.connection.scanner.ConfigurableDevicesScanner;
import com.estimote.sdk.connection.scanner.DeviceType;
import com.estimote.sdk.connection.settings.SettingCallback;
import com.estimote.sdk.connection.settings.Version;

import java.util.List;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity {
    private boolean connectedToTheConnectionProvider;
    private ConfigurableDevice deviceToConnectTo;
    private DeviceConnectionProvider connectionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                estimo();
            }
        });

        findViewById(R.id.read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connected();
            }
        });

        connectionProvider = new DeviceConnectionProvider(this);
        connectionProvider.connectToService(
                new DeviceConnectionProvider.ConnectionProviderCallback() {
                    @Override
                    public void onConnectedToService() {
                        System.out.println("connect");
                        MainActivity.this.connectedToTheConnectionProvider = true;
                    }
                });
    }

    private void estimo() {

        ConfigurableDevicesScanner deviceScanner = new ConfigurableDevicesScanner(this.getApplicationContext());

// Only report your own devices. You need to set App ID and Token for this, so
// that the SDK can download the list of your own devices from your Cloud account.
        deviceScanner.setOwnDevicesFiltering(true);
// Only report Location Beacons and next-gen Proximity Beacons.
// Yes, we know it's a bit weird that "LOCATION_BEACON" also discovers Proximity
// Beacons. Yes, we want to fix that in the future. For now, please trust us on
// this one ¯\_(ツ)_/¯
        deviceScanner.setDeviceTypes(DeviceType.LOCATION_BEACON);

        deviceScanner.scanForDevices(new ConfigurableDevicesScanner.ScannerCallback() {
            @Override
            public void onDevicesFound(List<ConfigurableDevicesScanner
                    .ScanResultItem> devices) {
                String deviceIdentifier = "[fbc7ed741c620f8c4c6e2d4bc234023a]";
                for (ConfigurableDevicesScanner.ScanResultItem item : devices) {
                    System.out.println(item.device.deviceId.toString());
                    if (item.device.deviceId.toString().equals(deviceIdentifier)) {
                        deviceToConnectTo = item.device;
                        System.out.print(connectedToTheConnectionProvider + deviceToConnectTo.toString());
                    }
                }
            }
        });



    }
    private void connected() {
        if (this.connectedToTheConnectionProvider && deviceToConnectTo != null) {
            System.out.print(connectedToTheConnectionProvider + deviceToConnectTo.toString());
            DeviceConnection connection = connectionProvider.getConnection(
                    this.deviceToConnectTo);
            connection.connect(new DeviceConnectionCallback() {
                @Override
                public void onConnected() {
                    System.out.print("connect 111");
                    Log.d("DeviceConnection", "Connected");
                }

                @Override
                public void onDisconnected() {
                    Log.d("DeviceConnection", "Disconnected");
                }

                @Override
                public void onConnectionFailed(DeviceConnectionException exception) {
                    Log.d("DeviceConnection",
                            "Connection failed with error: " + exception.toString());
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        connectionProvider.destroy();
        super.onDestroy();
    }
}
