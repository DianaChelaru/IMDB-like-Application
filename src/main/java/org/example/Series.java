package org.example;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Series extends Production {
    private int series_year;
    private int number_of_seasons;
    private Map<String, List<Episoade>> dictorinary_series;

    public Series(String name, List<String> directors_names, List<String> actors_names, List<Genre> genres, List<Rating> ratings, String subject, Double score, int series_year, int number_of_seasons, Map<String, List<Episoade>> dictorinary_series, String type) {
        super(name, directors_names, actors_names, genres, ratings, subject, score, type);
        this.series_year = series_year;
        this.number_of_seasons = number_of_seasons;
        this.dictorinary_series = dictorinary_series;
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
        sb.append("Series: ");
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
            if (subject != null)
                sb.append("Description: ").append(subject).append("\n");
            if (series_year > 0)
                sb.append("Release year: ").append(series_year).append("\n");
            if (number_of_seasons > 0)
                sb.append("Number of seasons: ").append(number_of_seasons).append("\n");
            if (calculateScore() != 0.0) {
                DecimalFormat d = new DecimalFormat("#.##");
                String s = d.format(calculateScore());
                double score = Double.parseDouble(s);
                sb.append("Score: ").append(score).append("\n");
            }
            if (dictorinary_series != null) {
                Map<String, List<Episoade>> sorted = new LinkedHashMap<>(dictorinary_series);
                for (Map.Entry<String, List<Episoade>> season : sorted.entrySet()) {
                    String key = season.getKey();
                    sb.append("Season name: ").append(key).append("\n\tEpisodes:\n");
                    for (Episoade episoade : season.getValue()) {
                        String ep_name = episoade.getEpisoade_name();
                        String ep_duration = episoade.getEpisoade_duration();
                        sb.append("\t\tEp name: ").append(ep_name).append("\n\t\t\tDuration: ").append(ep_duration).append("\n");
                    }
                }
            }
            if (ratings != null) {
                sb.append("Ratings: ").append("\n");
                for (Rating r : ratings)
                    sb.append("\t").append(r);
            }
        }
        return sb.toString();
    }

    public int getSeries_year() {
        return series_year;
    }

    public void setSeries_year(int series_year) {
        this.series_year = series_year;
    }

    public int getNumber_of_seasons() {
        return number_of_seasons;
    }

    public void setNumber_of_seasons(int number_of_seasons) {
        this.number_of_seasons = number_of_seasons;
    }

    public Map<String, List<Episoade>> getDictorinary_series() {
        return dictorinary_series;
    }

    public void setDictorinary_series(Map<String, List<Episoade>> dictorinary_series) {
        this.dictorinary_series = dictorinary_series;
    }
}
