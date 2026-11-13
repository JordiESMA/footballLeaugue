package com.ligafutbol.api.controllers;

import com.ligafutbol.api.models.EventType;
import com.ligafutbol.api.models.Match;
import com.ligafutbol.api.models.Player;
import com.ligafutbol.api.services.EventService;
import com.ligafutbol.api.services.MatchService;
import com.ligafutbol.api.services.PlayerService;
import com.ligafutbol.api.services.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class MatchController {

    private final MatchService matchService;
    private final EventService eventService;
    private final TeamService teamService;
    private final PlayerService playerService;

    public MatchController(MatchService matchService, EventService eventService,
                           TeamService teamService, PlayerService playerService) {
        this.matchService = matchService;
        this.eventService = eventService;
        this.teamService = teamService;
        this.playerService = playerService;
    }

    @GetMapping({"/calendar", "/matches"})
    public String calendar(Model model) {
        model.addAttribute("matches", matchService.listMatches());
        return "calendar";
    }

    @GetMapping("/matches/results")
    public String results(Model model) {
        model.addAttribute("matches", matchService.listFinishedMatches());
        return "matchesResults";
    }

    @GetMapping("/matches/new")
    public String form(Model model) {
        model.addAttribute("teams", teamService.listTeams());
        return "match-form";
    }

    @PostMapping("/matches")
    public String add(@RequestParam Integer matchday,
                      @RequestParam Long homeTeamId,
                      @RequestParam Long awayTeamId,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        try {
            matchService.addMatch(matchday, homeTeamId, awayTeamId);
            redirectAttributes.addFlashAttribute("success", "Partido añadido correctamente");
            return "redirect:/calendar";
        } catch (IllegalArgumentException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("matchday", matchday);
            model.addAttribute("homeTeamId", homeTeamId);
            model.addAttribute("awayTeamId", awayTeamId);
            model.addAttribute("teams", teamService.listTeams());
            return "match-form";
        }
    }

    @GetMapping("/matches/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("match", matchService.getMatchById(id));
            return "match-detail";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("matches", matchService.listMatches());
            return "calendar";
        }
    }

    @GetMapping("/matches/{id}/events/new")
    public String eventForm(@PathVariable Long id, Model model) {
        try {
            prepareEventForm(id, model);
            return "event-form";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("matches", matchService.listMatches());
            return "calendar";
        }
    }

    @PostMapping("/matches/{id}/events")
    public String addEvent(@PathVariable Long id,
                           @RequestParam String eventType,
                           @RequestParam Integer minute,
                           @RequestParam Long playerId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            EventType type = EventType.valueOf(eventType);
            eventService.addEvent(id, type, minute, playerId);
            redirectAttributes.addFlashAttribute("success", "Evento añadido correctamente");
            return "redirect:/matches/" + id;
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("eventType", eventType);
            model.addAttribute("minute", minute);
            model.addAttribute("playerId", playerId);
            try {
                prepareEventForm(id, model);
                return "event-form";
            } catch (NoSuchElementException matchNotFound) {
                model.addAttribute("matches", matchService.listMatches());
                return "calendar";
            }
        }
    }

    @PostMapping("/matches/{id}/finish")
    public String finish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            matchService.finishMatch(id);
            redirectAttributes.addFlashAttribute("success", "Partido terminado correctamente");
            return "redirect:/matches/" + id;
        } catch (NoSuchElementException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/matches/" + id;
        }
    }

    private void prepareEventForm(Long matchId, Model model) {
        Match match = matchService.getMatchById(matchId);
        List<Player> players = new ArrayList<>();
        players.addAll(playerService.listPlayersByTeam(match.getHomeTeam().getId()));
        players.addAll(playerService.listPlayersByTeam(match.getAwayTeam().getId()));
        model.addAttribute("match", match);
        model.addAttribute("players", players);
    }
}
