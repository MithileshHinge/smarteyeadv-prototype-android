package com.example.app1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Home on 13-07-2017.
 */
public class ActivityLogFragment extends Fragment {

    ActivityLogCustomAdapter logCustomAdapter;
    ExpandableListView expandableListView;
    public static String datenow;

    List<String> listDataHeaders;
    HashMap<String, List<ActivityLogDatabaseRow>> listDataChild;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activitylog_fragment, container, false);
        getActivity().setTitle("Activity");

        NotifyService.db = new ActivityLogDatabaseHandler(getActivity());

        //datenow = ActivityLogDatabaseRow.dateFormat.format(new Date());
        //db.addRow(new ActivityLogDatabaseRow("Ravi", datenow, 0));


        expandableListView= (ExpandableListView) v.findViewById(R.id.listView1);


        listDataHeaders = new ArrayList<>();
        listDataChild = new HashMap<>();

        List<ActivityLogDatabaseRow> databaseItems = NotifyService.db.getAllRows(); // get all items from db TODO: later optimize this to only load 20 items

        Log.e("Database size", String.valueOf(databaseItems.size()));

        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


        for (int i=databaseItems.size() - 1; i>=0; i--){
            //sort all items into different ArrayLists a/c to date

            ActivityLogDatabaseRow row = databaseItems.get(i);
            String datetime = row.getDateTime();
            String date = datetime.substring(8,10) + " " + months[Integer.parseInt(datetime.substring(5,7)) - 1];

            List<ActivityLogDatabaseRow> tempItem;
            if (listDataChild.containsKey(date)){
                tempItem = listDataChild.get(date);
            }else {
                tempItem = new ArrayList<>();
                listDataHeaders.add(date);
            }
            tempItem.add(row);
            listDataChild.put(date, tempItem);
        }

        // Demo data

        /*
        listDataHeaders.add("25 October");
        listDataHeaders.add("24 October");
        listDataHeaders.add("23 October");

        List<ActivityLogDatabaseRow> oct23 = new ArrayList<>();
        oct23.add(new ActivityLogDatabaseRow("Suspicious Activity", "2017-10-23 19:50:12", 0));
        oct23.add(new ActivityLogDatabaseRow("Suspicious Activity", "2017-10-23 20:50:12", 0));
        oct23.add(new ActivityLogDatabaseRow("Face Found", "2017-10-23 21:50:12", 0));
        oct23.add(new ActivityLogDatabaseRow("Face Found", "2017-10-23 22:50:12", 0));

        List<ActivityLogDatabaseRow> oct24 = new ArrayList<>();
        oct24.add(new ActivityLogDatabaseRow("Face Found", "2017-10-24 09:50:12", 0));
        oct24.add(new ActivityLogDatabaseRow("Face Found", "2017-10-24 10:50:12", 0));
        oct24.add(new ActivityLogDatabaseRow("Face Found", "2017-10-24 15:50:12", 0));

        List<ActivityLogDatabaseRow> oct25 = new ArrayList<>();
        oct25.add(new ActivityLogDatabaseRow("Face Found", "2017-10-25 11:50:12", 0));
        oct25.add(new ActivityLogDatabaseRow("Suspicious Activity", "2017-10-25 12:50:12", 0));
        oct25.add(new ActivityLogDatabaseRow("Abrupt End", "2017-10-25 13:50:12", 0));

        listDataChild.put(listDataHeaders.get(0), oct23);
        listDataChild.put(listDataHeaders.get(1), oct24);
        listDataChild.put(listDataHeaders.get(2), oct25);
        */

        logCustomAdapter = new ActivityLogCustomAdapter(getContext(), listDataHeaders, listDataChild);

        expandableListView.setAdapter(logCustomAdapter);

        /*
        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //db.addRow(new ActivityLogDatabaseRow("ISHA" , datenow));
                //Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        */

        return v;
    }
}