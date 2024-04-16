package it.uniroma1;

public class Anomaly {
    private String datetime;
    private String details;
    private boolean fixed;

    public Anomaly(String datetime, String details) {
        this.datetime = datetime;
        this.details = details;
        this.fixed = false;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getDetails() {
        return details;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    @Override
    public String toString() {
        return "Anomaly{" +
                "datetime='" + datetime + '\'' +
                ", details='" + details + '\'' +
                ", fixed=" + fixed +
                '}';
    }
}
