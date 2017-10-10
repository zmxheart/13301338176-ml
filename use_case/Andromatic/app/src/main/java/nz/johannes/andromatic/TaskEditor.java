package nz.johannes.andromatic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TaskEditor extends AppCompatActivity {

    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskedit);
        task = Main.getTask(this, getIntent().getStringExtra("task"));
        setTitle(task.getName());
        populateTriggerList();
        populateConditionsList();
        populateActionsList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        }, 1);
    }

    private void populateTriggerList() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final ListView triggerList = (ListView) findViewById(R.id.triggerList);
        final ArrayList<Trigger> listItems = new ArrayList<>();
        final Trigger.TriggerListViewAdapter adapter = new Trigger().new TriggerListViewAdapter(this, R.layout.default_list_row, listItems);
        registerForContextMenu(triggerList);
        triggerList.setAdapter(adapter);
        triggerList.setClickable(true);
        triggerList.setLongClickable(true);
        triggerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Trigger trigger = (Trigger) triggerList.getItemAtPosition(position);
                if (trigger.getType().equals("Add new...")) {
                    addComponent("TRIGGER");
                }
            }
        });
        triggerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Trigger triggerToDelete = (Trigger) triggerList.getItemAtPosition(position);
                if (triggerToDelete.getType().equals("Add new...")) {
                    addComponent("TRIGGER");
                    return true;
                }
                alert.setTitle("Delete trigger?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        task.removeTrigger(getBaseContext(), triggerToDelete);
                        populateTriggerList();
                    }
                });
                alert.setNegativeButton("No", null);
                alert.show();
                return true;
            }
        });
        for (Trigger trigger : task.getTriggers()) listItems.add(trigger);
        Collections.sort(listItems, new Comparator<Trigger>() {
            @Override
            public int compare(Trigger lhs, Trigger rhs) {
                return lhs.getType().compareTo(rhs.getType());
            }
        });
        Trigger addNew = new Trigger();
        addNew.setType("Add new...");
        listItems.add(addNew);
        adapter.notifyDataSetChanged();
        setDynamicListHeight(triggerList);
    }

    private void populateConditionsList() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final ListView conditionList = (ListView) findViewById(R.id.conditionList);
        final ArrayList<Condition> listItems = new ArrayList<>();
        final Condition.ConditionListViewAdapter adapter = new Condition().new ConditionListViewAdapter(this, R.layout.default_list_row, listItems);
        registerForContextMenu(conditionList);
        conditionList.setAdapter(adapter);
        conditionList.setClickable(true);
        conditionList.setLongClickable(true);
        conditionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Condition condition = (Condition) conditionList.getItemAtPosition(position);
                if (condition.getType().equals("Add new...")) {
                    addComponent("CONDITION");
                }
            }
        });
        conditionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Condition conditionToDelete = (Condition) conditionList.getItemAtPosition(position);
                if (conditionToDelete.getType().equals("Add new...")) {
                    addComponent("CONDITION");
                    return true;
                }
                alert.setTitle("Delete condition?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        task.removeCondition(getBaseContext(), conditionToDelete);
                        populateConditionsList();
                    }
                });
                alert.setNegativeButton("No", null);
                alert.show();
                return true;
            }
        });
        for (Condition condition : task.getConditions()) listItems.add(condition);
        Collections.sort(listItems, new Comparator<Condition>() {
            @Override
            public int compare(Condition lhs, Condition rhs) {
                return lhs.getType().compareTo(rhs.getType());
            }
        });
        Condition addNew = new Condition();
        addNew.setType("Add new...");
        listItems.add(addNew);
        adapter.notifyDataSetChanged();
        setDynamicListHeight(conditionList);
    }

    private void populateActionsList() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final ListView actionList = (ListView) findViewById(R.id.actionList);
        final ArrayList<Action> listItems = new ArrayList<>();
        final Action.ActionListViewAdapter adapter = new Action().new ActionListViewAdapter(this, R.layout.default_list_row, listItems);
        registerForContextMenu(actionList);
        actionList.setAdapter(adapter);
        actionList.setClickable(true);
        actionList.setLongClickable(true);
        actionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Action action = (Action) actionList.getItemAtPosition(position);
                if (action.getCommand().equals("Add new...")) {
                    addComponent("ACTION");
                }
            }
        });
        actionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Action actionToDelete = (Action) actionList.getItemAtPosition(position);
                if (actionToDelete.getCommand().equals("Add new...")) {
                    addComponent("ACTION");
                    return true;
                }
                alert.setTitle("Delete action?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        task.removeAction(getBaseContext(), actionToDelete);
                        populateActionsList();
                    }
                });
                alert.setNegativeButton("No", null);
                alert.show();
                return true;
            }
        });
        for (Action action : task.getActions()) listItems.add(action);
        Collections.sort(listItems, new Comparator<Action>() {
            @Override
            public int compare(Action lhs, Action rhs) {
                return lhs.getCommand().compareTo(rhs.getCommand());
            }
        });
        Action addNew = new Action();
        addNew.setCommand("Add new...");
        listItems.add(addNew);
        adapter.notifyDataSetChanged();
        setDynamicListHeight(actionList);
    }

    private void addComponent(String type) {
        Intent intent = new Intent(this, AddComponent.class);
        intent.putExtra("Task", task.getName());
        intent.setAction(type);
        startActivityForResult(intent, 0);
    }

    private static void setDynamicListHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) return;
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }

}
