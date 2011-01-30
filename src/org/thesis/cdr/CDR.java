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

/**
 * Klasa CDR zwiera w sobie metody uruchamiające eksperyment "JUNG na Azul", polegający na próbie wyznaczenie miar węzłów bardzo dużej sieci, 
 * za pomocą biblioteki JUNG uruchomionej na maszynie Azul. 
 * @author ymir
 *
 */
public class CDR {


	public UndirectedSparseGraph<String, Number> Graph;
	private BigFile bFile;
	public static String FILE = "/ext/mmincer/cdr"; // lokalizacja odpowiada zasobom na komputerze siemowit
	public static String OUT_DIR = "/ext/mmincer";
	//public static String FILE = "/home/mmincer/cdr-slice.txt";
	//public static String FILE = "test-case.txt";
	private FileWriter saveFile;
	private BufferedWriter output;
	List<String> vertices;
	
	/**
	 * Metoda ładuje dane z pliku znajdującego się w lokalizacji ustawionej w polu FILE. 
	 * Następnie uruchamiane są metody służące do obliczania wartości miar węzłów. 
	 * Wynik ich pracy jest zapisywany do plików, których nazwa jest podawana jako parametr danej metody.
	 * Pliki te są tworzone w katalogu OUT_DIR.
	 * Zmienne FILE i OUT_DIR są ustawiane w pliku config.properties. 
	 * 
	 * @param configFile jako parametr musi być podana ścieżka do pliku config.properties, zawierającego ustawienia
	 */
	public static void main(String[] args) {

		

		
		CDR myCDR = new CDR(args[0]);
		myCDR.readData();
		myCDR.insertNodes();
		
		long startTime = System.currentTimeMillis();
		myCDR.computeDegrees(CDR.OUT_DIR+"degrees.txt");
		long endTime = System.currentTimeMillis();
		System.out.println("Całkowity czas wykonania computeDegrees: " + (endTime-startTime) + "ms");
		
		startTime = System.currentTimeMillis();
		myCDR.computeClosenessCentrality(CDR.OUT_DIR+"closenessCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Całkowity czas wykonania closenessCent: " + (endTime-startTime) + "ms");
		
		startTime = System.currentTimeMillis();
		myCDR.computeEigenvectorCentrality(CDR.OUT_DIR+"eigenvectorCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Całkowity czas wykonania eigenvectorCent: " + (endTime-startTime) + "ms");
				
		startTime = System.currentTimeMillis();
		myCDR.computeBetweennessCentrality(CDR.OUT_DIR+"betweenessCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Całkowity czas wykonania betweenessCent: " + (endTime-startTime) + "ms");
		
		startTime = System.currentTimeMillis();
		myCDR.computeFreemansClosenessCentrality(CDR.OUT_DIR+"freemanClosenessCentrality.txt");
		endTime = System.currentTimeMillis();
		System.out.println("Całkowity czas wykonania freemansClosenessCent: " + (endTime-startTime) + "ms");
		

	}
	
	public CDR(String propsFile) {
		Graph = new UndirectedSparseGraph<String, Number>();
		Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream(propsFile));
		} catch (IOException e) {
			System.out.print("Nie można otworzyć pliku properties");
		}
		
		CDR.FILE = configFile.getProperty("FILE");
		CDR.OUT_DIR = configFile.getProperty("OUT_DIR");
		
		
	}
	
	/**
	 * Metoda tworzy sieć na podstawie danych wczytanych z pliku. 
	 */
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
	
	/**
	 * Metoda wyznacza wartość pośrednictwa dla każdego węzła. 
	 * @param outFilename
	 */
	public void computeBetweennessCentrality(String outFilename) {
		BetweennessCentrality<String, Number> bc = new BetweennessCentrality<String, Number>(
				Graph);
		saveScore((VertexScorer<String, Double>)bc, outFilename);
	}
	
	/**
	 * Metoda wyznacza wartość bliskości dla każdego węzła. 
	 * @param outFilename
	 */
	public void computeClosenessCentrality(String outFilename) {
		ClosenessCentrality<String, Number> bc = new ClosenessCentrality<String, Number>(
				Graph);
		saveScore((VertexScorer<String, Double>)bc, outFilename);
	}
	
	/**
	 * Metoda wyznacza stopień dla każdego węzła. 
	 * @param outFilename
	 */
	public void computeDegrees(String outFilename){
		DegreeScorer<String> ds = new DegreeScorer<String>(Graph);
		saveScore((VertexScorer<String, Integer>)ds, outFilename);
	}
	
	/**
	 * Metoda wyznacza wartość bliskości w sensie Freemana dla każdego węzła. 
	 * @param outFilename
	 */
	public void computeFreemansClosenessCentrality(String outFilename) {
		DistanceCentralityScorer<String, Number> fc = new DistanceCentralityScorer<String, Number>(
				Graph, false);
		saveScore((VertexScorer<String, Double>)fc, outFilename);
	}
	
	/**
	 * Metoda wyznacza wartość centralności własnej dla każdego węzła. 
	 * @param outFilename
	 */
	public void computeEigenvectorCentrality(String outFilename) {
		EigenvectorCentrality<String, Number> fc = new EigenvectorCentrality<String, Number>(
				Graph);
		saveScore((VertexScorer<String, Double>)fc, outFilename);
	}
	
	
	/**
	 * Metoda zapisuje wartości miar dla węzłów w wynikowym pliku tekstowym. 
	 * @param <S> typ wartości miar
	 * @param vs VertexScorer zawierający wyliczone wartości miar
	 * @param outFilename nazwa pliku
	 */
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
	
	/**
	 * Metoda pomocnicza, otwiera plik z danymi
	 */
	public void readData() {
		try {
			bFile = new BigFile(FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda pomocnicza, otwiera plik do zapisu
	 * @param filename
	 */
	public void openSaveFile(String filename) {
		try {
			saveFile = new FileWriter(filename);
			output = new BufferedWriter(saveFile);
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Metoda pomocnicza, zamyka plik po zapisie. 
	 */
	public void closeSaveFile() {
		try {
			output.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}	

}