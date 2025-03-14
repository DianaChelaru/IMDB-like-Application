package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class IMDBgui {
    private JFrame frame;
    private JTextField emailTextField;
    private JPasswordField passwordTextField;
    private JButton loginButton;

    public void init(){
        frame = new JFrame("IMDB");
        frame.setSize(800, 800);
        JPanel panel = new JPanel();
        frame.add(panel);
        LoginPanel(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    private void LoginPanel(JPanel panel){
        placeComponentsLogin(panel);
        addEventLogin(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void MainAppPanel(JPanel panel, String username) throws Exception {
        placeComponentsMainApp(panel, username);
        frame.revalidate();
        frame.repaint();
    }

    private void placeComponentsLogin(JPanel panel){
        panel.removeAll();
        panel.setLayout(null);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 20, 80, 25);
        panel.add(emailLabel);

        emailTextField = new JTextField(20);
        emailTextField.setBounds(100, 20, 165, 25);
        panel.add(emailTextField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordTextField = new JPasswordField(20);
        passwordTextField.setBounds(100, 50, 165, 25);
        panel.add(passwordTextField);

        loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);
    }

    private void addEventLogin(JPanel panel){
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailTextField.getText();
                String password = new String(passwordTextField.getPassword());
                System.out.println(password);
                try{
                    IMDB imdb = IMDB.getInstance();
                    imdb.read_json();
                    User u = null;
                    int ok = 0, again = 0;
                    String pw = null;
                    for (User user : imdb.getUsers()) {
                        if (user.getInformation().getCredentials().getEmail().equals(email)) {
                            u = user;
                            ok = 1;
                            pw = user.getInformation().getCredentials().getPassword();
                            break;
                        }
                    }
                    if (ok == 1 && password.equals(pw)) {
                        MainAppPanel(panel, u.getUsername());
                        System.out.println("Login succesfull");
                    } else if (ok == 1 && !password.equals(pw)) {
                        JOptionPane.showMessageDialog(null, "Invalid password");
                        System.out.println("Invalid password.");
                    } else if (ok == 0) {
                        JOptionPane.showMessageDialog(null, "Invalid email");
                        System.out.println("Invalid email.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
           }
        });
    }

    private void placeComponentsMainApp(JPanel panel, String username) throws Exception {
        IMDB imdb = IMDB.getInstance();
        imdb.read_json();
        Set<String> addedProductions = new HashSet<>();

        panel.removeAll();
        panel.setLayout(null);

        JScrollPane movieScroll, seriesScroll;
        JPanel buttonPanelM = new JPanel(), buttonPanelS = new JPanel();
        buttonPanelM.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton buttonM, buttonS;
        buttonPanelS.setLayout(new GridLayout(0,1));
        for (Production p : imdb.getProductions()){
            if (p.getClass().equals(Movie.class) && !addedProductions.contains(p.getName())){
                buttonM = createProdButton(p, panel, username);
                buttonPanelM.add(buttonM);
                addedProductions.add(p.getName());
            }else if(p.getClass().equals(Series.class) && !addedProductions.contains(p.getName())){
                buttonS = createProdButton(p, panel, username);
                buttonPanelS.add(buttonS);
                addedProductions.add(p.getName());
            }
        }
        movieScroll = new JScrollPane(buttonPanelM);
        movieScroll.setBounds(60, 410, 1050, 310);
        movieScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(movieScroll);

        seriesScroll = new JScrollPane(buttonPanelS);
        seriesScroll.setBounds(1200, 20, 250, 700);
        seriesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(seriesScroll);

        JButton menuButton = new JButton("Menu");
        menuButton.setBounds(10, 20, 125, 25);
        panel.add(menuButton);

        JButton actorsButton = new JButton("Check out the actors!");
        actorsButton.setBounds(60, 300, 500, 100);
        Font font = new Font("Century Gothic", Font.CENTER_BASELINE, 25);
        actorsButton.setFont(font);
        actorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    placeComponentsActor(panel, username);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        panel.add(actorsButton);

        JTextField serachField = new JTextField(200);
        serachField.setBounds(60, 270, 250, 25);
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(320, 270, 90, 25);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    search(serachField.getText(), panel, username);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        panel.add(serachField);
        panel.add(searchButton);

        JButton genreButton = new JButton("Genre");
        JButton ratingsButton = new JButton("Ratings");

        genreButton.setBounds(20, 150, 90, 25);
        genreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<String> addedProds = new HashSet<>();
                JTextField g = new JTextField();
                Object[] addgenre = {"Enter the genre: ", g};
                int option = JOptionPane.showConfirmDialog(frame, addgenre, "Sorting", JOptionPane.OK_CANCEL_OPTION);
                String genre = g.getText();
                System.out.println(genre);
                int ok = 0;
                if (option == JOptionPane.OK_OPTION){
                    List<Production> productions = new ArrayList<>();
                    for (Production p : imdb.getProductions())
                        if (p.getGenres().contains(genre) && !addedProds.contains(p.getName())){
                            ok ++;
                            addedProds.add(p.getName());
                            productions.add(p);
                        }
                    if (ok == 0)
                        JOptionPane.showMessageDialog(frame, "Invalid genre", "Error", JOptionPane.ERROR_MESSAGE);
                    else
                        updateMainApp(panel, username, menuButton, actorsButton, serachField, searchButton, genreButton,ratingsButton, productions);
                }
            }
        });
        panel.add(genreButton);

        ratingsButton.setBounds(150, 150, 90, 25);
        ratingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<String> addedProds = new HashSet<>();
                List<Production> productions = new ArrayList<>();
                List<Production> prodList = new ArrayList<>(imdb.getProductions());
                Comparator<Production> numOfRatings = Comparator.comparingInt(Production::getNumberOfRatings);
                Collections.sort(prodList, numOfRatings.reversed());
                for (Production p : prodList){
                            addedProds.add(p.getName());
                            productions.add(p);
                        }
                updateMainApp(panel, username, menuButton, actorsButton, serachField, searchButton, genreButton, ratingsButton, productions);
                }
        });
        panel.add(ratingsButton);
    }

    private void updateMainApp(JPanel panel, String username, JButton menuButton, JButton actorsButton, JTextField serachField, JButton searchButton,JButton genreButton,JButton ratingsButton, List<Production> productions){
        Set<String> addedProductions = new HashSet<>();

        panel.removeAll();
        panel.setLayout(null);
        JScrollPane movieScroll, seriesScroll;
        JPanel buttonPanelM = new JPanel(), buttonPanelS = new JPanel();
        buttonPanelM.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton buttonM, buttonS;
        buttonPanelS.setLayout(new GridLayout(0,1));
        for (Production p : productions){
            if (p.getClass().equals(Movie.class) && !addedProductions.contains(p.getName())){
                buttonM = createProdButton(p, panel, username);
                buttonPanelM.add(buttonM);
                addedProductions.add(p.getName());
            }else if(p.getClass().equals(Series.class) && !addedProductions.contains(p.getName())){
                buttonS = createProdButton(p, panel, username);
                buttonPanelS.add(buttonS);
                addedProductions.add(p.getName());
            }
        }
        movieScroll = new JScrollPane(buttonPanelM);
        movieScroll.setBounds(60, 410, 1050, 310);
        movieScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(movieScroll);

        seriesScroll = new JScrollPane(buttonPanelS);
        seriesScroll.setBounds(1200, 20, 250, 700);
        seriesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(seriesScroll);

        panel.add(menuButton);
        panel.add(actorsButton);
        panel.add(serachField);
        panel.add(searchButton);
        panel.add(genreButton);
        panel.add(ratingsButton);

        frame.revalidate();
        frame.repaint();

    }

    private JButton createProdButton(Production p, JPanel panel, String username) {
        JButton button = new JButton();
        JPanel customPanel = new JPanel(null);

        ImageIcon icon;
        if (p.getName().equals("The Lord of the Rings: The Return of the King"))
            icon = new ImageIcon("src/pictures/" + "The Lord of the Rings" + ".jfif");
        else if (p.getName().equals("Mad Max: Fury Road"))
            icon = new ImageIcon("src/pictures/" + "Mad Max" + ".jfif");
        else
            icon = new ImageIcon("src/pictures/" + p.getName() + ".jfif");
        Image image = icon.getImage().getScaledInstance(180, 230, Image.SCALE_DEFAULT);
        ImageIcon scaled = new ImageIcon(image);
        JLabel imgLabel = new JLabel(scaled);
        imgLabel.setBounds(0,0,200,250);
        customPanel.add(imgLabel);

        JLabel title = new JLabel(p.getName());
        title.setBounds(0,240,160,30);
        Font font = new Font("Century Gothic", Font.CENTER_BASELINE, 16);
        title.setFont(font);
        title.setForeground(Color.BLACK);
        customPanel.add(title);

        button.setPreferredSize(new Dimension(240, 280));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showProductionDetails(p, panel, username);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        customPanel.setLayout(null);
        button.add(customPanel);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
//        button.setFont(font);
        return button;
    }
    private void showProductionDetails(Production p, JPanel panel, String username) throws Exception {
        JPanel prodDetails = new JPanel();
        prodDetails.setLayout(new BoxLayout(prodDetails, BoxLayout.Y_AXIS));

        ImageIcon icon;
        if (p.getName().equals("The Lord of the Rings: The Return of the King"))
            icon = new ImageIcon("src/pictures/" + "The Lord of the Rings" + ".jfif");
        else if (p.getName().equals("Mad Max: Fury Road"))
            icon = new ImageIcon("src/pictures/" + "Mad Max" + ".jfif");
        else
            icon = new ImageIcon("src/pictures/" + p.getName() + ".jfif");
        Image image = icon.getImage().getScaledInstance(250, 310, Image.SCALE_DEFAULT);
        ImageIcon scaled = new ImageIcon(image);
        JLabel imgLabel = new JLabel(scaled);
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        prodDetails.add(imgLabel);

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        if (p.getClass().equals(Movie.class))
            details.setText(((Movie)p).displayInfo());
        if (p.getClass().equals(Series.class))
            details.setText(((Series)p).displayInfo());
        Font font = new Font("Century Gothic", Font.PLAIN, 16);
        details.setFont(font);
        prodDetails.add(details);

        JButton backButton = new JButton("Main Page");
        backButton.setBounds(10, 20, 125, 25);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(panel);
                frame.revalidate();
                frame.repaint();
            }
        });
        prodDetails.add(backButton);

        IMDB imdb = IMDB.getInstance();
        imdb.read_json();
        JButton addFavs = new JButton("Add To Favorites");
        User user = null;
        for (User u : imdb.getUsers())
            if (u.getUsername().equals(username))
                user = u;
        User finalUser = user;
        addFavs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("BEFORE: ");
                System.out.println(finalUser.getFavorites());
                if (finalUser.getFavorites().contains(p))
                    JOptionPane.showMessageDialog(frame, "Production already added", "Error", JOptionPane.ERROR_MESSAGE);
                else {
                    finalUser.getFavorites().add(p);
                    JOptionPane.showMessageDialog(frame, "Added to Favorites");
                    System.out.println("\nAFTER: ");
                    System.out.println(finalUser.getFavorites());
                }
            }
        });
        prodDetails.add(addFavs);

        JButton addRating = new JButton("Add rating");
        addRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField score = new JTextField();
                JTextField comment = new JTextField();
                Object[] review = {"\tUsername: ", username, "Score (1-10): ", score, "Comment", comment};
                int option = JOptionPane.showConfirmDialog(frame, review, "Add Rating", JOptionPane.OK_CANCEL_OPTION);
                if (addedReview(p,username)){
                    JOptionPane.showMessageDialog(frame, "Review already added", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (option == JOptionPane.OK_OPTION) {
                    try {
                        double rating = Double.parseDouble(score.getText());
                        if (rating < 1 || rating > 10)
                            JOptionPane.showMessageDialog(frame, "Invalid score", "Error", JOptionPane.ERROR_MESSAGE);
                        else {
                            prodDetails.removeAll();
                            Regular user = null;
                            String com = null;
                            for (User u : imdb.getUsers())
                                if (u.getUsername().equals(username))
                                    user = (Regular) u;
                            com = comment.getText();
                            Rating r = new Rating(username, rating, com);
                            p.getRatings().add(r);
                            user.getRatingsProd().add(p.getName());
                            r.addObserver(user);
                            user.setExperienceStrategy(r);
                            int exp = user.calculateExperience(user.getExperience(), 1);
                            String experience = Integer.toString(exp);
                            user.setExperience(experience);
                            String notif = "New review added for '" + p.getName() + "' by '" + username + "'.";
                            r.notifyObservers(notif, username, p.getName(), 1);
                            int user_erased = 1;
                            for (User u : imdb.getUsers()) {
                                if (u.getClass().equals(Contributor.class)) {
                                    for (Object prod : ((Contributor) u).getProductionsANDactors()) {
                                        if (prod.getClass().equals(Movie.class) || prod.getClass().equals(Series.class)) {
                                            if (((Production) prod).getName().equals(p.getName())) {
                                                user_erased = 0;
                                                r.addObserver(u);
                                                r.notifyObservers(notif, u.getUsername(), p.getName(), 2);
                                            }

                                        }
                                    }
                                }
                                if (u.getClass().equals(Admin.class)) {
                                    for (Object prod : ((Admin) u).getProductionsANDactors()) {
                                        if (prod.getClass().equals(Movie.class) || prod.getClass().equals(Series.class)) {
                                            if (((Production) prod).getName().equals(p.getName())) {
                                                user_erased = 0;
                                                r.addObserver(u);
                                                r.notifyObservers(notif, u.getUsername(), p.getName(), 3);
                                            }

                                        }
                                    }
                                }
                            }
                            if (user_erased == 1) {
                                for (User u : imdb.getUsers())
                                    if (u.getClass().equals(Admin.class)) {
                                        r.addObserver(u);
                                    }
                                r.notifyObservers(notif, "ADMIN", p.getName(), 4);
                            }
                            updateProductionDetails(p, prodDetails, imgLabel,backButton,addFavs,addRating);
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid score. Please insert a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (HeadlessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        prodDetails.add(addRating);

        JScrollPane scrollPane = new JScrollPane(prodDetails);

        frame.setContentPane(scrollPane);

        frame.revalidate();
        frame.repaint();
    }

    private void updateProductionDetails(Production p, JPanel prodDetails, JLabel imgLabel, JButton backButton, JButton addFavs, JButton addRating){
//        JScrollPane scrollPane = new JScrollPane(prodDetails);
        prodDetails.removeAll();
        prodDetails.add(imgLabel);

        JTextArea details = new JTextArea();
        details.setEditable(false);

        if (p.getClass().equals(Movie.class))
            details.setText(((Movie)p).displayInfo());
        if (p.getClass().equals(Series.class))
            details.setText(((Series)p).displayInfo());
        Font font = new Font("Century Gothic", Font.PLAIN, 16);
        details.setFont(font);
        prodDetails.add(details);
        prodDetails.add(backButton);
        prodDetails.add(addFavs);
        prodDetails.add(addRating);

        prodDetails.revalidate();
        prodDetails.repaint();
    }

    private boolean addedReview(Production p, String username) {
        for (Rating r : p.getRatings())
            if (r.getUsername().equals(username)) {
                System.out.println("Review already added");
                return true;
            }
        return false;
    }

    private void placeComponentsActor(JPanel panel, String username) throws Exception {
        IMDB imdb = IMDB.getInstance();
        imdb.read_json();
        List<Actor> actList = new ArrayList<>(imdb.getActors());
        JPanel actorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel forMainButton = new JPanel(new BorderLayout());
        actList.sort(Actor::compareTo);
        JButton actorButton;
        Set<String> addedActors = new HashSet<>();
        for (Actor a : actList){
            if (!addedActors.contains(a.getName())) {
                addedActors.add(a.getName());
                actorButton = createActorButton(a, panel, username);
                actorPanel.add(actorButton);
            }
        }
        JScrollPane actorScroll = new JScrollPane(actorPanel);
        actorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JButton backButton = new JButton("Main Page");
        backButton.setBounds(10, 20, 125, 25);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(panel);
                frame.revalidate();
                frame.repaint();
            }
        });

        forMainButton.add(backButton, BorderLayout.NORTH);
        forMainButton.add(actorScroll, BorderLayout.CENTER);

        frame.setContentPane(forMainButton);
        frame.revalidate();
        frame.repaint();
    }

    private JButton createActorButton(Actor a, JPanel panel, String username) {
        JButton button = new JButton();
        JPanel customPanel = new JPanel();

        ImageIcon icon = new ImageIcon("src/pictures/" + a.getName() + ".jfif");
        Image image = icon.getImage().getScaledInstance(180, 230, Image.SCALE_DEFAULT);
        ImageIcon scaled = new ImageIcon(image);
        JLabel imgLabel = new JLabel(scaled);
        imgLabel.setBounds(0,0,200,250);
        customPanel.add(imgLabel);

        JLabel name = new JLabel(a.getName());
        name.setBounds(0,240,160,30);
        Font font = new Font("Century Gothic", Font.CENTER_BASELINE, 16);
        name.setFont(font);
        name.setForeground(Color.BLACK);
        customPanel.add(name);

        button.setPreferredSize(new Dimension(240, 280));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showActorDetails(a, panel, username);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        button.add(customPanel);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        return button;
    }

    private void showActorDetails(Actor a, JPanel panel, String username) throws Exception {
        JPanel actorDetails = new JPanel();
        actorDetails.setLayout(new BoxLayout(actorDetails, BoxLayout.Y_AXIS));

        ImageIcon icon = new ImageIcon("src/pictures/" + a.getName() + ".jpg");
        Image image = icon.getImage().getScaledInstance(250, 310, Image.SCALE_DEFAULT);
        ImageIcon scaled = new ImageIcon(image);
        JLabel imgLabel = new JLabel(scaled);
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        actorDetails.add(imgLabel);

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        details.setText(a.toString());

        Font font = new Font("Century Gothic", Font.PLAIN, 16);
        details.setFont(font);
        actorDetails.add(details);

        JButton backButton = new JButton("Main Page");
        backButton.setBounds(10, 20, 125, 25);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(panel);
                frame.revalidate();
                frame.repaint();
            }
        });
        actorDetails.add(backButton);

        IMDB imdb = IMDB.getInstance();
        imdb.read_json();
        JButton addFavs = new JButton("Add To Favorites");
        User user = null;
        for (User u : imdb.getUsers())
            if (u.getUsername().equals(username))
                user = u;
        User finalUser = user;
        addFavs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("BEFORE: ");
                System.out.println(finalUser.getFavorites());
                if (finalUser.getFavorites().contains(a))
                    JOptionPane.showMessageDialog(frame, "Actor already added", "Error", JOptionPane.ERROR_MESSAGE);
                else {
                    finalUser.getFavorites().add(a);
                    JOptionPane.showMessageDialog(frame, "Added to Favorites");
                    System.out.println("\nAFTER: ");
                    System.out.println(finalUser.getFavorites());
                }
            }
        });
        actorDetails.add(addFavs);


        JScrollPane scrollPane = new JScrollPane(actorDetails);

        frame.setContentPane(scrollPane);

        frame.revalidate();
        frame.repaint();
    }

    private void search(String input, JPanel panel, String username) throws Exception {
        IMDB imdb = IMDB.getInstance();
        imdb.read_json();
        JButton prodButton;
        JButton actorButton;
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel forMainButton = new JPanel(new BorderLayout());
        Set<String> added = new HashSet<>();
        for (Production p : imdb.getProductions())
            if (p.getName().contains(input) && !added.contains(p.getName())) {
                added.add(p.getName());
                prodButton = createProdButton(p, panel, username);
                searchPanel.add(prodButton);
            }
        for (Actor a : imdb.getActors())
            if (a.getName().contains(input) && !added.contains(a.getName())) {
                added.add(a.getName());
                actorButton = createActorButton(a, panel, username);
                searchPanel.add(actorButton);
            }

        if (!added.isEmpty()) {
            JScrollPane actorScroll = new JScrollPane(searchPanel);
            actorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            JButton backButton = new JButton("Main Page");
            backButton.setBounds(10, 20, 125, 25);
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setContentPane(panel);
                    frame.revalidate();
                    frame.repaint();
                }
            });

            forMainButton.add(backButton, BorderLayout.NORTH);
            forMainButton.add(actorScroll, BorderLayout.CENTER);

            frame.setContentPane(forMainButton);
            frame.revalidate();
            frame.repaint();

        } else {
            String not_found = "Not found";
            JOptionPane.showMessageDialog(frame, not_found,"Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
