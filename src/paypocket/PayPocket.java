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
        PayPocket app = new PayPocket();
        app.startApp(); 
      
    }
   
    public void startApp(){
           Scanner scan = new Scanner(System.in);
        while(true){
        System.out.println("welcome to PayPocket");
        System.out.println("For Signup press 1 and For Signin Press 2");
         System.out.println("To Exit press 8");
     
     
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
       else if(checkuser.equals("4")){
           CreateUser createUser = new CreateUser();
           createUser.selectquery();
           
       }
       else if (checkuser.equals("8")) {
                System.out.println("Thank you for using PayPocket! Goodbye!");
                break; // Exit the loop and terminate the application
            }
       
       else{
           System.out.println("Invalid choice. Please restart the application.");
       }
       
    }
        
}
    
}
