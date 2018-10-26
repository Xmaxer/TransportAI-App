package objects;

public class Car {

    private String make, model, regNo;
    private int status;

    public Car(String make, String model, String regNo, int status) {
        this.make = make;
        this.model = model;
        this.status = status;
        this.regNo = regNo;
    }

    public String getMake() {
        return make;
    }

    public String getCarModel() {
        return model;
    }

    public String getRegNumber() {
        return regNo;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString(){
        return make + " " + model;
    }
}
