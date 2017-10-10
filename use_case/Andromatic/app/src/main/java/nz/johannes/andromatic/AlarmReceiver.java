package nz.johannes.andromatic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if ("checkServices".equals(intent.getStringExtra("type"))) {
            SocialMediaManager.check(context);
            ReceiverManager.manageReceivers(context);
            return;
        }
        try {
            Task task = Main.getTask(context, intent.getData().getHost());
            task.runTask(context);
            task.setAlarms(context);
        } catch (NullPointerException e) {
            // Task no longer exists but alarm was not unset
        }
    }

}
