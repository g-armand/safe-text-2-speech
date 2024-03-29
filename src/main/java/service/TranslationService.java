package service;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TranslationService {

    public PhonemesLoader phonemesLoader = new PhonemesLoader();

    private Set<String> translationsSet;

    public TranslationService(){
        try {
            this.translationsSet = FileUtils.readFileToList("src/main/java/resources/fre_latn_broad.txt", false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String translate(String input){
        String output = "";
        List<String> phonemesList = Arrays.stream(input.split("\s"))
                .map(word ->{
                    String toAdd = this.translationsSet.stream()
                            .filter(entry -> Objects.equals(entry.split("\t")[0], word))
                            .map(entry -> entry.split("\t")[1])
                            .findFirst()
                            .orElse("");
                    return toAdd;
                })
                .collect(Collectors.toList());
        output = String.join("", phonemesList);
        return output;
    }

    public List<String> litteralTranslation(String input){
        List<String> output = new ArrayList<>();
        HashMap<String, String> possibleOutputs = new HashMap<>();
        possibleOutputs.put("", input);
        boolean endLoop = false;
        while(!endLoop){
            HashMap<String, String> tempMap = new HashMap<>();
            HashMap<String, String> finalPossibleOutputs = possibleOutputs;
            possibleOutputs.entrySet().stream().forEach(entry -> {
                phonemesLoader.graphemesSortedList.forEach(grapheme -> {
                    boolean isForeign = false;
                    if (grapheme.startsWith("*")){
                        isForeign = true;
                        grapheme = grapheme.substring(1);
                    }
                    int graphemeSize = grapheme.length();
                    boolean found = false;
                    if(entry.getValue().startsWith(grapheme)){
                        phonemesLoader.graphemesMap.get(grapheme).keySet()
                            .forEach(phoneme ->
                                tempMap.put(
                                    entry.getKey() + phoneme,
                                    entry.getValue().substring(graphemeSize))
                            );
                    } else if(entry.getValue().length()==0){
                       tempMap.put(entry.getKey(), entry.getValue());
                    }
                });
            });
            if(tempMap.keySet().containsAll(finalPossibleOutputs.keySet())){
                endLoop = true;
            } else {
                possibleOutputs = tempMap;
            }
        }
        output = possibleOutputs.entrySet()
                .stream()
                .filter(entry -> entry.getValue().length()==0)
                .map(Map.Entry::getKey)
                .toList();
        return output;
    }

    public Double computeProbability(String input){
        Double output = 0.0;
        AtomicReference<Double> sum = new AtomicReference<>(0.0);
        AtomicReference<Integer> count = new AtomicReference<>(0);
        AtomicReference<String> processedInput = new AtomicReference<>(input);
        phonemesLoader.frequencies.keySet()
                .stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .forEach(phoneme -> {
                    int occurences = (processedInput.get().length() - processedInput.get().replaceAll(phoneme, "").length())/phoneme.length();
                    sum.set(sum.get() +phonemesLoader.frequencies.get(phoneme)*occurences);
                    count.set(count.get() + occurences);
                    processedInput.set(processedInput.get().replaceAll(phoneme, ""));
                });
        output = sum.get() /count.get();
        return output;
    }
}
