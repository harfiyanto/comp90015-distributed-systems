/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * [Dictionary.java]
 * 
 * Class to handle the GUI for the server and responsible for creating 
 * threads (Dictionary.java) upon the request from client (ClientWindow.java).
 * 
 */

import java.io.*;
import java.net.Socket;
import org.json.*;

import java.net.SocketException;
import java.util.StringTokenizer;

public class Dictionary extends Thread {

    private Socket s;
    private int num;
    private File file;
    private ServerWindow window;
    private String msg;
    private String request;
    private String word;
    private String definition;

    /**
     * Constructors
     */
    public Dictionary(Socket s, int num, File file,
                                 ServerWindow window) {
        this.s = s;
        this.num = num;
        this.file = file;
        this.window = window;
//        System.out.print(file);
    }

    @Override
    public void run(){
        try {
            
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    s.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    s.getOutputStream(), "UTF-8")); 
            
            // Waiting for messages
            while ((msg = in.readLine())!= null) {

                StringTokenizer input = new StringTokenizer(msg, " ");
           
                window.logField.append("Client " + num + " requesting: " +
                        msg + "\n");
                request = input.nextToken();
                JSONObject data = readFile();
                // Process the request from the client
                switch (request) {
                    // Case 1: Query the meaning of a word.
                    case "Query":
                        word = input.nextToken();
                        query(word, data, out);
                        break;
                    // Case 2: Add a new word to the dictionary.
                    case "Add":
                        word = input.nextToken();
                        // Check for duplicate
                        if (data.has(word.toLowerCase())) {
                        	window.logField.append("Duplicate has been found\n");
                        	dupeFound(out);
                        } else {
                        	definition = input.nextToken("\n").trim();
                        	addWord(word, definition, data, out);
                        }
                        break;
                    // Case 3: Remove a word from dictionary
                    case "Remove":
                        word = input.nextToken();
                        // Check if the word exists
                        if (data.has(word.toLowerCase()))
                        	remove(word, data, out);
                        else
                        	// word cannot be found
                        	wordNotFound(out);
                        break;
                }
                window.logField.append("Replied\n");
            }
        } catch (SocketException e) {
        	window.logField.append("Socket for Client " + num + " is closing... \n");
        } catch (FileNotFoundException e) {
        	window.logField.append("Error could not find the dictionary file.\n");
        } catch (JSONException e) {
        	window.logField.append("Error when reading the dictionary.\n");
        } catch (IOException e) {
        	window.logField.append("Error during data transmission.\n");
        } finally {
        	// Closing the socket after the client is disconnected
            if (s != null) {
                try {
                    s.close();
                    window.logField.append("Client " + num +
                            " has disconnected from the server.\n");
                }
                catch (IOException e) {
                	window.logField.append("Error when tring to close Client " 
                			+ num + "socket.\n");
                }
            }
        }
    }
    
//    // Method to check the existence of a word in the dictionary
//    private boolean checkExistence(String word, JSONObject dictData) {
//    	if (dictData.has(word.toLowerCase())) {
//        	return true;
//        }
//        else {
//            return false;
//        }
//    }
  
    // Method to search the dictionary for a particular word
    private void query(String word, JSONObject dictData, 
    		BufferedWriter out) throws IOException {
    	try {
    		if (dictData.has(word.toLowerCase())) {
            	System.out.print("this word is familiar ... \n");
                JSONArray w = dictData.getJSONArray(word.toLowerCase());
                String m = w.getString(0);
                out.write(m + "\n");
                out.flush();
            }
            else {
                wordNotFound(out);
            }
    	} catch (IOException e) {
    		window.logField.append("Error during data transmission.\n");
    	}
        
    }
    
    // Method to tell the client that the word is already in the dictionary
    private void wordNotFound(BufferedWriter out)
            throws IOException {
    	try {
    		out.write("FailWordNotFound\n");
        	out.flush();
    	} catch (IOException e) {
    		window.logField.append("Error during data transmission.\n");
    	}
    	
    }
    
    // Method to insert a word into the dictionary
    private synchronized void addWord(String word, String definition, JSONObject dict,
                                     BufferedWriter out)
            throws IOException {
        JSONArray newDefinition = new JSONArray();
        newDefinition.put(definition);
        dict.put(word.toLowerCase(), newDefinition);
        out.write("WordAdded\n");
        out.flush();
        updateFile(dict);
    }
    
    // Method to tell the client that the word is already in the dictionary
    private void dupeFound(BufferedWriter out)
            throws IOException {
    	try {
    		out.write("DuplicateFound\n");
        	out.flush();
    	} catch (IOException e) {
    		window.logField.append("Error during data transmission.\n");
    	}
    	
    }
    
    // Method to remove a word from the dictionary
    private synchronized void remove(String word, JSONObject dict, BufferedWriter out)
            throws IOException {
    	try {
    		dict.remove(word.toLowerCase());
            out.write("WordRemoved\n");
            out.flush();
            // Update the dictionary.
            updateFile(dict);
    	} catch (IOException e) {
    		window.logField.append("Error during data transmission.\n");
    	}
        
    }

    // Method to load data from the JSON dictionary
    private JSONObject readFile() throws FileNotFoundException, JSONException {
    	JSONObject data = null;
    	JSONTokener dict = null;
    	try {
    		dict = new JSONTokener(new FileReader(file));
            data = new JSONObject(dict);
            return data;
    	} catch (FileNotFoundException e) {
    		window.logField.append("Dictionary file not found.\n");
    	} catch (JSONException e) {
    		window.logField.append("Error when opening the dictionary.\n");
    	}
    	return data;
    	
    }
    
    // Method to update the content of the JSON dictionary
    private void updateFile(JSONObject dict) {
        FileWriter out = null;
        try {
            out = new FileWriter(file, false);
            out.write(dict.toString());
            out.flush();
            out.close();
            window.logField.append("Dictionary has been updated!\n");
        } catch (IOException e) {
            window.logField.append("Error when updating the dictionary.\n");
        }
    }
}