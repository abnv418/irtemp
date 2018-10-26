/**
 * Main.java
 * 
 * UNH CS753
 * Progrmming Assignment 3
 * Group 2
 * 
 * This is the Main file for our third programming assignment. It first
 * creates the run files for part 1, ===TODO FINISH THIS==== There is a script that runs
 * all of these steps and can be run with the command bash prog3.sh. 
 * 
**/



package edu.unh.cs753;

import edu.unh.cs753.indexing.LuceneSearcher;
import edu.unh.cs753.indexing.LuceneIndexer;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;






public class Main {

    
    private static String docFilesPath;  
    private final static String indexPath = "paragraphs";
    private static LuceneSearcher searcher;
    
    /**
     * Function: main
     * Desc: facilitate the trec_eval and evaluation algorithms.
     * @param args: program args (only 1 to run which part)
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, Exception {
        
        System.setProperty("file.encoding", "UTF-8");
        // must have arg4 be which part of the program to run
        
        try {
            docFilesPath = args[0];
        }
        catch(ArrayIndexOutOfBoundsException iob) {
            System.out.println("Error: not enough args provided");
            throw iob; 
        }
          
        //
        // run the indexing mechanism (only once per project run)
        System.out.print("\n\n\nCreating index... ");
        LuceneIndexer indexer = new LuceneIndexer(indexPath);
        long startTime = System.currentTimeMillis();
        try {
            indexer.doIndex(docFilesPath);
        } catch(FileNotFoundException fnf) {
            throw fnf;
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(Math.round(((float)elapsedTime / 1000)
                        * 100.0) / 100.0 + " seconds");
        //
            
        runSpamQueries();
        
        testRPrec();

        
        
    }
    
    
    
    // words found mostly in spam emails, and some terms that
    // should not (not likely to) exist in a spam email 
    private static void testRPrec() throws IOException {
    
        searcher = new LuceneSearcher(indexPath);
        // searcher.custom();
        
        String q = "7j***";
        float rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        q = "viagra";
        rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        q = "cialis";
        rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        q = "discount";
        rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        q = "discount viagra";
        rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        
        q = "save";
        rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        
        // Try terms that should not be in spam
        q = "propogate"; // 0, doesn't appear in any spam emails
        rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        
        // term in spam that isn't (necessarily) indicitive of a spam email
        q = "past"; 
        rPrec = searcher.getRPrec(q);
        System.out.println("RPREC: " + rPrec + " for query \"" + q + "\"");
        
        
        
    
    }
    
        
    private static void runSpamQueries() throws IOException {
        
        try {
            searcher = new LuceneSearcher(indexPath);
        } catch(Exception fnf) {
            System.out.println("Error creating lucene searcher");
            throw fnf;
        }
        
        //searcher.custom();
        
        int topN = 20;
        
        final String spamQuery1 = "viagra";
        searcher.runSpamQuery(spamQuery1, topN);
        
        final String spamQuery2 = "viagra discount";
        searcher.runSpamQuery(spamQuery2, topN);
            
            
    }
    
    
}
