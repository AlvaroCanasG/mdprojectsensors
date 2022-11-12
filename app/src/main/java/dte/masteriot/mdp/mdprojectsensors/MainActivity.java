// Parts of the code of this example app have ben taken from:
// https://enoent.fr/posts/recyclerview-basics/
// https://developer.android.com/guide/topics/ui/layout/recyclerview

package dte.masteriot.mdp.mdprojectsensors;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final String LOGSLOADWEBCONTENT = "LOGSLOADWEBCONTENT";
    private static final String TAG = "ListOfItems, MainActivity";

    // App-specific dataset:
    private static final List<Item> listofitems = new ArrayList<>();
    private static boolean listofitemsinitialized = false;

    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;
    private MyOnItemActivatedListener onItemActivatedListener;
    private Object next;
    String TomatoLight, TomatoTemperature, TomatoHumidity, TomatoDate = "---";
    String EggplantLight, EggplantTemperature, EggplantHumidity, EggplantDate = "---";
    String PepperLight, PepperTemperature, PepperHumidity, PepperDate = "---";
    String GreenBeanLight, GreenBeanTemperature, GreenBeanHumidity, GreenBeanDate = "---";
    String ZucchiniLight, ZucchiniTemperature, ZucchiniHumidity, ZucchiniDate = "---";
    String CucumberLight, CucumberTemperature, CucumberHumidity, CucumberDate = "---";
    String MelonLight, MelonTemperature, MelonHumidity, MelonDate = "---";
    String WatermelonLight, WatermelonTemperature, WatermelonHumidity, WatermelonDate = "---";

    ExecutorService es;//[MGM] Background
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the list of items (the dataset):
        initListOfItems();

        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new MyAdapter(this, listofitems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // some options for the layout manager:  GridLayoutManager, LinearLayoutManager, StaggeredGridLayoutManager
        // initially, a linear layout is chosen:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Selection tracker (to allow for selection of items):
        onItemActivatedListener = new MyOnItemActivatedListener(this, recyclerViewAdapter);
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED, recyclerViewAdapter),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListener)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);

        if (savedInstanceState != null) {
            // Restore state related to selections previously made
            tracker.onRestoreInstanceState(savedInstanceState);
        }
        /*
        handler = new Handler(Looper.getMainLooper()) { //Handler for the message received from the background. Depending on the key (topic), the message will be assigned to a specif String variable
            @Override                                   //Then, the attributes (Temperature, Humidity and Light) of each item are updated.
            public void handleMessage(Message inputMessage) {
                TomatoLight = inputMessage.getData().getString("Tomato/Light");
                TomatoTemperature = inputMessage.getData().getString("Tomato/Temperature");
                TomatoHumidity = inputMessage.getData().getString("Tomato/Humidity");
                TomatoDate = inputMessage.getData().getString("Tomato/Date");
                recyclerViewAdapter.getItemWithKey(0).setParameters(TomatoLight,TomatoHumidity, TomatoTemperature, TomatoDate);
                if(Float.parseFloat(TomatoTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(0).setStatus(false);
                }else if(Float.parseFloat((TomatoTemperature)) < R.dimen.MINTEMP){
                    recyclerViewAdapter.getItemWithKey(0).setStatus(false);
                }

                PepperLight = inputMessage.getData().getString("Pepper/Light");
                PepperTemperature = inputMessage.getData().getString("Pepper/Temperature");
                PepperHumidity = inputMessage.getData().getString("Pepper/Humidity");
                PepperDate = inputMessage.getData().getString("Pepper/Date");
                recyclerViewAdapter.getItemWithKey(1).setParameters(PepperLight,PepperHumidity, PepperTemperature, PepperDate);
                if(Float.parseFloat(PepperTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(1).setStatus(false);
                }else if(Float.parseFloat((PepperTemperature)) < R.dimen.MINTEMP) {
                    recyclerViewAdapter.getItemWithKey(1).setStatus(false);
                }

                EggplantLight = inputMessage.getData().getString("Eggplant/Light");
                EggplantTemperature = inputMessage.getData().getString("Eggplant/Temperature");
                EggplantHumidity = inputMessage.getData().getString("Eggplant/Humidity");
                EggplantDate = inputMessage.getData().getString("Eggplant/Date");
                recyclerViewAdapter.getItemWithKey(2).setParameters(EggplantLight,EggplantHumidity, EggplantTemperature, EggplantDate);
                if(Float.parseFloat(EggplantTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(2).setStatus(false);
                }else if(Float.parseFloat((EggplantTemperature)) < R.dimen.MINTEMP){
                    recyclerViewAdapter.getItemWithKey(2).setStatus(false);
                }


                GreenBeanLight = inputMessage.getData().getString("GreenBean/Light");
                GreenBeanTemperature = inputMessage.getData().getString("GreenBean/Temperature");
                GreenBeanHumidity = inputMessage.getData().getString("GreenBean/Humidity");
                GreenBeanDate = inputMessage.getData().getString("GreenBean/Date");
                recyclerViewAdapter.getItemWithKey(3).setParameters(GreenBeanLight,GreenBeanHumidity, GreenBeanTemperature, GreenBeanDate);
                if(Float.parseFloat(GreenBeanTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(3).setStatus(false);
                }else if(Float.parseFloat((GreenBeanTemperature)) < R.dimen.MINTEMP) {
                    recyclerViewAdapter.getItemWithKey(3).setStatus(false);
                }

                ZucchiniLight = inputMessage.getData().getString("Zucchini/Light");
                ZucchiniTemperature = inputMessage.getData().getString("Zucchini/Temperature");
                ZucchiniHumidity = inputMessage.getData().getString("Zucchini/Humidity");
                ZucchiniDate = inputMessage.getData().getString("Zucchini/Date");
                recyclerViewAdapter.getItemWithKey(4).setParameters(ZucchiniLight,ZucchiniHumidity, ZucchiniTemperature, ZucchiniDate);
                if(Float.parseFloat(ZucchiniTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(4).setStatus(false);
                }else if(Float.parseFloat((ZucchiniTemperature)) < R.dimen.MINTEMP) {
                    recyclerViewAdapter.getItemWithKey(4).setStatus(false);
                }


                CucumberLight = inputMessage.getData().getString("Cucumber/Light");
                CucumberTemperature = inputMessage.getData().getString("Cucumber/Temperature");
                CucumberHumidity = inputMessage.getData().getString("Cucumber/Humidity");
                CucumberDate = inputMessage.getData().getString("Cucumber/Date");
                recyclerViewAdapter.getItemWithKey(5).setParameters(CucumberLight,CucumberHumidity, CucumberTemperature, CucumberDate);
                if(Float.parseFloat(CucumberTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(5).setStatus(false);
                }else if(Float.parseFloat((CucumberTemperature)) < R.dimen.MINTEMP) {
                    recyclerViewAdapter.getItemWithKey(5).setStatus(false);
                }


                MelonLight = inputMessage.getData().getString("Melon/Light");
                MelonTemperature = inputMessage.getData().getString("Melon/Temperature");
                MelonHumidity = inputMessage.getData().getString("Melon/Humidity");
                MelonDate = inputMessage.getData().getString("Melon/Date");
                recyclerViewAdapter.getItemWithKey(6).setParameters(MelonLight,MelonHumidity, MelonTemperature, MelonDate);
                if(Float.parseFloat(MelonTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(6).setStatus(false);
                }else if(Float.parseFloat((MelonTemperature)) < R.dimen.MINTEMP) {
                    recyclerViewAdapter.getItemWithKey(6).setStatus(false);
                }


                WatermelonLight = inputMessage.getData().getString("Watermelon/Light");
                WatermelonTemperature = inputMessage.getData().getString("Watermelon/Temperature");
                WatermelonHumidity = inputMessage.getData().getString("Watermelon/Humidity");
                WatermelonDate = inputMessage.getData().getString("Watermelon/Date");
                recyclerViewAdapter.getItemWithKey(7).setParameters(WatermelonLight,WatermelonHumidity, WatermelonTemperature, WatermelonDate);
                if(Float.parseFloat(WatermelonTemperature) > R.dimen.MAXTEMP){
                    recyclerViewAdapter.getItemWithKey(7).setStatus(false);
                }else if(Float.parseFloat((WatermelonTemperature)) < R.dimen.MINTEMP) {
                    recyclerViewAdapter.getItemWithKey(7).setStatus(false);
                }

            }
        };
        es = Executors.newSingleThreadExecutor();
        MQTTSub task = new MQTTSub(handler, this);
        es.execute(task);

         */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_delete:
                confirmation();
                return true;
            case R.id.menu_select_all:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tracker.onSaveInstanceState(outState); // Save state about selections.
    }

    // ------ Initialization of the dataset ------ //

    private void initListOfItems () {

        listofitems.add(new Item("Tomato", TomatoLight,TomatoHumidity,TomatoTemperature,TomatoDate, "March - April - May" , (long) 0 , R.drawable.tomato, true ));
        listofitems.add(new Item("Pepper", PepperLight, PepperHumidity, PepperTemperature,PepperDate, "March - April - May" , (long) 1 , R.drawable.peper , false ));
        listofitems.add(new Item("Eggplant", EggplantLight, EggplantHumidity, EggplantTemperature,EggplantDate, "July - August" , (long) 2 , R.drawable.eggplant , true ));
        listofitems.add(new Item("Green bean", GreenBeanLight,GreenBeanHumidity, GreenBeanTemperature,GreenBeanDate, "May - Jun" , (long) 3 , R.drawable.green_bean , false ));
        listofitems.add(new Item("Zucchini", ZucchiniLight, ZucchiniHumidity, ZucchiniTemperature,ZucchiniDate, "May" , (long) 4 , R.drawable.zucchini, true ));
        listofitems.add(new Item("Cucumber", CucumberLight, CucumberHumidity,CucumberTemperature,CucumberDate,"April" , (long) 5 , R.drawable.cucumber , true ));
        listofitems.add(new Item("Melon", MelonLight,MelonHumidity, MelonTemperature,MelonDate, "March - April - May" , (long) 6 , R.drawable.melon , true ));
        listofitems.add(new Item("Watermelon", WatermelonLight,WatermelonHumidity, WatermelonTemperature,WatermelonDate, "February - March - April" , (long) 7 , R.drawable.watermelon , true ));

        listofitemsinitialized = true;



    }

    // ------ Buttons' on-click listeners ------ //

    public void listLayout(View view) {
        // Button to see in a linear fashion has been clicked:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void gridLayout(View view) {
        // Button to see in a grid fashion has been clicked:
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Intent i = new Intent(MainActivity.this, ThirdActivity.class);
        startActivity(i);
    }

    public void seeCurrentSelection(View view) {
        // Button "see current selection" has been clicked:

        Iterator iteratorSelectedItemsKeys = tracker.getSelection().iterator();
        // This iterator allows to navigate through the keys of the currently selected items.
        // Complete info on getSelection():
        // https://developer.android.com/reference/androidx/recyclerview/selection/SelectionTracker#getSelection()
        // Complete info on class Selection (getSelection() returns an object of this class):
        // https://developer.android.com/reference/androidx/recyclerview/selection/Selection

        String text = "";
        while (iteratorSelectedItemsKeys.hasNext()) {
            text += iteratorSelectedItemsKeys.next().toString();
            if (iteratorSelectedItemsKeys.hasNext()) {
                text += ", ";
            }
        }
        text = "Keys of currently selected items = \n" + text;
        Intent i = new Intent(this, SecondActivity.class);
        i.putExtra("text", text);
        startActivity(i);
    }

    public void Eliminate() {
        // Button "see current selection" has been clicked:

        Iterator iteratorSelectedItemsKeys = tracker.getSelection().iterator();

        while (iteratorSelectedItemsKeys.hasNext()) {
            recyclerViewAdapter.removeItem(Long.parseLong(iteratorSelectedItemsKeys.next().toString()));
            //recyclerViewAdapter.notifyItemRemoved(Integer.parseInt(iteratorSelectedItemsKeys.next().toString()));
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }
    public void confirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want delete ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Eliminate();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    /*
    public void buttonAsyncListener(View view) {
        //Log.d(logTag, "Scheduling new task in background thread");
        es.execute(new LengthyTask());
    }


     */

}