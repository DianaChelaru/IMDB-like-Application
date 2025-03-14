package org.example;

import java.util.*;

import static org.example.AccountType.*;

public abstract class Staff<T extends Comparable<T>> extends User implements StaffInterface {
    private List<Request> requests;
    private SortedSet<T> productionsANDactors;


    public Staff(String username, AccountType accountType, Information.Builder information, String experience, ArrayList<String> notifs, SortedSet<T> favorites, SortedSet<T> productionsANDactors) {
        super(username, accountType, information, experience, notifs, favorites);
        this.requests = new ArrayList<>();
        this.productionsANDactors = productionsANDactors;
    }

    public SortedSet<T> getProductionsANDactors() {
        return productionsANDactors;
    }

    @Override
    public void addProductionSystem(List<Production> productions) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert production title: ");
        String title = scanner.nextLine();
        System.out.println("Insert the director(s) (Ex: Director1, Director2 etc.): ");
        String directorsInput = scanner.nextLine();
        String[] dirs = directorsInput.split(",");
        List<String> directors = new ArrayList<>(Arrays.asList(dirs));
        System.out.println("Insert the actor(s) (Ex: Actor1,Actor2 etc.): ");
        String actorsInput = scanner.nextLine();
        String[] acts = actorsInput.split(",");
        List<String> actors = new ArrayList<>(Arrays.asList(acts));
        System.out.println("Insert the genre(s): ");
        int again = 0;
        List<Genre> genres = new ArrayList<>();
        while (again == 0) {
            System.out.println("Choose the genre: ");
            for (int i = 0; i < Genre.values().length; i++)
                System.out.println((i + 1) + ") " + Genre.values()[i]);
            String n = scanner.nextLine();
            try {
                int num = Integer.parseInt(n);
                if (num < 1 || num > Genre.values().length) {
                    System.out.println("Invalid genre");
                } else {
                    genres.add(Genre.values()[num - 1]);
                    System.out.println("Choose again? [Y/N]");
                    String ans = scanner.nextLine();
                    if (ans.equals("N"))
                        again = 1;
                    else if (!ans.equals("Y")) {
                        System.out.println("Invalid answer");
                        break;
                    }
                }
            }catch (NumberFormatException e) {
                System.out.println("Not a valid number");
            }

        }
        System.out.println("Insert the description: ");
        String description = scanner.nextLine();
        System.out.println("Movie or Series? [M/S]");
        String type = scanner.nextLine();
        if (type.equals("M")) {
            System.out.println("Insert the duration of the movie (x minutes):");
            String movie_duration = scanner.nextLine();
            System.out.println("Insert the year: ");
            String y = scanner.nextLine();
            int year = 0;
            try {
                year = Integer.parseInt(y);
            } catch (NumberFormatException e) {
                System.out.println("Invalid year");
            }
            Movie movie = new Movie(title, directors, actors, genres, new ArrayList<>(), description, 0.0, movie_duration, year, "Movie");
            this.productionsANDactors.add((T) movie);
            productions.add(movie);
            System.out.println("Movie added");
            if (!this.getClass().equals(Admin.class)) {
                this.setExperienceStrategy(movie);
                int exp = this.calculateExperience(this.getExperience(), 1);
                String experience = Integer.toString(exp);
                this.setExperience(experience);
            }
        } else if (type.equals("S")) {
            System.out.println("Insert the year: ");
            String y = scanner.nextLine();
            int year = 0;
            try {
                year = Integer.parseInt(y);
            } catch (NumberFormatException e){
                System.out.println("Invalid number");
            }
            System.out.println("Insert number of seasons: ");
            String nr  = scanner.nextLine();
            int number = 0;
            try {
                number = Integer.parseInt(nr);
            } catch (NumberFormatException e){
                System.out.println("Invalid number");
            }
            Map<String,List<Episoade>> dictionary = new LinkedHashMap<>();
            for (int i = 0; i < number; i++) {
                System.out.println("Insert the season " + (i + 1) + " name:");
                String season_name = scanner.nextLine();
                System.out.println("Insert the number of episoades for this season:");
                String nr_ep = scanner.nextLine();
                int nrEp = 0;
                try {
                    nrEp = Integer.parseInt(nr_ep);
                } catch (NumberFormatException e){
                    System.out.println("Invalid number");
                    break;
                }
                List<Episoade> episoades= new ArrayList<>();
                for (int j = 0; j < nrEp; j++) {
                    System.out.println("\tInsert episoade name:");
                    String epName = scanner.nextLine();
                    System.out.println("\tInsert episoade duration:");
                    String epDuration = scanner.nextLine();
                    Episoade ep = new Episoade(epName,epDuration);
                    episoades.add(ep);
                }
                dictionary.put(season_name, episoades);
            }
            Series series = new Series(title, directors, actors, genres, new ArrayList<>(), description, 0.0, year, number, dictionary, "Series");
            this.productionsANDactors.add((T) series);
            productions.add(series);
            System.out.println("Series added");
            if (!this.getClass().equals(Admin.class)) {
                this.setExperienceStrategy(series);
                int exp = this.calculateExperience(this.getExperience(), 1);
                String experience = Integer.toString(exp);
                this.setExperience(experience);
            }
        } else
            System.out.println("Invalid choice");
    }

    @Override
    public void updateProduction(String name, List<Production> productions) {
        String updateStr = null;
        List<String> updateLStr = null;
        int updateInt = 0;
        Scanner scanner = new Scanner(System.in);
        int ok = 1, not_updated = 0;
        SortedSet<T> set = this.getProductionsANDactors();
        for (T el : set)
            if (el.getClass().equals(Movie.class) || el.getClass().equals(Series.class)){
                Production prodEl = (Production) el;
                if (prodEl.getName().equals(name)){
                    productions.remove(prodEl);
                    System.out.println("What do you want to modify?");
                    System.out.println("\t1) Title\n\t2) Directors\n\t3) Actors\n\t4) Genres\n\t5) Description");
                    if (prodEl.getClass().equals(Movie.class))
                        System.out.println("\t6) Duration\n\t7) Production year");
                    if (prodEl.getClass().equals(Series.class))
                        System.out.println("\t6) Production year\n\t7) Number of seasons\n\t8) Seasons");
                    String answer = scanner.nextLine();
                    switch (answer){
                        case "1":{
                            System.out.println("Insert new title:");
                            updateStr = scanner.nextLine();
                            prodEl.setName(updateStr);
                            ok =0;
                            break;
                        }
                        case "2":{
                            System.out.println("Insert the directors:");
                            updateStr = scanner.nextLine();
                            String[] dirs = updateStr.split(",");
                            updateLStr = new ArrayList<>(Arrays.asList(dirs));
                            prodEl.setDirectors_names(updateLStr);
                            ok=0;
                            break;
                        }
                        case "3":{
                            System.out.println("Insert the actors:");
                            updateStr = scanner.nextLine();
                            String[] actors = updateStr.split(",");
                            updateLStr = new ArrayList<>(Arrays.asList(actors));
                            prodEl.setActors_names(updateLStr);
                            ok=0;
                            break;
                        }
                        case "4":{
                            int again = 0;
                            List<Genre> genres = new ArrayList<>();
                            while (again == 0) {
                                System.out.println("Choose the genre: ");
                                for (int i = 0; i < Genre.values().length; i++)
                                    System.out.println((i + 1) + ") " + Genre.values()[i]);
                                String n = scanner.nextLine();
                                try {
                                int num = Integer.parseInt(n);
                                    if (num < 1 || num > Genre.values().length) {
                                        System.out.println("Invalid genre");
                                    } else {
                                        genres.add(Genre.values()[num - 1]);
                                        System.out.println("Choose again? [Y/N]");
                                        String ans = scanner.nextLine();
                                        if (ans.equals("N"))
                                            again = 1;
                                        else if (!ans.equals("Y")) {
                                            System.out.println("Invalid answer");
                                            break;
                                        }
                                    }
                                }catch (NumberFormatException e) {
                                    System.out.println("Not a valid number");
                                }

                            }
                            prodEl.setGenres(genres);
                            break;
                        }
                        case "5":{
                            System.out.println("Insert the description");
                            updateStr = scanner.nextLine();
                            prodEl.setSubject(updateStr);
                            ok=0;
                            break;
                        }
                        case "6":{
                            if(prodEl.getClass().equals(Movie.class)){
                                System.out.println("Insert the duration of the movie (x minutes):");
                                updateStr = scanner.nextLine();
                                ((Movie) prodEl).setMovie_duration(updateStr);
                            } else {
                                System.out.println("Insert the year: ");
                                updateStr = scanner.nextLine();
                                try {
                                    updateInt = Integer.parseInt(updateStr);
                                } catch (NumberFormatException e)
                                {
                                    ok =0;
                                    System.out.println("Invalid number");
                                    break;
                                }
                                ((Series) prodEl).setSeries_year(updateInt);
                            }
                            break;
                        }
                        case "7":{
                            if (prodEl.getClass().equals(Movie.class)){
                                System.out.println("Insert the year: ");
                                updateStr = scanner.nextLine();
                                try {
                                    updateInt = Integer.parseInt(updateStr);
                                } catch (NumberFormatException e){
                                    System.out.println("Invalid number");
                                    ok = 0;
                                    break;
                                }
                                ((Movie) prodEl).setMovie_year(updateInt);
                            } else {
                                System.out.println("Insert number of seasons: ");
                                updateStr  = scanner.nextLine();
                                try {
                                    updateInt = Integer.parseInt(updateStr);
                                } catch (NumberFormatException e){
                                    System.out.println("Invalid number");
                                    ok = 0;
                                    break;
                                }
                                ((Series) prodEl).setNumber_of_seasons(updateInt);
                            }
                            break;
                        }
                        case "8":{
                            if(prodEl.getClass().equals(Series.class)){
                                Map<String,List<Episoade>> dictionary = new LinkedHashMap<>();
                                for (int i = 0; i < ((Series) prodEl).getNumber_of_seasons(); i++) {
                                    System.out.println("Insert the season " + (i + 1) + " name:");
                                    String season_name = scanner.nextLine();
                                    System.out.println("Insert the number of episoades for this season:");
                                    String nr_ep = scanner.nextLine();
                                    int nrEp = 0;
                                    try{
                                        nrEp = Integer.parseInt(nr_ep);
                                    } catch (NumberFormatException e){
                                        System.out.println("Invalid number");
                                        ok = 0;
                                        break;
                                    }
                                    List<Episoade> episoades= new ArrayList<>();
                                    for (int j = 0; j < nrEp; j++) {
                                        System.out.println("\tInsert episoade name:");
                                        String epName = scanner.nextLine();
                                        System.out.println("\tInsert episoade duration:");
                                        String epDuration = scanner.nextLine();
                                        Episoade ep = new Episoade(epName,epDuration);
                                        episoades.add(ep);
                                    }
                                    dictionary.put(season_name, episoades);
                                }
                                ((Series) prodEl).setDictorinary_series(dictionary);
                            } else
                                System.out.println("Invalid choice");
                        }
                        default:
                            System.out.println("Invalid choice");
                    }
                    if (not_updated == 1) {
                        productions.add(prodEl);
                        System.out.println("Production updated");
                        ok = 0;
                    }
                        break;
                }
            }
        if (ok == 1)
            System.out.println("Production not found in your contributions");
    }

    @Override
    public void addActorSystem(List<Actor> actors) {
        Scanner scanner = new Scanner(System.in);
        int mistake = 0;
        System.out.println("Insert name:");
        String name = scanner.nextLine();
        System.out.println("Insert roles (Ex. title1-type1,title2-type2 etc.): ");
        String r = scanner.nextLine();
        List<Map.Entry<String, String>> roles = new ArrayList<>();
        String[] rolesData = r.split(",");
        for (String role : rolesData) {
            String[] titleANDtype = role.split("-");
            if (titleANDtype[1].equals("Movie") || titleANDtype[1].equals("Series")){
                Map.Entry<String, String> pair = new AbstractMap.SimpleEntry<>(titleANDtype[0], titleANDtype[1]);
                roles.add(pair);
            } else {
                mistake = 1;
                System.out.println("Invalid type");
                break;
            }
        }
        if (mistake != 1) {
            System.out.println("Insert biography:");
            String biography = scanner.nextLine();
            Actor actor = new Actor(name, roles, biography);
            this.productionsANDactors.add((T) actor);
            actors.add(actor);
            System.out.println("Actor added");
            if (!this.getClass().equals(Admin.class)) {
                this.setExperienceStrategy(actor);
                int exp = this.calculateExperience(this.getExperience(), 1);
                String experience = Integer.toString(exp);
                this.setExperience(experience);
            }
        } else {
            System.out.println("Actor not added");
        }
    }

    @Override
    public void updateActor(String name, List<Actor> actors) {
        Scanner scanner = new Scanner(System.in);
        String updateStr = null;
        List<String> updateLStr = null;
        int updateInt = 0;
        int ok = 1, mistake = 0;
        SortedSet<T> set = this.getProductionsANDactors();
        for (T el : set)
            if (el.getClass().equals(Actor.class)){
                Actor actEl = (Actor) el;
                if (actEl.getName().equals(name)){
                    //this.productionsANDactors.remove(actEl);
                    actors.remove(actEl);
                    System.out.println("What do you want to modify?");
                    System.out.println("\t1)Name\n\t2) Roles\n\t3) Biography");
                    String answer = scanner.nextLine();
                    switch (answer){
                        case "1":{
                            System.out.println("Insert name:");
                            updateStr = scanner.nextLine();
                            actEl.setName(updateStr);
                            break;
                        }
                        case "2":{
                            System.out.println("Insert roles (Ex. title1-type1,title2-type2 etc.): ");
                            updateStr = scanner.nextLine();
                            List<Map.Entry<String, String>> roles = new ArrayList<>();
                            String[] rolesData = updateStr.split(",");
                            for (String role : rolesData) {
                                String[] titleANDtype = role.split("-");
                                if (titleANDtype[1].equals("Movie") || titleANDtype[1].equals("Series")){
                                    Map.Entry<String, String> pair = new AbstractMap.SimpleEntry<>(titleANDtype[0], titleANDtype[1]);
                                    roles.add(pair);
                                } else {
                                    mistake = 1;
                                    System.out.println("Invalid type");
                                    break;
                                }
                            }
                            actEl.setRoles(roles);
                            break;
                        }
                        case "3":{
                            System.out.println("Insert biography:");
                            updateStr = scanner.nextLine();
                            actEl.setBiography(updateStr);
                            break;
                        }
                        default:
                    }
                    if (mistake != 1) {
                        actors.add(actEl);
                        System.out.println("Actor updated");
                        ok = 0;
                        break;
                    }
                }
            }
        if (ok == 1 && mistake != 1)
            System.out.println("Actor not found in your contributions");
    }

    @Override
    public void removeProductionSystem(String name, List<Production> productions) {
        int ok = 1;
        SortedSet<T> set = this.getProductionsANDactors();
        for (T el : set)
            if (el.getClass().equals(Movie.class) || el.getClass().equals(Series.class)){
                Production prodEl = (Production) el;
                if (prodEl.getName().equals(name)){
                    this.productionsANDactors.remove(prodEl);
                    productions.remove(prodEl);
                    System.out.println("Production removed");
                    ok = 0;
                    if (!this.getClass().equals(Admin.class)) {
                        this.setExperienceStrategy(prodEl);
                        int exp = this.calculateExperience(this.getExperience(), -1);
                        String experience = null;
                        if (exp != 0)
                            experience = Integer.toString(exp);
                        this.setExperience(experience);
                    }
                    break;
                }
            }
        if (ok == 1)
            System.out.println("Production not found in your contributions");
    }

    @Override
    public void removeActorSystem(String name, List<Actor> actors) {
        int ok = 1;
        SortedSet<T> set = this.getProductionsANDactors();
        for (T el : set)
            if (el.getClass().equals(Actor.class)){
                Actor actEl = (Actor) el;
                if (actEl.getName().equals(name)){
                    this.productionsANDactors.remove(actEl);
                    actors.remove(actEl);
                    System.out.println("Actor removed");
                    ok = 0;
                    if (!this.getClass().equals(Admin.class)) {
                        this.setExperienceStrategy(actEl);
                        int exp = this.calculateExperience(this.getExperience(), -1);
                        String experience = null;
                        if (exp != 0)
                            experience = Integer.toString(exp);
                        this.setExperience(experience);
                    }
                    break;
                }
            }
        if (ok == 1)
            System.out.println("Actor not found in your contributions");
    }


    @Override
    public void solveRequests(Staff user, List<User> users, List<Request> requests) {
        Scanner scanner = new Scanner(System.in);
        int i = 0, ok = 1;
        List<Request> yourRequests = new ArrayList<>();
        String answer = null;
        while (ok == 1) {
            System.out.println("Visualize or mark solved? [V/M]");
            answer = scanner.nextLine();
            if (answer.equals("V") || answer.equals("M")) {
                ok = 0;
                for (Request r : requests) {
                    if (r.getUsername_fix().equals(user.getUsername()))
                        yourRequests.add(r);
                    if(r.getUsername_fix().equals("ADMIN") && this.getAccountType().equals(ADMIN))
                        yourRequests.add(r);
                }
                if (user.getAccountType().equals(ADMIN))
                    for (Request r: RequestHolder.getRequests())
                        yourRequests.add(r);
            } else
                System.out.println("Invalid choice");
        }
        if (answer.equals("V")){
            i=1;
            System.out.println("These are your requests:");
            for (Request r : yourRequests){
                System.out.println(i + ") " + r);
                i++;
            }
            System.out.println("Do you want to reject a request? [Y/N]");
            String reject = scanner.nextLine();
            if (reject.equals("Y")) {
                System.out.println("Enter the number of the request:");
                String n = scanner.nextLine();
                int number = Integer.parseInt(n);
                if (number < 1 || number > i-1)
                    System.out.println("Invalid choice");
                else{
                    Request r = yourRequests.get(number - 1);
                    User us = null;
                    for (User u : users)
                        if (u.getUsername().equals(r.getUsername_problem())){
                            us = u;
                            break;
                        }
                    String notif = "Your request '" + r.getProblem_desciption() + "' got rejected";
                    r.addObserver(us);
                    r.notifyObservers(notif, r.getUsername_problem(), null, 1);
                    remove_request(r, users, requests);
                }
            }
            if (!reject.equals("Y") && !reject.equals("N"))
                System.out.println("Invalid choice");
        }
        if (answer.equals("M")) {
            i = 1;
            for (Request r : yourRequests) {
                System.out.println(i + ") " + r);
                i++;
            }
            System.out.println("Enter the number of the request you solved:");
            String n = scanner.nextLine();
            int number = Integer.parseInt(n);
            if (number < 1 || number > i-1)
                System.out.println("Invalid choice");
            else{
                Request r = yourRequests.get(number - 1);
                User us = null;
                for (User u : users)
                    if (u.getUsername().equals(r.getUsername_problem()))
                        us = u;
                //r.addObserver(us);
                if (!r.getRequestType().equals(RequestTypes.DELETE_ACCOUNT)) {
                    String notif = "Your request '" + r.getProblem_desciption() + "' got solved";
                    r.notifyObservers(notif, r.getUsername_problem(), null, 1);
                    if (!r.getRequestType().equals(RequestTypes.OTHERS)) {
                        us.setExperienceStrategy(r);
                        int exp = us.calculateExperience(us.getExperience(), 1);
                        String experience = Integer.toString(exp);
                        us.setExperience(experience);
                    }
                }
                remove_request(r, users, requests);
            }
        }

    }

    public void remove_request(Request r, List<User> users, List<Request> requests){
        if (requests.contains(r))
            requests.remove(r);
        if (RequestHolder.getRequests().contains(r))
            RequestHolder.removeRequest(r);
        String username = r.getUsername_problem();
        for (User u : users)
            if (u.getUsername().equals(username)) {
                if (u.getAccountType().equals(REGULAR)) {
                    Regular reg = (Regular) u;
                    reg.getRequests().remove(r);
                }
                if (u.getAccountType().equals(CONTRIBUTOR)) {
                    Contributor c = (Contributor) u;
                    c.getRequests().remove(r);
                }
            }

    }

    @Override
    public void addMovie(Movie movie) {
        super.addMovie(movie);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
