package objects;

public class PointsEarned {

    private String date, pointsGained;

    public PointsEarned(String date, String pointsGained) {
        this.date = date;
        this.pointsGained = pointsGained;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPointsGained() {
        return pointsGained;
    }
}
