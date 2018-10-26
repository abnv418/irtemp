



/**
 * QrelMap.java
 * 
 * UNH CS753
 * Progrmming Assignment 2
 * Group 2
 * 
 * This class is mainly a hashmap wrapper to store the "ground truth"
 * qrel queries and relevant documents. It is needed for comparison to 
 * see how well our search algorithms performed, and to see how they
 * measure up to the trec_eval results from part 2.
 * 
 * Website (open source) I got the main idea for:
 * https://www.javatips.net/api/twitter-tools-master/twitter-tools-rm3/src/main/java/edu/illinois/lis/utils/Qrels.java#
 * 
**/



package edu.unh.cs753.utils;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.Scanner;



public class QrelMap {
    
    public static final Pattern SPACE_PATTERN = Pattern.compile(" ", Pattern.DOTALL);
    private static final int QUERY_COLUMN = 0;
    private static final int DOCNO_COLUMN = 2;
    private static final int REL_COLUMN   = 3;
    private Map<String,Set<String>> map;
    
    /** 
	 * Construct a QrelMap.
	 * @param pathToQrelsFile: the path containing the qrel file.
    */
    public QrelMap(String pathToQrelsFile) {
        map = new HashMap<String,Set<String>>();
        try {
            fillMap(pathToQrelsFile);
        } catch(IOException ioe) {}
    }
    
    /**
     * Function: fillMap
     * Desc: Fill the map with relevant qrel results.
     * @param pathToQrelsFile: path to qrel file.
     */
    private void fillMap(String pathToQrelsFile) throws IOException {
        
        try (BufferedReader br = new BufferedReader(new FileReader(pathToQrelsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Scanner scanner = new Scanner(line);
                String queryId = "";
                String docId = "";
                for(int i = 0; scanner.hasNext(); i++) {
                    String token = scanner.next();
                    if(i == 0) 
                        queryId = token;
                    else if(i == 2) 
                        docId = token;
                }
                Set<String> relDocs = null;
                if(!map.containsKey(queryId)) 
                    relDocs = new HashSet<String>();
                else 
                    relDocs = map.get(queryId);
                relDocs.add(docId);
                map.put(queryId, relDocs);
                scanner.close();
            }
        }
    }
    
    /**
     * Function: isRel
     * Desc: Given a query, check if a document is relevant.
     * @param query: the query to check relevance
     * @param docno: the document.
     * @return if relevant.
     */
    public boolean isRelevant(String queryId, String docId) {
        if(!map.containsKey(queryId)) 
            return false;
        return map.get(queryId).contains(docId);
    }
    
    /**
     * Function: numRel
     * Desc: Given a query, retrieve the number of relevant documents.
     * @param query: the query to get relevant docs
     * @return number of relevant documents.
     */
    public double nRelevant(String queryId) {
        if(!map.containsKey(queryId)) 
            return 0.0;
        return (double)map.get(queryId).size();
    }
    
}
