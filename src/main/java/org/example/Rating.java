package org.example;

import java.util.ArrayList;
import java.util.List;

public class Rating implements Subject, ExperienceStrategy{
    private String username;
    private double score;
    private String comments;
    private static List<Observer> observers = new ArrayList<>();

    public Rating(String username, double score, String comments) {
        this.username = username;
        this.score = score;
        this.comments = comments;
    }

    @Override
    public int calculateExperience(String exp, int addORremove) {
        int experience = 0;
        if (addORremove == 1){
            if (exp != null)
                experience = Integer.parseInt(exp);
            experience += 1;
            System.out.println("+1 experience for adding review");
        } else {
            experience = Integer.parseInt(exp);
            experience -= 1;
            System.out.println("-1 experience for deleting review");
        }
        return experience;
    }

    @Override
    public void addObserver(Observer obs) {
        if (!observers.contains(obs))
            observers.add(obs);
    }

    @Override
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObservers(String notification, String name, String prodName, int ok) {
        for (Observer obs : observers) {
            if (obs instanceof Regular && ((Regular)obs).getRatingsProd().contains(prodName) && !((Regular)obs).getUsername().equals(name) & ok == 1)
                obs.update(notification);
            if (obs instanceof Contributor && ((Contributor)obs).getUsername().equals(name) && ok == 2){
                for (Object p : ((Contributor<?>) obs).getProductionsANDactors())
                    if (p.getClass().equals(Movie.class) || p.getClass().equals(Series.class)) {
                        if (((Production) p).getName().equals(prodName))
                            obs.update(notification);
                    }
            }
            if (obs instanceof Admin && ((Admin)obs).getUsername().equals(name) && ok == 3) {
                for (Object p : ((Admin<?>) obs).getProductionsANDactors())
                    if (p.getClass().equals(Movie.class) || p.getClass().equals(Series.class)) {
                        if (((Production) p).getName().equals(prodName))
                            obs.update(notification);
                    }
            }
            if (obs instanceof Admin && name.equals("ADMIN") && ok == 4)
                obs.update(notification);
        }
    }

    public String getUsername() {
        return username;
    }

    public double getScore() {
        if (score >=1 && score <=10)
            return score;
        return 1;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tUsername: ").append(username).append("\n");
        sb.append("\tScore: ").append(score).append("\n");
        sb.append("\tComment: ").append(comments).append("\n");
        return sb.toString();
    }
}
