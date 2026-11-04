package com.ligafutbol.api.controllers;

import com.ligafutbol.api.services.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("teams", teamService.listTeams());
        return "teams";
    }

    @PostMapping
    public String add(@RequestParam String name, @RequestParam String city, @RequestParam Integer foundationYear, Model model) {

        try {
            teamService.addTeam(name, city, foundationYear);
            return "redirect:/teams";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "team-form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        try {
            model.addAttribute("team", teamService.getTeamById(id));
            return "team-detail";

        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("teams", teamService.listTeams());
            return "teams";
        }
    }

    @GetMapping("/new")
    public String form() {
        return "team-form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {

        try {
            teamService.deleteTeam(id);
            return "redirect:/teams";

        } catch (NoSuchElementException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("teams", teamService.listTeams());
            return "teams";
        }
    }
}
