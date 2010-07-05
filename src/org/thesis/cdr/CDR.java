package org.thesis.cdr;

import java.io.*;
import java.util.*;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.importance.AbstractRanker;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.io.PajekNetReader;


public class CDR {


	public DirectedSparseGraph<String, Number> Graph;
	private BigFile bFile;
	public static String FILE = "/media/Right/cdr-slice.txt";
	private FileWriter saveFile;
	private BufferedWriter output;
	List<String> vertices;
	
	public static void main(String[] args) {
		CDR myCDR = new CDR();
		myCDR.readData();
		myCDR.insertNodes();
		myCDR.computeBetweennessCentrality("bet.txt");


	}
	
	public CDR() {
		Graph = new DirectedSparseGraph<String, Number>();
	}
	
	public void insertNodes() {
		int edgeNo = 0;
		for (String line : bFile) {
			
			String read_line[] = line.split("\t");
			Graph.addVertex(read_line[2]); // caller
			Graph.addVertex(read_line[3]); // target
			Graph.addEdge(edgeNo++, read_line[2], read_line[3], EdgeType.DIRECTED);
			
			System.out.println(line);
		}
		vertices = new ArrayList<String>(Graph.getVertices());
	}
	
	public void computeBetweennessCentrality(String outFilename) {
		BetweennessCentrality<String, Number> bc = new BetweennessCentrality<String, Number>(
				Graph);

		openSaveFile(outFilename);

		for (String v : vertices) {
			try {
				output.write(v + " " + bc.getVertexScore(v).toString() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}	
	
	public void readData() {
		// a lot lots to read...
		try {
			bFile = new BigFile(FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void openSaveFile(String filename) {
		try {
			saveFile = new FileWriter(filename);
			output = new BufferedWriter(saveFile);
			
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void closeSaveFile() {
		try {
			output.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}	

}