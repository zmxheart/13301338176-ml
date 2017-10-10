package nz.johannes.andromatic;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceAdmin extends DeviceAdminReceiver {

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Disabling device admin will remove all functions relating to screen timeout and lock.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        for (Task task : Main.getAllStoredTasks(context)) {
            for (Action action : task.getActions()) {
                if (action.getCommand().startsWith("Action.LockMode") || action.getCommand().startsWith("Action.Timeout"))
                    task.removeAction(context, action);
            }
        }
    }

}
