package it.uniroma1.databaseservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma1.databaseservice.entities.Anomaly;

public interface AnomalyRepository extends JpaRepository<Anomaly, Long>  {
    
    @Query("SELECT a FROM Anomaly a WHERE a.hashCode = ?1")
    public Anomaly findByHashCode(String hashCode);
}
