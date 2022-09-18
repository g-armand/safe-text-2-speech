import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import service.PhonemesFinder;
import service.TranslationService;

public class TranslationServiceTest {

    TranslationService translationService = new TranslationService();

    @Test
    public void testJsonRead() throws IOException {
        Set<String> phonemesWithGraphemes = translationService.phonemesLoader.graphemesMap.keySet()
                .stream()
                .map(grapheme -> translationService.phonemesLoader.graphemesMap.get(grapheme).keySet())
                .flatMap(Set::stream)
                .map(phoneme -> phoneme.replaceAll("\\*", ""))
                .map(phoneme -> phoneme.replaceAll("\\/", ""))
                .collect(Collectors.toSet());
        Set<String> phonemesWithFrequency = translationService.phonemesLoader.frequencies.keySet();
        phonemesWithGraphemes.forEach(phoneme -> {
            if(!phonemesWithFrequency.contains(phoneme)) System.out.println(phoneme);
        });
    }

    @Test
    public void translateTest(){
        String input = "démarrer la voiture";
        String output = translationService.translate(input);
        System.out.println(output);
    }

    @Test
    public void litteralTranslationTest(){
        List<String> result = translationService.litteralTranslation("lol");
        result.forEach(System.out::println);
    }

    @Test
    public void litteralTranslationAndFrequenciesTest(){
        List<String> result = translationService.litteralTranslation("lesnazi");
        result.stream()
                .sorted(Comparator.comparingDouble(translationService::computeProbability).reversed())
                .forEach(System.out::println);
    }

    @Test
    public void sortList() throws IOException {

        File file = new File("src/main/java/resources/g2p_wiki_suffix_withdoublons.json");
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        List<String> finalList = new ArrayList<>();
        Map<String,List<String>> map = new HashMap<>();
        String jsonText = "";
        String nextLine;
        while((nextLine = br.readLine()) != null){
            if(!nextLine.contains("{")|| !nextLine.contains("}")){
                try{
                    nextLine = nextLine.trim().replace("\"", "");
                    nextLine = nextLine.replace(",", "");
                    String[] line = nextLine.split("\s:\s");
                    if(!map.keySet().contains(line[0])){
                        List<String> newList = new ArrayList<String>();
                        newList.add(line[1]);
                        map.put(line[0], newList);
                    } else {
                        List<String> newList = map.get(line[0]);
                        newList.add(line[1]);
                        map.put(line[0], newList);
                    }
                } catch (Exception e){
                    System.out.println("oups");
                }

            }
        }
        File file2 = new File("src/main/java/resources/g2p_wiki_suffix_withdoublons.json");

        FileWriter writer = new FileWriter(file2);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write("{\n");
        map.keySet().stream().sorted(String::compareTo).forEach(key -> {
            try {
                bw.write("\t\""+key+"\" : \""+ map.get(key)+"\",\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        bw.write("\n}");
        bw.flush();
    }

    @Test
    public void testFindIfUnique() throws IOException {
        String word = "prefix";
        File file = new File("src/main/java/resources/g2p_wiki_"+ word +".json");
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
            System.out.println(grapheme +" "+ json.getString(grapheme));
            try {
                System.out.println(PhonemesFinder.findIfUnique(word, grapheme, json.getString(grapheme)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void createFile(){
        if(Files.exists(Path.of("C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-1.0.txt"))){
            File file = new File("C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-1.0.txt");
            File file2 = new File("C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-correspondances-1.0.txt");
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                FileWriter fileWriter = new FileWriter(file2);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                String nextLine;
                String previousLine = "";
                while(( nextLine = bufferedReader.readLine()) != null){
                    String toWrite = "#"+nextLine.split("\\|")[0]+"#|"+nextLine.split("\\|")[3].replace(".", "");
                    if(!toWrite.endsWith("|") && !previousLine.contains(toWrite)){
                        bw.write(toWrite+"\n");
                    }
                    previousLine = toWrite;
                }
                bw.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
