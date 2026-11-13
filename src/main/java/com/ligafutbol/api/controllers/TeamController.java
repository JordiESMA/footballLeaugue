package com.ligafutbol.api.controllers;

import com.ligafutbol.api.services.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String add(@RequestParam String name, @RequestParam String competitionGroup,
                      Model model, RedirectAttributes redirectAttributes) {

        try {
            teamService.addTeam(name, competitionGroup);
            redirectAttributes.addFlashAttribute("success", "Equipo añadido correctamente");
            return "redirect:/teams";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("competitionGroup", competitionGroup);
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

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("team", teamService.getTeamById(id));
            return "team-edit";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("teams", teamService.listTeams());
            return "teams";
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @RequestParam String name, @RequestParam String competitionGroup,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            teamService.editTeam(id, name, competitionGroup);
            redirectAttributes.addFlashAttribute("success", "Equipo actualizado correctamente");
            return "redirect:/teams/" + id;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("competitionGroup", competitionGroup);
            try {
                model.addAttribute("team", teamService.getTeamById(id));
                return "team-edit";
            } catch (NoSuchElementException teamNotFound) {
                model.addAttribute("teams", teamService.listTeams());
                return "teams";
            }
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        try {
            teamService.deleteTeam(id);
            redirectAttributes.addFlashAttribute("success", "Equipo eliminado correctamente");
            return "redirect:/teams";

        } catch (NoSuchElementException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("teams", teamService.listTeams());
            return "teams";
        }
    }

}
