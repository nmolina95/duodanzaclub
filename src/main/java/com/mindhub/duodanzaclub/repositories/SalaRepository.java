package com.mindhub.duodanzaclub.repositories;

import com.mindhub.duodanzaclub.models.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface SalaRepository extends JpaRepository<Sala, Long> {
}
