package dte.masteriot.mdp.mdprojectsensors;

public class Item {
    // This class contains the actual data of each item of the dataset

    private String title;
    private String subtitle;
    private long key;
    private boolean status;

    private int image;
    private String Light;
    private String Humidity;
    private String Temperature;
    private String Date;

    //Item(String title, String stringURI, String subtitle, Long key, int image, boolean status) {
        Item(String title, String Light, String Humidity, String Temperature,String Date, String subtitle, long key, int image , boolean status) {
        this.title = title;
        this.subtitle = subtitle;
        this.key = key;
        this.image = image;
        this.Light = Light;
        this.Humidity = Humidity;
        this.Temperature = Temperature;
        this.Date = Date;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public long getKey() {
        return key;
    }

    public int getImage(){ return image;}

    public String getLight(){return Light; }
    public String getHumidity(){ return Humidity;}
    public String getTemperature(){ return Temperature;}
    public String getDate(){ return Date;}


    public void setParameters(String light, String humidity, String temperature, String date){
            Light = light;
            Temperature = temperature;
            Humidity = humidity;
            Date = date;
    }

    public boolean getStatus(){return status; }

    public void setStatus(boolean Status){
            status = Status;
    }

    // We override the "equals" operator to only compare keys
    // (useful when searching for the position of a specific key in a list of Items):
    public boolean equals(Object other) {
        return this.key == ((Item) other).getKey();
    }

}