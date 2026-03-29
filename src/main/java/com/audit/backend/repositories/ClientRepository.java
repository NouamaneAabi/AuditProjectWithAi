package com.audit.backend.repositories;

import com.audit.backend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findBySecteurActivite(String secteur);
    boolean existsByEmail(String email);
}