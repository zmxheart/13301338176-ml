package nz.johannes.andromatic;

import android.Manifest;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Action {

    private String command;
    private int data = -1;
    private ArrayList<String> multiData;

    public void doAction(final Context context) {
        switch (command) {
            case "Action.StartCall":
                if (!Main.weHavePermission(context, Manifest.permission.CALL_PHONE)) return;
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + multiData.get(0)));
                call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                boolean callPermission = context.checkCallingOrSelfPermission("android.permission.CALL_PHONE") == PackageManager.PERMISSION_GRANTED;
                if (callPermission) context.startActivity(call);
                break;
            case "Action.AcceptCall":
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    final MediaSessionManager mediaSessionManager = (MediaSessionManager) context.getApplicationContext()
                            .getSystemService(Context.MEDIA_SESSION_SERVICE);
                    try {
                        List<MediaController> mediaControllerList = mediaSessionManager.getActiveSessions
                                (new ComponentName(context.getApplicationContext(), NotificationReceiverService.class));
                        for (MediaController m : mediaControllerList) {
                            if ("com.android.server.telecom".equals(m.getPackageName())) {
                                m.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                                m.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                                break;
                            }
                        }
                    } catch (SecurityException e) {
                        Main.showToast(context, "Could not accept call - notification access not granted!");
                    }
                } else {
                    Intent answerCall = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    answerCall.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                    context.sendOrderedBroadcast(answerCall, null);
                    answerCall.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                    context.sendOrderedBroadcast(answerCall, null);
                }
                break;
            case "Action.EndCall":
                try {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    Class c = Class.forName(tm.getClass().getName());
                    Method m = c.getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    Object telephonyService = m.invoke(tm);
                    c = Class.forName(telephonyService.getClass().getName());
                    m = c.getDeclaredMethod("endCall");
                    m.setAccessible(true);
                    m.invoke(telephonyService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Action.SpeakerphoneEnable":
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setSpeakerphoneOn(true);
                break;
            case "Action.SpeakerphoneToggle":
                boolean speakerToggle = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isSpeakerphoneOn();
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setSpeakerphoneOn(!speakerToggle);
                break;
            case "Action.SpeakerphoneDisable":
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setSpeakerphoneOn(false);
                break;
            case "Action.MicEnable":
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setMicrophoneMute(false);
                break;
            case "Action.MicToggle":
                boolean micToggle = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMicrophoneMute();
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setMicrophoneMute(!micToggle);
                break;
            case "Action.MicDisable":
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setMicrophoneMute(true);
                break;
            case "Action.SendSMS":
                if (!Main.weHavePermission(context, Manifest.permission.SEND_SMS)) return;
                SmsManager.getDefault().sendTextMessage(multiData.get(1), null, multiData.get(2), null, null);
                break;
            case "Action.MediaPlay":
                Intent play = new Intent(Intent.ACTION_MEDIA_BUTTON);
                play.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
                context.sendOrderedBroadcast(play, null);
                play.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
                context.sendOrderedBroadcast(play, null);
                break;
            case "Action.MediaPause":
                Intent pause = new Intent(Intent.ACTION_MEDIA_BUTTON);
                pause.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE));
                context.sendOrderedBroadcast(pause, null);
                pause.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE));
                context.sendOrderedBroadcast(pause, null);
                break;
            case "Action.MediaPlayPause":
                Intent playPause = new Intent(Intent.ACTION_MEDIA_BUTTON);
                playPause.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                context.sendOrderedBroadcast(playPause, null);
                playPause.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                context.sendOrderedBroadcast(playPause, null);
                break;
            case "Action.MediaSkip":
                Intent skip = new Intent(Intent.ACTION_MEDIA_BUTTON);
                skip.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
                context.sendOrderedBroadcast(skip, null);
                skip.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
                context.sendOrderedBroadcast(skip, null);
                break;
            case "Action.MediaVolume":
                AudioManager musicManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float mediaVolume = ((float) musicManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 100 * data;
                musicManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(mediaVolume), 0);
                break;
            case "Action.LaunchApp":
                ComponentName component = new ComponentName(multiData.get(1), multiData.get(2));
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.setComponent(component);
                context.startActivity(intent);
                break;
            case "Action.Vibrate":
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
                break;
            case "Action.PlaySoundNotification":
                RingtoneManager.getRingtone(context, Uri.parse(multiData.get(1))).play();
                break;
            case "Action.PlaySoundCustom":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    context.getContentResolver().takePersistableUriPermission(Uri.parse(multiData.get(1)), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                MediaPlayer mPlayer = MediaPlayer.create(context.getApplicationContext(), Uri.parse(multiData.get(1)));
                mPlayer.start();
                break;
            case "Action.ShowNotification":
                NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                notification.setSmallIcon(R.mipmap.ic_launcher);
                notification.setContentTitle(multiData.get(0));
                notification.setContentText(multiData.get(1));
                int id = new Random().nextInt(10000);
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, notification.build());
                break;
            case "Action.TTSTime":
                Intent ttsTime = new Intent(context, TTSService.class);
                SimpleDateFormat formatter = new SimpleDateFormat("HH mm a", Locale.getDefault());
                ttsTime.putExtra("text", "The time is " + formatter.format(Calendar.getInstance().getTime()));
                context.startService(ttsTime);
                break;
            case "Action.TTSSMS":
                Intent ttsSMS = new Intent(context, TTSService.class);
                Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
                if (cursor.moveToFirst()) {
                    String msgData = "";
                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                    String from = Main.getNameFromNumber(context, cursor.getString(cursor.getColumnIndex("address")));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    ttsSMS.putExtra("text", "SMS from " + from + "... " + body);
                }
                context.startService(ttsSMS);
                break;
            case "Action.TTSCustom":
                Intent ttsCustom = new Intent(context, TTSService.class);
                ttsCustom.putExtra("text", multiData.get(0));
                context.startService(ttsCustom);
                break;
            case "Action.BluetoothEnable":
                BluetoothAdapter.getDefaultAdapter().enable();
                break;
            case "Action.BluetoothToggle":
                boolean bluetoothToggle = BluetoothAdapter.getDefaultAdapter().isEnabled();
                if (bluetoothToggle) BluetoothAdapter.getDefaultAdapter().disable();
                else BluetoothAdapter.getDefaultAdapter().enable();
                break;
            case "Action.BluetoothDisable":
                BluetoothAdapter.getDefaultAdapter().disable();
                break;
            case "Action.WifiEnable":
                ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);
                break;
            case "Action.WifiToggle":
                boolean wifiToggle = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
                ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(!wifiToggle);
                break;
            case "Action.WifiDisable":
                ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
                break;
            case "Action.LockModeNone":
                if (!Main.weHavePermission(context, "device_admin")) return;
                DevicePolicyManager lockNoneManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName lockNoneAdmin = new ComponentName(context, DeviceAdmin.class);
                if (!lockNoneManager.isAdminActive(lockNoneAdmin)) return;
                lockNoneManager.setPasswordMinimumLength(lockNoneAdmin, 0);
                lockNoneManager.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                break;
            case "Action.LockModePin":
                if (!Main.weHavePermission(context, "device_admin")) return;
                DevicePolicyManager lockPinManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName lockPinAdmin = new ComponentName(context, DeviceAdmin.class);
                if (!lockPinManager.isAdminActive(lockPinAdmin)) return;
                lockPinManager.setPasswordQuality(lockPinAdmin, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
                lockPinManager.resetPassword(multiData.get(0), DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                lockPinManager.lockNow();
                break;
            case "Action.LockModePassword":
                if (!Main.weHavePermission(context, "device_admin")) return;
                DevicePolicyManager lockPassManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName lockPassAdmin = new ComponentName(context, DeviceAdmin.class);
                if (!lockPassManager.isAdminActive(lockPassAdmin)) return;
                lockPassManager.setPasswordQuality(lockPassAdmin, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                lockPassManager.resetPassword(multiData.get(0), DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                lockPassManager.lockNow();
                break;
            case "Action.Timeout":
                if (!Main.weHavePermission(context, "device_admin")) return;
                DevicePolicyManager timeoutManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName timeoutAdmin = new ComponentName(context, DeviceAdmin.class);
                if (!timeoutManager.isAdminActive(timeoutAdmin)) return;
                int timeoutTimeMillis = -1;
                switch (data) {
                    case 0:
                        timeoutTimeMillis = 15 * 1000;
                        break;
                    case 1:
                        timeoutTimeMillis = 30 * 1000;
                        break;
                    case 2:
                        timeoutTimeMillis = 60 * 1000;
                        break;
                    case 3:
                        timeoutTimeMillis = 120 * 1000;
                        break;
                    case 4:
                        timeoutTimeMillis = 300 * 1000;
                        break;
                    case 5:
                        timeoutTimeMillis = 600 * 1000;
                }
                timeoutManager.setMaximumTimeToLock(timeoutAdmin, timeoutTimeMillis);
                break;
            case "Action.RingerVolume":
                AudioManager ringerManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float ringerVolume = ((float) ringerManager.getStreamMaxVolume(AudioManager.STREAM_RING)) / 100 * data;
                ringerManager.setStreamVolume(AudioManager.STREAM_RING, Math.round(ringerVolume), 0);
                break;
            case "Action.NotificationVolume":
                AudioManager notifyManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float notifyVolume = ((float) notifyManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)) / 100 * data;
                notifyManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, Math.round(notifyVolume), 0);
                break;
            case "Action.SendTweet":
                SocialMediaManager.sendTweet(context, multiData.get(0));
                break;
            case "Action.TwitterMessage":
                SocialMediaManager.sendTwitterMessage(context, multiData.get(0), multiData.get(1));
                break;
        }
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public ArrayList getMultiData() {
        return this.multiData;
    }

    public void setData(ArrayList data) {
        this.multiData = data;
    }

    public class ActionListViewAdapter extends ArrayAdapter<Action> {

        public ActionListViewAdapter(Context context, int resource, List<Action> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Action action = getItem(position);
            boolean hasExtraData = (action.getData() != -1 || action.getMultiData() != null);
            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                convertView = hasExtraData ? vi.inflate(R.layout.default_list_row, null) : vi.inflate(R.layout.list_row_nodetail, null);
            }
            TextView type = (TextView) convertView.findViewById(R.id.type);
            TextView detail = (TextView) convertView.findViewById(R.id.detail);
            switch (action.getCommand()) {
                case "Action.StartCall":
                    type.setText("Start call");
                    if (action.getMultiData().get(1) != null)
                        detail.setText(action.getMultiData().get(1) + " (" + action.getMultiData().get(0) + ")");
                    else detail.setText((String) action.getMultiData().get(0));
                    break;
                case "Action.AcceptCall":
                    type.setText("Accept call");
                    break;
                case "Action.EndCall":
                    type.setText("End call");
                    break;
                case "Action.SpeakerphoneEnable":
                    type.setText("Enable speakerphone");
                    break;
                case "Action.SpeakerphoneToggle":
                    type.setText("Toggle speakerphone");
                    break;
                case "Action.SpeakerphoneDisable":
                    type.setText("Disable speakerphone");
                    break;
                case "Action.MicEnable":
                    type.setText("Unmute microphone");
                    break;
                case "Action.MicToggle":
                    type.setText("Toggle microphone");
                    break;
                case "Action.MicDisable":
                    type.setText("Mute microphone");
                    break;
                case "Action.SendSMS":
                    type.setText("Send SMS message");
                    if (action.getMultiData().get(1) != null)
                        detail.setText(action.getMultiData().get(0) + " (" + action.getMultiData().get(1) + ")");
                    else detail.setText((String) action.getMultiData().get(1));
                    break;
                case "Action.MediaPlay":
                    type.setText("Play media");
                    break;
                case "Action.MediaPause":
                    type.setText("Pause media");
                    break;
                case "Action.MediaPlayPause":
                    type.setText("Media play/pause");
                    break;
                case "Action.MediaSkip":
                    type.setText("Skip media");
                    break;
                case "Action.MediaVolume":
                    type.setText("Set media volume");
                    detail.setText(action.getData() + " percent");
                    break;
                case "Action.LaunchApp":
                    type.setText("Launch app");
                    detail.setText((String) action.getMultiData().get(0));
                    break;
                case "Action.Vibrate":
                    type.setText("Vibrate");
                    break;
                case "Action.PlaySoundNotification":
                    type.setText("Play sound");
                    detail.setText((String) action.getMultiData().get(0));
                    break;
                case "Action.PlaySoundCustom":
                    type.setText("Play sound");
                    detail.setText((String) action.getMultiData().get(0));
                    break;
                case "Action.ShowNotification":
                    type.setText("Show notification");
                    detail.setText(action.getMultiData().get(0) + " - " + action.getMultiData().get(1));
                    break;
                case "Action.TTSTime":
                    type.setText("Text to speech");
                    detail.setText("Current time");
                    break;
                case "Action.TTSSMS":
                    type.setText("Text to speech");
                    detail.setText("Last SMS received");
                    break;
                case "Action.TTSCustom":
                    type.setText("Text to speech");
                    detail.setText("\"" + action.getMultiData().get(0) + "\"");
                    break;
                case "Action.BluetoothEnable":
                    type.setText("Enable bluetooth");
                    break;
                case "Action.BluetoothToggle":
                    type.setText("Toggle bluetooth");
                    break;
                case "Action.BluetoothDisable":
                    type.setText("Disable bluetooth");
                    break;
                case "Action.WifiEnable":
                    type.setText("Enable wifi");
                    break;
                case "Action.WifiToggle":
                    type.setText("Toggle wifi");
                    break;
                case "Action.WifiDisable":
                    type.setText("Disable wifi");
                    break;
                case "Action.LockModeNone":
                    type.setText("Set lock mode");
                    detail.setText("None");
                    break;
                case "Action.LockModePin":
                    type.setText("Set lock mode");
                    detail.setText("PIN");
                    break;
                case "Action.LockModePassword":
                    type.setText("Set lock mode");
                    detail.setText("Password");
                    break;
                case "Action.Timeout":
                    type.setText("Set screen timeout");
                    String timeoutChoices[] = new String[]{"15 seconds", "30 seconds", "1 minute", "2 minutes", "5 minutes", "10 minutes"};
                    detail.setText(timeoutChoices[action.getData()]);
                    break;
                case "Action.RingerVolume":
                    type.setText("Set ringer volume");
                    detail.setText(action.getData() + " percent");
                    break;
                case "Action.NotificationVolume":
                    type.setText("Set notification volume");
                    detail.setText(action.getData() + " percent");
                    break;
                case "Action.SendTweet":
                    type.setText("Update Twitter status");
                    detail.setText("\"" + action.getMultiData().get(0) + "\"");
                    break;
                case "Action.TwitterMessage":
                    type.setText("Send Twitter Message");
                    detail.setText((String) action.getMultiData().get(0));
                    break;
                default:
                    type.setText(action.getCommand());
            }
            return convertView;
        }
    }

}
