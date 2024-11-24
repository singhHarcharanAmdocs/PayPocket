/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package paypocket;

import java.util.Scanner;

/**
 *
 * @author user
 */
public class PayPocket {

    
    public static void main(String[] args) {
         Scanner scan = new Scanner(System.in);
        
        System.out.println("welcome to PayPocket");
        System.out.println("For Signup press 1 and For Signin Press 2");
        
     
     
       String checkuser = scan.nextLine();
       
       if(checkuser.equals("1")){
        
     
      System.out.println("Set username");
       String userName = scan.nextLine();
       
          
    
      System.out.println("Set password");
       String passWord = scan.nextLine();
       
        CreateUser createUser = new CreateUser();
        
       createUser.addUser(userName, passWord);
       
       }
       else if(checkuser.equals("2")){
            System.out.println("welcome user");
            System.out.println("Enter Username");
            String userName = scan.nextLine();
            
             System.out.println("Enter password");
       String passWord = scan.nextLine();
            
   CreateUser createUser = new CreateUser();
        
       createUser.checkUser(userName, passWord);
            
       }
       else if(checkuser.equals("3")){
           CreateUser createUser = new CreateUser();
           createUser.selectquery();
           
       }
       else{
           System.out.println("Invalid choice. Please restart the application.");
       }
        scan.close();
    }
   
    
}
