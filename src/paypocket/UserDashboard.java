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
                System.out.println("Pay Bills           :        Press 7");
                

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

                    case 7:
                        payBill(username);
                        break; // Exit the loop after deleting the user

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

public void payBill(String username) {
    Scanner sc = new Scanner(System.in);

    try {
        // Fetch and display bills for the user
        ResultSet rs = DBLoader.executeQuery("SELECT * FROM bills WHERE user_name = '" + username + "'");
        boolean hasBills = false;

        System.out.println("\nYour Bills:");
        while (rs.next()) {
            hasBills = true;
            String billId = rs.getString("id");
            String billCost = rs.getString("bill_cost");
            String billname = rs.getString("bill_name");
            String date = rs.getString("date_time");

            System.out.println("----------------------------");
            System.out.println("Bill ID: " + billId);
             System.out.println("Bill Type: " + billname);
            System.out.println("Bill Cost: " + billCost);
            System.out.println("Due Date: " + date);
        }

        if (!hasBills) {
            System.out.println("No pending bills to pay.");
            return;
        }

        int billIdToPay = -1;
        while (true) {
            try {
                System.out.println("\nEnter the ID of the bill you want to pay:");
                billIdToPay = sc.nextInt();
                sc.nextLine(); // Consume newline
                if (billIdToPay <= 0) {
                    System.out.println("Bill ID must be a positive number. Please try again.");
                } else {
                    break; // Valid input, exit the loop
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a numeric Bill ID.");
                sc.nextLine(); // Clear invalid input
            }
        }

        // Check if the bill exists and fetch its details
        ResultSet billDetails = DBLoader.executeQuery("SELECT * FROM bills WHERE id = " + billIdToPay + " AND user_name = '" + username + "'");
        if (!billDetails.next()) {
            System.out.println("Invalid Bill ID or the bill does not belong to you!");
            return;
        }

        double billCost = billDetails.getDouble("bill_cost");

        // Fetch user's account balance
        ResultSet userRs = DBLoader.executeQuery("SELECT account_balance FROM users WHERE username = '" + username + "'");
        if (!userRs.next()) {
            System.out.println("User not found!");
            return;
        }

        double userBalance = userRs.getDouble("account_balance");

        // Check if the user has sufficient balance
        if (userBalance < billCost) {
            System.out.println("Insufficient balance! Please add funds to your account.");
            return;
        }

        // Deduct the bill cost from the user's balance
        String updateBalanceSql = "UPDATE users SET account_balance = account_balance - ? WHERE username = ?";
        int rowsAffected = DBLoader.executeUpdate(updateBalanceSql, billCost, username);

        if (rowsAffected > 0) {
            // Delete the bill from the bills table
            String deleteBillSql = "DELETE FROM bills WHERE id = ?";
            DBLoader.executeUpdate(deleteBillSql, billIdToPay);

            System.out.println("Bill paid successfully!");
            System.out.println("Amount deducted: " + billCost);
            System.out.println("Remaining balance: " + (userBalance - billCost));
        } else {
            System.out.println("Failed to update user balance. Bill payment unsuccessful.");
        }

    } catch (Exception e) {
        System.out.println("An error occurred while paying the bill: " + e.getMessage());
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

    try (Connection con = connect()) {
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

        int numTickets;
        while (true) {
            try {
                System.out.println("Enter the number of tickets you want to book (numeric only):");
                String input = sc.nextLine().trim(); // Read and trim input
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Input cannot be empty!");
                }
                numTickets = Integer.parseInt(input); // Try parsing input as integer
                if (numTickets <= 0) {
                    throw new IllegalArgumentException("Number of tickets must be a positive integer!");
                }
                if (numTickets > seatsLeft) {
                    throw new IllegalArgumentException("Only " + seatsLeft + " seats are available for this show.");
                }
                if (numTickets * ticketCost > userBalance) {
                    throw new IllegalArgumentException("Insufficient balance! You need at least " 
                        + (numTickets * ticketCost) + " to book " + numTickets + " tickets.");
                }
                break; // Exit loop if input is valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        double totalCost = numTickets * ticketCost;

        // Deduct total ticket cost from user's balance
        String updateUserBalanceSql = "UPDATE users SET account_balance = ? WHERE username = ?";
        PreparedStatement pstUpdateUser = con.prepareStatement(updateUserBalanceSql);
        pstUpdateUser.setDouble(1, userBalance - totalCost);
        pstUpdateUser.setString(2, username);
        pstUpdateUser.executeUpdate();

        // Reduce seats left in movie_shows
        String updateSeatsSql = "UPDATE movie_shows SET seats_left = ? WHERE id = ?";
        PreparedStatement pstUpdateSeats = con.prepareStatement(updateSeatsSql);
        pstUpdateSeats.setInt(1, seatsLeft - numTickets);
        pstUpdateSeats.setInt(2, showId);
        pstUpdateSeats.executeUpdate();

        // Record the transaction
        String transactionSql = "INSERT INTO transactions (username, show_id, show_name, cost, date_time,booked_seats) VALUES (?, ?, ?, ?, ?,?)";
        PreparedStatement pstTransaction = con.prepareStatement(transactionSql);
        pstTransaction.setString(1, username);
        pstTransaction.setInt(2, showId);
        pstTransaction.setString(3, showName);
        pstTransaction.setDouble(4, totalCost);
        pstTransaction.setInt(6, numTickets);
        pstTransaction.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // Correctly bind timestamp

        pstTransaction.executeUpdate();

        System.out.println("Ticket(s) booked successfully for " + showName + "!");
        System.out.println("Number of tickets booked: " + numTickets);
        System.out.println("Total amount deducted: " + totalCost);
        System.out.println("Remaining balance: " + (userBalance - totalCost));

    } catch (Exception e) {
        System.out.println("An error occurred while booking the ticket(s): " + e.getMessage());
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
                System.out.println("\nYour Booking Details");

                // Iterate through the result set and print booking details
                do {
                    int showId = rs.getInt("show_id");
                    int bookedtickets = rs.getInt("booked_seats");
                    String showName = rs.getString("show_name");
                    double cost = rs.getDouble("cost");
                    String dateTime = rs.getString("date_time");

                    System.out.println("Show ID: " + showId);
                    System.out.println("Show Name: " + showName);
                     System.out.println("Booked seats: " + bookedtickets);
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
