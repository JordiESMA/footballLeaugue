package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Match {

    //ATRIBUTS

    private int matchday;
    private Team homeTeam;
    private Team awayTeam;
    private MatchStatus matchStatus = MatchStatus.SCHEDULED ;
    private ArrayList<Event> events = new ArrayList<>();

    //CONSTRUCTOR

    public Match(int matchday, Team homeTeam, Team awayTeam) {
        this.matchday = matchday;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    /**
     * Method that makes the result with the data of the match
     * @return the formatted result of the match
     */
    public String getResult(){
        int homeGoals = 0;
        int awayGoals = 0;

        for (Event event : events){
            if (event.getEventName() == EventType.GOAL){
             if (event.getPlayer().getTeam() == homeTeam){
                 homeGoals ++;
             }else if (event.getPlayer().getTeam() == awayTeam){
                 awayGoals ++;
             }
            }
        }//If later I want manipulate the result in the controller I have to change that method
        return homeTeam.getName() +" : " +  homeGoals + " - " + awayGoals + " : " + awayTeam.getName();
    }

    /**
     * Add the event after the service has validated it
     * @param event (GOL REDCARD YELLOWCARD)
     */
    public void addEvent(Event event){
        events.add(event);
    }

    //GETTERS

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
    //If we don't pass a copy or a unmodifiable list, with .getEvents.clear() we can edit and it's a problem
    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
