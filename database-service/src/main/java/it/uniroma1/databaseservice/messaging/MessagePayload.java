package it.uniroma1.databaseservice.messaging;

import it.uniroma1.databaseservice.entities.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessagePayload {

    private OperationType operationType;
    private Member user;
    
}