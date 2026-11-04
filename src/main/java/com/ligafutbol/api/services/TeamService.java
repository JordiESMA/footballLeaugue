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

    //For check the year of foundation
    private static final int MIN_YEAR = 1000;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // - ADD TEAM -

    /**
     * Creates a new team after validating the data.
     *
     * @param name           team name (required, no duplicates)
     * @param city           city where the team is based (required)
     * @param foundationYear year the team was founded (between MIN_YEAR and the current year)
     * @return the saved team
     * @throws IllegalArgumentException if the year is out of range, the name or city
     *                                  is empty, or a team with that name already exists
     */
    public Team addTeam(String name, String city, int foundationYear) {
        checkYear(foundationYear);

        // Check the name and the city are not null or empty
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (!StringUtils.hasText(city)) {
            throw new IllegalArgumentException("La ciudad es obligatoria");
        }

        String nameClear = name.trim();
        String cityClear = city.trim();

        // If the team name exists we throw an exception, else we save
        if (teamRepository.existsByNameIgnoreCase(nameClear)) {
            throw new IllegalArgumentException("Ese equipo ya existe");
        }

        return teamRepository.save(new Team(nameClear, cityClear, foundationYear));
    }

    // - EDIT TEAM -

    /**
     * Updates an existing team after validating the data.
     *
     * @param id             id of the team to edit
     * @param name           new team name (required, no duplicates with other teams)
     * @param city           new city (required)
     * @param foundationYear new foundation year (between MIN_YEAR and the current year)
     * @return the updated team
     * @throws NoSuchElementException   if there is no team with that id
     * @throws IllegalArgumentException if the year is out of range, the name or city
     *                                  is empty, or another team already has that name
     */
    public Team editTeam(Long id, String name, String city, int foundationYear) {
        checkYear(foundationYear);

        // Check the name and the city are not null or empty
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (!StringUtils.hasText(city)) {
            throw new IllegalArgumentException("La ciudad es obligatoria");
        }

        String nameClear = name.trim();
        String cityClear = city.trim();

        Team team = getTeamById(id);

        // The name can exist only if it belongs to this same team
        boolean usedByOtherTeam = teamRepository.findByNameIgnoreCase(nameClear)
                .filter(other -> !other.getId().equals(id))
                .isPresent();

        if (usedByOtherTeam) {
            throw new IllegalArgumentException("Ese equipo ya existe");
        }

        team.setName(nameClear);
        team.setCity(cityClear);
        team.setFoundationYear(foundationYear);

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

    // - CHECK YEAR -

    /**
     * Checks that the foundation year is valid.
     *
     * @param year foundation year to check
     * @throws IllegalArgumentException if it is below MIN_YEAR or after the current year
     */
    private void checkYear(int year) {
        int maxYear = java.time.Year.now().getValue();
        if (year < MIN_YEAR || year > maxYear) {
            throw new IllegalArgumentException("El año debe estar entre " + MIN_YEAR + " y " + maxYear);
        }
    }
}