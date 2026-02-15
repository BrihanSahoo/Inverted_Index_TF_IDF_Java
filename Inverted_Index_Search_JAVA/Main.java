import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TextProcessor{
    private final static Set<String> STOP_WORDS = Set.of("the","is","and","a","of","in");

    // STEMMING

    public static String simpleStem(String word){
        if(word.endsWith("ing")){
            return word.substring(0,word.length()-3);
        }

        if(word.endsWith("ed")){
            return word.substring(0,word.length()-2);
        }

        if(word.endsWith("s") && word.length()>3){
            return word.substring(0,word.length()-1);
        }
        return word;
    }

    // TOKENIZE

    public static  List<String> tokenize(String text){
        return Arrays.stream(
            text.toLowerCase()
            .replaceAll("[^a-z0-9 ]","")
            .split("\\s+") 
        )
        .filter(token->!STOP_WORDS.contains(token))
        .map(TextProcessor::simpleStem)
        .toList();
    }
}

class TfIdfEngine{
    private final Map<String,Map<Integer,Integer>> index = new HashMap<>();
        int totalDocuments = 0;

    public void addDocument(int docId,String content){
        totalDocuments++;
        List<String> tokens = TextProcessor.tokenize(content);

        for(String token:tokens){
            index.putIfAbsent(token,new HashMap<>());
            Map<Integer,Integer> postings = index.get(token);

            postings.put(docId,postings.getOrDefault(docId,0)+1);
        }
    }

    public double computeIdf(String term){
        Map<Integer,Integer> postings = index.get(term);
        if(postings==null) return 0.0;

        int docsWithTerm = postings.size();

        return Math.log((double)totalDocuments/docsWithTerm);
    }

    public Map<Integer,Double> search(String term){
        Map<Integer,Double> scores = new HashMap<>();

        Map<Integer,Integer> postings = index.get(term);

        if(postings==null) return scores;

        double idf = computeIdf(term);

        for(Map.Entry<Integer,Integer> entry : postings.entrySet()){
            int docId = entry.getKey();
            int tf = entry.getValue();

            double score = tf*idf;

            scores.put(docId,score);
        }

        return scores;

    }
}


class Main{
    
    public static void main(String[] args) {
        
        TfIdfEngine engine = new TfIdfEngine();

        engine.addDocument(1, "pizza pizza love");
        engine.addDocument(2, "pizza pasta");
        engine.addDocument(3, "pasta love");

        Map<Integer, Double> results = engine.search("pizza");

        results.forEach((docId, score) ->
                System.out.println("Doc " + docId + " Score: " + score));

    }
}