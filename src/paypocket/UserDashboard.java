///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package paypocket;
//
//import java.sql.ResultSet;
//import java.util.Scanner;
//
///**
// *
// * @author user
// */
//public class UserDashboard {
//
//    public void displayMenu(String username) {
//
//        Scanner scan = new Scanner(System.in);
//        
//        while(true){
//
//        System.out.println("For add money       :        Press 1");
//        System.out.println("For Transfer money  :        Press 2");
//        System.out.println("For check Balance   :        Press 3");
//        System.out.println("Delete user         :        Press 4");
//        System.out.println("Logout              :        Press 5");
//
//        String number = scan.nextLine();
//
//        if (number.equals("4")) {
//            deleteUser(username);
//           PayPocket start = new PayPocket();
//           start.startApp();
//           
//        }
//        else if(number.equals("3")){
//            viewBalance(username);
//            
//            displayMenu(username);
//        }
//        else if (number.equals("1")){
//            
//            System.out.println("Enter the amount you want to add:");
//            int balance = scan.nextInt();
//            
//            updateBalance(username,balance);
//            
//            displayMenu(username);
//            
//            
//        }
//         else if (number.equals("2")) {
//             selectquery();
//            transferMoney(username);
//            displayMenu(username);
//        }
//        
//         else if(number.equals("5")){
//                  System.out.println("You Have been Logout! ");
//               
//                  PayPocket start = new PayPocket();
//           start.startApp();
//         }
//
//    }
//    }
//
//    public void deleteUser(String username) {
//        try {
//            String sql = "DELETE FROM users WHERE username = ?";
//            int rows = DBLoader.executeUpdate(sql, username);
//            System.out.println(username + " deleted.");
//            
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void viewBalance(String username) {
//
//        String sql = "select * from users where username ='" + username + "'";
//
//        try {
//
//            ResultSet rs = DBLoader.executeQuery(sql);
//            
//            while(rs.next()){
//                String balance = rs.getString("account_balance");
//                
//                  System.out.println("Your balance: " + balance);
//
//                System.out.println("----------------------------");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//      public void updateBalance(String username, int balance) {
//        try {
//            
//             String getBalanceSql = "SELECT account_balance FROM users WHERE username = '"+username+"'";
//        ResultSet rs = DBLoader.executeQuery(getBalanceSql);
//        int currentBalance ;
//
//        if (rs.next()) {
//            currentBalance = rs.getInt("account_balance");
//        } else {
//            System.out.println("User not found!");
//            return; // Exit the method if the user does not exist
//        }
//             int updatedBalance = currentBalance + balance;
//            
//            String sql = "UPDATE users SET account_balance = ? WHERE username = ?";
//            int rows = DBLoader.executeUpdate(sql, updatedBalance,username);
//            System.out.println("Added Balance: "+ balance );
//             System.out.println("------------------------------" );
//           
//             
//             viewBalance(username);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//       public void transferMoney(String username) {
//        Scanner scan = new Scanner(System.in);
//
//        try {
//            // Get recipient username
//            System.out.println("Enter the recipient's username:");
//            String recipientUsername = scan.nextLine();
//
//            // Check if the recipient exists
//            String checkRecipientSql = "SELECT * FROM users WHERE username = '"+recipientUsername+"'";
//            ResultSet rsRecipient = DBLoader.executeQuery(checkRecipientSql);
//
//            if (!rsRecipient.next()) {
//                System.out.println("Recipient not found!");
//                return;
//            }
//
//            // Get sender's current balance
//            String checkSenderBalanceSql = "SELECT account_balance FROM users WHERE username = '"+username+"'";
//            ResultSet rsSender = DBLoader.executeQuery(checkSenderBalanceSql);
//            int senderBalance;
//
//            if (rsSender.next()) {
//                senderBalance = rsSender.getInt("account_balance");
//            } else {
//                System.out.println("Sender not found!");
//                return;
//            }
//
//            // Enter transfer amount
//            System.out.println("Enter the amount to transfer:");
//            int transferAmount = scan.nextInt();
//            scan.nextLine(); // Consume newline
//
//            if (transferAmount > senderBalance) {
//                System.out.println("Insufficient funds! Transfer failed.");
//                return;
//            }
//
//            // Deduct from sender's balance
//            String deductSenderSql = "UPDATE users SET account_balance = ? WHERE username = ?";
//            int updatedSenderBalance = senderBalance - transferAmount;
//            DBLoader.executeUpdate(deductSenderSql, updatedSenderBalance, username);
//
//            // Add to recipient's balance
//            int recipientBalance = rsRecipient.getInt("account_balance");
//            int updatedRecipientBalance = recipientBalance + transferAmount;
//            String addRecipientSql = "UPDATE users SET account_balance = ? WHERE username = ?";
//            DBLoader.executeUpdate(addRecipientSql, updatedRecipientBalance, recipientUsername);
//
//            // Confirm transaction
//            System.out.println("Transfer successful!");
//            System.out.println("You transferred " + transferAmount + " to " + recipientUsername);
//            System.out.println("Your updated balance: " + updatedSenderBalance);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//        public void selectquery() {
//        try {
//            ResultSet rs = DBLoader.executeQuery("select * from users");
//
//            while (rs.next()) {
//
//                String userName = rs.getString("username");
//                
//
//                // Print the retrieved data
//                System.out.println("User Name: " + userName);
//              
//
//                System.out.println("----------------------------");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//}


package paypocket;

import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserDashboard {

    public void displayMenu(String username) {
        Scanner scan = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("For add money       :        Press 1");
                System.out.println("For Transfer money  :        Press 2");
                System.out.println("For check Balance   :        Press 3");
                System.out.println("Delete user         :        Press 4");
                System.out.println("Logout              :        Press 5");

                System.out.print("Enter your choice: ");
                String input = scan.nextLine();

                // Validate the input
                int number = Integer.parseInt(input);

                switch (number) {
                    case 1:
                        System.out.println("Enter the amount you want to add:");
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
}