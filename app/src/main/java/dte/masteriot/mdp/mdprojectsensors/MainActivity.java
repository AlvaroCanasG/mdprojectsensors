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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final String LOGSLOADWEBCONTENT = "LOGSLOADWEBCONTENT";
    private static final String TAG = "ListOfItems, MainActivity";

    MyApplication myApplication = (MyApplication) this.getApplication();
    // App-specific dataset:
    List<Item> listofitems =  myApplication.getListofitems();
    private static boolean listofitemsinitialized = false;

    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;
    private MyOnItemActivatedListener onItemActivatedListener;
    private Object next;

    String  TomatoLight,TomatoTemperature, TomatoHumidity, TomatoDate ;
    String EggplantLight, EggplantTemperature, EggplantHumidity, EggplantDate;
    String PepperLight, PepperTemperature, PepperHumidity, PepperDate;
    String GreenBeanLight, GreenBeanTemperature, GreenBeanHumidity, GreenBeanDate;
    String ZucchiniLight, ZucchiniTemperature, ZucchiniHumidity, ZucchiniDate;
    String CucumberLight, CucumberTemperature, CucumberHumidity, CucumberDate;
    String MelonLight, MelonTemperature, MelonHumidity, MelonDate;
    String WatermelonLight, WatermelonTemperature, WatermelonHumidity, WatermelonDate;


    ExecutorService es;//[MGM] Background
   // Handler handler;
    MQTTClient Greenhouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the list of items (the dataset):
        //initListOfItems();

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

        // Creation of the MQTT Client and subscription to the topics
        Greenhouse = new MQTTClient(this);
        Random random = new Random();
        Greenhouse.clientId = Greenhouse.clientId + random.nextInt(100000);
        Greenhouse.mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), Greenhouse.serverUri, Greenhouse.clientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        //Last Will message
        mqttConnectOptions.setWill(Greenhouse.publishTopic,Greenhouse.LWillmessage.getBytes(),0,false);

        try { //Create and connect the MQTT client. Also, create the subscriptions.
            Greenhouse.mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    Greenhouse.mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    Greenhouse.subscriptionTopic = "Tomato/#";
                    Greenhouse.subscribeToTopic();
                    Greenhouse.subscriptionTopic = "Pepper/#";
                    Greenhouse.subscribeToTopic();
                    Greenhouse.subscriptionTopic = "Eggplant/#";
                    Greenhouse.subscribeToTopic();
                    Greenhouse.subscriptionTopic = "GreenBean/#";
                    Greenhouse.subscribeToTopic();
                    Greenhouse.subscriptionTopic = "Cucumber/#";
                    Greenhouse.subscribeToTopic();
                    Greenhouse.subscriptionTopic = "Zucchini/#";
                    Greenhouse.subscribeToTopic();
                    Greenhouse.subscriptionTopic = "Melon/#";
                    Greenhouse.subscribeToTopic();
                    Greenhouse.subscriptionTopic = "Watermelon/#";
                    Greenhouse.subscribeToTopic();
                    Snackbar.make(findViewById(R.id.bNewMeasurement), "Client connected and subscribed", 2000).show();// Inform the user
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }




        es = Executors.newSingleThreadExecutor();
        MQTTSub task = new MQTTSub(handler, Greenhouse);
        es.execute(task);


    }

    Handler handler = new Handler(Looper.getMainLooper()) { //Handler for the message received from the background. Depending on the key (topic), the message will be assigned to a specif String variable
        @Override                                   //Then, the attributes (Temperature, Humidity and Light) of each item are updated.
        public void handleMessage(Message inputMessage) {
            this.obtainMessage();
            TomatoLight = inputMessage.getData().getString("Tomato/Light");
            TomatoTemperature = inputMessage.getData().getString("Tomato/Temperature");
            TomatoHumidity = inputMessage.getData().getString("Tomato/Humidity");
            TomatoDate = inputMessage.getData().getString("Tomato/Date");
            recyclerViewAdapter.getItemWithKey(0).setParameters(TomatoLight,TomatoHumidity, TomatoTemperature,TomatoDate);
            if(TomatoTemperature != null) {
                if (Float.parseFloat(TomatoTemperature) > (float)27.0) {//Max temperature for tomato
                    recyclerViewAdapter.getItemWithKey(0).setStatus(false);
                    recyclerViewAdapter.notifyDataSetChanged();
                } else if (Float.parseFloat((TomatoTemperature)) < (float)18.0) {//Min temperature for tomato
                    recyclerViewAdapter.getItemWithKey(0).setStatus(false);
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    recyclerViewAdapter.getItemWithKey(0).setStatus(true);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

                PepperLight = inputMessage.getData().getString("Pepper/Light");
                PepperTemperature = inputMessage.getData().getString("Pepper/Temperature");
                PepperHumidity = inputMessage.getData().getString("Pepper/Humidity");
                PepperDate = inputMessage.getData().getString("Pepper/Date");
                recyclerViewAdapter.getItemWithKey(1).setParameters(PepperLight,PepperHumidity, PepperTemperature, PepperDate);
                if(PepperTemperature!= null) {
                    if (Float.parseFloat(PepperTemperature) > 30.0) {
                        recyclerViewAdapter.getItemWithKey(1).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (Float.parseFloat((PepperTemperature)) < (float)22.0) {
                        recyclerViewAdapter.getItemWithKey(1).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewAdapter.getItemWithKey(1).setStatus(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }

                EggplantLight = inputMessage.getData().getString("Eggplant/Light");
                EggplantTemperature = inputMessage.getData().getString("Eggplant/Temperature");
                EggplantHumidity = inputMessage.getData().getString("Eggplant/Humidity");
                EggplantDate = inputMessage.getData().getString("Eggplant/Date");
                recyclerViewAdapter.getItemWithKey(2).setParameters(EggplantLight,EggplantHumidity, EggplantTemperature, EggplantDate);
                if(EggplantTemperature != null) {
                    if (Float.parseFloat(EggplantTemperature) > 30.0) {
                        recyclerViewAdapter.getItemWithKey(2).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (Float.parseFloat((EggplantTemperature)) < (float)23.0) {
                        recyclerViewAdapter.getItemWithKey(2).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewAdapter.getItemWithKey(2).setStatus(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }


                GreenBeanLight = inputMessage.getData().getString("GreenBean/Light");
                GreenBeanTemperature = inputMessage.getData().getString("GreenBean/Temperature");
                GreenBeanHumidity = inputMessage.getData().getString("GreenBean/Humidity");
                GreenBeanDate = inputMessage.getData().getString("GreenBean/Date");
                recyclerViewAdapter.getItemWithKey(3).setParameters(GreenBeanLight,GreenBeanHumidity, GreenBeanTemperature, GreenBeanDate);
                if(GreenBeanTemperature != null) {
                    if (Float.parseFloat(GreenBeanTemperature) > (float)30.0) {
                        recyclerViewAdapter.getItemWithKey(3).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (Float.parseFloat((GreenBeanTemperature)) < (float)16.0) {
                        recyclerViewAdapter.getItemWithKey(3).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewAdapter.getItemWithKey(3).setStatus(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }

                ZucchiniLight = inputMessage.getData().getString("Zucchini/Light");
                ZucchiniTemperature = inputMessage.getData().getString("Zucchini/Temperature");
                ZucchiniHumidity = inputMessage.getData().getString("Zucchini/Humidity");
                ZucchiniDate = inputMessage.getData().getString("Zucchini/Date");
                recyclerViewAdapter.getItemWithKey(4).setParameters(ZucchiniLight,ZucchiniHumidity, ZucchiniTemperature, ZucchiniDate);
                if(ZucchiniTemperature != null) {
                    if (Float.parseFloat(ZucchiniTemperature) > (float)28.0) {
                        recyclerViewAdapter.getItemWithKey(4).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (Float.parseFloat((ZucchiniTemperature)) < (float)22.0) {
                        recyclerViewAdapter.getItemWithKey(4).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewAdapter.getItemWithKey(4).setStatus(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }


                CucumberLight = inputMessage.getData().getString("Cucumber/Light");
                CucumberTemperature = inputMessage.getData().getString("Cucumber/Temperature");
                CucumberHumidity = inputMessage.getData().getString("Cucumber/Humidity");
                CucumberDate = inputMessage.getData().getString("Cucumber/Date");
                recyclerViewAdapter.getItemWithKey(5).setParameters(CucumberLight,CucumberHumidity, CucumberTemperature, CucumberDate);
                if(CucumberTemperature != null) {
                    if (Float.parseFloat(CucumberTemperature) > (float)30.0) {
                        recyclerViewAdapter.getItemWithKey(5).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (Float.parseFloat((CucumberTemperature)) < (float)24.0) {
                        recyclerViewAdapter.getItemWithKey(5).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewAdapter.getItemWithKey(5).setStatus(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }


                MelonLight = inputMessage.getData().getString("Melon/Light");
                MelonTemperature = inputMessage.getData().getString("Melon/Temperature");
                MelonHumidity = inputMessage.getData().getString("Melon/Humidity");
                MelonDate = inputMessage.getData().getString("Melon/Date");
                recyclerViewAdapter.getItemWithKey(6).setParameters(MelonLight,MelonHumidity, MelonTemperature, MelonDate);
                if(MelonTemperature!= null) {
                    if (Float.parseFloat(MelonTemperature) > (float)28.0) {
                        recyclerViewAdapter.getItemWithKey(6).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (Float.parseFloat((MelonTemperature)) < (float)21.0) {
                        recyclerViewAdapter.getItemWithKey(6).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewAdapter.getItemWithKey(6).setStatus(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }


                WatermelonLight = inputMessage.getData().getString("Watermelon/Light");
                WatermelonTemperature = inputMessage.getData().getString("Watermelon/Temperature");
                WatermelonHumidity = inputMessage.getData().getString("Watermelon/Humidity");
                WatermelonDate = inputMessage.getData().getString("Watermelon/Date");
                recyclerViewAdapter.getItemWithKey(7).setParameters(WatermelonLight,WatermelonHumidity, WatermelonTemperature, WatermelonDate);
                if(WatermelonTemperature != null) {
                    if (Float.parseFloat(WatermelonTemperature) > (float)27.0) {
                        recyclerViewAdapter.getItemWithKey(7).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (Float.parseFloat((WatermelonTemperature)) < (float)20.0) {
                        recyclerViewAdapter.getItemWithKey(7).setStatus(false);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewAdapter.getItemWithKey(7).setStatus(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }

        }
    };

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
            case R.id.sort_AZ:
                Collections.sort(listofitems, GreenhouseAZ);
                Toast.makeText(MainActivity.this, "Sort A to Z", Toast.LENGTH_SHORT).show();
                recyclerViewAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_za:
                Collections.sort(listofitems, GreenhouseZA);
                Toast.makeText(MainActivity.this, "Sort Z to A", Toast.LENGTH_SHORT).show();
                recyclerViewAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_status:
                Collections.sort(listofitems, GreenhouseStatus);
                Toast.makeText(MainActivity.this, "Sort by Status", Toast.LENGTH_SHORT).show();
                recyclerViewAdapter.notifyDataSetChanged();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tracker.onSaveInstanceState(outState); // Save state about selections.
    }



    public void NewMeasurement(View view) {
        // Button "New Measurement" has been clicked:

        Intent i = new Intent(this, SecondActivity.class);
        startActivity(i); // Start SecondActivity

    }

    //pop up message to confirmation
    private void confirmation() {
        Iterator iteratorSelectedItemsKeys = tracker.getSelection().iterator();

        if (iteratorSelectedItemsKeys.hasNext()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want delete these items?")
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
            //recyclerViewAdapter.notifyItemRemoved(Integer.parseInt(iteratorSelectedItemsKeys.next().toString()));

        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No items selected");
        }
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


    //Sort by menu
    public static Comparator<Item> GreenhouseAZ = new Comparator<Item>() {
        @Override
        public int compare(Item t1, Item t2) {

            return t1.getTitle().compareTo(t2.getTitle());
        }
    };
    public static Comparator<Item> GreenhouseZA = new Comparator<Item>() {
        @Override
        public int compare(Item t1, Item t2) {

            return t2.getTitle().compareTo(t1.getTitle());
        }
    };
    public static Comparator<Item> GreenhouseStatus = new Comparator<Item>() {
        @Override
        public int compare(Item t1, Item t2) {

            return Boolean.compare(t1.getStatus(),t2.getStatus());
        }
    };




}