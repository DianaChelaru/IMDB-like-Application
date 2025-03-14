package org.example;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Actor implements Common, ExperienceStrategy{
    private String name;
    private List<Map.Entry<String, String>> roles;
    private String biography;

    public Actor(String name, List<Map.Entry<String, String>> roles, String biography) {
        this.name = name;
        this.roles = roles;
        this.biography = biography;
    }

    @Override
    public int calculateExperience(String exp, int addORremove) {
        int experience = 0;
        if (addORremove == 1){
            if (exp != null)
                experience = Integer.parseInt(exp);
            experience += 1;
            System.out.println("+1 experience for adding actor");
        } else {
            experience = Integer.parseInt(exp);
            experience -= 1;
            System.out.println("-1 experience for removing actor");
        }
        return experience;
    }

    @Override
    public String getMethod() {
        return name;
    }

    @Override
    public int compareTo(@NotNull Actor o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public int compareTo(@NotNull Common o) {
        return 0;
    }

    @Override
    public int compareTo(@NotNull Production o) {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map.Entry<String, String>> getRoles() {
        return roles;
    }

    public void setRoles(List<Map.Entry<String, String>> roles) {
        this.roles = roles;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        if (roles != null) {
            sb.append("Roles: ");
            for (Map.Entry<String, String> r : roles) {
                if (roles.indexOf(r) + 1 == roles.size())
                    sb.append(r.getKey() + "(" + r.getValue() + ")").append("\n");
                else
                    sb.append(r.getKey() + "(" + r.getValue() + ")").append(", ");
            }
        }
        if (biography != null)
            sb.append("Biography: " + biography).append("\n");
        return sb.toString();
    }

}
