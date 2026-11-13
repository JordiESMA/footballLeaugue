package com.ligafutbol.api.services;

import com.ligafutbol.api.models.Event;
import com.ligafutbol.api.models.EventType;
import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.MatchStatus;
import com.ligafutbol.api.models.Player;
import com.ligafutbol.api.repositories.MatchRepository;
import com.ligafutbol.api.repositories.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class EventService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    public EventService(MatchRepository matchRepository, PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    /** Adds a goal or card to a match. */
    @Transactional
    public Match addEvent(Long matchId, EventType eventType, int minute, Long playerId) {
        if (eventType == null) {
            throw new IllegalArgumentException("El tipo de evento es obligatorio");
        }
        if (minute < 1 || minute > 120) {
            throw new IllegalArgumentException("El minuto debe estar entre 1 y 120");
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Partido no encontrado: " + matchId));
        if (match.getMatchStatus() == MatchStatus.FINISHED) {
            throw new IllegalStateException("No se pueden añadir eventos a un partido terminado");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Jugador no encontrado: " + playerId));
        Long playerTeamId = player.getTeam().getId();
        if (!playerTeamId.equals(match.getHomeTeam().getId())
                && !playerTeamId.equals(match.getAwayTeam().getId())) {
            throw new IllegalArgumentException("El jugador debe pertenecer a uno de los equipos del partido");
        }

        match.addEvent(new Event(eventType, minute, player));
        match.setMatchStatus(MatchStatus.IN_PROGRESS);
        return matchRepository.save(match);
    }
}
