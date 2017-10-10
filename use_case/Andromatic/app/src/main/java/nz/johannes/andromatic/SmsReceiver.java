package nz.johannes.andromatic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Main.weHavePermission(context, Manifest.permission.RECEIVE_SMS)) return;
        SmsMessage[] messages;
        if (Build.VERSION.SDK_INT >= 19) messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        else {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        for (Task task : Main.getAllStoredTasks(context)) {
            for (Trigger trigger : task.getTriggers()) {
                if (trigger.getType().equals("Trigger.AnySMS")) task.runTask(context);
                if (trigger.getType().equals("Trigger.SMSByContent")) {
                    for (SmsMessage message : messages) {
                        String body = message.getMessageBody();
                        if (trigger.getExtraData().get(0).equals("Exact") && body.equalsIgnoreCase(trigger.getMatch()))
                            task.runTask(context);
                        if (trigger.getExtraData().get(0).equals("Partial") && body.toLowerCase().contains(trigger.getMatch().toLowerCase()))
                            task.runTask(context);
                    }
                }
                if (trigger.getType().equals("Trigger.SMSBySender")) {
                    for (SmsMessage message : messages) {
                        String sender = message.getOriginatingAddress();
                        if (PhoneNumberUtils.compare(sender, trigger.getMatch())) task.runTask(context);
                    }
                }
            }
        }
    }

}
