package org.example;

import java.util.*;

public interface Subject {
    public void addObserver(Observer obs);
    public void removeObserver(Observer obj);
    public void notifyObservers(String notification, String name, String podName, int ok);
    //public Object getUpdate(Observer obj);
}
