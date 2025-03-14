package org.example;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;

import static org.example.AccountType.ADMIN;
import static org.example.AccountType.CONTRIBUTOR;

public class Regular<T extends Common> extends User implements RequestsManager, FactoryInterface{
    private ArrayList<Request> requests;
    private List<String> ratingsProd;

    public <T> Regular(String username, User.Information.Builder information, AccountType accountType, String experience, ArrayList<String> notifs, SortedSet<T> favorites) {
        super(username, accountType, information, experience, notifs, favorites);
        this.requests = new ArrayList<>();
        this.ratingsProd = new ArrayList<>();
    }

    @Override
    public void update(String notification) {
        super.update(notification);
    }

    @Override
    public User logout(User user) {
        return super.logout(user);
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
                        i += 1;
                    }
                    System.out.println("Insert the production (number):" );
                    prodORactChoice = scanner.nextLine();
                    try {
                        poz = Integer.parseInt(prodORactChoice);
                        productionORactor = productions.get(poz - 1).getName();
                    } catch (NumberFormatException e) {
                        System.out.println("Not a number");
                        break;
                    }
                }
                System.out.println("Description: ");
                String description = scanner.nextLine();
                LocalDateTime date = LocalDateTime.now();
                Request request;
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
                            System.out.println(s.getUsername());
                            System.out.println(s.getProductionsANDactors());
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
                        if (username_fix.equals("ADMIN"))
                        {
                            request = new Request(type, date, productionORactor, description, this.getUsername(), username_fix);
                            for (User u : users)
                                if (u.getAccountType().equals(ADMIN))
                                    request.addObserver(u);
                            notif = "New " + type + " request from user '" + this.getUsername() + "'.";
                            request.notifyObservers(notif, "ADMIN", null, 3);
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
                            RequestHolder.addRequest(request);
                        } else {
                            request = new Request(type, date, productionORactor, description, this.getUsername(), username_fix);
                            for (User u : users)
                                if (u.getUsername().equals(username_fix)){
                                    request.addObserver(u);
                                    break;
                                }
                            notif = "New " + type + " request from user '" + this.getUsername() + "'.";
                            request.notifyObservers(notif, username_fix, null, 2);
                            this.requests.add(request);
                            //user.createRequest(request);
                            int yes_obs = 0;
                            for (Observer obs : request.getObservers())
                                if (obs.equals(this)){
                                    yes_obs = 1;
                                    break;
                                }
                            if (yes_obs == 0)
                                request.addObserver(this);
                            reqs.add(request);
                        }
                        System.out.println("Added request");
                    }
                }
            }
        }
        //requests.add(r);
        System.out.println("The request was added(Regular class)");
    }

    @Override
    public void removeRequest(List<Request> reqs) {
        Scanner scanner = new Scanner(System.in);
        int i = 1;
        for(Request r : this.requests) {
            System.out.println(i + ") " + r.toString());
            i++;
        }
        System.out.println(this.getRequests());
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
        System.out.println("The request was removed(Regular class)");
    }


    @Override
    public void displayNewUser() {
        System.out.println("New Regular User.");
    }


    @Override
    public void addMovie(Movie movie) {
        super.addMovie(movie);
    }

    @Override
    public void addSeries(Series series) {
        super.addSeries(series);
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
    }

    @Override
    public void removeMovie(Movie movie) {
        super.removeMovie(movie);
    }

    @Override
    public void removeSeries(Series series) {
        super.removeSeries(series);
    }

    @Override
    public void removeActor(Actor actor) {
        super.removeActor(actor);
    }


    @Override
    public int compareTo(@NotNull User o) {
        return 0;
    }
    public void addReview(Production prod, List<User> users) {
        double score = 0.0;
        String s = null, comment = null;
        Scanner scanner = new Scanner(System.in);
        int ok = 1, exc = 0;
        for (Rating r : prod.ratings)
            if (r.getUsername().equals(this.getUsername())) {
                System.out.println("Review already added");
                ok = 0;
                break;
            }
        if (ok == 1) {
            int aux = 1;
            while (aux == 1) {
                System.out.println("Insert score: ");
                s = scanner.nextLine();
                try {
                    score = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number");
                    exc = 1;
                    break;
                }
                if (score >= 1 && score <= 10)
                    aux = 0;
                else
                    System.out.println("Invalid score");
            }
            if (exc == 0) {
                System.out.println("Insert comment: ");
                comment = scanner.nextLine();
                Rating rating = new Rating(this.getUsername(), score, comment);
                prod.ratings.add(rating);
                this.ratingsProd.add(prod.getName());
                rating.addObserver(this);
                this.setExperienceStrategy(rating);
                int exp = this.calculateExperience(this.getExperience(), 1);
                String experience = Integer.toString(exp);
                this.setExperience(experience);
                String notif = "New review added for '" + prod.getName() + "' by '" + this.getUsername() + "'.";
                rating.notifyObservers(notif, this.getUsername(), prod.getName(), 1);
                int user_erased = 1;
                for (User u : users) {
                    if (u.getClass().equals(Contributor.class)) {
                        for (Object p : ((Contributor) u).getProductionsANDactors()) {
                            if (p.getClass().equals(Movie.class) || p.getClass().equals(Series.class)) {
                                if (((Production) p).getName().equals(prod.getName())) {
                                    user_erased = 0;
                                    rating.addObserver(u);
                                    rating.notifyObservers(notif, u.getUsername(), prod.getName(), 2);
                                }

                            }
                        }
                    }
                    if (u.getClass().equals(Admin.class)) {
                        for (Object p : ((Admin) u).getProductionsANDactors()) {
                            if (p.getClass().equals(Movie.class) || p.getClass().equals(Series.class)) {
                                if (((Production) p).getName().equals(prod.getName())) {
                                    user_erased = 0;
                                    rating.addObserver(u);
                                    rating.notifyObservers(notif, u.getUsername(), prod.getName(), 3);
                                }

                            }
                        }
                    }
                }
                if (user_erased == 1) {
                    for (User u : users)
                        if (u.getClass().equals(Admin.class)) {
                            rating.addObserver(u);
                        }
                    rating.notifyObservers(notif, "ADMIN", prod.getName(), 4);
                }
                System.out.println("Review added.");
            }
        }
    }

    public void addRatingsProd(String prodName) {
        this.ratingsProd.add(prodName);
        Rating rating = new Rating(null, 0.0, null);
        rating.addObserver(this);
    }

    public void deleteReview(User user, Production prod) {
        int ok = 1;
        Rating rating = null;
        for (Rating r : prod.ratings)
            if (r.getUsername().equals(user.getUsername())) {
                rating = r;
                ok = 0;
                prod.ratings.remove(rating);
                rating.removeObserver(user);
                this.setExperienceStrategy(rating);
                int exp = this.calculateExperience(this.getExperience(), -1);
                String experience = null;
                if (exp != 0)
                    experience = Integer.toString(exp);
                this.setExperience(experience);
                System.out.println("Review deleted");
                break;
            }
        if (ok == 1)
            System.out.println("Review not found");
    }

    public List<String> getRatingsProd() {
        return ratingsProd;
    }

    public void setRatingsProd(List<String> ratingsProd) {
        this.ratingsProd = ratingsProd;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }


    @Override
    public Information getInformation() {
        return super.getInformation();
    }

    @Override
    public void setInformation(Information.Builder information) {
        super.setInformation(information);
    }

    @Override
    public AccountType getAccountType() {
        return super.getAccountType();
    }

    @Override
    public void setAccountType(AccountType accountType) {
        super.setAccountType(accountType);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @Override
    public List<String> getNotifs() {
        return super.getNotifs();
    }

    @Override
    public void setNotifs(List notifs) {
        super.setNotifs(notifs);
    }

    @Override
    public SortedSet getFavorites() {
        return super.getFavorites();
    }

    @Override
    public void setFavorites(SortedSet favorites) {
        super.setFavorites(favorites);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
