package org.example;

import java.util.List;

interface RequestsManager {
    public void createRequest(List<User> suers, List<Production> productions, List<Actor> actors, List<Request> reqs);
    public void removeRequest(List<Request> reqs);
}
