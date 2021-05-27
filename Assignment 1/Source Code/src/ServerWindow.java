/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * [ServerWindow.java]
 * 
 * Class to handle the GUI for the server and responsible for creating 
 * threads (Dictionary.java) upon the request from client (ClientWindow.java)
 * 
 */

//import java.awt.EventQueue;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;

import org.apache.commons.cli.*;


public class ServerWindow {

	private JFrame frame;
	JTextArea logField;
	public static int PORT = 3012; // default port
	private JLabel serverLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception{
		
		// Code snippet to parse the command line (org.apache.commons.cli.*)
		Options options = new Options();

        Option input = new Option("p", "port", true, "port number");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("f", "file", true, "dictionary file");
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
        
        // Set port and path to the JSON dictionary from command line
        PORT = Integer.parseInt(cmd.getOptionValue("port"));
        String filePath = cmd.getOptionValue("file");
		
        // Initialize socket
		ServerSocket listeningSocket = null;
        Socket client = null;
        
        // Open window
        ServerWindow window = new ServerWindow();
		window.frame.setVisible(true);
		
		// Listen for incoming connection request
		try {
			listeningSocket = new ServerSocket(PORT);
			int i = 1; // number assigned for subsequent clients
			File dict = new File(filePath);
			// Waiting for clients ...
			while (true) {
				client = listeningSocket.accept();
				// Start a new thread to fulfill the client's requests
				Thread worker = new Thread(new Dictionary(client, i, dict , window));
                worker.start();
                window.logField.append(">>>> Client " +  i + " has connected.\n");
                i++;
			}
		} catch (IOException e) {
			// Error while setting up socket
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public ServerWindow() {
		initialize();
		serverLabel.setText("Server is online at Port:" +  PORT);
		logField.setText("Waiting for client(s) ...\n");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Server");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		logField = new JTextArea();
		frame.getContentPane().add(logField, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(logField); 
		frame.getContentPane().add(scrollPane);
		
		serverLabel = new JLabel("Console");
		serverLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		scrollPane.setColumnHeaderView(serverLabel);
	}

}
