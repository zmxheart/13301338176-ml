package nz.johannes.andromatic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;

public class ReceiverManager {

    public static void manageReceivers(final Context context) {
        context.stopService(new Intent(context, SensorService.class));
        context.stopService(new Intent(context, HeadphoneService.class));
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarm = new Intent("nz.johannes.ALARM_INTENT");
        alarm.putExtra("type", "checkServices");
        alarm.setData(Uri.parse("task://checkServices/0"));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, alarm, 0);
        aManager.cancel(pi);
        boolean[] receiverBools = new boolean[6];
        boolean[] sensorBools = new boolean[2];
        boolean headset = false;
        boolean socialMedia = false;
        PackageManager pm = context.getPackageManager();
        ComponentName[] receivers = new ComponentName[]{new ComponentName(context, AlarmReceiver.class),
                new ComponentName(context, BatteryReceiver.class), new ComponentName(context, BluetoothReceiver.class),
                new ComponentName(context, CallReceiver.class), new ComponentName(context, SmsReceiver.class),
                new ComponentName(context, WifiReceiver.class)};
        for (Task task : Main.getAllStoredTasks(context)) {
            for (Trigger trigger : task.getTriggers()) {
                switch (trigger.getType()) {
                    case "Trigger.Interval":
                    case "Trigger.Time":
                        receiverBools[0] = true;
                        break;
                    case "Trigger.BatteryLow":
                    case "Trigger.ChargerInserted":
                    case "Trigger.ChargerRemoved":
                        receiverBools[1] = true;
                        break;
                    case "Trigger.HeadphonesInserted":
                    case "Trigger.HeadphonesRemoved":
                        receiverBools[0] = true;
                        headset = true;
                        break;
                    case "Trigger.Bluetooth":
                        receiverBools[2] = true;
                        break;
                    case "Trigger.AnyIncomingCall":
                    case "Trigger.IncomingCallByCaller":
                    case "Trigger.AnyAnsweredCall":
                    case "Trigger.AnsweredCallByCaller":
                    case "Trigger.AnyEndedCall":
                    case "Trigger.EndedCallByCaller":
                        receiverBools[3] = true;
                        break;
                    case "Trigger.AnySMS":
                    case "Trigger.SMSByContent":
                    case "Trigger.SMSBySender":
                        receiverBools[4] = true;
                        break;
                    case "Trigger.WifiConnected":
                    case "Trigger.WifiConnectedBySSID":
                    case "Trigger.WifiDisconnected":
                    case "Trigger.WifiDisconnectedBySSID":
                        receiverBools[5] = true;
                        break;
                    case "Trigger.Shake":
                        sensorBools[0] = true;
                        receiverBools[0] = true;
                        break;
                    case "Trigger.Flip":
                    case "Trigger.FaceUp":
                    case "Trigger.FaceDown":
                        sensorBools[1] = true;
                        receiverBools[0] = true;
                        break;
                    case "Trigger.NewTweet":
                    case "Trigger.NewTweetByContent":
                    case "Trigger.NewTweetByUser":
                    case "Trigger.NewDM":
                    case "Trigger.NewDMByContent":
                    case "Trigger.NewDMByUser":
                    case "Trigger.NewRedditPost":
                        socialMedia = true;
                        receiverBools[0] = true;
                        break;
                }
            }
            for (Condition condition : task.getConditions()) {
                switch (condition.getType()) {
                    case "Condition.IncomingCallByCaller":
                    case "Condition.CallByCaller":
                        receiverBools[3] = true;
                        break;
                    case "Condition.FaceUp":
                    case "Condition.FaceDown":
                        sensorBools[1] = true;
                        receiverBools[0] = true;
                        break;
                }
            }
        }
        for (int i = 0; i < receivers.length; i++) {
            if (receiverBools[i])
                pm.setComponentEnabledSetting(receivers[i], PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            else pm.setComponentEnabledSetting(receivers[i], PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        Intent sensorIntent = new Intent(context, SensorService.class);
        sensorIntent.putExtra("shake", sensorBools[0]);
        sensorIntent.putExtra("flip", sensorBools[1]);
        if (sensorBools[0] || sensorBools[1]) context.startService(sensorIntent);
        if (headset) context.startService(new Intent(context, HeadphoneService.class));
        if (sensorBools[0] || sensorBools[1] || headset || socialMedia) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int interval = socialMedia ? Integer.parseInt(prefs.getString("socialMediaCheckInterval", "15")) : 60;
            long time = System.currentTimeMillis() + (1000 * 60 * interval);
            if (android.os.Build.VERSION.SDK_INT >= 23) aManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi);
            else if (android.os.Build.VERSION.SDK_INT >= 19) aManager.setExact(AlarmManager.RTC_WAKEUP, time, pi);
            else aManager.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }


}
