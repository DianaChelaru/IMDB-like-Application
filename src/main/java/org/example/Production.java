package org.example;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Production implements Common, ExperienceStrategy {
    protected String name;
    protected List<String> directors_names;
    protected List<String> actors_names;
    protected List<Genre> genres;
    protected List<Rating> ratings;
    protected String subject;
    protected Double score;
    protected String type;

    public Production(String name, List<String> directors_names, List<String> actors_names, List<Genre> genres, List<Rating> ratings, String subject, Double score, String type) {
        this.name = name;
        this.directors_names = directors_names;
        this.actors_names = actors_names;
        this.genres = genres;
        this.ratings = ratings;
        this.subject = subject;
        this.score = score;
        this.type = type;
    }

    @Override
    public int calculateExperience(String exp, int addORremove) {
        int experience = 0;
        if (addORremove == 1){
            if (exp != null)
                experience = Integer.parseInt(exp);
            experience += 1;
            System.out.println("+1 experience for adding production");
        } else {
            experience = Integer.parseInt(exp);
            experience -= 1;
            System.out.println("-1 experience for removing production");
        }
        return experience;
    }

    public Double calculateScore() {
        double sum = 0.0, score = 0.0;
        for (Rating r : this.ratings)
            sum += r.getScore();
        if (this.ratings.size() > 0 )
            score = sum/this.ratings.size();
        return score;
    }

    @Override
    public String getMethod() {
        return name;
    }

    @Override
    public int compareTo(@NotNull Common o) {
        return 0;
    }

    @Override
    public int compareTo(@NotNull Production o) {
        return Integer.compare(this.getRatings().size(), o.getRatings().size());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDirectors_names() {
        return directors_names;
    }

    public void setDirectors_names(List<String> directors_names) {
        this.directors_names = directors_names;
    }

    public List<String> getActors_names() {
        return actors_names;
    }

    public void setActors_names(List<String> actors_names) {
        this.actors_names = actors_names;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public abstract String displayInfo();

    public int getNumberOfRatings(){
        return this.ratings.size();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(name).append("\n");
        sb.append("Directors: ");
        for (String d : directors_names){
            if (directors_names.indexOf(d) + 1 == directors_names.size())
                sb.append(d).append("\n");
            else
                sb.append(d).append(", ");
        }
        sb.append("Actors: ");
        for (String a : actors_names){
            if (actors_names.indexOf(a) + 1 == actors_names.size())
                sb.append(a).append("\n");
            else
                sb.append(a).append(", ");
        }
        sb.append("Genre: ").append(genres).append("\n");
        sb.append("Description: ").append(subject).append("\n");
        sb.append("Score: ").append(calculateScore()).append("\n");
        sb.append("Ratings: ").append("\n");
        for (Rating r : ratings)
            sb.append("\t").append(r);
        return sb.toString();
    }
}
