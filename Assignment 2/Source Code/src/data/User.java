package data;

/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [User.java]
 * 
 * Contains information from each client 
 * (e.g. name, port, socket, etc.)
 */

import java.net.*;

public class User {
	private int id = 0;
    private String name = "Client";
    public Socket s;

    public User(int id, Socket s) {
        this.id = id;
        this.s = s;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Socket getSocket() {
        return s;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setSocket(Socket s) {
        this.s = s;
    }
}