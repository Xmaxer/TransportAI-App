package objects;

public class Review {

    private float rating;
    private String reviewText;

    public Review(float rating, String reviewText) {
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public float getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
}
