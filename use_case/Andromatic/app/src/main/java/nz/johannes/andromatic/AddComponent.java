package nz.johannes.andromatic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NotificationManagerCompat;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AddComponent extends PreferenceActivity {

    private static Task task;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        task = Main.getTask(this, getIntent().getStringExtra("Task"));
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(params);
        String componentType = getIntent().getAction();
        switch (componentType) {
            case "TRIGGER":
                getFragmentManager().beginTransaction().replace(android.R.id.content, new TriggerFragment()).commit();
                break;
            case "CONDITION":
                getFragmentManager().beginTransaction().replace(android.R.id.content, new ConditionFragment()).commit();
                break;
            case "ACTION":
                getFragmentManager().beginTransaction().replace(android.R.id.content, new ActionFragment()).commit();
                break;
        }
    }

    public static class TriggerFragment extends PreferenceFragment {

        private View view;
        private AutoCompleteTextView textView;
        private AlertDialog.Builder alert;
        private static List<String> elevatedTriggers = Arrays.asList("Trigger.AnyIncomingCall", "Trigger.IncomingCallByCaller",
                "Trigger.AnyOutgoingCall", "Trigger.OutgoingCallByCaller", "Trigger.AnyAnsweredCall", "Trigger.AnsweredCallByCaller",
                "Trigger.AnyEndedCall", "Trigger.EndedCallByCaller", "Trigger.AnySMS", "Trigger.SMSByContent", "Trigger.SMSBySender");

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Main.userIsPremium(getActivity()) || BuildConfig.DEBUG) addPreferencesFromResource(R.xml.triggers);
            else addPreferencesFromResource(R.xml.triggers_free);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (!prefs.getString("twitterID", "").equals("")) {
                Preference[] twitterPrefs = new Preference[]{findPreference("tweetScreen"), findPreference("twitterMessageScreen")};
                for (Preference preference : twitterPrefs) {
                    preference.setSummary("");
                }
            }
        }

        @Override
        public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, final Preference preference) {
            if (preference.getSummary() != null && preference.getSummary().toString().equals("Premium only")) {
                Main.purchasePremium(getActivity());
                getActivity().finish();
                return false;
            }
            if (preference.getSummary() != null && preference.getSummary().toString().equals("Not logged in")) {
                Intent settings = new Intent(context, Settings.class);
                startActivity(settings);
                getActivity().finish();
                return false;
            }
            if (preference instanceof PreferenceScreen) return false;
            String triggerType = preference.getKey();
            if (elevatedTriggers.contains(triggerType) && android.os.Build.VERSION.SDK_INT >= 23) {
                switch (triggerType) {
                    case "Trigger.AnyIncomingCall":
                    case "Trigger.AnyOutgoingCall":
                    case "Trigger.AnyAnsweredCall":
                    case "Trigger.AnyEndedCall":
                        if (Main.weHavePermission(context, Manifest.permission.READ_PHONE_STATE)) createTrigger(triggerType);
                        else requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, elevatedTriggers.indexOf(triggerType));
                        break;
                    case "Trigger.IncomingCallByCaller":
                    case "Trigger.OutgoingCallByCaller":
                    case "Trigger.AnsweredCallByCaller":
                    case "Trigger.EndedCallByCaller":
                        if (Main.weHavePermission(context, Manifest.permission.READ_CONTACTS) &&
                                Main.weHavePermission(context, Manifest.permission.READ_PHONE_STATE))
                            createTrigger(triggerType);
                        else requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS},
                                elevatedTriggers.indexOf(triggerType));
                        break;
                    case "Trigger.AnySMS":
                    case "Trigger.SMSByContent":
                        if (Main.weHavePermission(context, Manifest.permission.RECEIVE_SMS)) createTrigger(triggerType);
                        else requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, elevatedTriggers.indexOf(triggerType));
                        break;
                    case "Trigger.SMSBySender":
                        if (Main.weHavePermission(context, Manifest.permission.READ_CONTACTS) &&
                                Main.weHavePermission(context, Manifest.permission.RECEIVE_SMS)) {
                            createTrigger(triggerType);
                        } else requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS},
                                elevatedTriggers.indexOf(triggerType));
                        break;
                }
            } else createTrigger(triggerType);
            return true;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createTrigger(elevatedTriggers.get(requestCode));
            }
        }

        private void createTrigger(String triggerType) {
            final Trigger trigger = new Trigger();
            trigger.setType(triggerType);
            switch (triggerType) {
                case "Trigger.SMSByContent":
                case "Trigger.NewTweetByContent":
                case "Trigger.NewDMByContent":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_incomingmessage, null);
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText matchText = (EditText) ((AlertDialog) dialog).findViewById(R.id.content);
                            RadioButton exact = (RadioButton) ((AlertDialog) dialog).findViewById(R.id.radio_exact);
                            String match = matchText.getText().toString();
                            trigger.setMatch(match);
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(exact.isChecked() ? "Exact" : "Partial");
                            trigger.setExtraData(extras);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Trigger.IncomingCallByCaller":
                case "Trigger.OutgoingCallByCaller":
                case "Trigger.AnsweredCallByCaller":
                case "Trigger.EndedCallByCaller":
                case "Trigger.SMSBySender":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    textView.setHint("Name/number");
                    textView.setAdapter(Main.getTextViewAdapter(context, "contacts"));
                    textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                            String selection = ((TextView) view).getText().toString();
                            selection = selection.substring(selection.indexOf("(") + 1, selection.indexOf(")"));
                            textView.setText(selection);
                        }
                    });
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AutoCompleteTextView senderField = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(R.id.text);
                            String sender = senderField.getText().toString();
                            trigger.setMatch(sender);
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(Main.getNameFromNumber(context, sender));
                            trigger.setExtraData(extras);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Trigger.Interval":
                    alert = new AlertDialog.Builder(context);
                    final String timeChoices[] = new String[]{"1 minute", "5 minutes", "10 minutes", "30 minutes", "60 minutes", "120 minutes"};
                    alert.setItems(timeChoices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            trigger.setMatch(timeChoices[which]);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.show();
                    break;
                case "Trigger.Time":
                    TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String meridian = "AM";
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(String.valueOf(hourOfDay));
                            extras.add(String.valueOf(minute));
                            if (hourOfDay >= 12) {
                                hourOfDay = hourOfDay - 12;
                                if (hourOfDay == 0) hourOfDay = 12;
                                meridian = "PM";
                            }
                            String leadingZeroHour = (hourOfDay < 10) ? "0" : "";
                            String leadingZeroMinute = (minute < 10) ? "0" : "";
                            trigger.setMatch(leadingZeroHour + hourOfDay + ":" + leadingZeroMinute + minute + " " + meridian);
                            trigger.setExtraData(extras);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    }, 0, 0, false);
                    timePicker.setTitle(null);
                    timePicker.show();
                    break;
                case "Trigger.BluetoothByDeviceName":
                case "Trigger.WifiConnectedBySSID":
                case "Trigger.WifiDisconnectedBySSID":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    if (triggerType.equals("Trigger.BluetoothByDeviceName")) {
                        textView.setHint("Device name");
                        textView.setAdapter(Main.getTextViewAdapter(context, "bluetooth"));
                    } else {
                        textView.setHint("SSID");
                        textView.setAdapter(Main.getTextViewAdapter(context, "ssids"));
                    }
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText textField = (EditText) ((AlertDialog) dialog).findViewById(R.id.text);
                            String match = textField.getText().toString();
                            trigger.setMatch(match);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Trigger.NewTweetByUser":
                case "Trigger.NewDMByUser":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    textView.setHint("Username");
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AutoCompleteTextView senderField = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(R.id.text);
                            String sender = senderField.getText().toString();
                            trigger.setMatch(sender);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Trigger.NewRedditPost":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    textView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    textView.setHint("Subreddit");
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AutoCompleteTextView subredditField = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(R.id.text);
                            String subreddit = subredditField.getText().toString();
                            trigger.setMatch(subreddit);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Trigger.NewRedditPostByUser":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_doublesingleline, null);
                    EditText textView = (EditText) view.findViewById(R.id.text);
                    textView.setHint("Subreddit");
                    EditText detailView = (EditText) view.findViewById(R.id.detail);
                    detailView.setHint("Username");
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText subredditField = (EditText) ((AlertDialog) dialog).findViewById(R.id.text);
                            String subreddit = subredditField.getText().toString();
                            EditText usernameField = (EditText) ((AlertDialog) dialog).findViewById(R.id.detail);
                            String username = usernameField.getText().toString();
                            trigger.setMatch(subreddit);
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(username);
                            trigger.setExtraData(extras);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Trigger.NewRedditPostByTitle":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_redditpost, null);
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText subredditField = (EditText) ((AlertDialog) dialog).findViewById(R.id.text);
                            String subreddit = subredditField.getText().toString();
                            EditText matchText = (EditText) ((AlertDialog) dialog).findViewById(R.id.content);
                            RadioButton exact = (RadioButton) ((AlertDialog) dialog).findViewById(R.id.radio_exact);
                            String match = matchText.getText().toString();
                            trigger.setMatch(subreddit);
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(exact.isChecked() ? "Exact" : "Partial");
                            extras.add(match);
                            trigger.setExtraData(extras);
                            task.addNewTrigger(context, trigger);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                default:
                    task.addNewTrigger(context, trigger);
                    getActivity().finish();
            }
        }

    }

    public static class ConditionFragment extends PreferenceFragment {

        private View view;
        private AutoCompleteTextView textView;
        private AlertDialog.Builder alert;
        private static List<String> elevatedConditions = Arrays.asList("Condition.NoCall", "Condition.AnyIncomingCall",
                "Condition.IncomingCallByCaller", "Condition.AnyCall", "Condition.CallByCaller");

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Main.userIsPremium(getActivity()) || BuildConfig.DEBUG) addPreferencesFromResource(R.xml.conditions);
            else addPreferencesFromResource(R.xml.conditions_free);
        }

        @Override
        public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getSummary() != null && preference.getSummary().toString().equals("Premium only")) {
                Main.purchasePremium(getActivity());
                getActivity().finish();
                return false;
            }
            if (preference.getSummary() != null && preference.getSummary().toString().equals("Not logged in")) {
                Intent settings = new Intent(context, Settings.class);
                startActivity(settings);
                getActivity().finish();
                return false;
            }
            if (preference instanceof PreferenceScreen) return false;
            String conditionType = preference.getKey();
            if (elevatedConditions.contains(conditionType) && android.os.Build.VERSION.SDK_INT >= 23) {
                switch (conditionType) {
                    case "Condition.NoCall":
                    case "Condition.AnyIncomingCall":
                    case "Condition.AnyCall":
                        if (Main.weHavePermission(context, Manifest.permission.READ_PHONE_STATE)) createCondition(conditionType);
                        else requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, elevatedConditions.indexOf(conditionType));
                        break;
                    case "Condition.IncomingCallByCaller":
                    case "Condition.CallByCaller":
                        if (Main.weHavePermission(context, Manifest.permission.READ_CONTACTS) &&
                                Main.weHavePermission(context, Manifest.permission.READ_PHONE_STATE))
                            createCondition(conditionType);
                        else requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS},
                                elevatedConditions.indexOf(conditionType));
                        break;
                }
            } else createCondition(conditionType);
            return true;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createCondition(elevatedConditions.get(requestCode));
            }
        }

        private void createCondition(String conditionType) {
            final Condition condition = new Condition();
            condition.setType(conditionType);
            switch (conditionType) {
                case "Condition.IncomingCallByCaller":
                case "Condition.CallByCaller":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    textView.setHint("Name/number");
                    textView.setAdapter(Main.getTextViewAdapter(context, "contacts"));
                    textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                            String selection = ((TextView) view).getText().toString();
                            selection = selection.substring(selection.indexOf("(") + 1, selection.indexOf(")"));
                            textView.setText(selection);
                        }
                    });
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AutoCompleteTextView senderField = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(R.id.text);
                            String sender = senderField.getText().toString();
                            condition.setMatch(sender);
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(Main.getNameFromNumber(context, sender));
                            condition.setExtraData(extras);
                            task.addNewCondition(context, condition);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Condition.BatteryPercentage":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_seekbar_greaterlesser, null);
                    ((SeekBar) view.findViewById(R.id.seek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            TextView seekText = (TextView) view.findViewById(R.id.seek_text);
                            seekText.setText(progress + "%");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SeekBar seek = (SeekBar) ((AlertDialog) dialog).findViewById(R.id.seek);
                            RadioButton greater = (RadioButton) ((AlertDialog) dialog).findViewById(R.id.radio_greater);
                            String greaterThan = greater.isChecked() ? ">" : "<";
                            condition.setMatch(greaterThan + seek.getProgress());
                            task.addNewCondition(context, condition);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Condition.TimePeriod":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time_period, null);
                    final TextView startTimeBox = (TextView) view.findViewById(R.id.start_time);
                    final ArrayList<String> extras = new ArrayList<>(4);
                    while (extras.size() < 4) extras.add("0");
                    startTimeBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    String meridian = "AM";
                                    extras.add(0, String.valueOf(hourOfDay));
                                    extras.add(1, String.valueOf(minute));
                                    if (hourOfDay >= 12) {
                                        hourOfDay = hourOfDay - 12;
                                        if (hourOfDay == 0) hourOfDay = 12;
                                        meridian = "PM";
                                    }
                                    String leadingZeroHour = (hourOfDay < 10) ? "0" : "";
                                    String leadingZeroMinute = (minute < 10) ? "0" : "";
                                    startTimeBox.setText(leadingZeroHour + hourOfDay + ":" + leadingZeroMinute + minute + " " + meridian);
                                }
                            }, 0, 0, false);
                            timePicker.setTitle(null);
                            timePicker.show();
                        }
                    });
                    final TextView endTimeBox = (TextView) view.findViewById(R.id.end_time);
                    endTimeBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    String meridian = "AM";
                                    extras.add(2, String.valueOf(hourOfDay));
                                    extras.add(3, String.valueOf(minute));
                                    if (hourOfDay >= 12) {
                                        hourOfDay = hourOfDay - 12;
                                        if (hourOfDay == 0) hourOfDay = 12;
                                        meridian = "PM";
                                    }
                                    String leadingZeroHour = (hourOfDay < 10) ? "0" : "";
                                    String leadingZeroMinute = (minute < 10) ? "0" : "";
                                    endTimeBox.setText(leadingZeroHour + hourOfDay + ":" + leadingZeroMinute + minute + " " + meridian);
                                }
                            }, 0, 0, false);
                            timePicker.setTitle(null);
                            timePicker.show();
                        }
                    });
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            condition.setMatch(startTimeBox.getText() + " - " + endTimeBox.getText());
                            condition.setExtraData(extras);
                            task.addNewCondition(context, condition);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Condition.WifiConnectedBySSID":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    textView.setHint("SSID");
                    textView.setAdapter(Main.getTextViewAdapter(context, "ssids"));
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText textField = (EditText) ((AlertDialog) dialog).findViewById(R.id.text);
                            String match = textField.getText().toString();
                            condition.setMatch(match.replace("\"", ""));
                            task.addNewCondition(context, condition);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                default:
                    task.addNewCondition(context, condition);
                    getActivity().finish();
            }
        }

    }

    public static class ActionFragment extends PreferenceFragment {

        private View view;
        private AutoCompleteTextView textView;
        private AlertDialog.Builder alert;
        private static List<String> elevatedActions = Arrays.asList("Action.StartCall", "Action.SendSMS", "Action.TTSSMS", "Action.LockModeNone",
                "Action.LockModePin", "Action.LockModePassword", "Action.Timeout", "Action.PlaySoundNotification");

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Main.userIsPremium(getActivity()) || BuildConfig.DEBUG) addPreferencesFromResource(R.xml.actions);
            else addPreferencesFromResource(R.xml.actions_free);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (!prefs.getString("twitterID", "").equals("")) {
                Preference[] twitterPrefs = new Preference[]{findPreference("Action.SendTweet"), findPreference("Action.TwitterMessage")};
                for (Preference preference : twitterPrefs) {
                    if (preference.getSummary().equals("Not logged in")) preference.setSummary("");
                }
            }
        }

        @Override
        public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getSummary() != null && preference.getSummary().toString().equals("Premium only")) {
                Main.purchasePremium(getActivity());
                getActivity().finish();
                return false;
            }
            if (preference.getSummary() != null && preference.getSummary().toString().equals("Not logged in")) {
                Intent settings = new Intent(context, Settings.class);
                startActivity(settings);
                getActivity().finish();
                return false;
            }
            if (preference instanceof PreferenceScreen) return false;
            final String actionType = preference.getKey();
            if (elevatedActions.contains(actionType) && android.os.Build.VERSION.SDK_INT >= 23) {
                switch (actionType) {
                    case "Action.StartCall":
                        if (Main.weHavePermission(context, Manifest.permission.READ_CONTACTS) &&
                                Main.weHavePermission(context, Manifest.permission.CALL_PHONE))
                            createAction(actionType);
                        else requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS},
                                elevatedActions.indexOf(actionType));
                        break;
                    case "Action.SendSMS":
                    case "Action.TTSSMS":
                        if (Main.weHavePermission(context, Manifest.permission.READ_CONTACTS) &&
                                Main.weHavePermission(context, Manifest.permission.SEND_SMS))
                            createAction(actionType);
                        else requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS},
                                elevatedActions.indexOf(actionType));
                        break;
                    case "Action.LockModeNone":
                    case "Action.LockModePin":
                    case "Action.LockModePassword":
                    case "Action.Timeout":
                        if (Main.weHavePermission(context, "device_admin")) createAction(actionType);
                        else {
                            ComponentName component = new ComponentName(context, DeviceAdmin.class);
                            final Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component);
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                    "Screen timeout and lock functions require the app to have device admin permissions.");
                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setTitle("Device Admin Required");
                            alertDialog.setMessage("This action requires device admin permissions to function. You will now be taken to security settings to enable this permission.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startActivityForResult(intent, elevatedActions.indexOf(actionType));
                                        }
                                    });
                            alertDialog.show();
                        }
                        break;
                    case "Action.PlaySoundNotification":
                        if (Main.weHavePermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) createAction(actionType);
                        else requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, elevatedActions.indexOf(actionType));
                }
            } else createAction(actionType);
            return true;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createAction(elevatedActions.get(requestCode));
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
                Action action = new Action();
                action.setCommand("Action.PlaySoundCustom");
                String customSoundUri = data.getData().toString();
                String fileName = data.getData().getLastPathSegment();
                ArrayList<String> actionData = new ArrayList();
                actionData.add(fileName);
                actionData.add(customSoundUri);
                action.setData(actionData);
                task.addNewAction(context, action);
                getActivity().finish();
            } else if (resultCode == Activity.RESULT_OK) createAction(elevatedActions.get(requestCode));
        }

        private void createAction(String actionType) {
            final Action action = new Action();
            action.setCommand(actionType);
            switch (actionType) {
                case "Action.StartCall":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    textView.setHint("Name/number");
                    textView.setAdapter(Main.getTextViewAdapter(context, "contacts"));
                    textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                            String selection = ((TextView) view).getText().toString();
                            selection = selection.substring(selection.indexOf("(") + 1, selection.indexOf(")"));
                            textView.setText(selection);
                        }
                    });
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AutoCompleteTextView senderField = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(R.id.text);
                            String sender = senderField.getText().toString();
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(sender);
                            extras.add(Main.getNameFromNumber(context, sender));
                            action.setData(extras);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.show();
                    break;
                case "Action.AcceptCall":
                    Set<String> listeners = NotificationManagerCompat.getEnabledListenerPackages(context);
                    if (!listeners.contains(context.getPackageName())) {
                        alert = new AlertDialog.Builder(context);
                        alert.setMessage("This function requires Andromatic to be granted access to notifications. Would you like to be taken to " +
                                "the menu to enable this now?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                                getActivity().finish();
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        });
                        task.addNewAction(context, action);
                        alert.show();
                    } else {
                        task.addNewAction(context, action);
                        getActivity().finish();
                    }
                    break;
                case "Action.SendSMS":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_sendmessage, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.to);
                    textView.setHint("Name/number");
                    textView.setAdapter(Main.getTextViewAdapter(context, "contacts"));
                    textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                            String selection = ((TextView) view).getText().toString();
                            selection = selection.substring(selection.indexOf("(") + 1, selection.indexOf(")"));
                            textView.setText(selection);
                        }
                    });
                    alert.setItems(null, null);
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditText to = (EditText) ((AlertDialog) dialog).findViewById(R.id.to);
                            EditText message = (EditText) ((AlertDialog) dialog).findViewById(R.id.content);
                            ArrayList<String> actionData = new ArrayList();
                            actionData.add(Main.getNameFromNumber(context, to.getText().toString()));
                            actionData.add(to.getText().toString());
                            actionData.add(message.getText().toString());
                            action.setData(actionData);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Action.LaunchApp":
                    alert = new AlertDialog.Builder(context);
                    PackageManager pm = getActivity().getPackageManager();
                    final ArrayList<String> appChoices = new ArrayList<>();
                    final ArrayList<String> appChoicePackage = new ArrayList<>();
                    final ArrayList<String> appChoiceName = new ArrayList<>();
                    Intent main = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> packages = pm.queryIntentActivities(main, 0);
                    for (ResolveInfo appInfo : packages) {
                        appChoices.add(appInfo.loadLabel(pm).toString());
                        appChoicePackage.add(appInfo.activityInfo.packageName);
                        appChoiceName.add(appInfo.activityInfo.name);
                    }
                    alert.setItems(appChoices.toArray(new String[appChoices.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<String> actionData = new ArrayList();
                            actionData.add(appChoices.get(which));
                            actionData.add(appChoicePackage.get(which));
                            actionData.add(appChoiceName.get(which));
                            action.setData(actionData);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.show();
                    break;
                case "Action.PlaySoundNotification":
                    alert = new AlertDialog.Builder(context);
                    RingtoneManager manager = new RingtoneManager(context);
                    manager.setType(RingtoneManager.TYPE_NOTIFICATION);
                    Cursor cursor = manager.getCursor();
                    final ArrayList<String> soundChoices = new ArrayList<>();
                    final ArrayList<String> soundUris = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        soundChoices.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
                        soundUris.add(cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX));
                    }
                    alert.setItems(soundChoices.toArray(new String[soundChoices.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<String> actionData = new ArrayList();
                            actionData.add(soundChoices.get(which));
                            actionData.add(soundUris.get(which));
                            action.setData(actionData);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.show();
                    break;
                case "Action.PlaySoundCustom":
                    Intent chooseCustomSound = new Intent();
                    chooseCustomSound.setType("audio/*");
                    chooseCustomSound.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    startActivityForResult(chooseCustomSound, 99);
                    break;
                case "Action.ShowNotification":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_doublesingleline, null);
                    EditText titleView = (EditText) view.findViewById(R.id.text);
                    titleView.setHint("Title");
                    EditText detailView = (EditText) view.findViewById(R.id.detail);
                    detailView.setHint("Message");
                    alert.setItems(null, null);
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditText title = (EditText) ((AlertDialog) dialog).findViewById(R.id.text);
                            EditText message = (EditText) ((AlertDialog) dialog).findViewById(R.id.detail);
                            ArrayList<String> actionData = new ArrayList();
                            actionData.add(title.getText().toString());
                            actionData.add(message.getText().toString());
                            action.setData(actionData);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Action.TTSTime":
                    action.setData(1);
                    task.addNewAction(context, action);
                    getActivity().finish();
                    break;
                case "Action.TTSSMS":
                    action.setData(1);
                    task.addNewAction(context, action);
                    getActivity().finish();
                    break;
                case "Action.TTSCustom":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_autocomplete, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.text);
                    textView.setHint("Text...");
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AutoCompleteTextView textField = (AutoCompleteTextView) ((AlertDialog) dialog).findViewById(R.id.text);
                            String text = textField.getText().toString();
                            ArrayList<String> extras = new ArrayList<>();
                            extras.add(text);
                            action.setData(extras);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.show();
                    break;
                case "Action.LockModeNone":
                case "Action.LockModePin":
                case "Action.LockModePassword":
                    if (!actionType.equals("Action.LockModeNone")) {
                        alert = new AlertDialog.Builder(context);
                        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_singleline, null);
                        EditText textView = (EditText) view.findViewById(R.id.text);
                        textView.setHint((actionType.equals("Action.LockModePin")) ? "PIN" : "Password");
                        textView.setInputType((actionType.equals("Action.LockModePin")) ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);
                        textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        alert.setView(view);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText password = (EditText) ((AlertDialog) dialog).findViewById(R.id.text);
                                ArrayList data = new ArrayList();
                                data.add(password.getText().toString());
                                action.setData(data);
                                task.addNewAction(context, action);
                                getActivity().finish();
                            }
                        });
                        alert.setNegativeButton("Cancel", null);
                        alert.show();
                    } else {
                        action.setData(0);
                        task.addNewAction(context, action);
                        getActivity().finish();
                    }
                    break;
                case "Action.Timeout":
                    alert = new AlertDialog.Builder(context);
                    final String timeoutChoices[] = new String[]{"15 seconds", "30 seconds", "1 minute", "2 minutes", "5 minutes", "10 minutes"};
                    alert.setItems(timeoutChoices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            action.setData(which);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.show();
                    break;
                case "Action.MediaVolume":
                case "Action.RingerVolume":
                case "Action.NotificationVolume":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_seekbar, null);
                    ((SeekBar) view.findViewById(R.id.seek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            TextView seekText = (TextView) view.findViewById(R.id.seek_text);
                            seekText.setText(progress * 10 + "%");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SeekBar seek = (SeekBar) ((AlertDialog) dialog).findViewById(R.id.seek);
                            action.setData(seek.getProgress() * 10);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Action.SendTweet":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_multiline, null);
                    EditText textView = (EditText) view.findViewById(R.id.text);
                    textView.setHint("Tweet");
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String text = ((EditText) ((AlertDialog) dialog).findViewById(R.id.text)).getText().toString();
                            ArrayList data = new ArrayList();
                            data.add(text);
                            action.setData(data);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                case "Action.TwitterMessage":
                    alert = new AlertDialog.Builder(context);
                    view = getActivity().getLayoutInflater().inflate(R.layout.dialog_sendmessage, null);
                    textView = (AutoCompleteTextView) view.findViewById(R.id.to);
                    textView.setHint("User");
                    alert.setView(view);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditText to = (EditText) ((AlertDialog) dialog).findViewById(R.id.to);
                            EditText message = (EditText) ((AlertDialog) dialog).findViewById(R.id.content);
                            ArrayList<String> actionData = new ArrayList();
                            actionData.add(to.getText().toString());
                            actionData.add(message.getText().toString());
                            action.setData(actionData);
                            task.addNewAction(context, action);
                            getActivity().finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                    break;
                default:
                    task.addNewAction(context, action);
                    getActivity().finish();
            }
        }

    }

}
