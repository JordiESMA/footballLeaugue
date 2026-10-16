package com.ligafutbol.api.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {

    //ATRIBUTS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int matchday;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus = MatchStatus.SCHEDULED;

    @ElementCollection
    private List<Event> events = new ArrayList<>();

    //CONSTRUCTORS
    public Match() {}

    public Match(int matchday, Team homeTeam, Team awayTeam) {
        this.matchday = matchday;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    /**
     * Method that makes the result with the data of the match
     * @return the formatted result of the match
     */
    public String getResult() {
        int homeGoals = 0;
        int awayGoals = 0;

        for (Event event : events) {
            if (event.getEventName() == EventType.GOAL) {
                Long teamId = event.getPlayer().getTeam().getId();
                if (teamId.equals(homeTeam.getId())) {
                    homeGoals++;
                } else if (teamId.equals(awayTeam.getId())) {
                    awayGoals++;
                }
            }
        }
        return homeTeam.getName() + " : " + homeGoals + " - " + awayGoals + " : " + awayTeam.getName();
    }

    /**
     * Add the event after the service has validated it
     * @param event (GOAL REDCARD YELLOWCARD)
     */
    public void addEvent(Event event) {
        events.add(event);
    }

    //GETTERS
    public Long getId() {
        return id;
    }

    public int getMatchday() {
        return matchday;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    // unmodifiable list so nobody can edit the events with getEvents().clear()
    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }
}