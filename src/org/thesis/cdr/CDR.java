package org.thesis.cdr;

import java.io.IOException;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.importance.AbstractRanker;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.io.PajekNetReader;


public class CDR {


	public static DirectedSparseGraph<String, Number> Graph;
	public static BigFile bFile;
	public static String FILE = "/media/Right/cdr-slice.txt";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CDR myCDR = new CDR();
		myCDR.readData();
		myCDR.insertNodes();


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
	}
	
	
	public void readData() {
		// a lot lots to read...
		try {
			bFile = new BigFile(FILE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}