package com.ligafutbol.api.controllers;

import com.ligafutbol.api.services.PlayerService;
import com.ligafutbol.api.services.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
public class PlayerController {

    private final PlayerService playerService;
    private final TeamService teamService;

    public PlayerController(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @GetMapping("/players")
    public String list(Model model) {
        model.addAttribute("players", playerService.listPlayers());
        return "detailsPlayers";
    }

    @GetMapping("/players/team/{teamId}")
    public String listByTeam(@PathVariable Long teamId, Model model) {
        try {
            model.addAttribute("players", playerService.listPlayersByTeam(teamId));
            model.addAttribute("team", teamService.getTeamById(teamId));
            return "detailsPlayers";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("teams", teamService.listTeams());
            return "teams";
        }
    }

    @GetMapping("/teams/{teamId}/addPlayer")
    public String form(@PathVariable Long teamId, Model model) {
        try {
            model.addAttribute("team", teamService.getTeamById(teamId));
            return "team-addPlayer";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("teams", teamService.listTeams());
            return "teams";
        }
    }

    @PostMapping("/teams/{teamId}/addPlayer")
    public String add(@PathVariable Long teamId,
                      @RequestParam String name,
                      @RequestParam Integer shirtNumber,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        try {
            playerService.addPlayer(teamId, name, shirtNumber);
            redirectAttributes.addFlashAttribute("success", "Jugador añadido correctamente");
            return "redirect:/teams/" + teamId;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            try {
                model.addAttribute("team", teamService.getTeamById(teamId));
                return "team-addPlayer";
            } catch (NoSuchElementException teamNotFound) {
                model.addAttribute("teams", teamService.listTeams());
                return "teams";
            }
        }
    }
}
