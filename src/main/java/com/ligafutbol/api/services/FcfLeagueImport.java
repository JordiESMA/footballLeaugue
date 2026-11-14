package com.ligafutbol.api.services;

import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.MatchStatus;
import com.ligafutbol.api.models.Team;
import com.ligafutbol.api.repositories.MatchRepository;
import com.ligafutbol.api.repositories.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Loads the FCF 2026-27 group calendar on first app startup. */
@Component
@Order(1)
@ConditionalOnProperty(name = "league.import.enabled", havingValue = "true", matchIfMissing = true)
public class FcfLeagueImport implements CommandLineRunner {

    private static final String GROUP = "Quarta Catalana - Grup 34";
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public FcfLeagueImport(TeamRepository teamRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    /** Imports the configured fixtures when the application starts. */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<String> rows;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/fcf-2026-27.txt").getInputStream(), StandardCharsets.UTF_8))) {
            rows = reader.lines().filter(line -> !line.isBlank()).toList();
        }

        for (int i = 0; i < rows.size(); i++) {
            String[] columns = rows.get(i).split("\\|", -1);
            int firstHalfMatchday = i / 6 + 1;
            saveFixture(firstHalfMatchday, columns[0], columns[1], columns[2], columns[3]);
            saveFixture(firstHalfMatchday + 11, columns[1], columns[0], "", "");
        }
    }

    /** Saves a fixture unless it already exists for the same matchday and teams. */
    private void saveFixture(int matchday, String homeName, String awayName, String homeScore, String awayScore) {
        Team home = getOrCreateTeam(homeName);
        Team away = getOrCreateTeam(awayName);
        if (matchRepository.existsByMatchdayAndHomeTeamIdAndAwayTeamId(matchday, home.getId(), away.getId())) {
            return;
        }

        Match match = new Match(matchday, home, away);
        if (!homeScore.isBlank() && !awayScore.isBlank()) {
            match.setScore(Integer.parseInt(homeScore), Integer.parseInt(awayScore));
            match.setMatchStatus(MatchStatus.FINISHED);
        }
        matchRepository.save(match);
    }

    /** Finds a team by name or creates it in the imported competition group. */
    private Team getOrCreateTeam(String name) {
        return teamRepository.findByNameIgnoreCase(name).orElseGet(() ->
                teamRepository.save(new Team(name, GROUP)));
    }
}
