package nz.johannes.andromatic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) return;
        BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String deviceName = bluetoothDevice.getName();
        for (Task task : Main.getAllStoredTasks(context)) {
            for (Trigger trigger : task.getTriggers()) {
                if (trigger.getType().equals("Trigger.Bluetooth")) task.runTask(context);
                if (trigger.getType().equals("Trigger.BluetoothByDeviceName") && trigger.getMatch().equals(deviceName))
                    task.runTask(context);
            }
        }
    }

}
