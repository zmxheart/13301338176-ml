package nz.johannes.andromatic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = "";
        switch (intent.getAction()) {
            case "android.intent.action.ACTION_POWER_CONNECTED":
                type = "Trigger.ChargerInserted";
                break;
            case "android.intent.action.ACTION_POWER_DISCONNECTED":
                type = "Trigger.ChargerRemoved";
                break;
            case "android.intent.action.BATTERY_LOW":
                type = "Trigger.BatteryLow";
                break;
        }
        for (Task task : Main.getAllStoredTasks(context)) {
            for (Trigger trigger : task.getTriggers()) {
                if (trigger.getType().equals(type)) task.runTask(context);
            }
        }
    }

}
