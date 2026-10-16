package com.ligafutbol.api.repositories;

import com.ligafutbol.api.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Team> findByNameIgnoreCase(String name);
    List<Team> findAllByOrderByNameAsc();
}