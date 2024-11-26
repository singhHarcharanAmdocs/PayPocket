package paypocket;

import java.sql.ResultSet;
import java.util.Scanner;

public class CreateUser {

    public void addUser(String userName, String password) {
        Scanner scan = new Scanner(System.in);

        try {
            // Validate password
            while (!isPasswordValid(password)) {
                System.out.println("Invalid password! Password must contain:");
                System.out.println("- At least 8 characters");
                System.out.println("- At least one uppercase letter");
                System.out.println("- At least one numeric digit");
                System.out.println("- At least one special character");

                System.out.println("Please enter a valid password:");
                password = scan.nextLine();
            }

            // Insert user into the database
            String sql = "INSERT INTO users (username, password, account_balance) VALUES (?, ?, ?)";
            int rows = DBLoader.executeUpdate(sql, userName, password, 0);

            System.out.println(rows + " user created.");
            System.out.println("Welcome user!");

            System.out.println("Enter Username:");
            String name = scan.nextLine();

            System.out.println("Enter password:");
            String pass = scan.nextLine();

            checkUser(name, pass);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPasswordValid(String password) {

        boolean specialChar = false;
        boolean digit = false;
        boolean capitalLetter = false;
        boolean smallLetter = false;

        //Checks each character to see if it is acceptable.
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (c >= 'a' && c <= 'z') {
                smallLetter = true;
            } else if (c >= 'A' && c <= 'Z') {
                capitalLetter = true;
            } else if (c >= '0' && c <= '9') {
                digit = true;
            } else {
                specialChar = true;
            }
        }

        if (!specialChar || !digit || !smallLetter || !capitalLetter || password.length() < 8) {
            return false;
        } else {
            // if the password is valid, tell the user it's accepted
            System.out.println("Password Accepted");
            return true;
        }
    }



    public void checkUser(String userName, String password) {
        int maxRetries = 3; // Maximum retry attempts allowed
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResultSet rs = DBLoader.executeQuery("select * from users where username='" + userName + "' and password='" + password + "'");

                if (rs.next()) {
                    System.out.println("Login Successful. Welcome " + userName);
                    System.out.println();

                    UserDashboard userdashboard = new UserDashboard();
                    userdashboard.displayMenu(userName);
                    return; // Exit the method once login is successful
                } else {
                    if (attempt < maxRetries) {
                        System.out.println("Wrong Credientials Login failed. You have " + (maxRetries - attempt) + " attempts left.");
                        // Assuming you have a way to get updated credentials for retrying
                        System.out.println("Please enter your username: ");
                        userName = new java.util.Scanner(System.in).nextLine();
                        System.out.println("Please enter your password: ");
                        password = new java.util.Scanner(System.in).nextLine();
                    } else {
                        System.out.println("Login failed. Maximum attempts reached. Please sign up or contact support.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void selectquery() {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from users");

            while (rs.next()) {

                String userName = rs.getString("username");
                String password = rs.getString("password");
                String balance = rs.getString("account_balance");

                // Print the retrieved data
                System.out.println("User Name: " + userName);
                System.out.println("Password: " + password);
                System.out.println("balance: " + balance);

                System.out.println("----------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
