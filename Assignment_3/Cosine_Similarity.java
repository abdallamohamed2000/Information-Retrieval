package filetokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


class FileTokenizer {
    ArrayList<String> sources;  // store the words
    String filename;
    ArrayList<Integer> count;

    FileTokenizer() {
        sources = new ArrayList<String>();count = new ArrayList<Integer>();
    }

    public void buildSources(String file) {
        filename=file;
        try (BufferedReader f = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = f.readLine()) != null) {
                String[] words = ln.split("\\W+");
                for (String word : words) {
                    word = word.toLowerCase();
                    // check to see if the word is not in the dictionary
                    if (!sources.contains(word)) {
                        sources.add(word);
                        count.add(1);
                    }
                    else{
                        count.set(sources.indexOf(word),count.get(sources.indexOf(word))+1);

                    }
                }
            }

        } catch (IOException e) {
            System.out.println("File " + file + " not found. Skip it");
        }
    }

}
class cousine {
    ArrayList<Integer> calculateVector(FileTokenizer f,HashSet<String> words) {
        ArrayList<String>answerWords=new ArrayList<String>();
        ArrayList<Integer> answer = new ArrayList<Integer>();
        boolean found=false;
        for (String s : words) {
            for(String w : f.sources){
                if(s.toLowerCase().equals(w.toLowerCase()) && !answerWords.contains(s.toLowerCase())){
                    answerWords.add(s);
                    answer.add(f.count.get(f.sources.indexOf(s)));
                    found=true;
                    break;
                }
            }
            if (!found){
                answerWords.add(s);
                answer.add(0);
            }
            found=false;
        }
        return answer;
    }
    double dot (ArrayList<Integer> v1,ArrayList<Integer>v2){
        double answer=0.0;
        for(int i=0;i<v1.size();i++){
            answer+=(v1.get(i)*v2.get(i));
        }
        return answer;
    }
    double magnitude(ArrayList<Integer> v){
        double answer=0.0;
        for(int i=0;i<v.size();i++){
            answer+=Math.pow(v.get(i),2);
        }
        return Math.sqrt(answer);
    }

    double sim (ArrayList<Integer> v1,ArrayList<Integer>v2){
        return (dot(v1,v2)/(magnitude(v1)*magnitude(v2)));
    }
    
    public HashSet<String> union(String document1, String document2) {
        
        HashSet<String> document_sentence = new HashSet<String>();
        
        try ( BufferedReader file = new BufferedReader(new FileReader(document1))) {
                String ln;
                
                if ((ln = file.readLine()) != null) 
                {
                    String[] sentence = ln.split("\\W+");
                    for (String w : sentence) 
                       document_sentence.add(w.toLowerCase());
                }
                        
        } catch (IOException e) {
            System.out.println("File " + document1 + " not found. Skip it");
        }
          try ( BufferedReader file = new BufferedReader(new FileReader(document2))) {
                String lnn;
                
                if ((lnn = file.readLine()) != null) 
                {
                    String[] sentence1 = lnn.split("\\W+");
                    for (String ww : sentence1) 
                        if (!document_sentence.contains(ww.toLowerCase()))
                        {
                            document_sentence.add(ww.toLowerCase());
                        }
                }
                        
        } catch (IOException e) {
            System.out.println("File " + document2 + " not found. Skip it");
        }  
        

       return document_sentence;
              
    }
    


}


public class cosinesimilarity {
    public static void main(String args[]) throws IOException {
        cousine c=new cousine();
        FileTokenizer f1 = new FileTokenizer();
        f1.buildSources("E:\\IR\\Files\\cosine1.txt");
        FileTokenizer f2 = new FileTokenizer();
        f2.buildSources("E:\\IR\\Files\\cosine2.txt");
        HashSet<String> tokens = c.union("E:\\IR\\Files\\cosine1.txt", "E:\\IR\\Files\\cosine2.txt");
        
       ArrayList<Integer> v1=c.calculateVector(f1,tokens);
        ArrayList<Integer> v2=c.calculateVector(f2,tokens);
//        System.out.println(v1);
//        System.out.println(v2);

      System.out.println("\n"+"====================== Cosine Similarity for two files ================="+"\n");
       System.out.println("similarity = "+c.sim(v1,v2));



    }
}