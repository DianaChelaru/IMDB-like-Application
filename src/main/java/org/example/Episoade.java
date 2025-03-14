package org.example;

public class Episoade {
    private String episoade_name;
    private String episoade_duration;

    public Episoade(String episoade_name, String episoade_duration) {
        this.episoade_name = episoade_name;
        this.episoade_duration = episoade_duration;
    }

    public String getEpisoade_name() {
        return episoade_name;
    }

    public void setEpisoade_name(String episoade_name) {
        this.episoade_name = episoade_name;
    }

    public String getEpisoade_duration() {
        return episoade_duration;
    }

    public void setEpisoade_duration(String episoade_duration) {
        this.episoade_duration = episoade_duration;
    }

    @Override
    public String toString() {
        return "Episoade{" +
                "episoade_name='" + episoade_name + '\'' +
                ", episoade_duration='" + episoade_duration + '\'' +
                '}';
    }
}
