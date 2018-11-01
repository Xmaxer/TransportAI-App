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

    public String getMake() {
        return make;
    }

    public String getCarModel() {
        return model;
    }

    public String getCarID() {
        return carID;
    }

    public int getStatus() {
        return status;
    }

    public String getImgURL() {
        return carImgURL;
    }

    @Override
    public String toString(){
        return make + " " + model;
    }
}
