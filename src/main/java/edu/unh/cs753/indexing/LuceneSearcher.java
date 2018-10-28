
/**
 * LuceneSearcher.java
 * 
 * UNH CS753
 * Progrmming Assignment 2
 * Group 2
 * 
 * This class is responsible for the bulk of the work in this program.
 * After index creation, it is called upon repeatedly to perform searches
 * and evaluation routines per program 2 specifications. 
 * 
**/

package edu.unh.cs753.indexing;

import edu.unh.cs753.utils.SearchUtils;
import edu.unh.cs753.utils.IndexUtils;
import edu.unh.cs753.utils.QrelMap;
import edu.unh.cs.treccar_v2.Data;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.search.MatchAllDocsQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.text.DecimalFormat;
import org.apache.lucene.search.similarities.BM25Similarity;



public class LuceneSearcher { 
	
    public final IndexSearcher searcher;
    private String methodName;
    private static final int MAX_REL_DOCS = Integer.MAX_VALUE;
    private final static int WIDTH = 1000, HEIGHT = 750;
    
    private static HashMap<String, Boolean> groundTruthMap = new HashMap<String, Boolean>();
    private static HashMap<String,Boolean>partial=new HashMap<String, Boolean>();
    //private int nSpamDocs = 0;
    
	/** 
	 * Construct a Lucene Searcher.
	 * @param indexLoc: the path containing the index.
    */
    public LuceneSearcher(String indexLoc) throws IOException {
        searcher = SearchUtils.createIndexSearcher(indexLoc);
        methodName = "default";
        searcher.setSimilarity(new BM25Similarity());
        
        groundTruthMap = new HashMap<String, Boolean>();

        partial=new HashMap<String,Boolean>();
        
        fillGroundTruthMap();
        partial();


    }
    
    // use the ground truth file (file named index) to populate a hashmap containing
    // document ID's (key) and whether or not they are spam (value)
    private void fillGroundTruthMap() throws IOException {
        
        // for now using "full"
        String truthFileLoc = "trec07p/full/index";
        try (BufferedReader br = new BufferedReader(new FileReader(truthFileLoc))) {
            String line;
            while ((line = br.readLine()) != null) {
                Scanner lineScan = new Scanner(line);
                boolean isSpam = true;
                while(lineScan.hasNext()) {
                    String token = lineScan.next();
                    if(token.equals("ham")) 
                        isSpam = false;
                    else if(!token.equals("spam")) {
                        int index = token.lastIndexOf('/');
                        String docID = token.substring(index + 1, token.length());
                        groundTruthMap.put(docID, isSpam);
                    }
                }
            }
        } 
    }

    private void partial() throws IOException {

        // for now using "partial"
        String truthFileLoc = "/Users/abnv/Desktop/trec07p/partial/index";
        String line1;
        BufferedReader br = new BufferedReader(new FileReader(truthFileLoc));

        while ((line1 = br.readLine()) != null) {
            Scanner Scan = new Scanner(line1);
            boolean isSpampar = true;
            while(Scan.hasNext()) {
                String token = Scan.next();
                if(token.equals("ham"))
                    isSpampar = false;
                else if(!token.equals("spam")) {
                    int index = token.lastIndexOf('/');
                    String docID = token.substring(index + 1, token.length());
                    partial.put(docID,isSpampar);
                }
            }
        }
    }




    private int N() {
        return groundTruthMap.size();
    }
    

    /**
     * Function: query
     * Desc: Queries Lucene paragraph corpus using a standard similarity function.
     *       Note that this uses the StandardAnalyzer.
     * @param queryString: The query string that will be turned into a boolean query.
     * @param nResults: How many search results should be returned
     * @return TopDocs (ranked results matching query)
     */
    public TopDocs query(String queryString, Integer nResults) {
        Query q = SearchUtils.createStandardBooleanQuery(queryString, "text");
        try {
            return searcher.search(q, nResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


	/**
     * Function: custom
     * Desc: A custom scoring function which is the sum of hits within a document.
     */
    public void custom() throws IOException {
        methodName = "custom";
        SimilarityBase mysimilarity= new SimilarityBase() {
            @Override
            protected float score(BasicStats basicStats, float v, float v1) {
                float sum1 = 0.0f;
                sum1 += v;
                return sum1;
            }

            @Override
            public String toString() {
                return null;
            }
        };
        searcher.setSimilarity(mysimilarity);
    }
    
    
    
    public void runSpamQuery(String query, int topN) throws IOException {
        
        System.out.println("Running spam query: \"" + query + "\" on top "
                            + topN + " documents");

        TopDocs topDocs = query(query, topN);
        
        if(topDocs.scoreDocs.length == 0) {
            System.out.println("No documents returned.");
            return;
        }
        
        int rank = topDocs.scoreDocs.length;
            // iterate through n (<= 100) top docs and output to run file  
        for (ScoreDoc sd : topDocs.scoreDocs) { 
            Document doc = searcher.doc(sd.doc);
            String docID = doc.get("id");
            String score = Float.toString(sd.score);
            System.out.println("Rank[" + rank-- + "]: " +  docID + ", " + score);
            
            
        }
    }
    
    
    public float getRPrec(String query) throws IOException {
        
        // TODO: error checking, does lucene already do this? (probably...)
        //query = query.trim();
        
        TopDocs topDocs = query(query, Integer.MAX_VALUE);
        int R = topDocs.scoreDocs.length;
        if(R == 0) 
            return 0;

        int tp = 0;

        for(ScoreDoc sd : topDocs.scoreDocs) { 
            Document doc = searcher.doc(sd.doc);
            String docID = doc.get("id");
            // NOTE: double check this: if R > 0, groundTruthMap contains docID
            //if(groundTruthMap.containsKey(docID)) {
            if(groundTruthMap.get(docID)) {
                tp++;
            }
            
        }
        return (float) tp / R ;
        
    }
    
    
       
}
