package com.ligafutbol.api.repositories;

import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByMatchdayOrderByIdAsc(int matchday);
    List<Match> findByMatchStatus(MatchStatus status);
    List<Match> findByHomeTeamIdOrAwayTeamId(Long home, Long away);
    List<Match> findAllByOrderByMatchdayAsc();
}