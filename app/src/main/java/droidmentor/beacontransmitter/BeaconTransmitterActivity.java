package droidmentor.beacontransmitter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

import droidmentor.beacontransmitter.BluetoothHelper.BluetoothHelper;
import droidmentor.beacontransmitter.BluetoothHelper.BluetoothListener;

public class BeaconTransmitterActivity extends AppCompatActivity implements
        BluetoothListener.OnBluetoothSupportedCheckListener, BluetoothListener.OnBluetoothEnabledCheckListener,
        BluetoothListener.BluetoothTrigger,BeaconConsumer {

    protected static final String TAG = "Beacon Transmitter";
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    Button btn_transmit;
    Button btn_apply;
    BluetoothHelper bluetoothHelper;

    EditText etUUID;
    EditText etMajorValue;
    EditText etMinorValue;

    boolean isBluetoothEnabled;

    Beacon beacon;
    BeaconParser beaconParser;
    BeaconTransmitter beaconTransmitter;

    private BeaconManager beaconManager;

    int beaconLayout=0;

    String[] beaconFormat = { "AltBeacon", "iBeacon"};
    String[] parserLayout = { "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25",
            "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"};

    Spinner spin_beaconFormat;

    String currentuuid,currentmajorValue,currentminorValue;
    int currentType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_transmitter);

        bluetoothHelper = new BluetoothHelper();
        bluetoothHelper.initializeBluetooth(this);

        MyApplication app = (MyApplication)this.getApplication();
        beaconManager = app.getBeaconManager();

        beaconManager.setBackgroundBetweenScanPeriod(5000l);
        beaconManager.setForegroundBetweenScanPeriod(5000l);

        beaconManager.bind(this);

        btn_transmit = (Button) findViewById(R.id.btn_transmit);
        btn_apply = (Button) findViewById(R.id.btn_apply);

        etUUID = (EditText) findViewById(R.id.et_uuid);
        etMajorValue = (EditText) findViewById(R.id.et_major);
        etMinorValue = (EditText) findViewById(R.id.et_minor);

        etUUID.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
               if(beaconTransmitter!=null&&!s.equals(currentuuid))
                   btn_apply.setEnabled(true);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        etMajorValue.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(beaconTransmitter!=null&&!s.equals(currentmajorValue))
                    btn_apply.setEnabled(true);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        etMinorValue.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(beaconTransmitter!=null&&!s.equals(currentminorValue))
                    btn_apply.setEnabled(true);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        spin_beaconFormat=(Spinner)findViewById(R.id.spinner_lang);
        ArrayAdapter< String > adapter1 = new ArrayAdapter < String > (this,
                R.layout.spinner_header_item, beaconFormat);

        adapter1.setDropDownViewResource(R.layout.spinner_item);

        spin_beaconFormat.setAdapter(adapter1);

        spin_beaconFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                beaconLayout=position;

                if(beaconLayout!=currentType)
                    btn_apply.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_transmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isBluetoothEnabled)
                {
                    try
                    {
                        trasmitClick();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(BeaconTransmitterActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();


                    }

                }

                else
                    Toast.makeText(BeaconTransmitterActivity.this, "Check your bluetooth connection", Toast.LENGTH_LONG).show();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    btn_transmit.performClick();
                    btn_transmit.performClick();
            }
        });
    }

    public void trasmitClick() {

        if (beaconTransmitter == null) {

            String major, minor, uuid;

            uuid = etUUID.getText().toString().trim();
            major = etMajorValue.getText().toString().trim();
            minor = etMinorValue.getText().toString().trim();

            if (TextUtils.isEmpty(uuid))
                uuid = "94339309-bfe2-4807-b747-9aee23508620";
            if (TextUtils.isEmpty(major))
                major = "8";
            if (TextUtils.isEmpty(minor))
                minor = "2";

            currentType=beaconLayout;
            currentuuid=uuid;
            currentmajorValue=major;
            currentminorValue=minor;

            beacon = new Beacon.Builder()
                    .setId1(uuid)
                    .setId2(major)
                    .setId3(minor)
                    .setManufacturer(0x0118) // It is for AltBeacon.  Change this for other beacon layouts
                    .setTxPower(-59)
                    .setDataFields(Arrays.asList(new Long[]{6l, 7l})) // Remove this for beacon layouts without d: fields
                    .build();

            // Change the layout below for other beacon types

            beaconParser = new BeaconParser()
                    .setBeaconLayout(parserLayout[beaconLayout]);

            beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
            beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                }
            });
            btn_transmit.setText("Stop Advertising");
            btn_apply.setEnabled(false);

        } else {
            beaconTransmitter.startAdvertising();
            beaconTransmitter = null;
            btn_transmit.setText("Start Advertising");
            btn_apply.setEnabled(false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBLENotSupported() {
        Toast.makeText(BeaconTransmitterActivity.this, "BLE not supported", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBluetoothNotSupported() {
        Toast.makeText(BeaconTransmitterActivity.this, "Blutooth not supported", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBluetoothEnabled(boolean enable) {
        if (enable) {
            isBluetoothEnabled = true;
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    @Override
    public void initBluetooth() {
        if (bluetoothHelper != null)
            bluetoothHelper.initializeBluetooth(this);
    }

    @Override
    public void enableBluetooth() {
        if (bluetoothHelper != null)
            bluetoothHelper.enableBluetooth(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == AppCompatActivity.RESULT_CANCELED) {
            Toast.makeText(BeaconTransmitterActivity.this, "Bluetooth permission denied", Toast.LENGTH_LONG).show();
            bluetoothHelper = null;
            return;
        } else {
            isBluetoothEnabled = true;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothHelper != null)
            bluetoothHelper.enableBluetooth(this);
        MyApplication.isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.isActive = false;
    }


    @Override
    public void onBeaconServiceConnect() {

    }
}
