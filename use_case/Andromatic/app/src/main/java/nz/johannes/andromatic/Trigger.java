package nz.johannes.andromatic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Trigger {

    private String type;
    private String match;
    private ArrayList<String> extraData;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMatch() {
        return match;
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

    public class TriggerListViewAdapter extends ArrayAdapter<Trigger> {

        public TriggerListViewAdapter(Context context, int resource, List<Trigger> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Trigger trigger = getItem(position);
            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                convertView = (trigger.getMatch() != null) ?
                        vi.inflate(R.layout.default_list_row, null) : vi.inflate(R.layout.list_row_nodetail, null);
            }
            if (trigger != null) {
                TextView type = (TextView) convertView.findViewById(R.id.type);
                TextView detail = (TextView) convertView.findViewById(R.id.detail);
                switch (trigger.getType()) {
                    case "Trigger.AnyIncomingCall":
                        type.setText("Any incoming call");
                        break;
                    case "Trigger.IncomingCallByCaller":
                        type.setText("Incoming call");
                        if (trigger.getExtraData().get(0) != null)
                            detail.setText(trigger.getExtraData().get(0) + " (" + trigger.getMatch() + ")");
                        else detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.AnyAnsweredCall":
                        type.setText("Any answered call");
                        break;
                    case "Trigger.AnsweredCallByCaller":
                        type.setText("Answered call");
                        if (trigger.getExtraData().get(0) != null)
                            detail.setText(trigger.getExtraData().get(0) + " (" + trigger.getMatch() + ")");
                        else detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.AnyEndedCall":
                        type.setText("Any ended call");
                        break;
                    case "Trigger.EndedCallByCaller":
                        type.setText("Ended call");
                        if (trigger.getExtraData().get(0) != null)
                            detail.setText(trigger.getExtraData().get(0) + " (" + trigger.getMatch() + ")");
                        else detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.AnySMS":
                        type.setText("Any received SMS");
                        break;
                    case "Trigger.SMSByContent":
                        type.setText("SMS received");
                        detail.setText(trigger.getExtraData().get(0) + " match: \"" + trigger.getMatch() + "\"");
                        break;
                    case "Trigger.SMSBySender":
                        type.setText("SMS received");
                        if (trigger.getExtraData().get(0) != null)
                            detail.setText(trigger.getExtraData().get(0) + " (" + trigger.getMatch() + ")");
                        else detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.BatteryLow":
                        type.setText("Battery low");
                        break;
                    case "Trigger.ChargerInserted":
                        type.setText("Charger inserted");
                        break;
                    case "Trigger.ChargerRemoved":
                        type.setText("Charger removed");
                        break;
                    case "Trigger.HeadphonesInserted":
                        type.setText("Headphones inserted");
                        break;
                    case "Trigger.HeadphonesRemoved":
                        type.setText("Headphones removed");
                        break;
                    case "Trigger.Interval":
                        type.setText("Interval");
                        detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.Time":
                        type.setText("Time");
                        detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.Bluetooth":
                        type.setText("Any bluetooth connection");
                        break;
                    case "Trigger.BluetoothByDeviceName":
                        type.setText("Bluetooth connection");
                        detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.WifiConnected":
                        type.setText("Wifi connected");
                        break;
                    case "Trigger.WifiConnectedBySSID":
                        type.setText("Wifi connected");
                        detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.WifiDisconnected":
                        type.setText("Wifi disconnected");
                        break;
                    case "Trigger.WifiDisconnectedBySSID":
                        type.setText("Wifi disconnected");
                        detail.setText(trigger.getMatch());
                        break;
                    case "Trigger.Shake":
                        type.setText("Shake");
                        break;
                    case "Trigger.Flip":
                        type.setText("Flip");
                        break;
                    case "Trigger.FaceUp":
                        type.setText("Flip up");
                        break;
                    case "Trigger.FaceDown":
                        type.setText("Flip down");
                        break;
                    case "Trigger.NewTweet":
                        type.setText("Any new tweet");
                        break;
                    case "Trigger.NewTweetByContent":
                        type.setText("New tweet");
                        detail.setText(trigger.getExtraData().get(0) + " match: \"" + trigger.getMatch() + "\"");
                        break;
                    case "Trigger.NewTweetByUser":
                        type.setText("New tweet");
                        detail.setText("From user " + trigger.getMatch());
                        break;
                    case "Trigger.NewDM":
                        type.setText("Any new Twitter message");
                        break;
                    case "Trigger.NewDMByContent":
                        type.setText("New Twitter message");
                        detail.setText(trigger.getExtraData().get(0) + " match: \"" + trigger.getMatch() + "\"");
                        break;
                    case "Trigger.NewDMByUser":
                        type.setText("New Twitter message");
                        detail.setText("From user " + trigger.getMatch());
                        break;
                    case "Trigger.NewRedditPost":
                        type.setText("New Reddit post");
                        detail.setText("/r/" + trigger.getMatch());
                        break;
                    case "Trigger.NewRedditPostByUser":
                        type.setText("New Reddit post");
                        detail.setText("/r/" + trigger.getMatch() + " - by user " + trigger.getExtraData().get(0));
                        break;
                    case "Trigger.NewRedditPostByTitle":
                        type.setText("New Reddit post");
                        detail.setText("/r/" + trigger.getMatch() + " - " + trigger.getExtraData().get(0).toLowerCase() +
                                " match: \"" + trigger.getExtraData().get(1) + "\"");
                        break;
                    default:
                        type.setText(trigger.getType());
                }
            }
            return convertView;
        }
    }

}
