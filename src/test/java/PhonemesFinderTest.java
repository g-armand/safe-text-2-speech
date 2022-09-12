import org.json.JSONObject;
import org.junit.Test;
import service.PhonemesFinder;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PhonemesFinderTest {

    @Test
    public void getAllSplitsTest(){
        List<String> a = PhonemesFinder.getAllSplits("abar|obot");
        System.out.println(a);
    }

    @Test
    public void findPhonemesTest() throws IOException {
        PhonemesFinder.findPhonemes();
    }

    @Test
    public void testBasicGraphemes() throws IOException {
        File file = new File("src/main/java/resources/graphemes_to_phonemes_handmade.json");
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String jsonText = "";
        String nextLine;
        while((nextLine = br.readLine()) != null){
            jsonText += nextLine;
        }
        AtomicReference<String> finalJsonText = new AtomicReference<String>("{");
        JSONObject json = new JSONObject(jsonText);
        json.keySet().forEach(grapheme -> {
            String phoneme = json.getString(grapheme);
            grapheme = grapheme.replace("#", "");
            grapheme = grapheme.replace("-", "");
            try {
                if(PhonemesFinder.findIfUnique("affix", grapheme, phoneme)){
                    finalJsonText.set(finalJsonText.get() + ",\"" + grapheme + "\" : " + "\"" + phoneme +"\"");
                } else {
                    if(PhonemesFinder.findIfUnique("prefix", "#"+grapheme, phoneme)){
                        finalJsonText.set(finalJsonText.get() + ",\"#" + grapheme + "\" : " + "\"" + phoneme +"\"");
                    } else if(PhonemesFinder.findIfUnique("suffix", grapheme+"#", phoneme)){
                        finalJsonText.set(finalJsonText.get() + ",\"" + grapheme + "#\" : " + "\"" + phoneme +"\"");
                    } else if(PhonemesFinder.findIfUnique("infix", "-"+grapheme+"-", phoneme)){
                        finalJsonText.set(finalJsonText.get() + ",\"-" + grapheme + "-\" : " + "\"" + phoneme +"\"\n");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        finalJsonText.set(finalJsonText.get().replaceFirst(",", ""));
        finalJsonText.set(finalJsonText.get() +"}");
        FileWriter writer = new FileWriter(file);
        writer.write(finalJsonText.get());
        writer.flush();
        writer.close();
    }
}
