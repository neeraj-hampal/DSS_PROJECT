package liftride.Server;

public class LiftRideRequest {
    private int time;
    private int liftID;

    // Constructors, Getters, and Setters
    public LiftRideRequest() {}

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLiftID() {
        return liftID;
    }

    public void setLiftID(int liftID) {
        this.liftID = liftID;
    }
}
