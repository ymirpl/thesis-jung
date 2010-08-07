package org.thesis.cdr;

import java.io.*;
import java.util.*;

import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.DistanceCentralityScorer;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class CDR {


	public UndirectedSparseGraph<String, Number> Graph;
	private BigFile bFile;
	public static String FILE = "/ext/mmincer/cdr";
	//public static String FILE = "/home/mmincer/cdr-slice.txt";
	//public static String FILE = "test-case.txt";
	private FileWriter saveFile;
	private BufferedWriter output;
	List<String> vertices;
	
	public static void main(String[] args) {
		CDR myCDR = new CDR();
		myCDR.readData();
		myCDR.insertNodes();
		
		long startTime = System.currentTimeMillis();
		myCDR.computeDegrees("/ext/mmincer/degrees.txt");
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time of computeDegrees: " + (endTime-startTime) + "ms");
		
		startTime = System.currentTimeMillis();
		myCDR.computeClosenessCentrality("/ext/mmincer/closenessCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Total execution time of closenessCent: " + (endTime-startTime) + "ms");
		
		startTime = System.currentTimeMillis();
		myCDR.computeEigenvectorCentrality("/ext/mmincer/eigenvectorCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Total execution time of eigenvectorCent: " + (endTime-startTime) + "ms");
				
		startTime = System.currentTimeMillis();
		myCDR.computeBetweennessCentrality("/ext/mmincer/betweenessCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Total execution time of betweenessCent: " + (endTime-startTime) + "ms");
		
		startTime = System.currentTimeMillis();
		myCDR.computeFreemansClosenessCentrality("/ext/mmincer/freemanClosenessCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Total execution time of freemansClosenessCent: " + (endTime-startTime) + "ms");
		

	}
	
	public CDR() {
		Graph = new UndirectedSparseGraph<String, Number>();
	}
	
	public void insertNodes() {
		int edgeNo = 0;
		for (String line : bFile) {
			
			String read_line[] = line.split("\t");
			Graph.addVertex(read_line[2]); // caller
			Graph.addVertex(read_line[3]); // target
			Graph.addEdge(edgeNo++, read_line[2], read_line[3], EdgeType.UNDIRECTED);
		}
		vertices = new ArrayList<String>(Graph.getVertices());
		System.out.println("Vertices: " + Graph.getVertexCount());
		System.out.println("Edges: " + Graph.getEdgeCount());
	}
	
	public void computeBetweennessCentrality(String outFilename) {
		BetweennessCentrality<String, Number> bc = new BetweennessCentrality<String, Number>(
				Graph);
		saveScore((VertexScorer<String, Double>)bc, outFilename);
	}
	
	public void computeClosenessCentrality(String outFilename) {
		ClosenessCentrality<String, Number> bc = new ClosenessCentrality<String, Number>(
				Graph);
		saveScore((VertexScorer<String, Double>)bc, outFilename);
	}
	
	public void computeDegrees(String outFilename){
		DegreeScorer<String> ds = new DegreeScorer<String>(Graph);
		saveScore((VertexScorer<String, Integer>)ds, outFilename);
	}
	
	public void computeFreemansClosenessCentrality(String outFilename) {
		DistanceCentralityScorer<String, Number> fc = new DistanceCentralityScorer<String, Number>(
				Graph, false);
		saveScore((VertexScorer<String, Double>)fc, outFilename);
	}
	
	public void computeEigenvectorCentrality(String outFilename) {
		EigenvectorCentrality<String, Number> fc = new EigenvectorCentrality<String, Number>(
				Graph);
		saveScore((VertexScorer<String, Double>)fc, outFilename);
	}
	
	
	
	private <S> void saveScore(VertexScorer<String, S> vs, String outFilename)
	{
		openSaveFile(outFilename);

		for (String v : vertices) {
			try {
				output.write(v + " " + vs.getVertexScore(v).toString() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		closeSaveFile();
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