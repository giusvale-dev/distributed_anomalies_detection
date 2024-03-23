package it.uniroma1.databaseservice.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ACK<T> {

    private boolean success;
    private String message;
    private T payload;

}
