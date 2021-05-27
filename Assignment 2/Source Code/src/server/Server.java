/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [Server.java]
 * 
 *  Class to handle GUI for the server, connection requests 
 *  from client(s) and creating a corresponding thread (Worker) for them. 
 */

package server;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


import data.User;

import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Server {

	private JFrame frame;                                 
	private static int PORT = 3005;
	private static String HOST = "localhost";
	private static String ADDRESS = "localhost";
	private static ArrayList<User> users = new ArrayList<User>();
	private static DefaultListModel listUser;
	private final JPanel panel_1 = new JPanel();
	public static List<Socket> sockets = new ArrayList<Socket>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Parsing the command line parameters
		Options options = new Options();

		Option input = new Option("p", "port", true, "server port");
	    input.setRequired(true);
	    options.addOption(input);

		Option output = new Option("a", "address", true, "server address");
		output.setRequired(true);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);
			System.exit(1);
		}

		PORT = Integer.parseInt(cmd.getOptionValue("port"));
		ADDRESS = cmd.getOptionValue("address");		
		
		ServerSocket listeningSocket = null;
        Socket client = null;
        BroadcastList broadcast = new BroadcastList();
        listUser = new DefaultListModel();
        
        Server window = new Server();
		window.frame.setVisible(true);
		
        try {
			listeningSocket = new ServerSocket(PORT);
			
			int i = 1; // number assigned for subsequent clients
			// Waiting for clients ...
			while (true) {
				client = listeningSocket.accept(); // Wait and accept a connection
				sockets.add(client);
				// Create a User struct to hold client information. 
                User newUser = new User(i, client);
                // Start a new thread to fulfill the client's requests
                new Worker(client, broadcast, newUser, listUser);
                i++;
			}
		} catch (BindException e) {
			JOptionPane.showMessageDialog(null, "Address already in use. "
					+ "Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} catch (IOException e) {
			// Error while setting up socket
			e.printStackTrace();
		} 
	}

	/**
	 * Create the application.
	 */
	public Server() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 544, 300);
		frame.setSize(275, 309);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setTitle("Server");
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(panel_1);
		
		JList userL = new JList(listUser);
		frame.getContentPane().add(userL);
		
		JLabel lblNewLabel = new JLabel("User List (Port:" + PORT + "|HOST:" + HOST + ")");
		frame.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JButton kickBtn = new JButton("Kick");
		kickBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	            try {
	            	Socket target = sockets.get(userL.getSelectedIndex());
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(target.getOutputStream()));
	            	out.write("kick");
		            out.newLine();
		            out.flush();
					target.close();
					listUser.remove(userL.getSelectedIndex());
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		frame.getContentPane().add(kickBtn, BorderLayout.SOUTH);
	}
}

