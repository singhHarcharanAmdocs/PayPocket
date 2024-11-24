/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package paypocket;

import java.sql.ResultSet;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class UserDashboard {

    public void displayMenu(String username) {

        Scanner scan = new Scanner(System.in);
        
        while(true){

        System.out.println("For add money       :        Press 1");
        System.out.println("For Transfer money  :        Press 2");
        System.out.println("For check Balance   :        Press 3");
        System.out.println("Delete user         :        Press 4");
        System.out.println("Logout              :        Press 5");

        String number = scan.nextLine();

        if (number.equals("4")) {
            deleteUser(username);
           PayPocket start = new PayPocket();
           start.startApp();
           
        }
        else if(number.equals("3")){
            viewBalance(username);
            
            displayMenu(username);
        }
        else if (number.equals("1")){
            
            System.out.println("Enter the amount you want to add:");
            int balance = scan.nextInt();
            
            updateBalance(username,balance);
            
            displayMenu(username);
            
            
        }
         else if (number.equals("2")) {
            transferMoney(username);
            displayMenu(username);
        }
        
         else if(number.equals("5")){
                  System.out.println("You Have been Logout! ");
               
                  PayPocket start = new PayPocket();
           start.startApp();
         }

    }
    }

    public void deleteUser(String username) {
        try {
            String sql = "DELETE FROM users WHERE username = ?";
            int rows = DBLoader.executeUpdate(sql, username);
            System.out.println(username + " deleted.");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewBalance(String username) {

        String sql = "select * from users where username ='" + username + "'";

        try {

            ResultSet rs = DBLoader.executeQuery(sql);
            
            while(rs.next()){
                String balance = rs.getString("account_balance");
                
                  System.out.println("Your balance: " + balance);

                System.out.println("----------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
      public void updateBalance(String username, int balance) {
        try {
            
             String getBalanceSql = "SELECT account_balance FROM users WHERE username = '"+username+"'";
        ResultSet rs = DBLoader.executeQuery(getBalanceSql);
        int currentBalance ;

        if (rs.next()) {
            currentBalance = rs.getInt("account_balance");
        } else {
            System.out.println("User not found!");
            return; // Exit the method if the user does not exist
        }
             int updatedBalance = currentBalance + balance;
            
            String sql = "UPDATE users SET account_balance = ? WHERE username = ?";
            int rows = DBLoader.executeUpdate(sql, updatedBalance,username);
            System.out.println("Added Balance: "+ balance );
             System.out.println("------------------------------" );
           
             
             viewBalance(username);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
       public void transferMoney(String username) {
        Scanner scan = new Scanner(System.in);

        try {
            // Get recipient username
            System.out.println("Enter the recipient's username:");
            String recipientUsername = scan.nextLine();

            // Check if the recipient exists
            String checkRecipientSql = "SELECT * FROM users WHERE username = '"+recipientUsername+"'";
            ResultSet rsRecipient = DBLoader.executeQuery(checkRecipientSql);

            if (!rsRecipient.next()) {
                System.out.println("Recipient not found!");
                return;
            }

            // Get sender's current balance
            String checkSenderBalanceSql = "SELECT account_balance FROM users WHERE username = '"+username+"'";
            ResultSet rsSender = DBLoader.executeQuery(checkSenderBalanceSql);
            int senderBalance;

            if (rsSender.next()) {
                senderBalance = rsSender.getInt("account_balance");
            } else {
                System.out.println("Sender not found!");
                return;
            }

            // Enter transfer amount
            System.out.println("Enter the amount to transfer:");
            int transferAmount = scan.nextInt();
            scan.nextLine(); // Consume newline

            if (transferAmount > senderBalance) {
                System.out.println("Insufficient funds! Transfer failed.");
                return;
            }

            // Deduct from sender's balance
            String deductSenderSql = "UPDATE users SET account_balance = ? WHERE username = ?";
            int updatedSenderBalance = senderBalance - transferAmount;
            DBLoader.executeUpdate(deductSenderSql, updatedSenderBalance, username);

            // Add to recipient's balance
            int recipientBalance = rsRecipient.getInt("account_balance");
            int updatedRecipientBalance = recipientBalance + transferAmount;
            String addRecipientSql = "UPDATE users SET account_balance = ? WHERE username = ?";
            DBLoader.executeUpdate(addRecipientSql, updatedRecipientBalance, recipientUsername);

            // Confirm transaction
            System.out.println("Transfer successful!");
            System.out.println("You transferred " + transferAmount + " to " + recipientUsername);
            System.out.println("Your updated balance: " + updatedSenderBalance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
