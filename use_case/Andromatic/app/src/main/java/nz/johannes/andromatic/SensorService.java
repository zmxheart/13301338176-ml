package nz.johannes.andromatic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;

public class SensorService extends Service {

    public static boolean faceUp;

    private static Context context;
    private static PowerManager.WakeLock wakeLock;
    private static ShakeSensor shakeSensor = new ShakeSensor();
    private static FlipSensor flipSensor = new FlipSensor();

    private static long lastShook;
    private static float acceleration = 0.00f;
    private static float currentAcceleration = SensorManager.GRAVITY_EARTH;
    private static float previousAcceleration = SensorManager.GRAVITY_EARTH;

    private static float zAxisAccel = 0;
    private static int zAccelChanges = 0;
    private static final int zAccelMaxChanges = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (wakeLock == null) wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getPackageName());
        if (!wakeLock.isHeld()) wakeLock.acquire();
        if (intent.getBooleanExtra("shake", false))
            sensorManager.registerListener(shakeSensor, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (intent.getBooleanExtra("flip", false))
            sensorManager.registerListener(flipSensor, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    private void unregisterAll() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(shakeSensor);
    }

    @Override
    public void onDestroy() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
        unregisterAll();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class ShakeSensor implements SensorEventListener {

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            previousAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = currentAcceleration - previousAcceleration;
            acceleration = acceleration * 0.9f + delta;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int requiredAccel = Integer.parseInt(prefs.getString("shakeSensitivity", "15"));
            if (acceleration < requiredAccel || (System.currentTimeMillis() - lastShook < 3000)) return;
            for (Task task : Main.getAllStoredTasks(context)) {
                for (Trigger trigger : task.getTriggers()) {
                    if (trigger.getType().equals("Trigger.Shake")) {
                        task.runTask(context);
                        lastShook = System.currentTimeMillis();
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }

    private static class FlipSensor implements SensorEventListener {

        public void onSensorChanged(SensorEvent event) {
            float zAxisAccelNew = event.values[2];
            if (zAxisAccel == 0) {
                zAxisAccel = zAxisAccelNew;
                return;
            }
            if ((zAxisAccel * zAxisAccelNew) < 0) {
                zAccelChanges++;
                if (zAccelChanges == zAccelMaxChanges) {
                    zAxisAccel = zAxisAccelNew;
                    zAccelChanges = 0;
                    if (zAxisAccelNew < 0) {
                        faceUp = false;
                        for (Task task : Main.getAllStoredTasks(context)) {
                            for (Trigger trigger : task.getTriggers()) {
                                if (trigger.getType().equals("Trigger.FaceDown")) task.runTask(context);
                            }
                        }
                    } else if (zAxisAccelNew > 0) {
                        faceUp = true;
                        for (Task task : Main.getAllStoredTasks(context)) {
                            for (Trigger trigger : task.getTriggers()) {
                                if (trigger.getType().equals("Trigger.FaceUp")) task.runTask(context);
                            }
                        }
                    }
                }
            } else {
                if (zAccelChanges > 0) {
                    zAxisAccel = zAxisAccelNew;
                    zAccelChanges = 0;
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }

}
