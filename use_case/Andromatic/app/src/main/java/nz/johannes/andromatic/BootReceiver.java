package nz.johannes.andromatic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        for (Task task : Main.getAllStoredTasks(context)) {
            ReceiverManager.manageReceivers(context);
            task.setAlarms(context);
        }
    }

}
