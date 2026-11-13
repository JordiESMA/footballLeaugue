package com.ligafutbol.api.services;

import com.ligafutbol.api.models.Team;
import com.ligafutbol.api.repositories.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TeamService {

    //We instance the repository for use it
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // - ADD TEAM -

    /**
     * Creates a new team after validating the data.
     *
     * @param name           team name (required, no duplicates)
     * @param competitionGroup competition group (required)
     * @return the saved team
     * @throws IllegalArgumentException if a required field is empty or the team already exists
     */
    public Team addTeam(String name, String competitionGroup) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (!StringUtils.hasText(competitionGroup)) {
            throw new IllegalArgumentException("El grupo es obligatorio");
        }
        checkLength(name, "El nombre");
        checkLength(competitionGroup, "El grupo");

        String nameClear = name.trim();
        String groupClear = competitionGroup.trim();

        // If the team name exists we throw an exception, else we save
        if (teamRepository.existsByNameIgnoreCase(nameClear)) {
            throw new IllegalArgumentException("Ese equipo ya existe");
        }

        return teamRepository.save(new Team(nameClear, groupClear));
    }

    // - EDIT TEAM -

    /**
     * Updates an existing team after validating the data.
     *
     * @param id             id of the team to edit
     * @param name           new team name (required, no duplicates with other teams)
     * @param competitionGroup new competition group (required)
     * @return the updated team
     * @throws NoSuchElementException   if there is no team with that id
     * @throws IllegalArgumentException if a required field is empty or another team has that name
     */
    public Team editTeam(Long id, String name, String competitionGroup) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (!StringUtils.hasText(competitionGroup)) {
            throw new IllegalArgumentException("El grupo es obligatorio");
        }
        checkLength(name, "El nombre");
        checkLength(competitionGroup, "El grupo");

        String nameClear = name.trim();
        String groupClear = competitionGroup.trim();

        Team team = getTeamById(id);

        // The name can exist only if it belongs to this same team
        boolean usedByOtherTeam = teamRepository.findByNameIgnoreCase(nameClear)
                .filter(other -> !other.getId().equals(id))
                .isPresent();

        if (usedByOtherTeam) {
            throw new IllegalArgumentException("Ese equipo ya existe");
        }

        team.setName(nameClear);
        team.setCompetitionGroup(groupClear);

        return teamRepository.save(team);
    }

    // - LIST TEAMS -

    /**
     * Lists all the teams ordered by name.
     *
     * @return the list of teams, A to Z
     */
    public List<Team> listTeams() {
        return teamRepository.findAllByOrderByNameAsc();
    }

    // - SEARCH BY ID -

    /**
     * Searches a team by its id.
     *
     * @param id id of the team
     * @return the team found
     * @throws NoSuchElementException if there is no team with that id
     */
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado: " + id));
    }

    // - DELETE TEAM -

    /**
     * Deletes a team only if it has no players.
     *
     * @param id id of the team to delete
     * @throws NoSuchElementException if there is no team with that id
     * @throws IllegalStateException  if the team still has players
     */
    public void deleteTeam(Long id) {
        Team team = getTeamById(id);

        // If the team has players we throw an exception (it stops the method), else we delete it
        if (!team.getPlayers().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un equipo con jugadores");
        }
        teamRepository.delete(team);
    }

    /** Checks that a team field does not exceed the database length. */
    private void checkLength(String value, String field) {
        if (value.trim().length() > 255) {
            throw new IllegalArgumentException(field + " no puede superar 255 caracteres");
        }
    }
}
