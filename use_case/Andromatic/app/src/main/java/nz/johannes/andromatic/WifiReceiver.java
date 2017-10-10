package nz.johannes.andromatic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiReceiver extends BroadcastReceiver {

    private static String lastSSID;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.hasExtra("bssid")) return;
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo netInfo = (NetworkInfo) intent.getExtras().get("networkInfo");
        NetworkInfo.State currentState = netInfo.getState();
        if (isInitialStickyBroadcast()) {
            lastSSID = manager.getConnectionInfo().getSSID().replace("\"", "");
            return;
        }
        if (currentState.equals(NetworkInfo.State.CONNECTED)) {
            lastSSID = manager.getConnectionInfo().getSSID().replace("\"", "");
            for (Task task : Main.getAllStoredTasks(context)) {
                for (Trigger trigger : task.getTriggers()) {
                    if (trigger.getType().equals("Trigger.WifiConnected")) task.runTask(context);
                    if (trigger.getType().equals("Trigger.WifiConnectedBySSID") && trigger.getMatch().equals(lastSSID))
                        task.runTask(context);
                }
            }
        }
        if (currentState.equals(NetworkInfo.State.DISCONNECTED)) {
            for (Task task : Main.getAllStoredTasks(context)) {
                for (Trigger trigger : task.getTriggers()) {
                    if (trigger.getType().equals("Trigger.WifiDisconnected")) task.runTask(context);
                    if (trigger.getType().equals("Trigger.WifiDisconnectedBySSID") && trigger.getMatch().equals(lastSSID))
                        task.runTask(context);
                }
            }
        }
    }

}
