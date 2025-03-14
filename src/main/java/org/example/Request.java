package org.example;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Request implements Subject, ExperienceStrategy {
    private RequestTypes requestType;
    private LocalDateTime creationDate;
    private String titleORactor;
    private String problem_desciption;
    private String username_problem;
    private String username_fix;
    private List<Observer> observers;


    public Request(RequestTypes requestType, LocalDateTime creationDate, String titleORactor, String problem_desciption, String username_problem, String username_fix) {
        this.requestType = requestType;
        this.creationDate = creationDate;
        this.titleORactor = titleORactor;
        this.problem_desciption = problem_desciption;
        this.username_problem = username_problem;
        this.username_fix = username_fix;
        this.observers = new ArrayList<>();
    }

    public List<Observer> getObservers() {
        return observers;
    }

    @Override
    public int calculateExperience(String exp, int addORremove) {
        int experience = 0;
        if (addORremove == 1){
            if (exp != null)
                experience = Integer.parseInt(exp);
            experience += 1;
            System.out.println("+1 experience for solved request");
        } else {
            experience = Integer.parseInt(exp);
            experience -= 1;
            System.out.println("-1 experience for solved request");
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
    public void notifyObservers(String notification, String username, String prodName, int ok) {
        for (Observer obs : observers) {
            if (obs instanceof Regular && ((Regular)obs).getUsername().equals(username) && ok == 1)
                obs.update(notification);
            if (obs instanceof Contributor && ((Contributor)obs).getUsername().equals(username) && (ok== 1 || ok == 2))
                obs.update(notification);
            if (obs instanceof Admin && ((Admin)obs).getUsername().equals(username) && (ok == 1 || ok == 2))
                obs.update(notification);
            if (obs instanceof Admin && username.equals("ADMIN") && ok == 3)
                obs.update(notification);
        }
    }


    public RequestTypes getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTypes requestType) {
        this.requestType = requestType;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitleORactor() {
        return titleORactor;
    }

    public void setTitleORactor(String titleORactor) {
        this.titleORactor = titleORactor;
    }

    public String getProblem_desciption() {
        return problem_desciption;
    }

    public void setProblem_desciption(String problem_desciption) {
        this.problem_desciption = problem_desciption;
    }

    public String getUsername_problem() {
        return username_problem;
    }

    public void setUsername_problem(String username_problem) {
        this.username_problem = username_problem;
    }

    public String getUsername_fix() {
        return username_fix;
    }

    public void setUsername_fix(String username_fix) {
        this.username_fix = username_fix;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Request type: ").append(requestType).append("\n");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = creationDate.format(dtf);
        sb.append("Date: ").append(formattedDate).append("\n");
        if (titleORactor != null)
            sb.append("TitleOrActor: ").append(titleORactor).append("\n");
        sb.append("Description: ").append(problem_desciption).append("\n");
        sb.append("Username: ").append(username_problem).append("\n");
        sb.append("To: ").append(username_fix).append("\n");
        return sb.toString();
    }
}
