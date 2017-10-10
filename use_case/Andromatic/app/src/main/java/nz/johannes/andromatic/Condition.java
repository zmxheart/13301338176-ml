package nz.johannes.andromatic;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Condition {

    private String type;
    private String match;
    private ArrayList<String> extraData;

    public boolean check(Context context) {
        switch (type) {
            case "Condition.NoCall":
                TelephonyManager noCallChecker = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return noCallChecker.getCallState() == TelephonyManager.CALL_STATE_IDLE;
            case "Condition.AnyIncomingCall":
                TelephonyManager incCallChecker = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return incCallChecker.getCallState() == TelephonyManager.CALL_STATE_RINGING;
            case "Condition.IncomingCallByCaller":
                TelephonyManager incCallByCallerChecker = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return (incCallByCallerChecker.getCallState() == TelephonyManager.CALL_STATE_RINGING &&
                        PhoneNumberUtils.compare(CallReceiver.lastCaller, match));
            case "Condition.AnyCall":
                TelephonyManager callChecker = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return callChecker.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK;
            case "Condition.CallByCaller":
                TelephonyManager callByCallerChecker = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return (callByCallerChecker.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK &&
                        PhoneNumberUtils.compare(CallReceiver.lastCaller, match));
            case "Condition.BatteryPercentage":
                Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int rawBattery = batteryIntent.getIntExtra("level", -1);
                double scale = batteryIntent.getIntExtra("scale", -1);
                double level = -1;
                if (rawBattery >= 0 && scale > 0) level = rawBattery / scale * 100;
                if (match.substring(0, 1).equals("<")) return level < Integer.parseInt(match.substring(1));
                else return level > Integer.parseInt(match.substring(1));
            case "Condition.PhoneCharging":
                Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
            case "Condition.PhoneNotCharging":
                intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                return !(plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);
            case "Condition.TimePeriod":
                Calendar start = Calendar.getInstance();
                start.set(0, 0, 0, Integer.parseInt(extraData.get(0)), Integer.parseInt(extraData.get(1)));
                Calendar end = Calendar.getInstance();
                end.set(0, 0, 0, Integer.parseInt(extraData.get(2)), Integer.parseInt(extraData.get(3)));
                Calendar now = Calendar.getInstance();
                now.set(0, 0, 0);
                if (start.after(end)) end.add(Calendar.DATE, 1);
                return (now.after(start) && now.before(end));
            case "Condition.MobileDataActive":
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
                    return android.provider.Settings.Global.getInt(context.getContentResolver(), "mobile_data", 1) == 1;
                else return android.provider.Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1;
            case "Condition.MobileDataInactive":
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
                    return !(android.provider.Settings.Global.getInt(context.getContentResolver(), "mobile_data", 1) == 1);
                else return !(android.provider.Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1);
            case "Condition.WifiConnected":
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return wifiInfo.isConnected();
            case "Condition.WifiConnectedBySSID":
                WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                return match.equals(manager.getConnectionInfo().getSSID().replace("\"", ""));
            case "Condition.WifiNotConnected":
                connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return !wifiInfo.isConnected();
            case "Condition.FaceUp":
                return SensorService.faceUp;
            case "Condition.FaceDown":
                return !SensorService.faceUp;
        }
        return false;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMatch() {
        return this.match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public ArrayList<String> getExtraData() {
        return extraData;
    }

    public void setExtraData(ArrayList data) {
        this.extraData = data;
    }

    public class ConditionListViewAdapter extends ArrayAdapter<Condition> {

        public ConditionListViewAdapter(Context context, int resource, List<Condition> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Condition condition = getItem(position);
            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                convertView = (condition.getMatch() != null) ?
                        vi.inflate(R.layout.default_list_row, null) : vi.inflate(R.layout.list_row_nodetail, null);
            }
            if (condition != null) {
                TextView type = (TextView) convertView.findViewById(R.id.type);
                TextView detail = (TextView) convertView.findViewById(R.id.detail);
                switch (condition.getType()) {
                    case "Condition.NoCall":
                        type.setText("No call in progress");
                        break;
                    case "Condition.AnyIncomingCall":
                        type.setText("Call incoming");
                        break;
                    case "Condition.IncomingCallByCaller":
                        type.setText("Call incoming");
                        if (condition.getExtraData().get(0) != null)
                            detail.setText(condition.getExtraData().get(0) + " (" + condition.getMatch() + ")");
                        else detail.setText(condition.getMatch());
                        break;
                    case "Condition.AnyCall":
                        type.setText("Call in progress");
                        break;
                    case "Condition.CallByCaller":
                        type.setText("Call in progress");
                        if (condition.getExtraData().get(0) != null)
                            detail.setText(condition.getExtraData().get(0) + " (" + condition.getMatch() + ")");
                        else detail.setText(condition.getMatch());
                        break;
                    case "Condition.BatteryPercentage":
                        type.setText("Battery percentage");
                        String modifierText = condition.getMatch().substring(0, 1).equals("<") ? "Less than " : "Greater than ";
                        detail.setText(modifierText + condition.getMatch().substring(1) + " percent");
                        break;
                    case "Condition.PhoneCharging":
                        type.setText("Phone charging");
                        break;
                    case "Condition.PhoneNotCharging":
                        type.setText("Phone not charging");
                        break;
                    case "Condition.TimePeriod":
                        type.setText("Time period");
                        detail.setText(condition.getMatch());
                        break;
                    case "Condition.MobileDataActive":
                        type.setText("Mobile data enabled");
                        break;
                    case "Condition.MobileDataInactive":
                        type.setText("Mobile data disabled");
                        break;
                    case "Condition.WifiConnected":
                        type.setText("Wifi connected");
                        break;
                    case "Condition.WifiConnectedBySSID":
                        type.setText("Wifi connected");
                        detail.setText(condition.getMatch());
                        break;
                    case "Condition.WifiNotConnected":
                        type.setText("Wifi not connected");
                        break;
                    case "Condition.FaceUp":
                        type.setText("Phone face-up");
                        break;
                    case "Condition.FaceDown":
                        type.setText("Phone face-down");
                        break;
                    default:
                        type.setText(condition.getType());
                }
            }
            return convertView;
        }
    }

}
