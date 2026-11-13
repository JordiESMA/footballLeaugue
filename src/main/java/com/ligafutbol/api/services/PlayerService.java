package com.ligafutbol.api.services;

import com.ligafutbol.api.models.Player;
import com.ligafutbol.api.models.Team;
import com.ligafutbol.api.repositories.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamService teamService;

    public PlayerService(PlayerRepository playerRepository, TeamService teamService) {
        this.playerRepository = playerRepository;
        this.teamService = teamService;
    }

    /** Adds a player to an existing team. */
    public Player addPlayer(Long teamId, String name, int shirtNumber) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (name.trim().length() > 255) {
            throw new IllegalArgumentException("El nombre no puede superar 255 caracteres");
        }
        if (shirtNumber < 1 || shirtNumber > 99) {
            throw new IllegalArgumentException("El dorsal debe estar entre 1 y 99");
        }

        Team team = teamService.getTeamById(teamId);
        return playerRepository.save(new Player(name.trim(), shirtNumber, team));
    }

    /** Lists all players ordered by name. */
    public List<Player> listPlayers() {
        return playerRepository.findAll(org.springframework.data.domain.Sort.by("name").ascending());
    }

    /** Lists the players of one team ordered by name. */
    public List<Player> listPlayersByTeam(Long teamId) {
        teamService.getTeamById(teamId);
        return playerRepository.findByTeamIdOrderByNameAsc(teamId);
    }
}
