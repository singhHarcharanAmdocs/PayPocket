// DataBase tables

// pay bill		    -> bill_id, username, bill_name, bill_cost, due_date
// users			-> Username, password, balance
// admin			-> admin_userName, admin_password
// transactions	    -> transaction id, username, show_id, show_name, show_cost, date-time, countOfSeats, booked seats
// movie_shows		-> show_id, show_name, show_cost, numberOfSeats, Date-time



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package paypocket;

import java.sql.*;
import java.util.Scanner;

/**
 *
 * @author amrit
 */
public class AdminOperations {

    private Connection connect() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "Jatin$123");
    }

    public boolean authenticateAdmin(String username, String password) {
        try ( Connection con = connect()) {
            String sql = "SELECT * FROM admin_table WHERE admin_username = ? AND admin_password = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            return rs.next(); // Return true if credentials match
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println("Driver Not Found: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return false;
    }

    void adminMenu(String username) throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n========== Admin Dashboard ==========");
            System.out.println("1. View All Movie Shows");
            System.out.println("2. View all bookings");
            System.out.println("3. Add a New Movie Show");
            System.out.println("4. Delete a Movie Show");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                String input = sc.nextLine();
                choice = Integer.parseInt(input); 
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number corresponding to the menu options.");
                continue; // Restart the loop if input is invalid
            }

            switch (choice) {
                case 1:
                    viewAllMovieShows();
                    break;
                case 2:
                    viewAllBookings();
                    break;
                case 3:
                    addMovieShow();
                    break;
                case 4:
                    deleteMovieShow();
                    break;
                case 5:
                    System.out.println("Logged out successfully!");
                    PayPocket app = new PayPocket();
                    app.startApp();
                    return; // Exit the method to logout
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void addMovieShow() {
        Scanner sc = new Scanner(System.in);
    
        System.out.println("\nEnter Show Name:");
        String showName = sc.nextLine();
    
        double ticketCost;
        while (true) {
            try {
                System.out.println("Enter Ticket Cost:");
                String input = sc.nextLine().trim();
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Ticket cost cannot be empty!");
                }
                ticketCost = Double.parseDouble(input);
                if (ticketCost <= 0) {
                    throw new IllegalArgumentException("Ticket cost must be a positive number!");
                }
                break; // Exit loop if input is valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value for ticket cost.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    
        int seatsLeft;
        while (true) {
            try {
                System.out.println("Enter Number of Seats:");
                String input = sc.nextLine().trim();
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Number of seats cannot be empty!");
                }
                seatsLeft = Integer.parseInt(input);
                if (seatsLeft <= 0) {
                    throw new IllegalArgumentException("Number of seats must be a positive integer!");
                }
                break; // Exit loop if input is valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value for the number of seats.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    
        System.out.println("Enter City:");
        String city = sc.nextLine();
    
        System.out.println("Enter Movie Time (format: YYYY-MM-DD HH:MM:SS):");
        String time = sc.nextLine();
    
        try (Connection con = connect()) {
            String sql = "INSERT INTO movie_shows (show_name, ticket_cost, seats_left, city, show_time) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, showName);
            pst.setDouble(2, ticketCost);
            pst.setInt(3, seatsLeft);
            pst.setString(4, city);
            pst.setString(5, time); // Make sure the input matches the correct format
    
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Movie show added successfully!");
            } else {
                System.out.println("Failed to add movie show.");
            }
        }catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println("Driver Not Found: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("An error occurred while adding the movie show: " + e.getMessage());
            
        }
    }


    private void deleteMovieShow() {
        Scanner sc = new Scanner(System.in);

        int id;
        while (true) {
            try {
                System.out.println("\nEnter the ID of the Movie Show to delete (numeric only):");
                String input = sc.nextLine().trim(); // Read input and trim whitespace
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Input cannot be empty!");
                }
                id = Integer.parseInt(input); // Try parsing input as an integer
                if (id <= 0) {
                    throw new IllegalArgumentException("Movie Show ID must be a positive number!");
                }
                break; // Exit loop if input is valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        try ( Connection con = connect()) {
            String sql = "DELETE FROM movie_shows WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Movie show deleted successfully!");
            } else {
                System.out.println("No movie show found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println("Driver Not Found: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("An error occurred while deleting the movie show: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewAllMovieShows() {
        try ( Connection con = connect()) {
            String sql = "SELECT * FROM movie_shows";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n========== Movie Shows ==========");
            System.out.printf("%-5s %-50s %-10s %-15s %-20s %-20s\n", "ID", "Show Name", "Cost", "Seats Left", "City" , "show_time");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String showName = rs.getString("show_name");
                double ticketCost = rs.getDouble("ticket_cost");
                int seatsLeft = rs.getInt("seats_left");
                String city = rs.getString("city");
                String time = rs.getString("show_time");

                System.out.printf("%-5d %-50s %-10.2f %-15d %-20s %-20s\n", id, showName, ticketCost, seatsLeft, city, time);
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println("Driver Not Found: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void viewAllBookings() throws Exception {
        try ( Connection con = connect()) {
            // Query to retrieve all transactions
            String getAllBookingsSql = "SELECT * FROM transactions";
            try ( PreparedStatement pst = con.prepareStatement(getAllBookingsSql)) {
                ResultSet rs = pst.executeQuery();

                // Check if any bookings are present
                if (!rs.next()) {
                    System.out.println("No bookings found.");
                    return;
                }

                // Display all bookings
                System.out.println("\nAll Bookings:");

                // Iterate through the result set and print booking details
                do {
                    String username = rs.getString("username");
                    int showId = rs.getInt("show_id");
                    String showName = rs.getString("show_name");
                    double cost = rs.getDouble("cost");
                    String dateTime = rs.getString("date_time");

                    System.out.println("User: " + username);
                    System.out.println("Show ID: " + showId);
                    System.out.println("Show Name: " + showName);
                    System.out.println("Ticket Cost: " + cost);
                    System.out.println("Booking Date & Time: " + dateTime);
                    System.out.println("------------------------------------------");
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println("Driver Not Found: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
