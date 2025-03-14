package org.example;

import java.util.*;

public final class RequestHolder {
    private static ArrayList<Request> requests = new ArrayList<>();;

    public static ArrayList<Request> getRequests() {
        return requests;
    }
    public static void addRequest(Request request) {
        requests.add(request);
    }
    public static void removeRequest(Request request) {
        requests.remove(request);
    }
}
