package com.ligafutbol.api.services;

import com.ligafutbol.api.models.ClassificationLine;
import com.ligafutbol.api.models.Event;
import com.ligafutbol.api.models.EventType;
import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.MatchStatus;
import com.ligafutbol.api.models.Team;
import com.ligafutbol.api.repositories.MatchRepository;
import com.ligafutbol.api.repositories.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassificationService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public ClassificationService(TeamRepository teamRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    /** Calculates the league table using finished matches. */
    @Transactional(readOnly = true)
    public List<ClassificationLine> getClassification() {
        List<Team> teams = teamRepository.findAllByCompetitionGroupOrderByNameAsc("Quarta Catalana - Grup 34");
        Map<Long, int[]> totals = new HashMap<>();
        for (Team team : teams) {
            totals.put(team.getId(), new int[7]);
        }

        List<Match> finishedMatches = matchRepository.findByMatchStatus(MatchStatus.FINISHED);
        for (Match match : finishedMatches) {
            Long homeId = match.getHomeTeam().getId();
            Long awayId = match.getAwayTeam().getId();
            int[] home = totals.get(homeId);
            int[] away = totals.get(awayId);
            if (home == null || away == null) {
                continue;
            }

            int homeGoals = match.getHomeGoals();
            int awayGoals = match.getAwayGoals();

            home[0]++;
            away[0]++;
            home[4] += homeGoals;
            home[5] += awayGoals;
            away[4] += awayGoals;
            away[5] += homeGoals;

            if (homeGoals > awayGoals) {
                home[1]++;
                home[6] += 3;
                away[3]++;
            } else if (homeGoals < awayGoals) {
                away[1]++;
                away[6] += 3;
                home[3]++;
            } else {
                home[2]++;
                away[2]++;
                home[6]++;
                away[6]++;
            }
        }

        List<ClassificationLine> classification = new ArrayList<>();
        for (Team team : teams) {
            int[] line = totals.get(team.getId());
            classification.add(new ClassificationLine(team, line[0], line[1], line[2], line[3],
                    line[4], line[5], line[6]));
        }

        classification.sort(Comparator
                .comparingInt(ClassificationLine::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(ClassificationLine::getGoalsDifference).reversed())
                .thenComparing(Comparator.comparingInt(ClassificationLine::getGoalsFor).reversed())
                .thenComparing(line -> line.getTeam().getName(), String.CASE_INSENSITIVE_ORDER));
        return classification;
    }
}
