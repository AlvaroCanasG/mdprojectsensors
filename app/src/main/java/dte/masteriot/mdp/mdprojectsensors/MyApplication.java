package dte.masteriot.mdp.mdprojectsensors;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    String TomatoLight, TomatoTemperature, TomatoHumidity, TomatoDate;
    String EggplantLight, EggplantTemperature, EggplantHumidity, EggplantDate;
    String PepperLight, PepperTemperature, PepperHumidity, PepperDate;
    String GreenBeanLight, GreenBeanTemperature, GreenBeanHumidity, GreenBeanDate;
    String ZucchiniLight, ZucchiniTemperature, ZucchiniHumidity, ZucchiniDate;
    String CucumberLight, CucumberTemperature, CucumberHumidity, CucumberDate;
    String MelonLight, MelonTemperature, MelonHumidity, MelonDate;
    String WatermelonLight, WatermelonTemperature, WatermelonHumidity, WatermelonDate;

    private static final List<Item> listofitems = new ArrayList<Item>(); // Create a global list of greenhouses

    public static List<Item> getListofitems(){
        return listofitems;
    }

    public MyApplication() {
        initListOfItems(); //Initialize the array when the app starts


    }

    private void initListOfItems () {

        listofitems.add(new Item("Tomato", TomatoLight,TomatoHumidity,TomatoTemperature,TomatoDate, "March - April - May" , (long) 0 , R.drawable.tomato, true ));
        listofitems.add(new Item("Pepper", PepperLight, PepperHumidity, PepperTemperature,PepperDate, "March - April - May" , (long) 1 , R.drawable.peper , true ));
        listofitems.add(new Item("Eggplant", EggplantLight, EggplantHumidity, EggplantTemperature,EggplantDate, "July - August" , (long) 2 , R.drawable.eggplant , true ));
        listofitems.add(new Item("Green bean", GreenBeanLight,GreenBeanHumidity, GreenBeanTemperature,GreenBeanDate, "May - Jun" , (long) 3 , R.drawable.green_bean , true ));
        listofitems.add(new Item("Zucchini", ZucchiniLight, ZucchiniHumidity, ZucchiniTemperature,ZucchiniDate, "May" , (long) 4 , R.drawable.zucchini, true ));
        listofitems.add(new Item("Cucumber", CucumberLight, CucumberHumidity,CucumberTemperature,CucumberDate,"April" , (long) 5 , R.drawable.cucumber , true ));
        listofitems.add(new Item("Melon", MelonLight,MelonHumidity, MelonTemperature,MelonDate, "March - April - May" , (long) 6 , R.drawable.melon , true ));
        listofitems.add(new Item("Watermelon", WatermelonLight,WatermelonHumidity, WatermelonTemperature,WatermelonDate, "February - March - April" , (long) 7 , R.drawable.watermelon , true ));

    }

}





