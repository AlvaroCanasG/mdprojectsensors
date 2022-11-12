package dte.masteriot.mdp.mdprojectsensors;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static List<Item> listofitems = new ArrayList<>();

    public List<Item> getListofitems(){
        return listofitems;
    }

    public void setListofitems( List<Item> listofitems){
        this.listofitems = listofitems;
    }

    private static boolean listofitemsinitialized = true;
/*
    public void initListOfItems () {

        listofitems.add(new Item("Tomato", "https://www.tierraburritos.com/", "March - April - May" , (long) 0 , R.drawable.tomato, true ));
        listofitems.add(new Item("Peper", "https://ochentagrados.com/", "March - April - May" , (long) 1 , R.drawable.peper , false ));
        listofitems.add(new Item("Eggplant", "https://grupolamusa.com/restaurante-musa-malasana/", "July - August" , (long) 2 , R.drawable.eggplant , true ));
        listofitems.add(new Item("Green bean", "https://lamejorhamburguesa.com/", "May - Jun" , (long) 3 , R.drawable.green_bean , false ));
        listofitems.add(new Item("Zucchini", "https://www.sublimeworldrestaurant.com//", "May" , (long) 4 , R.drawable.zucchini, true ));
        listofitems.add(new Item("Cucumber", "https://www.loscervecistas.es/locales-cervecistas/el-2-de-fortuny/", "April" , (long) 5 , R.drawable.cucumber , true ));
        listofitems.add(new Item("Melon", "https://www.loscervecistas.es/locales-cervecistas/el-2-de-fortuny/", "March - April - May" , (long) 6 , R.drawable.melon , true ));
        listofitems.add(new Item("Watermelon", "https://www.loscervecistas.es/locales-cervecistas/el-2-de-fortuny/", "February - March - April" , (long) 7 , R.drawable.watermelon , true ));

        listofitemsinitialized = true;

    }

*/
}





