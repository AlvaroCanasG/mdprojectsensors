package dte.masteriot.mdp.mdprojectsensors;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    //Initialize variable
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

// set
    public void setText(String s){
        mutableLiveData.setValue(s);
    }

    //Getter
    public MutableLiveData<String> getText(){
        return mutableLiveData;
    }
}
