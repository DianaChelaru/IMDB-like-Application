package org.example;

import java.util.ArrayList;
import java.util.TreeSet;

public class UserFactory<T extends Common> {
    public FactoryInterface createUser(AccountType type, String username, User.Information.Builder information, Credentials credentials, String experience, ArrayList notifs, TreeSet favorites, TreeSet productionsANDactors) {
        if (type == null)
            return null;
        switch (type) {
            case REGULAR:
                return new Regular<T>(username, information, AccountType.REGULAR, experience, notifs, favorites);
            case CONTRIBUTOR:
                return new Contributor<T>(username, information, AccountType.CONTRIBUTOR, experience, notifs, favorites, productionsANDactors);
            case ADMIN:
                return new Admin<T>(username, information, AccountType.ADMIN, experience, notifs, favorites, productionsANDactors);
            default:
                return null;
        }
    }
}
