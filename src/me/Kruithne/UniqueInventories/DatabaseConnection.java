package me.Kruithne.UniqueInventories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DatabaseConnection {

	 private Connection connection = null;

     private String url = "jdbc:mysql://localhost:3306/minecraft1";
     private String user = "minecraft";
     private String password = "HorsePoopRaddishGuilloutine";
     
     private Logger log = null;
     
     public DatabaseConnection(Logger log)
     {
    	 this.log = log;
    	 this.establishConnection();
     }
     
     public void establishConnection()
     {
    	try
    	{
    		this.connection = DriverManager.getConnection(this.url, this.user, this.password);
    	}
    	catch (SQLException e)
    	{
    		this.log.log(Level.SEVERE, String.format("SQL Error: %s", e.getMessage()));
		}
     }
     
     public void closeConnection()
     {
    	try
    	{
    		this.connection.close();
		}
    	catch (SQLException e)
    	{
    		this.log.log(Level.SEVERE, String.format("SQL Error: %s", e.getMessage()));
		}
     }
     
     public ResultSet getQuery(String query)
     {
    	 try
    	 {
    		 if (this.connection.isClosed())
    		 {
    			 this.establishConnection();
    		 }
    	 }
    	 catch (SQLException e1)
    	 {
    		 this.log.log(Level.SEVERE, String.format("SQL Error: %s", e1.getMessage()));
    	 }
    	 
    	 ResultSet result = null;
    	 
    	 try
    	 {
    		 Statement newStatement = connection.createStatement();
    		 result = newStatement.executeQuery(query);
    	 }
    	 catch (SQLException e)
    	 {
    		 this.log.log(Level.SEVERE, String.format("SQL Error: %s", e.getMessage()));
    	 }
    	 
    	 return result;
     }
     
     public boolean query(String query)
     {
    	 try
    	 {
    		 if (this.connection.isClosed())
    		 {
    			 this.establishConnection();
    		 }
    	 }
    	 catch (SQLException e1)
    	 {
    		 this.log.log(Level.SEVERE, String.format("SQL Error: %s", e1.getMessage()));
    	 }
    	 
    	 try
    	 {
    		 Statement statement = this.connection.createStatement();
    		 
    		 statement.executeUpdate(query);
    		 return true;
    		 
    	 }
    	 catch (SQLException e)
    	 {
    		 this.log.log(Level.SEVERE, String.format("SQL Error: %s", e.getMessage()));
    		 return false;
    	 }
     }
}
