package repositories;

import models.Team;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TeamRepository {
    //Here I'm gonna to save the teams in an array.


    private final List<Team> teams = new ArrayList<>();

    /**
     * Pass all the teams that  we have
     * @return a copy of the teams array
     */
    public List<Team> findAll() {
        return new ArrayList<>(teams);
    }

    public boolean validate(Team team) {
        return teams.equals(team);
    }
    /**
     * Save a team in the repo
     * @param team
     */
    public void save(Team team) {
        teams.add(team);
    }

    public void delete(Team team) {
        teams.remove(team);
    }

}
