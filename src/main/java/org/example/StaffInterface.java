package org.example;

import java.util.List;

interface StaffInterface {
    public void addProductionSystem(List<Production> p);
    public void addActorSystem(List<Actor> a);
    public void removeProductionSystem(String name, List<Production> productions);
    public void removeActorSystem(String name, List<Actor> actors);
    public void updateProduction(String name, List<Production> productions);
    public void updateActor(String name, List<Actor> actors);
    public void solveRequests(Staff user, List<User> users, List<Request> requests);
}
