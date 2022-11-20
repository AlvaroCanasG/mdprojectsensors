package dte.masteriot.mdp.mdprojectsensors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;

public class MyOnItemActivatedListener implements OnItemActivatedListener {

    // This class serves to "Register an OnItemActivatedListener to be notified when an item
    // is activated (tapped or double clicked)."
    // [https://developer.android.com/reference/androidx/recyclerview/selection/OnItemActivatedListener]

    private static final String TAG = "ListOfItems, MyOnItemActivatedListener";

    private Context context;
    dte.masteriot.mdp.mdprojectsensors.MyAdapter adapter;

    public MyOnItemActivatedListener(Context context, dte.masteriot.mdp.mdprojectsensors.MyAdapter ad) {
        this.context = context;
        this.adapter = ad;
    }

    // ------ Implementation of methods ------ //

    @SuppressLint("LongLogTag")
    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails itemdetails,
                                   @NonNull MotionEvent e) {

//Send the information required for the ThirdActivity
        Intent i = new Intent(context, ThirdActivity.class);
        Item selecteditem = adapter.getItemAtPosition(itemdetails.getPosition());
             i.putExtra("Light", selecteditem.getLight());
             i.putExtra("Humidity", selecteditem.getHumidity());
             i.putExtra("Temperature", selecteditem.getTemperature());
             i.putExtra("Date", selecteditem.getDate());
             i.putExtra("Name", selecteditem.getTitle());

        context.startActivity(i);
        return true;


    }

}
