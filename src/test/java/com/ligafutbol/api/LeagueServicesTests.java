package com.ligafutbol.api;

import com.ligafutbol.api.models.ClassificationLine;
import com.ligafutbol.api.models.Event;
import com.ligafutbol.api.models.EventType;
import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.MatchStatus;
import com.ligafutbol.api.models.Player;
import com.ligafutbol.api.models.Team;
import com.ligafutbol.api.repositories.MatchRepository;
import com.ligafutbol.api.repositories.PlayerRepository;
import com.ligafutbol.api.repositories.TeamRepository;
import com.ligafutbol.api.services.ClassificationService;
import com.ligafutbol.api.services.MatchService;
import com.ligafutbol.api.services.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:league-tests;DB_CLOSE_DELAY=-1", "league.import.enabled=false"})
@Transactional
class LeagueServicesTests {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    void playerServiceSavesPlayerWithSelectedTeam() {
        Team team = teamRepository.save(new Team("Equipo", "Test Group"));

        Player player = playerService.addPlayer(team.getId(), "  Alex  ", 10);

        assertEquals("Alex", player.getName());
        assertEquals(10, player.getShirtNumber());
        assertEquals(team.getId(), player.getTeam().getId());
    }

    @Test
    void playerServiceRejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> playerService.addPlayer(1L, "  ", 10));
    }

    @Test
    void matchShowsImportedScoreWithoutGoalEvents() {
        Team home = new Team("Local", "Test Group");
        Team away = new Team("Visitante", "Test Group");
        Match match = new Match(1, home, away);
        match.setScore(3, 1);

        assertEquals("Local : 3 - 1 : Visitante", match.getResult());
        assertEquals(3, match.getHomeGoals());
        assertEquals(1, match.getAwayGoals());
    }

    @Test
    void matchServiceRejectsSameTeam() {
        assertThrows(IllegalArgumentException.class, () -> matchService.addMatch(1, 1L, 1L));
        assertEquals(0, matchRepository.count());
    }

    @Test
    void classificationCountsGoalsAndPoints() {
        Team home = teamRepository.save(new Team("A Equipo", "Quarta Catalana - Grup 34"));
        Team away = teamRepository.save(new Team("B Equipo", "Quarta Catalana - Grup 34"));
        Player homePlayer = playerRepository.save(new Player("Local", 10, home));
        Player awayPlayer = playerRepository.save(new Player("Visitante", 11, away));

        Match match = new Match(1, home, away);
        match.addEvent(new Event(EventType.GOAL, 10, homePlayer));
        match.addEvent(new Event(EventType.GOAL, 25, homePlayer));
        match.addEvent(new Event(EventType.GOAL, 40, awayPlayer));
        match.setMatchStatus(MatchStatus.FINISHED);
        matchRepository.save(match);

        List<ClassificationLine> classification = classificationService.getClassification();
        ClassificationLine homeLine = classification.stream()
                .filter(line -> line.getTeam().getId().equals(home.getId())).findFirst().orElseThrow();
        ClassificationLine awayLine = classification.stream()
                .filter(line -> line.getTeam().getId().equals(away.getId())).findFirst().orElseThrow();

        assertEquals(3, homeLine.getPoints());
        assertEquals(2, homeLine.getGoalsFor());
        assertEquals(1, homeLine.getGoalsAgainst());
        assertEquals(0, awayLine.getPoints());
        assertEquals(1, awayLine.getGoalsFor());
    }
}
