package org.example;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;

import static org.example.AccountType.ADMIN;
import static org.example.AccountType.CONTRIBUTOR;

public class Contributor<T extends Common> extends Staff implements RequestsManager, FactoryInterface{
    //private Information information;
    //private Credentials credentials;
    private ArrayList<Request> requests;


    public <T> Contributor(String username, User.Information.Builder information, AccountType accountType, String experience, ArrayList notifs, SortedSet<T> favorites, SortedSet productionsANDactors) {
        super(username, accountType, information, experience, notifs, favorites, productionsANDactors);
        this.requests = new ArrayList<>();
        //this.credentials = credentials;
        //this.information = information.build();
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    @Override
    public void displayNewUser() {
        System.out.println("New Contributor User.");
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }

    @Override
    public void createRequest(List<User> users, List<Production> productions, List<Actor> actors, List<Request> reqs) {
        Scanner scanner = new Scanner(System.in);
        String notif = null;
        int ok = 1;
        String productionORactor = null, username_fix = null;
        while (ok == 1) {
            System.out.println("Choose request type:");
            System.out.println("\t1) ACTOR_ISSUE\n" + "\t2) MOVIE_ISSUE\n" + "\t3) DELETE_ACCOUNT\n" + "\t4) OTHERS");
            String reqType = scanner.nextLine();
            RequestTypes type = null;
            switch (reqType){
                case "1": {
                    type = RequestTypes.ACTOR_ISSUE;
                    ok = 0;
                    break;
                }
                case "2": {
                    type = RequestTypes.MOVIE_ISSUE;
                    ok = 0;
                    break;
                }
                case "3": {
                    type = RequestTypes.DELETE_ACCOUNT;
                    ok = 0;
                    break;
                }
                case "4": {
                    type = RequestTypes.OTHERS;
                    ok = 0;
                    break;
                }
                default:
                    System.out.println("Invalid request type");
            }
            if (ok == 0) {
                Staff s = null;
                String prodORactChoice = null;
                int poz = 0;
                if (type == RequestTypes.ACTOR_ISSUE) {
                    System.out.println("Actors:");
                    int i = 0;
                    for (Actor a : actors) {
                        System.out.println((i + 1) + ") " + a.getName());
                        i += 1;
                    }
                    System.out.println("Insert the actor (number):" );
                    prodORactChoice = scanner.nextLine();
                    try {
                        poz = Integer.parseInt(prodORactChoice);
                        productionORactor = actors.get(poz - 1).getName();
                    } catch (NumberFormatException e){
                        System.out.println("Not a number");
                        break;
                    }
                }
                if (type == RequestTypes.MOVIE_ISSUE) {
                    System.out.println("Productions:");
                    int i = 0;
                    for (Production p : productions){
                        System.out.println((i + 1) + ") " + p.getName());
                        i +=1;
                    }
                    System.out.println("Insert the production (number):" );
                    prodORactChoice = scanner.nextLine();
                    try {
                        poz = Integer.parseInt(prodORactChoice);
                        productionORactor = productions.get(poz - 1).getName();
                    } catch (NumberFormatException e){
                        System.out.println("Not a number");
                        break;
                    }
                }
                System.out.println("Description: ");
                String description = scanner.nextLine();
                LocalDateTime date = LocalDateTime.now();
                Request request = null;
                if (type == RequestTypes.DELETE_ACCOUNT || type == RequestTypes.OTHERS){
                    request = new Request(type, date, null, description, this.getUsername(), "ADMIN");
                    for (User u : users)
                        if (u.getAccountType().equals(ADMIN))
                            request.addObserver(u);
                    notif = "New " + type + " request from user '" + this.getUsername() + "'.";
                    request.notifyObservers(notif, "ADMIN", null, 3);
                    RequestHolder.addRequest(request);
                    //user.createRequest(request);
                    this.requests.add(request);
                    int yes_obs = 0;
                    for (Observer obs : request.getObservers())
                        if (obs.equals(this)){
                            yes_obs = 1;
                            break;
                        }
                    if (yes_obs == 0)
                        request.addObserver(this);
                    System.out.println("Added request");
                }
                else {
                    for (User u : users)
                        if (u.getAccountType() == CONTRIBUTOR || u.getAccountType() == ADMIN) {
                            s = (Staff) u;
                            SortedSet<Common> set = s.getProductionsANDactors();
                            for (Common el : set){
                                if(el.getClass().equals(Actor.class))
                                {
                                    Actor actEl = (Actor) el;
                                    if (actEl.getName().equals(productionORactor))
                                        username_fix = s.getUsername();
                                }
                                if(el.getClass().equals(Movie.class) || el.getClass().equals(Series.class))
                                {
                                    Production prodEl = (Production) el;
                                    if (prodEl.name.equals(productionORactor))
                                        username_fix = s.getUsername();
                                }
                            }
                        }
                    if (productionORactor == null && (!reqType.equals("3") && !reqType.equals("4")))
                        System.out.println("Production or actor not found");
                    else {
                        if (username_fix.equals(this.getUsername()))
                            System.out.println("Cant create a request for the same contributor");
                        else if (username_fix.equals("ADMIN")){
                            request = new Request(type, date, productionORactor, description, this.getUsername(), username_fix);
                            for (User u : users)
                                if (u.getAccountType().equals(ADMIN))
                                    request.addObserver(u);
                            notif = "New " + type + " request from user '" + this.getUsername() + "'.";
                            request.notifyObservers(notif, "ADMIN", null, 3);
                            //user.createRequest(request);
                            this.requests.add(request);
                            RequestHolder.addRequest(request);
                            int yes_obs = 0;
                            for (Observer obs : request.getObservers())
                                if (obs.equals(this)){
                                    yes_obs = 1;
                                    break;
                                }
                            if (yes_obs == 0)
                                request.addObserver(this);
                            System.out.println("Added request");
                        } else {
                            request = new Request(type, date, productionORactor, description, this.getUsername(), username_fix);
                            for (User u : users)
                                if (u.getUsername().equals(username_fix)){
                                    request.addObserver(u);
                                    break;
                                }
                            notif = "New " + type + " request from user '" + this.getUsername() + "'.";
                            request.notifyObservers(notif, username_fix, null, 2);
                            //user.createRequest(request);
                            this.requests.add(request);
                            reqs.add(request);
                            int yes_obs = 0;
                            for (Observer obs : request.getObservers())
                                if (obs.equals(this)){
                                    yes_obs = 1;
                                    break;
                                }
                            if (yes_obs == 0)
                                request.addObserver(this);
                            System.out.println("Added request");
                        }
                    }
                }
            }
        }
        //requests.add(r);
        System.out.println("The request was added(Contributor class)");
    }

    @Override
    public void removeRequest(List<Request> reqs) {
        Scanner scanner = new Scanner(System.in);
        int i = 1;
        for(Object o : this.getRequests()) {
            Request r = (Request) o;
            System.out.println(i + ") " + r.toString());
            i++;
        }
        System.out.println("Insert the position of the request you want to delete:");
        String answer = scanner.nextLine();
        int position = Integer.parseInt(answer);
        if (position < 1 || position > i - 1)
            System.out.println("Invalid choice");
        else {
            Request request = this.getRequests().get(position-1);
            if (request.getUsername_fix().equals("ADMIN"))
                RequestHolder.removeRequest(request);
            else
                reqs.remove(request);
            this.requests.remove(request);
        }
        //requests.remove(r);
        System.out.println("The request was removed(Contributor class)");
    }


}
