/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [Worker.java]
 * 
 *  Class that acts as a worker and assigned to a 
 *  client to process its requests. 
 */

package server;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import data.User;


public class Worker extends Thread{
	private Socket client; 

    private BufferedReader in; 
    private BufferedWriter out; 
    private static int count; 
    private int id;
    public BroadcastList broadcastList; 
    private User user;
    private DefaultListModel luser;
    public String[] ss;
    public static List<String> userList = new ArrayList<String>();
    public static List<String> record = new ArrayList<String>();

    public Worker(Socket s, BroadcastList b, User newUser, DefaultListModel luser) throws IOException {
        this.client = s;
        this.broadcastList = b;
        this.user = newUser;
        this.luser = luser;
        
        
        System.out.println("Worker Created");
        if (initialize()) {
        	System.out.println("new thread created");
            this.start();
            this.luser.addElement(this.user.getName());
            userList.add(this.user.getName());
            
        } else {
            out.write("quit");
            out.newLine();
            out.flush();
        }
        catchUp();
    }
    
    public boolean initialize() throws IOException {
    	in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        user.setName(in.readLine());
        int response = JOptionPane.showConfirmDialog(null, "Someone wants to share your whiteboard: " + user.getName(), "Allow?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
              return false;
            }
            this.id = this.user.getId(); 
            out.write(String.valueOf(id)); 
            out.newLine();
            out.flush();
        return true;
    }
    
    public void run() {
    	String dataBuffer = null;
        try {
        	// Add this client's output writer into the list to broadcast
        	broadcastList.add(out); 
        	
        	// Read messages
            while (true) {
                dataBuffer = in.readLine();
//                System.out.println(dataBuffer);
                if (dataBuffer.equals("quit")) {
                	out.write("removeuser." + this.user.getName());
                    out.newLine();
                    out.flush();
                    userList.remove(this.user.getName());
                    break;
                }
                broadcastList.update(dataBuffer); 
                System.out.println(dataBuffer);
                record.add(dataBuffer);
            }
            
            // Client has exited
            broadcastList.remove(out);
            luser.removeElement(this.user.getName());

        } catch (IOException e) {

        } finally {
            try {
            	if (client != null)
                    client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Load the latest shared state for this client
    public void catchUp() throws IOException {
    	Iterator<String> u = userList.iterator();
    	Iterator<String> i = record.iterator();
    	
    	while (u.hasNext()) {
    		out.write("newuser." + u.next()); 
            out.newLine();
            out.flush();
    	}
    	while (i.hasNext()) {
    		out.write(i.next()); 
            out.newLine();
            out.flush();
    	}
    }
}
