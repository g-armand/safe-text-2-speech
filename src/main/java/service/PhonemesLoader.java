package service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PhonemesLoader {

    public HashMap<String, HashMap<String, List<String>>> graphemesMap;
    public List<String> graphemesSortedList;
    public static String defaultLanguage = "fr";
    public HashMap<String, Float> frequencies;

    public PhonemesLoader(){
        try {
            this.graphemesMap = loadPhonemes(defaultLanguage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.graphemesSortedList = this.graphemesMap.keySet()
                .stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList();
        try {
            this.frequencies = loadFrequencies();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, List<String>> loadPhonemesSimple(String affixType, boolean simplify) throws IOException {
        HashMap<String, List<String>> output = new HashMap<>();
        JSONObject json = null;
        String path;
        if (simplify){
            path = "C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\simplified\\g2p_wiki_"+affixType+"_withdoublons-simplified.json";
        } else {
            path = "src/main/java/resources/g2p_wiki_"+ affixType +"_withdoublons.json";
        }
        if(Files.exists(Path.of(path))){
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String jsonText = bufferedReader.readLine();
            String nextLine;
            while(( nextLine = bufferedReader.readLine()) != null){
                if(simplify){
                    nextLine = nextLine.replace("ẽ", "1")
                            .replace("õ", "2")
                            .replace("ã", "3")
                            .replace("œ̃", "4");
                } else {
                    nextLine = nextLine.replace("ɛ̃", "1")
                            .replace("ɔ̃", "2")
                            .replace("ɑ̃", "3")
                            .replace("œ̃", "4");
                }
                jsonText += nextLine;
            }
            String text = jsonText.toString();
            json = new JSONObject(text);
        }
        JSONObject finalJson = json;
        if(Objects.nonNull(json)){
            json.keySet().forEach(key -> {
                JSONArray array = finalJson.getJSONArray(key);
                List<String> listToAssign = new ArrayList<String>();
                array.forEach(value -> listToAssign.add(value.toString()));
                output.put(key, listToAssign);
            });
        }

        return output;
    }

    public HashMap<String, HashMap<String, List<String>>> loadPhonemes(String language) throws IOException {
        HashMap<String,HashMap<String, List<String>>> output = new HashMap<>();
        JSONObject json = null;
        if(Files.exists(Path.of("src/main/java/resources/"+ language +"_graphemes_to_phonemes.json"))){
            File file = new File("src/main/java/resources/"+ language +"_graphemes_to_phonemes.json");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String jsonText = bufferedReader.readLine();
            String nextLine;
            while(( nextLine = bufferedReader.readLine()) != null){
                jsonText += nextLine;
            }
            String text = jsonText.toString();
            json = new JSONObject(text);
        }
        if (Objects.nonNull(json)){
            JSONObject finalJson = json;
            json.keySet().forEach(character -> {
                finalJson.getJSONObject(character).keySet().forEach(grapheme -> {
                    HashMap<String, List<String>> graphemeMap = new HashMap<>();
                    finalJson.getJSONObject(character).getJSONObject(grapheme).keySet().forEach(phoneme -> {
                        List<String> exampleList = finalJson.getJSONObject(character).getJSONObject(grapheme).getJSONArray(phoneme)
                                .toList()
                                .stream()
                                .map(String::valueOf)
                                .toList();
                        graphemeMap.put(phoneme, exampleList);
                    });
                    output.put(grapheme, graphemeMap);
                });
            });
        }
        return output;
    }


    public HashMap<String, Float> loadFrequencies() throws IOException {
        HashMap<String, Float> frequencies = new HashMap<>();
        if(Files.exists(Path.of("src/main/java/resources/fr_phonemes_frequency.json"))){
            File file = new File("src/main/java/resources/fr_phonemes_frequency.json");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            String jsonText = "";
            while((line = bufferedReader.readLine())!=null){
                jsonText += line;
            }
            JSONObject json = new JSONObject(jsonText);
            json.keySet().stream().forEach(phoneme -> frequencies.put(phoneme, json.getFloat(phoneme)));
        }
        return frequencies;
    }

    public void computeFrequencies() throws IOException {
        List<String> phonemes = List.of("a","ʁ","l","e","s","i","ə","t","k","p","d","m","ɑ̃","n","u","v","o","u","ɔ̃","ø","ɔ","ɛ̃","f","j","w","ɥ","z","ʃ","œ̃","œ","g","ɑ","ɲ","ks","h","x","wa","ŋ","ʒ","ɛ","ε","b","dʒ","y");
        List<String> longPhonemes = List.of("ɑ̃","ɔ̃","ɛ̃","œ̃","ks","wa","dʒ");
        HashMap<String, Integer> phonemeMap = new HashMap<>();
        phonemes.forEach(phoneme -> phonemeMap.put(phoneme, 0));
        if(Files.exists(Path.of("C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-phonemes-1.0.txt"))){
            File file = new File("C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-phonemes-1.0.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String jsonText = bufferedReader.readLine();
            while((jsonText = bufferedReader.readLine())!=null){
                String finalJsonText = jsonText;
                longPhonemes.forEach(phoneme -> {
                    Integer count  = (finalJsonText.length() - finalJsonText.replace(phoneme, "").length())/2;
                    phonemeMap.put(phoneme, phonemeMap.get(phoneme) + count);
                    finalJsonText.replaceAll(phoneme, "");
                });
                Arrays.stream(finalJsonText.split("")).forEach(phoneme -> {
                    if (phonemes.contains(phoneme)){
                        phonemeMap.put(phoneme, phonemeMap.get(phoneme)+1);
                    } else {
                        System.out.println(phoneme);
                    }
                });
            }
            System.out.println(phonemeMap);
            AtomicInteger sum = new AtomicInteger(0);
            phonemeMap.values().forEach(val -> sum.set(sum.get()+val));
            phonemeMap.keySet()
                    .stream()
                    .sorted(Comparator.comparingInt(phonemeMap::get).reversed())
                    .forEach(phoneme -> System.out.println(phoneme
                    + " : "
                    + Float.valueOf(phonemeMap.get(phoneme))*100.0/Float.valueOf(sum.get())
                    + " : "
                    + phonemeMap.get(phoneme)
                    + "/"
                    + sum.get()));
        }
    }

    public void addCustomRules(Set<String> transcripts) throws IOException {
        File file = new File("src/main/java/resources/custom_graphemes_to_phonemes.json");
        JSONObject json;
        if(Files.exists(file.toPath())) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String jsonText = bufferedReader.readLine();
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                jsonText += nextLine;
            }
            json = new JSONObject(jsonText);
        } else {
            json = new JSONObject();
        }
        transcripts.forEach(transcript -> {
            if (!json.keySet().contains(transcript)){
                json.put(transcript.split("\\|")[0],transcript.split("\\|")[1]);
            }
        });
        String a = json.toString();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(json.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
