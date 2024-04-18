package  it.uniroma1.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor


public class Anomaly {
    private String datetime;
    private String details;
    private boolean fixed;


    public Anomaly(String datetime, String details) {
        this.datetime = datetime;
        this.details = details;

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
