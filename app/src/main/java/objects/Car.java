package objects;

public class Car {

    private String make, model, carID, carImgURL;
    private int status, seats;

    public Car(String make, String model, String carID, int status, String carImgURL, int seats) {
        this.make = make;
        this.model = model;
        this.status = status;
        this.carID = carID;
        this.carImgURL = carImgURL;
        this.seats = seats;
    }

    public String getCarID() {
        return carID;
    }

    public String getImgURL() {
        return carImgURL;
    }

    public int getSeats() {return seats;}

    @Override
    public String toString(){
        return make + " " + model;
    }
}
