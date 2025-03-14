package org.example;

import org.json.simple.JSONArray;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.lang.*;

import static org.example.AccountType.*;

class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message){
        super(message);
    }
}

public class IMDB {
    private static IMDB instance;
    private List<User> users;
    private List<Actor> actors;
    private List<Request> requests;
    private List<Production> productions;

    private IMDB() {
        this.users = new ArrayList<User>();
        this.actors = new ArrayList<Actor>();
        this.requests = new ArrayList<Request>();
        this.productions = new ArrayList<Production>();
    }

    public static IMDB getInstance() {
        if (instance == null)
            instance = new IMDB();
        return instance;
    }

    public <T extends Common> void read_accounts_json() throws Exception {
        Object obj = new JSONParser().parse(new FileReader("src\\accounts.json"));
        JSONArray accountsArray = (JSONArray) obj;
        SortedSet<Common> contributions;
        SortedSet favs;
        ArrayList<String> notifs;

        for (Object accountObj : accountsArray) {
            contributions = new TreeSet<>(Comparator.comparing(Common::getMethod));
            favs = new TreeSet<>(Comparator.comparing(Common::getMethod));
            notifs = new ArrayList<>();
            JSONObject account = (JSONObject) accountObj;
            String username = (String) account.get("username");
            String experience = (String) account.get("experience");

            JSONObject information = (JSONObject) account.get("information");
            JSONObject credentials = (JSONObject) information.get("credentials");
            String email = (String) credentials.get("email");
            String password = (String) credentials.get("password");
            String name = (String) information.get("name");
            String country = (String) information.get("country");
            long age = (long) information.get("age");
            String gender = (String) information.get("gender");
            String birth_date_string = (String) information.get("birthDate");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birth_date = LocalDate.parse(birth_date_string, dtf);

            String accountType = (String) account.get("userType");
            AccountType userType = AccountType.valueOf(accountType.toUpperCase());

            if (account.containsKey("productionsContribution")) {
                JSONArray prodCont = (JSONArray) account.get("productionsContribution");
                for (Object prod : prodCont) {
                    String production = (String) prod;
                    for (Production p : productions)
                        if (p.name.equals(production))
                            contributions.add(p);
                }
            }

                if (account.containsKey("actorsContribution")) {
                    JSONArray actorCont = (JSONArray) account.get("actorsContribution");
                    for (Object actor : actorCont) {
                        String act = (String) actor;
                        for (Actor a : actors)
                            if (a.getName().equals(act))
                                contributions.add(a);
                    }
                }

            if (account.containsKey("favoriteProductions")) {
                JSONArray prodFav = (JSONArray) account.get("favoriteProductions");
                for (Object prod : prodFav) {
                    String production = (String) prod;
                    for (Production p : productions)
                        if (p.name.equals(production))
                            favs.add(p);
                }
            }

            if (account.containsKey("favoriteActors")) {
                JSONArray actorCont = (JSONArray) account.get("favoriteActors");
                for (Object actor : actorCont) {
                    String act = (String) actor;
                        for (Actor a : actors)
                            if (a.getName().equals(act))
                                 favs.add(a);
                }
            }

            if (account.containsKey("notifications")) {
                JSONArray notifications = (JSONArray) account.get("notifications");
                notifs = new ArrayList<>();
                for (Object n : notifications) {
                    String notification = (String) n;
                    notifs.add(notification);
                }
            }

            if (userType == REGULAR) {
                Credentials credential = new Credentials(email, password);
                User.Information.Builder info = new User.Information.Builder(credential, name, country, (int) age, gender, birth_date);
                Regular<T> user = new Regular<T>(username, info, userType, experience, notifs, favs);
                users.add(user);
            }
            if (userType == CONTRIBUTOR) {
                Credentials credential = new Credentials(email, password);
                User.Information.Builder info = new User.Information.Builder(credential, name, country, (int) age, gender, birth_date);
                Contributor<T> user = new Contributor<T>(username, info, userType, experience, notifs, favs, contributions);
                users.add(user);
            }
            if (userType == ADMIN) {
                Credentials credential = new Credentials(email, password);
                User.Information.Builder info = new User.Information.Builder(credential, name, country, (int) age, gender, birth_date);
                Admin<T> user = new Admin<T>(username, info, userType, experience, notifs, favs, contributions);
                users.add(user);
            }
        }
    }
    public void read_productions_json() throws Exception {
        Object obj = new JSONParser().parse(new FileReader("src\\production.json"));
        JSONArray productionsArray = (JSONArray) obj;
        String plot, duration;
        Double averageRating;
        Map<String, List<Episoade>> seasons = new LinkedHashMap<>();
        long releaseYear, numSeasons;
        for (Object productionObj : productionsArray) {
            JSONObject production = (JSONObject) productionObj;
            String title = (String) production.get("title");
            String type = (String) production.get("type");
            ArrayList<String> directors = (ArrayList<String>) production.get("directors");
            ArrayList<String> actors = (ArrayList<String>) production.get("actors");
            ArrayList<Genre> genres = (ArrayList<Genre>) production.get("genres");

            JSONArray ratings = (JSONArray) production.get("ratings");
            ArrayList<Rating> rating = new ArrayList<>();
            for (Object r: ratings) {
                JSONObject rate = (JSONObject) r;
                String username = (String) rate.get("username");
                long score = (long) rate.get("rating");
                String comment = (String) rate.get("comment");
                Rating elem = new Rating(username, score, comment);
                rating.add(elem);
            }

            if (production.containsKey("plot")) {
                plot = (String) production.get("plot");
            } else {
                plot = null;
            }
            if (production.containsKey("averageRating")) {
                averageRating = (Double) production.get("averageRating");
            } else {
                averageRating = 0.0;
            }
            if (production.containsKey("duration")) {
                duration = (String) production.get("duration");
            } else {
                duration = null;
            }
            if (production.containsKey("releaseYear")) {
                releaseYear = (long) production.get("releaseYear");
            } else {
                releaseYear = -1;
            }
            if (production.containsKey("numSeasons")) {
                numSeasons = (long) production.get("numSeasons");
            } else {
                numSeasons = 0;
            }
            LinkedHashMap<String, List<Episoade>> seriesSeasons = new LinkedHashMap<>();
            if (production.containsKey("seasons")) {
                JSONObject sez = (JSONObject) production.get("seasons");
                for (Object season : sez.keySet()) {
                    String s = (String) season;
                    List<Episoade> ep = new ArrayList<>();
                    JSONArray eps = (JSONArray) sez.get(s);
                    for (Object episoade : eps) {
                        JSONObject e = (JSONObject) episoade;
                        String epName = (String) e.get("episodeName");
                        String epDuration = (String) e.get("duration");
                        Episoade EP = new Episoade(epName, epDuration);
                        ep.add(EP);
                    }
                    seriesSeasons.put(s, ep);
                }
            }
            if (type.equals("Movie")) {
                Movie movie = new Movie(title, directors, actors, genres, rating, plot, averageRating, duration, (int)releaseYear, type);
                productions.add(movie);
            }
            if (type.equals("Series")) {
                Series series = new Series(title, directors, actors, genres, rating, plot, averageRating, (int)releaseYear, (int)numSeasons, seriesSeasons, type);
                productions.add(series);
            }

        }
    }

    public void read_actors_json() throws Exception{
        Object obj = new JSONParser().parse(new FileReader("src\\actors.json"));
        JSONArray actorsArray = (JSONArray) obj;
        String name="", biography="";
        List<Map.Entry<String, String>> performances = new ArrayList<>();
        for (Object actorObj : actorsArray) {
            JSONObject actor = (JSONObject) actorObj;
            name = (String) actor.get("name");
            performances = new ArrayList<>();
            JSONArray performance = (JSONArray) actor.get("performances");
            for (Object perf: performance) {
                JSONObject p = (JSONObject) perf;
                String title = (String) p.get("title");
                String type = (String) p.get("type");
                Map.Entry<String, String> map = new AbstractMap.SimpleEntry<>(title,type);
                performances.add(map);
            }
            biography = (String) actor.get("biography");
            Actor a = new Actor(name, performances, biography);
            actors.add(a);
        }
    }

    public void read_requests_json() throws Exception {
        Object obj = new JSONParser().parse(new FileReader("src\\requests.json"));
        JSONArray requestsArray = (JSONArray) obj;
        String actorName, movieTitle;
        for (Object requestObj : requestsArray) {
            JSONObject request = (JSONObject) requestObj;
            String type = (String) request.get("type");
            RequestTypes requestType = RequestTypes.valueOf(type.toUpperCase());
            String createdDate = (String) request.get("createdDate");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(createdDate, dtf);
            String username = (String) request.get("username");
            actorName = null;
            if (request.containsKey("actorName"))
                actorName = (String) request.get("actorName");
            movieTitle = null;
            if (request.containsKey("movieTitle"))
                movieTitle = (String) request.get("movieTitle");
            String to = (String) request.get("to");
            String description = (String) request.get("description");
            String about = null;
            if (actorName != null)
                about = actorName;
            else
                about = movieTitle;
            Request req = new Request(requestType, date, about, description, username, to);
            requests.add(req);
        }

    }

    public void read_json() throws Exception{
        read_requests_json();
        read_actors_json();
        read_productions_json();
        read_accounts_json();
        for (User u : users) {
            for (Production p : productions)
                for (Rating r : p.ratings)
                    if (r.getUsername().equals(u.getUsername()) && u.getClass().equals(Regular.class))
                        ((Regular<?>) u).addRatingsProd(p.getName());
            for (Request r : requests){
                if (r.getUsername_problem().equals(u.getUsername()) && u.getClass().equals(Regular.class)){
                    r.addObserver(u);
                }
                if (r.getUsername_problem().equals(u.getUsername()) && u.getClass().equals(Contributor.class)){
                    r.addObserver(u);
                }
                if (r.getUsername_fix().equals(u.getUsername()) && u.getClass().equals(Contributor.class))
                    r.addObserver(u);
                if (u.getClass().equals(Admin.class))
                    r.addObserver(u);
            }
        }
        for (Production p : productions)
            for (String a : p.getActors_names()) {
                int ok = 1;
                for (Actor act : actors)
                    if (act.getName().equals(a)){
                        ok = 0;
                        break;
                    }
                if (ok == 1) {
                    List<Map.Entry<String,String>> roles = new ArrayList<>();
                    roles.add(new AbstractMap.SimpleEntry<>(p.getName(), p.getType()));
                    Actor actor = new Actor(a, roles, null);
                    for (User u : users)
                        if (u.getClass().equals(Admin.class))
                            ((Staff)u).getProductionsANDactors().add(actor);
                }

            }
    }

    public String login() {
        int again = 0;
        do {
            System.out.println("Welcome back! Enter your credentials!\n");
            Scanner scanner = new Scanner(System.in);
            String pw = null, username = null, experience = null;
            int ok = 0;
            System.out.print("\temail: ");
            String email = scanner.next();
            for (User user : users) {
                if (user.information.getCredentials().getEmail().equals(email)) {
                    ok = 1;
                    pw = user.information.getCredentials().getPassword();
                    username = user.getUsername();
                    experience = user.getExperience();
                    break;
                }
            }
            System.out.print("\tpassword: ");
            String password = scanner.next();
            if (ok == 1 && password.equals(pw)) {
                again = 0;
                System.out.println("Welcome back user " + username + "!");
                System.out.println("Username: " + username);
                System.out.println("User experience: " + experience);
                return username;
            } else if (ok == 1 && !password.equals(pw)) {
                again = 1;
                System.out.println("Invalid password.");
            } else if (ok == 0) {
                again = 1;
                System.out.println("Invalid email.");
            }

        } while(again == 1);
        return null;
    }

    public void view_productions(){
        Scanner scanner = new Scanner(System.in);
        String answer = null, filter = null, genre = null, ratings = null;
        List<String> details = new ArrayList<>();
        System.out.println("Use Filter? [Y/N]");
        answer = scanner.nextLine();
        if (answer.equals("Y")){
            System.out.println("Genre or Ratings? [G/R]");
            filter = scanner.nextLine();
            if (filter.equals("G")) {
                System.out.println("Insert genre (Ex. Action, Drama, Horror etc.): ");
                genre = scanner.nextLine();
                for (Production p : productions)
                    if (p.getGenres().contains(genre))
                        details.add(p.displayInfo());
                for (String d : details)
                    System.out.println(d);
            } else if (filter.equals("R")) {
                List<Production> prodList = new ArrayList<>(productions);
                Comparator<Production> numOfRatings = Comparator.comparingInt(Production::getNumberOfRatings);
                Collections.sort(prodList, numOfRatings.reversed());
                for (Production p : prodList)
                    details.add(p.displayInfo());
                for (String d : details)
                    System.out.println(d);
            } else
                System.out.println("Invalid filter");
        } else if (answer.equals("N")){
            for (Production p : productions)
                details.add(p.displayInfo());
            for (String d : details)
                System.out.println(d);
        } else
            System.out.println("Invalid choice");
    }

    public void view_actors(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Sort actors? [Y/N]");
        String answer = scanner.nextLine();
        List<Actor> actList = new ArrayList<>(actors);
        if (answer.equals("Y")){
            Collections.sort(actList, Actor::compareTo);
            for (Actor a : actList)
                System.out.println(a);
        } else {
        for (Actor a : actors)
            System.out.println(a);
        }
    }

    public void view_notifications(String username){
        for (User user : users)
            if (user.getUsername().equals(username)){
                for (Object notif : user.getNotifs())
                    System.out.println(notif);
                break;
            }
    }

    public void search_movieORproduction(){
        Scanner scanner = new Scanner(System.in);
        int ok = 1, found = 0;
        while (ok == 1) {
            System.out.println("Production or Actor? [P/A] ");
            String answer = scanner.nextLine();
            if (answer.equals("P")) {
                System.out.println("Insert title: ");
                String title = scanner.nextLine();
                for (Production p : productions)
                    if (p.name.equals(title)) {
                        System.out.println(p);
                        found = 1;
                        break;
                    }
                ok = 0;
                if (found == 0)
                    System.out.println("Couldn't find production " + title);
            } else if(answer.equals("A")){
                System.out.println("Insert name: ");
                String name = scanner.nextLine();
                for (Actor a : actors)
                    if (a.getName().equals(name)) {
                        System.out.println(a);
                        found = 1;
                        break;
                    }
                ok = 0;
                if (found == 0)
                    System.out.println("Couldn't find actor " + name);
            } else
                System.out.println("Invalid choice");
        }

    }

    public void add_productionORactor_favorites(User user) {
        Scanner scanner = new Scanner(System.in);
        int ok = 1, found = 0;
        while (ok == 1) {
            System.out.println("Production or actor? [P/A]");
            String answer = scanner.nextLine();
            if (answer.equals("P")) {
                System.out.println("Insert production: ");
                String title = scanner.nextLine();
                for (Production p : productions)
                    if (p.getName().equals(title)) {
                        if (p.getClass().equals(Movie.class)) {
                            Movie m = (Movie) p;
                            user.addMovie(m);
                        }
                        if (p.getClass().equals(Series.class)){
                            Series s = (Series) p;
                            user.addSeries(s);
                        }
                        System.out.println("Production " + title + " added to favorites");
                        found = 1;
                        break;
                    }
                ok = 0;
                if (found == 0)
                    System.out.println("Production " + title + " not found");
            } else if (answer.equals("A")) {
                System.out.println("Insert actor: ");
                String name = scanner.nextLine();
                for (Actor a : actors)
                    if (a.getName().equals(name)) {
                        user.addActor(a);
                        System.out.println("Actor " + name + " added to favorites");
                        found = 1;
                        break;
                    }
                ok = 0;
                if (found == 0)
                    System.out.println("Actor " + name + " not found");
            } else
                System.out.println("Invalid choice");
        }
    }

    public void remove_productionORactor_favorites(User user) {
        Scanner scanner = new Scanner(System.in);
        int ok = 1, found = 0;
        while (ok == 1) {
            System.out.println("Production or actor? [P/A]");
            String answer = scanner.nextLine();
            if (answer.equals("P")) {
                System.out.println("Insert production: ");
                String title = scanner.nextLine();
                for (Production p : productions)
                    if (p.name.equals(title) && user.favorites.contains(p)) {
                        if (p.getClass().equals(Movie.class)) {
                            Movie m = (Movie) p;
                            user.removeMovie(m);
                        }
                        if (p.getClass().equals(Series.class)){
                            Series s = (Series) p;
                            user.removeSeries(s);
                        }
                        System.out.println("Production " + title + " removed from favorites");
                        found = 1;
                        break;
                    }
                ok = 0;
                if (found == 0)
                    System.out.println("Production " + title + " not found");
            } else if (answer.equals("A")) {
                System.out.println("Insert actor: ");
                String name = scanner.nextLine();
                for (Actor a : actors)
                    if (a.getName().equals(name) && user.favorites.contains(a)) {
                        user.removeActor(a);
                        System.out.println("Actor " + name + " removed from favorites");
                        found = 1;
                        break;
                    }
                ok = 0;
                if (found == 0)
                    System.out.println("Actor " + name + " not found");
            } else
                System.out.println("Invalid choice");
        }
    }

    public void add_rating(Regular user) {
        Production prod = null;
        int ok = 1, score = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert Production: ");
        String title = scanner.nextLine();
            for (Production p : productions)
                if (p.name.equals(title)) {
                    prod = p;
                    ok = 0;
                    break;
                }
        if (ok == 1)
            System.out.println("Invalid production");
        if (ok == 0)
            user.addReview(prod, users);
    }
    public void delete_rating(Regular user){
        Production prod = null;
        int ok = 1;
        Scanner scanner = new Scanner(System.in);
        while (ok == 1) {
            System.out.println("Insert Production: ");
            String title = scanner.nextLine();
            for (Production p : productions)
                if (p.name.equals(title)) {
                    prod = p;
                    ok = 0;
                    break;
                }
            if (ok == 1)
                System.out.println("Invalid production");
        }
        user.deleteReview(user, prod);
    }
    public void add_user(Admin user){
        User newUser = user.createUser(users);
        if (newUser != null) {
            users.add(newUser);
            for (User u : users)
                System.out.println(u.toString());
        }
    }
    public void add_productionORactor_system(Staff user){
        int ok = 1;
        Production prod = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Add production or actor? [P/A]");
        String answer = scanner.nextLine();
        if (answer.equals("P"))
            user.addProductionSystem(productions);
        if (answer.equals("A"))
            user.addActorSystem(actors);

    }
    public void remove_productionORactor_system(Staff user) {
        Scanner scanner = new Scanner(System.in);
        SortedSet<Common> set = user.getProductionsANDactors();
        System.out.println("Production or actor? [P/A]");
        String answer = scanner.nextLine();
        if (answer.equals("P")) {
            System.out.println("These are the productions you are allowed to delete:");
            for (Common el : set)
                if (el.getClass().equals(Movie.class) || el.getClass().equals(Series.class)) {
                    Production prodEl = (Production) el;
                    System.out.println(prodEl.getName());
                }
            System.out.println("\nInsert title: ");
            String title = scanner.nextLine();
            user.removeProductionSystem(title, productions);
        }
        if (answer.equals("A")) {
            System.out.println("These are the actors you are allowed to delete:");
            for (Common el : set)
                if (el.getClass().equals(Actor.class)){
                    Actor actEl = (Actor) el;
                    System.out.println(actEl.getName());
                }
            System.out.println("\nInsert name:");
            String name = scanner.nextLine();
            user.removeActorSystem(name, actors);
        }
        if (!answer.equals("P") && !answer.equals("A"))
            System.out.println("Invalid choice");
    }
    public void update_productionOractor_system(Staff user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Production or actor? [P/A]");
        String answer = scanner.nextLine();
        SortedSet<Common> set = user.getProductionsANDactors();
        if (answer.equals("P")) {
            System.out.println("These are the productions you are allowed to update:");
            for (Common el : set)
                if (el.getClass().equals(Movie.class) || el.getClass().equals(Series.class)){
                    Production prodEl = (Production) el;
                    System.out.println(prodEl.getName());
                }
            System.out.println("Insert title: ");
            String title = scanner.nextLine();
            user.updateProduction(title, productions);
        }
        if (answer.equals("A")) {
            System.out.println("These are the actors you are allowed to update:");
            for (Common el : set)
                if (el.getClass().equals(Actor.class)) {
                    Actor actEl = (Actor) el;
                    System.out.println(actEl.getName());
                }
            System.out.println("Insert name:");
            String name = scanner.nextLine();
            user.updateActor(name, productions);
        }
        if (!answer.equals("P") && !answer.equals("A"))
            System.out.println("Invalid choice");
    }


    public User regular_user(String username, User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose action: ");
        System.out.println("\t1) View productions details");
        System.out.println("\t2) View actors details");
        System.out.println("\t3) View notifications");
        System.out.println("\t4) Search for movie/actor/film");
        System.out.println("\t5) Add production/actor in favorites");
        System.out.println("\t6) Remove production/actor from favorites");
        System.out.println("\t7) Create a request");
        System.out.println("\t8) Remove a request");
        System.out.println("\t9) Add rating");
        System.out.println("\t10) Delete rating");
        System.out.println("\t11) Logout");
        String action = scanner.nextLine();
        String answer = null;
        switch (action) {
            case "1": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_productions();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "2": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_actors();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "3": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_notifications(username);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "4": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    search_movieORproduction();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "5": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    add_productionORactor_favorites(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "6": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    remove_productionORactor_favorites(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "7":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    ((Regular) user).createRequest(users, productions, actors, requests);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "8":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    ((Regular) user).removeRequest(requests);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "9":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    add_rating((Regular) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "10":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    delete_rating((Regular) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "11":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    user = user.logout(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            default:
                throw new InvalidCommandException("Invalid choice");
        }
        return user;
    }

    public User contributor_user(String username, User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose action: ");
        System.out.println("\t1) View productions details");
        System.out.println("\t2) View actors details");
        System.out.println("\t3) View notifications");
        System.out.println("\t4) Search for movie/actor/film");
        System.out.println("\t5) Add production/actor in favorites");
        System.out.println("\t6) Remove production/actor from favorites");
        System.out.println("\t7) Create a request");
        System.out.println("\t8) Remove a request");
        System.out.println("\t9) Add production/actor");
        System.out.println("\t10) Remove production/actor");
        System.out.println("\t11) Visualize and solve request");
        System.out.println("\t12) Update production/actor");
        System.out.println("\t13) Logout");
        String action = scanner.nextLine();
        String answer = null;
        switch (action) {
            case "1": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_productions();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "2": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_actors();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "3": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_notifications(username);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "4": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    search_movieORproduction();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "5": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    add_productionORactor_favorites(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "6": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    remove_productionORactor_favorites(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "7":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    ((Contributor) user).createRequest(users, productions, actors, requests);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "8":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    ((Contributor) user).removeRequest(requests);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "9":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    add_productionORactor_system((Staff) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "10":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    remove_productionORactor_system((Staff) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "11":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    ((Staff)user).solveRequests((Staff)user, users, requests);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "12":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    update_productionOractor_system((Staff) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "13":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    user = user.logout(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            default:
                throw new InvalidCommandException("Invalid choice");
        }
        return user;
    }

    public User admin_user(String username, User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose action: ");
        System.out.println("\t1) View productions details");
        System.out.println("\t2) View actors details");
        System.out.println("\t3) View notifications");
        System.out.println("\t4) Search for movie/actor/film");
        System.out.println("\t5) Add production/actor in favorites");
        System.out.println("\t6) Remove production/actor from favorites");
        System.out.println("\t7) Add production/actor");
        System.out.println("\t8) Remove production/actor");
        System.out.println("\t9) Visualize and solve request");
        System.out.println("\t10) Update production/actor");
        System.out.println("\t11) Add new user");
        System.out.println("\t12) Delete user");
        System.out.println("\t13) Logout");
        String action = scanner.nextLine();
        String answer = null;
        switch (action){
            case "1": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_productions();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "2": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_actors();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "3": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    view_notifications(username);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "4": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    search_movieORproduction();
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "5": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    add_productionORactor_favorites(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "6": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    remove_productionORactor_favorites(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "7": {
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    add_productionORactor_system((Staff) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "8":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    remove_productionORactor_system((Staff) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "9":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    ((Staff)user).solveRequests((Staff)user, users, requests);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "10":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    update_productionOractor_system((Staff) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "11":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    add_user((Admin) user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "12":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    ((Admin)user).delete_user(users, productions, requests);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            case "13":{
                System.out.println("Are you sure? [Y/N]");
                answer = scanner.nextLine();
                if (answer.equals("Y"))
                    user = user.logout(user);
                else if (!answer.equals("N"))
                    System.out.println("Invalid choice");
                break;
            }
            default:
                throw new InvalidCommandException("Invalid choice");
        }
        return user;
    }

    public void run() throws Exception {
        read_json();
        boolean loggedin = true;
        AccountType type = null;
        while (loggedin) {
            String username = login();
            for (User u : users) {
                if (u.getUsername().equals(username)){
                    type = u.getAccountType();
                    while(u != null) {
                        if (type == REGULAR) {
                            try {
                                u = regular_user(username, u);
                            } catch (InvalidCommandException e){
                                System.out.println(e.getMessage());
                            }
                        }
                        if (type == CONTRIBUTOR) {
                            try {
                            u = contributor_user(username, u);
                            } catch (InvalidCommandException e){
                                System.out.println(e.getMessage());
                            }
                        }
                        if (type == ADMIN) {
                            try {
                            u = admin_user(username, u);
                        } catch (InvalidCommandException e){
                            System.out.println(e.getMessage());
                        }
                        }
                    }
                    break;
                }
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("How would you like to use the app?");
        System.out.println("\t1) Terminal\n\t2) GUI");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            IMDB imdb = IMDB.getInstance();
            imdb.run();
        } else if (choice.equals("2")){
            javax.swing.SwingUtilities.invokeLater(()->{
                IMDBgui gui = new IMDBgui();
                gui.init();
            });
        } else
            System.out.println("Invalid choice");
    }

}
