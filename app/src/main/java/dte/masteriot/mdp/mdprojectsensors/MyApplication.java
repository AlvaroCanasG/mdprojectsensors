package dte.masteriot.mdp.mdprojectsensors;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    String TomatoLight, TomatoTemperature, TomatoHumidity, TomatoDate = "---";
    String EggplantLight, EggplantTemperature, EggplantHumidity, EggplantDate = "---";
    String PepperLight, PepperTemperature, PepperHumidity, PepperDate = "---";
    String GreenBeanLight, GreenBeanTemperature, GreenBeanHumidity, GreenBeanDate = "---";
    String ZucchiniLight, ZucchiniTemperature, ZucchiniHumidity, ZucchiniDate = "---";
    String CucumberLight, CucumberTemperature, CucumberHumidity, CucumberDate = "---";
    String MelonLight, MelonTemperature, MelonHumidity, MelonDate = "---";
    String WatermelonLight, WatermelonTemperature, WatermelonHumidity, WatermelonDate = "---";



    private static final List<Item> listofitems = new ArrayList<Item>();

    public static List<Item> getListofitems(){
        return listofitems;
    }

    public void setTomatoLight(String tomatoLight) {
        TomatoLight = tomatoLight;
    }

    public void setTomatoTemperature(String tomatoTemperature) {
        TomatoTemperature = tomatoTemperature;
    }

    public void setTomatoHumidity(String tomatoHumidity) {
        TomatoHumidity = tomatoHumidity;
    }

    public void setTomatoDate(String tomatoDate) {
        TomatoDate = tomatoDate;
    }

    public MyApplication() {
        initListOfItems();


    }

    private void initListOfItems () {

        listofitems.add(new Item("Tomato", TomatoLight,TomatoHumidity,TomatoTemperature,TomatoDate, "March - April - May" , (long) 0 , R.drawable.tomato, true ));
        listofitems.add(new Item("Pepper", PepperLight, PepperHumidity, PepperTemperature,PepperDate, "March - April - May" , (long) 1 , R.drawable.peper , false ));
        listofitems.add(new Item("Eggplant", EggplantLight, EggplantHumidity, EggplantTemperature,EggplantDate, "July - August" , (long) 2 , R.drawable.eggplant , true ));
        listofitems.add(new Item("Green bean", GreenBeanLight,GreenBeanHumidity, GreenBeanTemperature,GreenBeanDate, "May - Jun" , (long) 3 , R.drawable.green_bean , false ));
        listofitems.add(new Item("Zucchini", ZucchiniLight, ZucchiniHumidity, ZucchiniTemperature,ZucchiniDate, "May" , (long) 4 , R.drawable.zucchini, true ));
        listofitems.add(new Item("Cucumber", CucumberLight, CucumberHumidity,CucumberTemperature,CucumberDate,"April" , (long) 5 , R.drawable.cucumber , true ));
        listofitems.add(new Item("Melon", MelonLight,MelonHumidity, MelonTemperature,MelonDate, "March - April - May" , (long) 6 , R.drawable.melon , true ));
        listofitems.add(new Item("Watermelon", WatermelonLight,WatermelonHumidity, WatermelonTemperature,WatermelonDate, "February - March - April" , (long) 7 , R.drawable.watermelon , true ));

    }
    /*
    public String getTomatoLight() {
        return TomatoLight;
    }
    public String getTomatoTemperature() {
        return TomatoTemperature;
    }

    public String getTomatoHumidity() {
        return TomatoHumidity;
    }

    public String getTomatoDate() {
        return TomatoDate;
    }

    public String getEggplantLight() {
        return EggplantLight;
    }

    public String getEggplantTemperature() {
        return EggplantTemperature;
    }

    public String getEggplantHumidity() {
        return EggplantHumidity;
    }

    public String getEggplantDate() {
        return EggplantDate;
    }

    public String getPepperLight() {
        return PepperLight;
    }

    public String getPepperTemperature() {
        return PepperTemperature;
    }

    public String getPepperHumidity() {
        return PepperHumidity;
    }

    public String getPepperDate() {
        return PepperDate;
    }

    public String getGreenBeanLight() {
        return GreenBeanLight;
    }

    public String getGreenBeanTemperature() {
        return GreenBeanTemperature;
    }

    public String getGreenBeanHumidity() {
        return GreenBeanHumidity;
    }

    public String getGreenBeanDate() {
        return GreenBeanDate;
    }

    public String getZucchiniLight() {
        return ZucchiniLight;
    }

    public String getZucchiniTemperature() {
        return ZucchiniTemperature;
    }

    public String getZucchiniHumidity() {
        return ZucchiniHumidity;
    }

    public String getZucchiniDate() {
        return ZucchiniDate;
    }

    public String getCucumberLight() {
        return CucumberLight;
    }

    public String getCucumberTemperature() {
        return CucumberTemperature;
    }

    public String getCucumberHumidity() {
        return CucumberHumidity;
    }

    public String getCucumberDate() {
        return CucumberDate;
    }

    public String getMelonLight() {
        return MelonLight;
    }

    public String getMelonTemperature() {
        return MelonTemperature;
    }

    public String getMelonHumidity() {
        return MelonHumidity;
    }

    public String getMelonDate() {
        return MelonDate;
    }

    public String getWatermelonLight() {
        return WatermelonLight;
    }

    public String getWatermelonTemperature() {
        return WatermelonTemperature;
    }

    public String getWatermelonHumidity() {
        return WatermelonHumidity;
    }

    public String getWatermelonDate() {
        return WatermelonDate;
    }

     */
}





