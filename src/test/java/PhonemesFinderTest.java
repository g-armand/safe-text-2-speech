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
        PhonemesFinder.findPhonemesInfix();
    }

    @Test
    public void findIfUniqueTest() throws IOException {
        PhonemesFinder.findIfUnique("suffix", "amme", "am");
        PhonemesFinder.findIfUnique("suffix", "ammes", "am");
        PhonemesFinder.findIfUnique("suffix", "che", "ʃ");
        PhonemesFinder.findIfUnique("suffix", "ches", "ʃ");
        PhonemesFinder.findIfUnique("suffix", "ium", "jom");
        PhonemesFinder.findIfUnique("suffix", "iums", "jom");
        PhonemesFinder.findIfUnique("suffix", "ome", "om");
        PhonemesFinder.findIfUnique("suffix", "oms", "om");
        PhonemesFinder.findIfUnique("suffix", "oom", "um");
        PhonemesFinder.findIfUnique("suffix", "ooms", "um");
        PhonemesFinder.findIfUnique("suffix", "dermes", "deʁm");
        PhonemesFinder.findIfUnique("suffix", "derme", "deʁm");
        PhonemesFinder.findIfUnique("suffix", "orme", "oʁm");
        PhonemesFinder.findIfUnique("suffix", "ormes", "oʁm");
        PhonemesFinder.findIfUnique("suffix", "gramme", "gʁam");
        PhonemesFinder.findIfUnique("suffix", "grammes", "gʁam");
        PhonemesFinder.findIfUnique("suffix", "âmes", "am");
        PhonemesFinder.findIfUnique("suffix", "eâmes", "am");
        PhonemesFinder.findIfUnique("suffix", "chent", "ʃ");
        PhonemesFinder.findIfUnique("suffix", "me", "m");
        PhonemesFinder.findIfUnique("suffix", "mes", "m");
        PhonemesFinder.findIfUnique("suffix", "mmes", "m");
        PhonemesFinder.findIfUnique("suffix", "mme", "m");
        PhonemesFinder.findIfUnique("suffix", "ement", "emã");
        PhonemesFinder.findIfUnique("suffix", "oiement", "wamã");
        PhonemesFinder.findIfUnique("suffix", "exe", "eks");
        PhonemesFinder.findIfUnique("suffix", "fum", "fœ̃");
        PhonemesFinder.findIfUnique("suffix", "fums", "fœ̃");
        PhonemesFinder.findIfUnique("suffix", "um", "om");
        PhonemesFinder.findIfUnique("suffix", "ums", "om");
        PhonemesFinder.findIfUnique("suffix", "üm", "om");
        PhonemesFinder.findIfUnique("suffix", "oing", "wẽ");
        PhonemesFinder.findIfUnique("suffix", "oings", "wẽ");
        PhonemesFinder.findIfUnique("suffix", "tchs", "tʃ");
        PhonemesFinder.findIfUnique("suffix", "tch", "tʃ");
        PhonemesFinder.findIfUnique("suffix", "axe", "aks");
        PhonemesFinder.findIfUnique("suffix", "ashe", "aʃ");
        PhonemesFinder.findIfUnique("suffix", "ashs", "aʃ");
        PhonemesFinder.findIfUnique("suffix", "ash", "aʃ");
        PhonemesFinder.findIfUnique("suffix", "xe", "ks");
        PhonemesFinder.findIfUnique("suffix", "ish", "iʃ");
        PhonemesFinder.findIfUnique("suffix", "xent", "ks");    }

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
