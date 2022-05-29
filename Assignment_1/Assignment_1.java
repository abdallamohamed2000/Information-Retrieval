
package inverted_index;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
class DictEntry2 {

    public int doc_freq = 0; 
    public int term_freq = 0; 
    public HashSet<Integer> postingList;

    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index2 {

   
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; //string-->Term Dic-->Postinglist
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

    
    //---------------------------------------------
    
//    public void printPostingList(HashSet<Integer> hset) {
//        Iterator<Integer> it2 = hset.iterator();
//        while (it2.hasNext()) {
//            System.out.print(it2.next() + ", ");
//        }
//        System.out.println("");
//    }
    //--------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }

    
    //----------------------------------------------------------------------------
    
    
    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet();
        Iterator it1 = pL1.iterator();
        Iterator it2 = pL2.iterator();

        Integer n1 = (Integer) it1.next();
        Integer n2 = (Integer) it2.next();

        while(n1 != null && n2 != null)//this loop will end when one of the lists ends
        {
            if(n1.compareTo(n2) == 0) //n1 = n2 -> will move the two iterator to the comming position
            {
                answer.add(n1);
                n1 = (it1.hasNext())? (Integer) it1.next(): null;
                n2 = (it2.hasNext())? (Integer) it2.next(): null;
            }
            else if(n1.compareTo(n2) < 0) //n1 < n2 -> Will move the first iterator
            {
                n1 = (it1.hasNext())? (Integer) it1.next(): null;
            }
            else//n1>n2 -> Will move the second iterator
            {
                n2 = (it2.hasNext())? (Integer) it2.next(): null;
            }
        }
        return answer;//answer will be holding the set of commen document number for both two posting lists
    }
   //-------------------------------------------------------------------------------------------------------------
    
    HashSet<Integer> Or(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();
        Iterator<Integer> p1 = pL1.iterator();
        Iterator<Integer> p2 = pL2.iterator();

        while (p1.hasNext()) {
            answer.add(p1.next());
        }

        
        while (p2.hasNext()) {
            answer.add(p2.next());
        }
        
        return answer;
    }
    //--------------------------------------------------------------------------
    
    
    HashSet<Integer> Not(HashSet<Integer> pL) {
        
        HashSet<Integer> answer = new HashSet<Integer>(sources.keySet());
        Object[] array = pL.toArray();

        for (int i = 0; i < pL.size(); i++) {
            if (answer.contains(array[i])) {
                answer.remove(array[i]);
            }
        }

        return answer;
    }
    //-----------------------------------------------------------------------
    public String find_3word_and(String phrase) { //p1 and p2 and p3
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        
        HashSet<Integer> answer1 = intersect(pL1, pL2);
        
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);        
        
        HashSet<Integer> answer2 = intersect(pL3, answer1);
              

      result = "Found in: \n";
        for (int num : answer2) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    
    public String find_2word_and(String phrase) { //p1 and p2 
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        
        HashSet<Integer> answer = intersect(pL1, pL2);

      result = "Found in: \n";
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    public String find_2word_and_not(String phrase) { //p1 and p2 
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        HashSet<Integer> Not_pL2 = Not(pL2);
        
        HashSet<Integer> answer = intersect(pL1, Not_pL2);

      result = "Found in: \n";
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    

    //-----------------------------------------------------------------------         
    public String find_3word_or(String phrase) { //p1 or p2 or p3
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        
        HashSet<Integer> answer1 =Or(pL1, pL2);
        
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);        
        
        HashSet<Integer> answer2 = Or(pL3, answer1);
              

      result = "Found in: \n";
        for (int num : answer2) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    
    
    
    public String find_2word_or(String phrase) { //p1 or p2 
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        
        HashSet<Integer> answer =Or(pL1, pL2);
        
      result = "Found in: \n";
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    
    
    public String find_1word_not(String phrase) { //not p1
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);

        HashSet<Integer> Not_pL1 = Not(pL1);
    
      result = "Found in: \n";
        for (int num : Not_pL1) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    
    
    public String find_3word_or_and(String phrase) { //p1 or p2 and p3
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        
        HashSet<Integer> answer1 =Or(pL1, pL2);
        
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);        
        
        HashSet<Integer> answer2 = intersect(pL3, answer1);
              

      result = "Found in: \n";
        for (int num : answer2) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    
    
   
    public String find_3word_not_and(String phrase) { //p1 and not p2 and  p3
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);

        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        HashSet<Integer> Not_pL2 = Not(pL2);

        HashSet<Integer> answer1 =intersect(pL1,Not_pL2);
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
        HashSet<Integer> answer2 =intersect(answer1,pL3);
        
      result = "Found in: \n";
        for (int num : answer2) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    
     public String find_3word_not_or(String phrase) { //p1 or  p2 or not p3
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);

        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        

        HashSet<Integer> answer1 =Or(pL1,pL2);
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
        HashSet<Integer> Not_pL3 = Not(pL3);
        HashSet<Integer> answer2 =Or(answer1,Not_pL3);
        
      result = "Found in: \n";
        for (int num : answer2) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
     public String find_3word_not_or_and(String phrase) { //p1 or not p2 and  p3
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);

        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        HashSet<Integer> Not_pL2 = Not(pL2);

        HashSet<Integer> answer1 =Or(pL1,Not_pL2);
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
        HashSet<Integer> answer2 =intersect(answer1,pL3);
        
      result = "Found in: \n";
        for (int num : answer2) {
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
 
    
}

public class Inverted_Index {

   
    public static void main(String[] args) throws IOException {
       Index2 index = new Index2();
        String phrase = "";
        
        index.buildIndex(new String[]{
                        
            "E:\\IR\\Files\\file1.txt", 
            "E:\\IR\\Files\\file2.txt",
            "E:\\IR\\Files\\file3.txt"
                       
        }); 
       
     



   //-----------------------------------------------------------------------

   Scanner scanner = new Scanner(System.in);
   
        System.out.println("__");
        System.out.println("Type the number of one of the options below:");
        System.out.println("1) word1 AND word2 AND  word3 ");
        System.out.println("2) word1 OR word2 OR word3   ");
        System.out.println("3) word1 OR word2 AND  word3  ");
        System.out.println("4) word1 AND NOt word2 AND word3  ");
        System.out.println("5) word1 OR  word2 OR not word3 ");
        System.out.println("6)  word1 OR NOT word2 AND word3 ");
        System.out.println("7)  word1 AND word2 ");
        System.out.println("8)  word1 OR word2 ");
        System.out.println("9)  NOT word1 ");
        System.out.println("10)  and not word ");
           int option = scanner.nextInt();
        switch (option) {
            case 1:
                 System.out.println("Print search phrase: ");
                 BufferedReader and3word = new BufferedReader(new InputStreamReader(System.in));
                 phrase = and3word.readLine();
                 System.out.println(index.find_3word_and(phrase));
                 break;

            case 2:
                System.out.println("Print search phrase: ");
                BufferedReader or3word = new BufferedReader(new InputStreamReader(System.in));
                phrase = or3word.readLine();
                System.out.println(index.find_3word_or(phrase));
                break;

            case 3:
                System.out.println("Print search phrase: ");
                BufferedReader or_and = new BufferedReader(new InputStreamReader(System.in));
                phrase = or_and.readLine();
                System.out.println(index.find_3word_or_and(phrase));
                break;
            case 4:
                System.out.println("Print search phrase: ");
                BufferedReader not_and = new BufferedReader(new InputStreamReader(System.in));
                phrase = not_and.readLine();
                System.out.println(index.find_3word_not_and(phrase));
                break;
            case 5:
                 System.out.println("Print search phrase: ");
                 BufferedReader not_or = new BufferedReader(new InputStreamReader(System.in));
                 phrase = not_or.readLine();
                 System.out.println(index.find_3word_not_or(phrase));
                break;
            case 6:
                System.out.println("Print search phrase: ");
                BufferedReader not_or_and = new BufferedReader(new InputStreamReader(System.in));
                phrase = not_or_and.readLine();
                System.out.println(index.find_3word_not_or_and(phrase));
                break;
            case 7:
                System.out.println("Print search phrase: ");
                BufferedReader and = new BufferedReader(new InputStreamReader(System.in));
                phrase = and.readLine();
                System.out.println(index.find_2word_and(phrase));
            case 8:
                System.out.println("Print search phrase: ");
                BufferedReader or = new BufferedReader(new InputStreamReader(System.in));
                phrase = or.readLine();
                System.out.println(index.find_2word_or(phrase));
                break;
            case 9:
                System.out.println("Print search phrase: ");
                BufferedReader not = new BufferedReader(new InputStreamReader(System.in));
                phrase = not.readLine();
                System.out.println(index.find_1word_not(phrase));
            case 10:
                System.out.println("Print search phrase: ");
                BufferedReader notand = new BufferedReader(new InputStreamReader(System.in));
                phrase = notand.readLine();
                System.out.println(index.find_2word_and_not(phrase));
            
       }
        
      
       
       
       
       
    }
   }
