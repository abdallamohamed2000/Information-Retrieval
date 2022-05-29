
package inverted_index;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


//=====================================================================
class Index2 {

  
     public float Jaccard(String phrase, String document) {
        
        HashSet<String> document_sentence = new HashSet<String>();
        HashSet<String> intersectRes = new HashSet<String>();
        HashSet<String> unionRes = new HashSet<String>();
        try ( BufferedReader file = new BufferedReader(new FileReader(document))) {
                String ln;
                
                if ((ln = file.readLine()) != null) 
                {
                    String[] sentence = ln.split("\\W+");
                    for (String w : sentence) 
                        document_sentence.add(w.toLowerCase());
                }
                        
        } catch (IOException e) {
            System.out.println("File " + document + " not found. Skip it");
        }
            
        String[] words = phrase.split("\\W+"); 
        for (String word : words) 
        {
            if (!document_sentence.contains(word.toLowerCase()))
                unionRes.add(word.toLowerCase());
            else
            {
                intersectRes.add(word.toLowerCase());
            }
        }

        float unionCount = document_sentence.size() + unionRes.size();
        float intersectCount = intersectRes.size();
        
        if (unionCount != 0)
        {
            return intersectCount / unionCount;
        }
        
        else
        {
            System.out.println("Invalid Document");
            return -1;
        }
              
    }
     
     
     
     
     
      
    
   
    
}

public class Inverted_Index {

   
    public static void main(String[] args) throws IOException {
       Index2 index = new Index2();
        String phrase = "";
 
        
     //--------------------------------------------Jaccard-----------------------------------------------------   
        
        System.out.println(index.Jaccard("idea of March", "E:\\IR\\Files\\doc1.txt"));
       
        System.out.println(index.Jaccard("idea of March", "E:\\IR\\Files\\doc2.txt"));
    
       
       
       
       
       
       
       
    }
   }
