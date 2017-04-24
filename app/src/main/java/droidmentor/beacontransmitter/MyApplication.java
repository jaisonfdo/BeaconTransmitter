package droidmentor.beacontransmitter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by Jaison on 02/03/17.
 */

public class MyApplication extends Application {

    private static final String TAG = ".MyApplication";
    RegionBootstrap regionBootstrap;
    BackgroundPowerSaver backgroundPowerSaver;
    BeaconManager beaconManager;
    Region region;
    private static Context context;
    public static boolean isActive;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        context = getApplicationContext();

        beaconManager = BeaconManager.getInstanceForApplication(this);

        // enables auto battery saving of about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    public static Context getAppContext() {
        return context;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }
    public Region getRegion() {return region; }
}
