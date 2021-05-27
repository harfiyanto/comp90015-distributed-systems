package data;

/* COMP90015 Distributed Systems (Semester 1, 2020)
 * Harfiyanto Santoso 
 * harfiyantos@student.unimelb.edu.au
 * (772503)
 * 
 * Assignment 2
 * 
 * [DataArray.java]
 * 
 *  ArrayList of DrawData which represents the steps 
 *  taken to reach the final system state. 
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataArray {

	public List<DrawData> array= new ArrayList<DrawData>();
	
	public DataArray(){
		array=new ArrayList<DrawData>();
	}
	
	public synchronized void addData(DrawData data){
		array.add(data);
	}
	
	public Iterator<DrawData> iterator(){
		return array.iterator();
	}
}

