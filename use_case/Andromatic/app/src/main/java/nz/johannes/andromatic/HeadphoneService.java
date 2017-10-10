package nz.johannes.andromatic;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class HeadphoneService extends Service {

    private BroadcastReceiver headsetReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        headsetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) return;
                boolean plugged = (intent.getIntExtra("state", -1) == 1);
                for (Task task : Main.getAllStoredTasks(context)) {
                    for (Trigger trigger : task.getTriggers()) {
                        if (trigger.getType().equals("Trigger.HeadphonesInserted") && plugged) task.runTask(context);
                        if (trigger.getType().equals("Trigger.HeadphonesRemoved") && !plugged) task.runTask(context);
                    }
                }
            }
        };
        registerReceiver(headsetReceiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(headsetReceiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
