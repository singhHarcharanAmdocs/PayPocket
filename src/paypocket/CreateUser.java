/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package paypocket;

import java.sql.ResultSet;
import java.util.Scanner;

public class CreateUser {

    public void addUser(String userName, String password) {

        try {
            String sql = "INSERT INTO users (username, password , account_balance) VALUES (?, ?,?)";
            int rows = DBLoader.executeUpdate(sql, userName, password,0);

            System.out.println(rows + " user created.");
            Scanner scan = new Scanner(System.in);

            System.out.println("welcome user");
            System.out.println("Enter Username");

            String name = scan.nextLine();

            System.out.println("Enter password");
            String pass = scan.nextLine();
            checkUser(name, pass);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkUser(String userName, String password) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from users where username='" + userName + "' and password='" + password + "'");

            if (rs.next()) {
                System.out.println("Login Succesfull. welcome " + userName);
                
                System.out.println("");
                
                System.out.println("");
                
                UserDashboard userdashboard = new UserDashboard();

                userdashboard.displayMenu(userName);

            } else {
                System.out.println("Login failed, new user signup first");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
