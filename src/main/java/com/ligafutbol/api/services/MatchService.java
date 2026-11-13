package com.ligafutbol.api.services;

import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.MatchStatus;
import com.ligafutbol.api.models.Team;
import com.ligafutbol.api.repositories.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamService teamService;

    public MatchService(MatchRepository matchRepository, TeamService teamService) {
        this.matchRepository = matchRepository;
        this.teamService = teamService;
    }

    /** Creates a scheduled match between two different teams. */
    public Match addMatch(int matchday, Long homeTeamId, Long awayTeamId) {
        if (matchday < 1) {
            throw new IllegalArgumentException("La jornada debe ser mayor que 0");
        }
        if (homeTeamId == null || awayTeamId == null) {
            throw new IllegalArgumentException("Debes seleccionar los dos equipos");
        }
        if (homeTeamId.equals(awayTeamId)) {
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo");
        }

        Team homeTeam = teamService.getTeamById(homeTeamId);
        Team awayTeam = teamService.getTeamById(awayTeamId);
        return matchRepository.save(new Match(matchday, homeTeam, awayTeam));
    }

    /** Lists matches ordered by matchday. */
    public List<Match> listMatches() {
        return matchRepository.findAllByOrderByMatchdayAsc();
    }

    /** Lists finished matches. */
    public List<Match> listFinishedMatches() {
        return matchRepository.findByMatchStatus(MatchStatus.FINISHED);
    }

    /** Finds a match by its id. */
    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Partido no encontrado: " + id));
    }

    /** Marks a match as finished. */
    public Match finishMatch(Long id) {
        Match match = getMatchById(id);
        if (match.getMatchStatus() == MatchStatus.FINISHED) {
            throw new IllegalStateException("El partido ya está terminado");
        }
        match.setMatchStatus(MatchStatus.FINISHED);
        return matchRepository.save(match);
    }
}
