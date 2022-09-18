import org.junit.Test;
import service.PhonemesLoader;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PhonemesLoaderTest {

    @Test
    public void loadPhonemesSimpleTest() throws IOException {
        System.out.println(PhonemesLoader.loadPhonemesSimple("prefix", true));
    }

    @Test
    public void simplify() throws IOException{
        String path = "src/main/java/resources/graphemes_to_phonemes_handmade.json" ;
        File file = new File(path);
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        File outFile =  new File(path.substring(0, path.length()-5)+"-simplified.json");
        FileWriter writer = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(writer);

        List<String> corres = new ArrayList<>();
        String nextLine = "";
        while((nextLine = br.readLine()) != null){
            nextLine = nextLine
                    .replace("ɛ̃", "ẽ")
                    .replace("ɛ", "e")
                    .replace("ə", "e")
                    .replace("ɑ̃", "ã")
                    .replace("ɑ", "a")
                    .replace("ø", "œ")
                    .replace("ɔ̃", "õ")
                    .replace("ɔ", "o");
            corres.add(nextLine);
        }
        corres.forEach(line -> {
            try {
                bw.write(line + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        bw.flush();
    }
}
