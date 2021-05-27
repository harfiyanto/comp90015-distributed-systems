package server;

/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [BroadcastList.java]
 * 
 *  A structure used broadcast message to all the 
 *  clients by keeping an instance of BufferedWriter 
 *  from each of them.
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BroadcastList {
	private ArrayList<BufferedWriter> outList; 

	public BroadcastList() {
		outList = new ArrayList<BufferedWriter>();
	}

	public synchronized void update(String data) throws IOException
	{
		for (int i = 0; i < outList.size(); i++) {
			BufferedWriter out = (BufferedWriter) outList.get(i);
			out.write(data);
			out.newLine();
			out.flush();
		}
	}
	
	public synchronized void add(BufferedWriter w) {
		outList.add(w); 
	}

	public synchronized void remove(BufferedWriter w) {
		outList.remove(w);
	}
}
