package org.example;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.example.AccountType.*;

class InformationIncompleteException extends RuntimeException{
    public InformationIncompleteException(String message){
        super(message);
    }
}

public class Admin<T extends Common> extends Staff implements FactoryInterface{
    private List<Request> requests;


    public <T> Admin(String username, User.Information.Builder information, AccountType accountType, String experience, ArrayList notifs, SortedSet<T> favorites, SortedSet productionsANDactors) {
        super(username, accountType, information, experience, notifs, favorites, productionsANDactors);
        this.requests = new ArrayList<>();
    }

    @Override
    public void displayNewUser() {
        System.out.println("New Admin User.");
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
    public String generate_username(String name, List<User> users) {
        String username = "";
        if (name.contains(" ")) {
            String[] str = name.split(" ", 10);
            for (String s : str)
                username = username + s.toLowerCase() + "_";
        } else {
            username = name.toLowerCase();
        }
        String final_name = null;
        Random rand = new Random();
        boolean aux = true;
        while (aux) {
            int number = rand.nextInt(100000);
            final_name = username + number;
            aux = is_it_not_unique(final_name, users);
        }
        return final_name;
    }

    public boolean is_it_not_unique(String final_name, List<User> users) {
        for (User user : users)
            if (user.getUsername().equals(final_name))
                return true;
        return false;
    }

    public String generate_password(){
        Random rand = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String ALPHABET = alphabet.toUpperCase();
        String numbers = "0123456789";
        String special = "!@#$%&*-_+=?~";
        String all = alphabet + ALPHABET + numbers + special;
        StringBuilder sb = new StringBuilder();
        int ct = 2, ind;
        char ch;
        while (ct != 0) {
            ind = rand.nextInt(alphabet.length());
            ch = alphabet.charAt(ind);
            sb.append(ch);
            ind = rand.nextInt(ALPHABET.length());
            ch = ALPHABET.charAt(ind);
            sb.append(ch);
            ind = rand.nextInt(numbers.length());
            ch = numbers.charAt(ind);
            sb.append(ch);
            ind = rand.nextInt(special.length());
            ch = special.charAt(ind);
            sb.append(ch);
            ind = rand.nextInt(all.length());
            ch = all.charAt(ind);
            sb.append(ch);
            ct--;
        }
        return sb.toString();
    }
    public User createUser(List<User> users){
        Scanner scanner = new Scanner(System.in);
        int ok = 1;
        System.out.println("Insert data for new user: ");

        System.out.println("Name: ");
        String name = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        AccountType accountType = null;
        while (ok == 1) {
            System.out.println("Account type (R/C/A): ");
            String accty = scanner.nextLine();
            switch (accty) {
                case "R":
                    accountType = REGULAR;
                    ok = 0;
                    break;
                case "C":
                    accountType = CONTRIBUTOR;
                    ok = 0;
                    break;
                case "A":
                    accountType = ADMIN;
                    ok = 0;
                    break;
                default:
                    System.out.println("Invalid account type");
            }
        }
        ok = 1;
        System.out.println("Country: ");
        String country = scanner.nextLine();
        int age = 0;
        LocalDate birthDate;
        try {
            System.out.println("Birth date(yyyy-MM-dd): ");
            String birth_date = scanner.nextLine();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            birthDate = LocalDate.parse(birth_date, dtf);
            LocalDate currentDate = LocalDate.now();
            age = Period.between(birthDate, currentDate).getYears();
            System.out.println("Age: " + age);
        } catch (DateTimeParseException e){
            System.out.println("Invalid date format");
            return null;
        }
        String gender = null;
        while (ok == 1) {
            System.out.println("Gender(M, F, N): ");
            gender = scanner.nextLine();
            if (gender.equals("M") || gender.equals("F") || gender.equals("N"))
                ok = 0;
            else
                System.out.println("Invalid gender");
        }

        String username = generate_username(name, users);
        String password = generate_password();
        Credentials credentials = new Credentials(email, password);

        try {
            if (credentials.getEmail().isEmpty() || name.isEmpty())
                throw new InformationIncompleteException("Credentials must be completed. Name or e-mail null. User not created");
            else {
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                User.Information.Builder information = new User.Information.Builder(credentials, name, country, age, gender, birthDate);


                UserFactory userFactory = new UserFactory();
                FactoryInterface user = userFactory.createUser(accountType, username, information, credentials, null, new ArrayList<>(), new TreeSet<>(), new TreeSet<>());

                user.displayNewUser();
                return (User) user;
            }
        } catch (InformationIncompleteException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void addProductionsANDactors(Staff erased, List<User> users) {
        SortedSet<T> set = erased.getProductionsANDactors();
        for (T el : set)
            for (User adm : users)
                if (adm.getClass().equals(Admin.class))
                    ((Staff)adm).getProductionsANDactors().add(el);
    }

    public void delete_user (List<User> users, List<Production> productions, List<Request> requests) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the username: ");
        String username = scanner.nextLine();
        int ok = 1;
        for (User u : users)
            if (u.getUsername().equals(username)) {
                if (!u.getUsername().equals(this.getUsername())) {
                    addProductionsANDactors((Staff) u, users);
                    users.remove(u);
                    ok = 0;
                    break;
                } else {
                    ok = 2;
                    System.out.println("You cannot delete yourself!");
                }
            }
        if (ok == 1)
            System.out.println("User not found");
        else if (ok == 0){
            for (Production p : productions) {
                Iterator<Rating> iterator = p.ratings.iterator();
                while (iterator.hasNext()){
                    Rating r = iterator.next();
                    if (r.getUsername().equals(username))
                        iterator.remove();
                }
            }
            Iterator<Request> reqIterator = requests.iterator();
            while (reqIterator.hasNext()){
                Request r = reqIterator.next();
                if (r.getUsername_problem().equals(username))
                    reqIterator.remove();
                if (r.getUsername_fix().equals(username)){
                    r.setUsername_fix("ADMIN");
                    RequestHolder.addRequest(r);
                    for (User u : users)
                        if (u.getAccountType().equals(ADMIN))
                            r.addObserver(u);
                    String notif = "New request for ADMIN (contributor erased) " + r.getProblem_desciption();
                    r.notifyObservers(notif,"ADMIN",null, 3);
                    reqIterator.remove();
                }
            }
            Iterator<Request> reqHolderIterator = RequestHolder.getRequests().iterator();
            while (reqHolderIterator.hasNext()) {
                Request r = reqHolderIterator.next();
                if (r.getUsername_problem().equals(username))
                    reqHolderIterator.remove();
            }
            System.out.println("User deleted");
        }
    }
}
