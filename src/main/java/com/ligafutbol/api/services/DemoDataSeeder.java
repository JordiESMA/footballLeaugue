package com.ligafutbol.api.services;

import com.ligafutbol.api.models.Event;
import com.ligafutbol.api.models.EventType;
import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.MatchStatus;
import com.ligafutbol.api.models.Player;
import com.ligafutbol.api.models.Team;
import com.ligafutbol.api.repositories.MatchRepository;
import com.ligafutbol.api.repositories.PlayerRepository;
import com.ligafutbol.api.repositories.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Replaces player names with fictional demo data and adds sample match events. */
@Component
@Order(2)
@ConditionalOnProperty(name = "league.demo-data.enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataSeeder implements CommandLineRunner {

    private static final int PLAYERS_PER_TEAM = 15;
    private static final List<String> FIRST_NAMES = List.of(
            "Alex", "Nico", "Leo", "Dani", "Bruno", "Izan", "Marc", "Joel", "Eric", "Pol",
            "Nil", "Hugo", "Gael", "Adri", "Jan", "Biel", "Enzo", "Teo", "Unai", "Iker");
    private static final List<String> SURNAMES = List.of(
            "Relámpago", "Centella", "Cometa", "Meteoro", "Cobalto", "Zafiro", "Horizonte", "Viento",
            "Estrella", "Prisma", "Nébula", "Órbita", "Brisa", "Rayo", "Solar", "Pixel", "Norte",
            "Cósmico", "Aurora", "Tornado");

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    public DemoDataSeeder(TeamRepository teamRepository,
                          PlayerRepository playerRepository,
                          MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    /** Updates existing players and fills short rosters with fictional names. */
    @Override
    @Transactional
    public void run(String... args) {
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            List<Player> players = playerRepository.findByTeamIdOrderByNameAsc(team.getId());
            players.sort(Comparator.comparing(Player::getId));

            for (Player player : players) {
                player.setName(fictionalName(player.getId()));
            }

            Set<Integer> shirtNumbers = new HashSet<>();
            players.forEach(player -> shirtNumbers.add(player.getShirtNumber()));
            int nextShirtNumber = 1;
            while (players.size() < PLAYERS_PER_TEAM && nextShirtNumber <= 99) {
                if (!shirtNumbers.contains(nextShirtNumber)) {
                    Player player = playerRepository.save(
                            new Player("Jugador de ejemplo", nextShirtNumber, team));
                    player.setName(fictionalName(player.getId()));
                    players.add(player);
                    shirtNumbers.add(nextShirtNumber);
                }
                nextShirtNumber++;
            }
            playerRepository.saveAll(players);
        }

        addEventsToFinishedMatches();
    }

    private void addEventsToFinishedMatches() {
        for (Match match : matchRepository.findByMatchStatus(MatchStatus.FINISHED)) {
            if (match.getHomeScore() == null || match.getAwayScore() == null || !match.getEvents().isEmpty()) {
                continue;
            }

            List<Player> homePlayers = playerRepository.findByTeamIdOrderByNameAsc(match.getHomeTeam().getId());
            List<Player> awayPlayers = playerRepository.findByTeamIdOrderByNameAsc(match.getAwayTeam().getId());
            if (homePlayers.isEmpty() || awayPlayers.isEmpty()) {
                continue;
            }

            List<Event> events = new ArrayList<>();
            addGoals(events, homePlayers, match.getHomeGoals(), 5);
            addGoals(events, awayPlayers, match.getAwayGoals(), 11);
            events.add(new Event(EventType.YELLOWCARD, 30, homePlayers.get(0)));
            events.add(new Event(EventType.YELLOWCARD, 70, awayPlayers.get(0)));
            events.sort(Comparator.comparingInt(Event::getMinute));
            events.forEach(match::addEvent);
            matchRepository.save(match);
        }
    }

    private void addGoals(List<Event> events, List<Player> players, int goals, int firstMinute) {
        for (int i = 0; i < goals; i++) {
            int minute = Math.min(firstMinute + (i * 14), 89);
            Player scorer = players.get(i % players.size());
            events.add(new Event(EventType.GOAL, minute, scorer));
        }
    }

    private String fictionalName(Long playerId) {
        long index = Math.max(0, playerId - 1);
        int firstNameIndex = (int) (index % FIRST_NAMES.size());
        int surnameIndex = (int) ((index / FIRST_NAMES.size()) % SURNAMES.size());
        long suffix = index / (FIRST_NAMES.size() * SURNAMES.size());
        String name = FIRST_NAMES.get(firstNameIndex) + " " + SURNAMES.get(surnameIndex);
        return suffix == 0 ? name : name + " " + (suffix + 1);
    }
}
