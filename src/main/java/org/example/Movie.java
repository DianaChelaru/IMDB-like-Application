package org.example;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class Movie extends Production{
    private String movie_duration;
    private int movie_year;

    public Movie(String name, List<String> directors_names, List<String> actors_names, List<Genre> genres, List<Rating> ratings, String subject, Double score, String movie_duration, int movie_year, String type) {
        super(name, directors_names, actors_names, genres, ratings, subject, score, type);
        this.movie_duration = movie_duration;
        this.movie_year = movie_year;
    }

    @Override
    public Double calculateScore() {
        return super.calculateScore();
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
        return 0;
    }

    @Override
    public int compareTo(@NotNull Actor o) {
        return 0;
    }

    @Override
    public String displayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(name).append("\n");
        if (directors_names != null) {
            sb.append("Directors: ");
            for (String d : directors_names) {
                if (directors_names.indexOf(d) + 1 == directors_names.size())
                    sb.append(d).append("\n");
                else
                    sb.append(d).append(", ");
            }
        }
        sb.append("Actors: ");
        if (actors_names != null) {
            for (String a : actors_names) {
                if (actors_names.indexOf(a) + 1 == actors_names.size())
                    sb.append(a).append("\n");
                else
                    sb.append(a).append(", ");
            }
            if (genres != null) {
                sb.append("Genre: ").append(genres).append("\n");
            }
            if (movie_duration != null)
                sb.append("Duration: ").append(movie_duration).append("\n");
            if (movie_year > 0)
                sb.append("Release year: ").append(movie_year).append("\n");
            if (subject != null)
                sb.append("Description: ").append(subject).append("\n");
            if (calculateScore() != 0.0) {
                DecimalFormat d = new DecimalFormat("#.##");
                String s = d.format(calculateScore());
                double score = Double.parseDouble(s);
                sb.append("Score: ").append(score).append("\n");
            }
            if (ratings != null) {
                sb.append("Ratings: ").append("\n");
                for (Rating r : ratings)
                    sb.append("\t").append(r);
            }
            return sb.toString();
        }
        return sb.toString();
    }

    public String getMovie_duration() {
        return movie_duration;
    }

    public void setMovie_duration(String movie_duration) {
        this.movie_duration = movie_duration;
    }

    public int getMovie_year() {
        return movie_year;
    }

    public void setMovie_year(int movie_year) {
        this.movie_year = movie_year;
    }
}
