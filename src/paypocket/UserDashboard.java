package paypocket;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDashboard {

    private Connection connect() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "Jatin$123");
    }

    public void displayMenu(String username) {
        Scanner scan = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("For add money       :        Press 1");
                System.out.println("For Transfer money  :        Press 2");
                System.out.println("For check Balance   :        Press 3");
                System.out.println("Delete user         :        Press 4");

                System.out.println("Logout              :        Press 5");
                System.out.println("Book Tickets        :        Press 6");

                System.out.print("Enter your choice: ");
                String input = scan.nextLine();

                // Validate the input
                int number = Integer.parseInt(input);

                switch (number) {
                    case 1:
//                        System.out.println("Enter the amount you want to add:");
                        int balance = getValidatedAmount(scan);
                        updateBalance(username, balance);
                        break;

                    case 2:
                        selectquery();
                        transferMoney(username);
                        break;

                    case 3:
                        viewBalance(username);
                        break;

                    case 4:
                        deleteUser(username);
                        PayPocket start = new PayPocket();
                        start.startApp();
                        return; // Exit the loop after deleting the user

                    case 5:
                        System.out.println("You have been logged out!");
                        PayPocket startApp = new PayPocket();
                        startApp.startApp();
                        return; // Exit the loop after logout

                    case 6:
                        userMenu(username);
                        return; // Exit the loop after deleting the user

                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 5.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input format! Please enter the correct data type.");
                scan.nextLine(); // Clear invalid input
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private int getValidatedAmount(Scanner scan) {
        int amount;
        while (true) {
            try {
                System.out.print("Enter a valid amount: ");
                amount = Integer.parseInt(scan.nextLine());
                if (amount <= 0) {
                    System.out.println("Amount must be greater than 0. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid numeric amount.");
            }
        }
        return amount;
    }

    public void deleteUser(String username) {
        try {
            String sql = "DELETE FROM users WHERE username = ?";
            int rows = DBLoader.executeUpdate(sql, username);
            System.out.println(username + " deleted.");
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewBalance(String username) {
        String sql = "SELECT * FROM users WHERE username ='" + username + "'";
        try {
            ResultSet rs = DBLoader.executeQuery(sql);
            if (rs.next()) {
                String balance = rs.getString("account_balance");
                System.out.println("Your balance: " + balance);
                System.out.println("----------------------------");
            } else {
                System.out.println("User not found!");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving balance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateBalance(String username, int balance) {
        try {
            String getBalanceSql = "SELECT account_balance FROM users WHERE username = '" + username + "'";
            ResultSet rs = DBLoader.executeQuery(getBalanceSql);
            int currentBalance;

            if (rs.next()) {
                currentBalance = rs.getInt("account_balance");
            } else {
                System.out.println("User not found!");
                return;
            }

            int updatedBalance = currentBalance + balance;
            String sql = "UPDATE users SET account_balance = ? WHERE username = ?";
            DBLoader.executeUpdate(sql, updatedBalance, username);
            System.out.println("Added Balance: " + balance);
            System.out.println("------------------------------");

            viewBalance(username);

        } catch (Exception e) {
            System.out.println("Error updating balance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void transferMoney(String username) {
        Scanner scan = new Scanner(System.in);

        try {
            System.out.println("Enter the recipient's username:");
            String recipientUsername = scan.nextLine();

            // Check if the recipient is the same as the sender
            if (recipientUsername.equals(username)) {
                System.out.println("You cannot transfer money to yourself. Transaction aborted.");
                return;
            }

            String checkRecipientSql = "SELECT * FROM users WHERE username = '" + recipientUsername + "'";
            ResultSet rsRecipient = DBLoader.executeQuery(checkRecipientSql);

            if (!rsRecipient.next()) {
                System.out.println("Recipient not found!");
                return;
            }

            String checkSenderBalanceSql = "SELECT account_balance FROM users WHERE username = '" + username + "'";
            ResultSet rsSender = DBLoader.executeQuery(checkSenderBalanceSql);
            int senderBalance;

            if (rsSender.next()) {
                senderBalance = rsSender.getInt("account_balance");
            } else {
                System.out.println("Sender not found!");
                return;
            }

            System.out.println("Enter the amount to transfer:");
            int transferAmount = getValidatedAmount(scan);

            if (transferAmount > senderBalance) {
                System.out.println("Insufficient funds! Transfer failed.");
                return;
            }

            String deductSenderSql = "UPDATE users SET account_balance = ? WHERE username = ?";
            int updatedSenderBalance = senderBalance - transferAmount;
            DBLoader.executeUpdate(deductSenderSql, updatedSenderBalance, username);

            int recipientBalance = rsRecipient.getInt("account_balance");
            int updatedRecipientBalance = recipientBalance + transferAmount;
            String addRecipientSql = "UPDATE users SET account_balance = ? WHERE username = ?";
            DBLoader.executeUpdate(addRecipientSql, updatedRecipientBalance, recipientUsername);

            System.out.println("Transfer successful!");
            System.out.println("You transferred " + transferAmount + " to " + recipientUsername);
            System.out.println("Your updated balance: " + updatedSenderBalance);

        } catch (Exception e) {
            System.out.println("Error during transfer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void selectquery() {
        try {
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                String userName = rs.getString("username");
                System.out.println("User Name: " + userName);
                System.out.println("----------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewAvailableShows() {
        try ( Connection con = connect()) {
            String sql = "SELECT * FROM movie_shows";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n========== Available Movie Shows ==========");
            System.out.printf("%-5s %-20s %-10s %-15s %-20s %-20s\n", "ID", "Show Name", "Cost", "Seats Left", "City", "show_time");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String showName = rs.getString("show_name");
                double ticketCost = rs.getDouble("ticket_cost");
                int seatsLeft = rs.getInt("seats_left");
                String city = rs.getString("city");
                String time = rs.getString("show_time");

                System.out.printf("%-5d %-20s %-10.2f %-15d %-20s %-20s\n", id, showName, ticketCost, seatsLeft, city, time);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userMenu(String username) throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n========== User Dashboard ==========");
            System.out.println("1. View Available Movie Shows");
            System.out.println("2. Book a Movie Ticket");
            System.out.println("3. Check Balance");
            System.out.println("4. View booked Movie");
            System.out.println("5. Back to Dashboard");
            System.out.print("Enter your choice: ");
            int choice;
            try {
                // Read input and handle non-integer values
                String input = sc.nextLine();
                choice = Integer.parseInt(input); // Try parsing the input as an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number corresponding to the menu options.");
                continue; // Restart the loop if input is invalid
            }

            switch (choice) {
                case 1:
                    viewAvailableShows();
                    break;
                case 2:
                    bookTicket(username);
                    break;

                case 3:
                    viewBalance(username);
                    break;
                case 4:
                    viewBookedTickets(username);
                    break;
                case 5:
                    displayMenu(username);
//                    System.out.println("Logged out successfully!");
                    return; // Exit the method to logout
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void bookTicket(String username) {
        Scanner sc = new Scanner(System.in);

        try ( Connection con = connect()) {
            // View available shows
            viewAvailableShows();

            int showId;
            while (true) {
                try {
                    System.out.println("\nEnter the ID of the show you want to book (numeric only):");
                    String input = sc.nextLine().trim(); // Read and trim input
                    if (input.isEmpty()) {
                        throw new IllegalArgumentException("Input cannot be empty!");
                    }
                    showId = Integer.parseInt(input); // Try parsing input as integer
                    if (showId <= 0) {
                        throw new IllegalArgumentException("Show ID must be a positive number!");
                    }
                    break; // Exit loop if parsing succeeds and input is valid
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a numeric value.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage()); // Handle empty or invalid numeric ranges
                }
            }

            String getShowSql = "SELECT * FROM movie_shows WHERE id = ?";
            PreparedStatement pstShow = con.prepareStatement(getShowSql);
            pstShow.setInt(1, showId);
            ResultSet rsShow = pstShow.executeQuery();

            if (!rsShow.next()) {
                System.out.println("Invalid show ID! Please try again.");
                return;
            }

            String showName = rsShow.getString("show_name");
            double ticketCost = rsShow.getDouble("ticket_cost");
            int seatsLeft = rsShow.getInt("seats_left");

            if (seatsLeft <= 0) {
                System.out.println("No seats left for this show!");
                return;
            }

            // Get user's balance
            String getUserSql = "SELECT account_balance FROM users WHERE username = ?";
            PreparedStatement pstUser = con.prepareStatement(getUserSql);
            pstUser.setString(1, username);
            ResultSet rsUser = pstUser.executeQuery();

            if (!rsUser.next()) {
                System.out.println("User not found!");
                return;
            }

            double userBalance = rsUser.getDouble("account_balance");

            if (userBalance < ticketCost) {
                System.out.println("Insufficient balance! Please add funds.");
                return;
            }

            // Deduct ticket cost from user's balance
            String updateUserBalanceSql = "UPDATE users SET account_balance = ? WHERE username = ?";
            PreparedStatement pstUpdateUser = con.prepareStatement(updateUserBalanceSql);
            pstUpdateUser.setDouble(1, userBalance - ticketCost);
            pstUpdateUser.setString(2, username);
            pstUpdateUser.executeUpdate();

            // Reduce seats left in movie_shows
            String updateSeatsSql = "UPDATE movie_shows SET seats_left = ? WHERE id = ?";
            PreparedStatement pstUpdateSeats = con.prepareStatement(updateSeatsSql);
            pstUpdateSeats.setInt(1, seatsLeft - 1);
            pstUpdateSeats.setInt(2, showId);
            pstUpdateSeats.executeUpdate();

            // Record the transaction
            String transactionSql = "INSERT INTO transactions (username, show_id, show_name, cost, date_time) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstTransaction = con.prepareStatement(transactionSql);
            pstTransaction.setString(1, username);
            pstTransaction.setInt(2, showId);
            pstTransaction.setString(3, showName);
            pstTransaction.setDouble(4, ticketCost);
            pstTransaction.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // Correctly bind timestamp
            pstTransaction.executeUpdate();

            System.out.println("Ticket booked successfully for " + showName + "!");
            System.out.println("Amount deducted: " + ticketCost);
            System.out.println("Remaining balance: " + (userBalance - ticketCost));

        } catch (Exception e) {
            System.out.println("An error occurred while booking the ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkBalance(String username) {
        try ( Connection con = connect()) {
            String sql = "SELECT balance FROM users_table WHERE username = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("Your current balance: " + balance);
            } else {
                System.out.println("User not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewBookedTickets(String username) throws Exception {
        try ( Connection con = connect()) {
            // Query to retrieve all bookings made by the user
            String getBookingsSql = "SELECT * FROM transactions WHERE username = ?";
            try ( PreparedStatement pst = con.prepareStatement(getBookingsSql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();

                // Check if the user has made any bookings
                if (!rs.next()) {
                    System.out.println("No bookings found for this user.");
                    return;
                }

                // Display the bookings
                System.out.println("\nYour booked tickets:");

                // Iterate through the result set and print booking details
                do {
                    int showId = rs.getInt("show_id");
                    String showName = rs.getString("show_name");
                    double cost = rs.getDouble("cost");
                    String dateTime = rs.getString("date_time");

                    System.out.println("Show ID: " + showId);
                    System.out.println("Show Name: " + showName);
                    System.out.println("Ticket Cost: " + cost);
                    System.out.println("Booking Date & Time: " + dateTime);
                    System.out.println("------------------------------------------");
                } while (rs.next());

            }
        } catch (SQLException e) {
            System.out.println("An error occurred while retrieving your bookings: " + e.getMessage());
        }
    }
}
