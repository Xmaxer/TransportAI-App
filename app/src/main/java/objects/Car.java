package objects;

public class Car {

    private String make, model, carID;
    private int status;

    public Car(String make, String model, String carID, int status) {
        this.make = make;
        this.model = model;
        this.status = status;
        this.carID = carID;
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

    @Override
    public String toString(){
        return make + " " + model;
    }
}
