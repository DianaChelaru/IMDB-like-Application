package org.example;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;

public abstract class User<T extends Common> implements Comparable<User<T>>, Observer, ExperienceStrategy{
    protected Information information;
    private AccountType accountType;
    private String username;
    private String experience;
    private List<String> notifs;
    protected SortedSet<T> favorites;
    private ExperienceStrategy experienceStrategy;
    //private boolean reviewed;


    public User(String username, AccountType accountType, Information.Builder information, String experience, ArrayList<String> notifs, SortedSet<T> favorites) {
        this.username = username;
        this.information = information.build();
        this.accountType = accountType;
        this.experience = experience;
        this.notifs = notifs;
        this.favorites = favorites;
    }

    public void setExperienceStrategy(ExperienceStrategy experienceStrategy) {
        this.experienceStrategy = experienceStrategy;
    }

    @Override
    public int calculateExperience(String exp, int addORremove) {
        return experienceStrategy.calculateExperience(this.getExperience(), addORremove);
    }

    @Override
    public void update(String notification) {
        System.out.println("User " + username + " received notifications:\n" + notification);
        this.notifs.add(notification);
    }


    public User logout(User user){
        int ok = 1;
        Scanner scanner = new Scanner(System.in);
        while (ok == 1) {
            System.out.println("1) Authentificate again\n2) Close app");
            String answer = scanner.nextLine();
            if (answer.equals("1")){
                user = null;;
                ok = 0;
            }
            else if (answer.equals("2"))
                System.exit(0);
            else
                System.out.println("Invalid choice");
        }
        return user;
    }


    @Override
    public int compareTo(@NotNull User<T> o) {
        return this.username.compareTo(o.username);
    }

    public void addMovie(Movie movie) {
        favorites.add((T) movie);
    }
    public void addSeries(Series series) {
        favorites.add((T)series);
    }
    public void addActor(Actor actor) {
        favorites.add((T)actor);
    }
    public void removeMovie(Movie movie) {
        favorites.remove(movie);
    }
    public void removeSeries(Series series) {
        favorites.remove((T)series);
    }
    public void removeActor(Actor actor) {
        favorites.remove(actor);
    }
    public Information getInformation() {
        return information;
    }

    public void setInformation(Information.Builder information) {
        this.information = information.build();
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public List<String> getNotifs() {
        return notifs;
    }

    public void setNotifs(List<String> notifs) {
        this.notifs = notifs;
    }

    public SortedSet<T> getFavorites() {
        return favorites;
    }

    public void setFavorites(SortedSet<T> favorites) {
        this.favorites = favorites;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(information).append("\n");
        sb.append("Account type: ").append(accountType).append("\n");
        sb.append("Username: ").append(username).append("\n");
        sb.append("Experience: ").append(experience).append("\n");
        sb.append("Notifications: ").append(notifs).append("\n");
        sb.append("Favorites: ").append(favorites).append("\n");
        return sb.toString();
    }

    public static class Information {
        private Credentials credentials;
        private String name;
        private String country;
        private int age;
        private String gender;
        private LocalDate birth_date_to_format;
        private String birth_date;

        public Information(Builder builder) {
            this.credentials = builder.credentials;
            this.name = builder.name;
            this.country = builder.country;
            this.age = builder.age;
            this.gender = builder.gender;
            this.birth_date_to_format = builder.birth_date_to_format;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.birth_date = this.birth_date_to_format.format(dtf);
        }

        public Credentials getCredentials() {
            return credentials;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public int getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public String getBirth_date() {
            return birth_date;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(credentials).append("\n");
            sb.append("Name: ").append(name).append("\n");
            sb.append("Country: ").append(country).append("\n");
            sb.append("Age: ").append(age).append("\n");
            sb.append("Gender: ").append(gender).append("\n");
            sb.append("Birth date: ").append(birth_date);
            return sb.toString();
        }

        public static class Builder {
            private Credentials credentials;
            private String name;
            private String country;
            private int age;
            private String gender;
            private LocalDate birth_date_to_format;

            public Builder(Credentials credentials, String name, String country, int age, String gender, LocalDate birth_date) {
                this.credentials = credentials;
                this.name = name;
                this.country = country;
                this.age = age;
                this.gender = gender;
                this.birth_date_to_format = birth_date;
            }
            public Builder credentials(Credentials credentials) {
                this.credentials = credentials;
                return this;
            }
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder country(String country) {
                this.country = country;
                return this;
            }
            public Builder age(int age) {
                this.age = age;
                return this;
            }
            public Builder gender(String gender) {
                this.gender = gender;
                return this;
            }
            public Builder birth_date(LocalDate birth_date_to_format) {
                this.birth_date_to_format = birth_date_to_format;
                return this;
            }
            public Information build(){
                return new Information(this);
            }
        }
    }
}
