package objects;

public class Transaction {

    private String paymentMethod;
    private double cost;
    private int pointsUsed;
    private String date;

    public Transaction(String paymentMethod, double cost, int pointsUsed, String date) {
        this.paymentMethod = paymentMethod;
        this.cost = cost;
        this.pointsUsed = pointsUsed;
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getPointsUsed() {
        return pointsUsed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
