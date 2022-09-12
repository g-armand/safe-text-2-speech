import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    public void testTranslate(){
        List<String> result = translationService.translate("lol");
        result.forEach(System.out::println);
    }

    @Test
    public void testTranslateAndFrequencies(){
        List<String> result = translationService.translate("lesnazi");
        result.stream()
                .sorted(Comparator.comparingDouble(translationService::computeProbability).reversed())
                .forEach(System.out::println);
    }

    @Test
    public void testFindIfUnique() throws IOException {
        System.out.println(PhonemesFinder.findIfUnique("suffix", "aim", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "aims", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ain", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ainc", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "aincs", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "aing", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "aings", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ains", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "aint", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "aints", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ein", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "eins", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "eing", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "eings", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "eint", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "eints", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "en", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ens", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ent", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "in", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "inct", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "incts", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ingt", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ingts", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "inq", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ins", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "int", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "înt", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "ym", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "yms", "ɛ̃"));
        System.out.println(PhonemesFinder.findIfUnique("suffix", "yn", "ɛ̃"));    }

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
