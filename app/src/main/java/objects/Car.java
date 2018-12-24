package objects;

public class Car {

    private String make, model, carID, carImgURL;
    private int status;

    public Car(String make, String model, String carID, int status, String carImgURL) {
        this.make = make;
        this.model = model;
        this.status = status;
        this.carID = carID;
        this.carImgURL = carImgURL;
    }

    public String getCarID() {
        return carID;
    }

    public String getImgURL() {
        return carImgURL;
    }

    @Override
    public String toString(){
        return make + " " + model;
    }
}
