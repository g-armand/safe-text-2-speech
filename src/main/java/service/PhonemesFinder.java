package service;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class PhonemesFinder {


    public static void findPhonemesSuffixLighter() throws IOException {
        HashMap <String, List<String>> suffixes = PhonemesLoader.loadPhonemesSimple("suffix", true);
        List<String> suffixesSortedKeys = suffixes.keySet().stream().sorted((a,b) -> Integer.compare(b.length(), a.length())).toList();
//        HashMap <String, List<String>> prefixes = PhonemesLoader.loadPhonemesSimple("prefix");
//        HashMap <String, List<String>> infixes = PhonemesLoader.loadPhonemesSimple("infix");
        String path = "C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-correspondances-1.0.txt";
        Set<String> fileLines = FileUtils.readFileToList(path, true);
        fileLines.stream().forEach(line-> {
            String lineGrapheme = line.split("\\|")[0];
            String linePhoneme = line.split("\\|")[1];
            try{
                String ruleApplied = suffixesSortedKeys
                        .stream()
                        .filter(suffix -> lineGrapheme.endsWith(suffix))
                        .filter(suffix -> suffixes.get(suffix)
                                .stream()
                                .anyMatch(linePhoneme::endsWith))
                        .map(suffix -> suffix +"|"+suffixes.get(suffix)
                                .stream()
                                .filter(linePhoneme::endsWith)
                                .findFirst().orElse(""))
                        .findFirst()
                        .get();
                try{
                    String remainingGrapheme = lineGrapheme.substring(0, lineGrapheme.length() - ruleApplied.split("\\|")[0].length());
                    String remainingPhoneme = linePhoneme.substring(0, linePhoneme.length() - ruleApplied.split("\\|")[1].length());
                    try {
                        boolean a = findIfUnique("prefix", remainingGrapheme, remainingPhoneme);
                        if (a) {
//                            System.out.println("new prefix : "
//                                    + remainingGrapheme
//                                    + " -> "
//                                    + remainingPhoneme
//                                    +"\tfrom "
//                                    + line
//                                    + " <-> "
//                                    + ruleApplied);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch(ArrayIndexOutOfBoundsException e){
                    ;
                }
            } catch(NoSuchElementException e){
                System.out.println("No existing rule for suffix of: " + lineGrapheme);
            }
        });
    }


    public static void findPhonemesPrefixLighter() throws IOException {
        HashMap <String, List<String>> prefixes = PhonemesLoader.loadPhonemesSimple("prefix", true);
        List<String> suffixesSortedKeys = prefixes.keySet().stream().sorted((a,b) -> Integer.compare(b.length(), a.length())).toList();
//        HashMap <String, List<String>> prefixes = PhonemesLoader.loadPhonemesSimple("prefix");
//        HashMap <String, List<String>> infixes = PhonemesLoader.loadPhonemesSimple("infix");
        String path ="C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\simplified\\psychoGLAFF-correspondances-simplified-1.0.txt";
        Set<String> fileLines = FileUtils.readFileToList(path, true);
        fileLines.stream().forEach(line-> {
            String lineGrapheme = line.split("\\|")[0];
            String linePhoneme = line.split("\\|")[1];
            try{
                String ruleApplied = suffixesSortedKeys
                        .stream()
                        .filter(prefix -> lineGrapheme.startsWith(prefix))
                        .filter(prefix -> prefixes.get(prefix)
                                .stream()
                                .anyMatch(linePhoneme::startsWith))
                        .map(prefix -> prefix +"|"+ prefixes.get(prefix)
                                .stream()
                                .filter(linePhoneme::startsWith)
                                .findFirst().orElse(""))
                        .findFirst()
                        .get();
                try{
                    String remainingGrapheme = lineGrapheme.substring(ruleApplied.split("\\|")[0].length());
                    String remainingPhoneme = linePhoneme.substring(ruleApplied.split("\\|")[1].length());
                    try {
                        boolean a = findIfUnique("suffix", remainingGrapheme, remainingPhoneme);
//                        if (a) {
//                            System.out.println("new suffix : "
//                                    + remainingGrapheme
//                                    + " -> "
//                                    + remainingPhoneme
//                                    +"\tfrom "
//                                    + line
//                                    + " <-> "
//                                    + ruleApplied);
//                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch(ArrayIndexOutOfBoundsException ignored){
                    ;
                }
            } catch(NoSuchElementException e){
                System.out.println("No existing rule for prefix of: " + lineGrapheme);
            }
        });
    }


    public static void findPhonemes() throws IOException {
        HashMap<String,String> rules = new HashMap<>();
        HashMap<String,String> singleRules = new HashMap<>();
        Set<String> alreadyTested = new HashSet();
        AtomicInteger count  = new AtomicInteger(0);
        rules.put("de#", "d");
        File file = new File("C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-correspondances-1.0.txt");
        Set<String> fileLines = new HashSet<>();
        if(Files.exists(file.toPath())) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                fileLines.add(nextLine);
                System.out.println(nextLine);
            }
        }
        fileLines.stream().forEach(line -> {
            System.out.println("working on line n°"+count.getAndIncrement()+ " of "+ fileLines.size());
            if (count.get() == 1500){
                System.out.println("");
            }
            AtomicBoolean isModified = new AtomicBoolean(false);
            List<String> affixList = new ArrayList<>(rules.keySet()
                    .stream()
                    .filter(rule -> !rule.contains("#"))
                    .filter(rule -> !rule.contains("-"))
                    .sorted(Comparator.comparingInt(String::length))
                    .toList());
            List<String> otherRulesList = rules.keySet()
                    .stream()
                    .filter(rule -> rule.contains("#") || rule.contains("-"))
                    .sorted(Comparator.comparingInt(String::length))
                    .toList();
            affixList.addAll(otherRulesList);
            List<String> finalRulesToTry = affixList.stream()
                    .map(rule -> {
                        List<String> finalRules = new ArrayList<>();
                        String grapheme = rule;
                        String phoneme = rules.get(rule);
                        String type = "";
                        if(!rule.contains("#")
                            && !rule.contains("-")
                            && line.split("\\|")[0].contains(rule)){
                            isModified.set(true);
                            int graphemePartsLen= line.split("\\|")[0].split(rule).length;
                            int phonemePartsLen = line.split("\\|")[1].split(rule).length;
                            if(graphemePartsLen==phonemePartsLen){
                                IntStream.range(0,graphemePartsLen).forEach(index ->
                                        finalRules.add(
                                                line.split("\\|")[0].split(rule)[index]
                                                + "|"
                                                + line.split("\\|")[1].split(phoneme)[index]));
                            }
                        } else if (rule.endsWith("-")
                                && rule.startsWith("-")
                                && line.contains(rule.substring(1, rule.length() - 1))
                                && !line.contains("#"+rule.substring(1, rule.length() - 1))
                                && !line.contains(rule.substring(1, rule.length() - 1)+"#")) {
                            int graphemePartsLen= line.split("\\|")[0].split(rule.substring(1, rule.length() - 1)).length;
                            int phonemePartsLen = line.split("\\|")[1].split(phoneme).length;
                            if(graphemePartsLen == phonemePartsLen
                                && graphemePartsLen >= 2
                                && phonemePartsLen >= 2){
                                isModified.set(true);
                                System.out.println("1: "+line.split("\\|")[0].split(rule.substring(1, rule.length() - 1)).toString());
                                System.out.println("2: "+line);
                                System.out.println("3: "+rule);
                                System.out.println("4: "+rule.substring(1, rule.length() - 1));
                                finalRules.add(
                                        line.split("\\|")[0].split(rule.substring(1, rule.length() - 1))[0]
                                                + "|"
                                                + line.split("\\|")[1].split(phoneme)[0]);
                                finalRules.add(
                                        line.split("\\|")[0].split(rule.substring(1, rule.length() - 1))[1]
                                                + "|"
                                                + line.split("\\|")[1].split(phoneme)[1]);
                            }
                        } else if (rule.startsWith("#")
                                && line.split("\\|")[0].startsWith(rule)
                                && !line.split("\\|")[0].contentEquals(rule+"#")) {
                            isModified.set(true);
                            finalRules.add(line.split("\\|")[0].substring(rule.length())
                                    + "|"
                                    + line.split("\\|")[1].substring(rules.get(rule).length()));
                        } else if (rule.endsWith("#")
                                && line.split("\\|")[0].endsWith(rule)
                                && !line.split("\\|")[0].contentEquals("#" + rule)) {
                            isModified.set(true);
                            finalRules.add(line.split("\\|")[0].substring(0, line.split("\\|")[0].length() - rule.length())
                                    + "|"
                                    + line.split("\\|")[1].substring(0, line.split("\\|")[1].length() - rules.get(rule).length()));
                        }
                        return finalRules;
                    })
                    .filter(rule -> rule.size() > 0)
                    .flatMap(Collection::stream)
                    .toList();
            System.out.println("finalRulesToTry: " + finalRulesToTry +" "+ line);
            if(!isModified.get()){
                singleRules.put(line.split("\\|")[0], line.split("\\|")[1]);
                System.out.println("new single rule: "+line.split("\\|")[0] + "|" + line.split("\\|")[1]);
                alreadyTested.add(line.split("\\|")[0] + "|" + line.split("\\|")[1]);
            } else {
                finalRulesToTry = finalRulesToTry.stream()
                        .map(rule -> rule.replace("#", ""))
                        .map(rule -> rule.replace("-", ""))
                        .map(PhonemesFinder::getAllSplits)
                        .flatMap(List::stream)
                        .toList();
                finalRulesToTry.stream()
                        .filter(rule -> rule.split("\\|").length == 2)
                        .filter(rule -> !alreadyTested.contains(rule))
                        .forEach(finalRule -> {
                            String phoneme = finalRule.split("\\|")[1];
                            String grapheme = finalRule.split("\\|")[0];
                            if(finalRule.length() > 0){
                                try {
                                    if(findIfUnique("affix", grapheme, phoneme)){
                                        rules.put(grapheme, phoneme);
                                        System.out.println("new affix rule: "+grapheme+"|"+ phoneme);
                                        alreadyTested.add(grapheme+"|"+ phoneme);
                                    } else {
                                        if(findIfUnique("prefix", "#"+grapheme, phoneme)){
                                            rules.put("#"+grapheme, phoneme);
                                            System.out.println("new prefix rule: "+"#"+grapheme+"|"+ phoneme);
                                        }
                                        if(findIfUnique("suffix", grapheme+"#", phoneme)){
                                            rules.put(grapheme+"#", phoneme);
                                            System.out.println("new suffix rule: "+grapheme+"#" + "|" + phoneme);
                                        }
                                        if(findIfUnique("infix", grapheme, phoneme)){
                                            rules.put("-"+grapheme+"-", phoneme);
                                            System.out.println("new infix rule: "+"-"+grapheme+"-" + "|"+ phoneme);
                                        }
                                        alreadyTested.add(grapheme+"|"+ phoneme);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
            }
        });
        System.out.println(rules);
        System.out.println(singleRules.size());

    }
    public static boolean findIfUnique(String type, String grapheme, String phoneme) throws IOException {
        String path = "C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\simplified\\psychoGLAFF-correspondances-simplified-1.0.txt";
        Set<String> lines = FileUtils.readFileToList(path, true);
        return findIfUnique(type, grapheme, phoneme, lines);
    }

    public static boolean findIfUnique(String type, String grapheme, String phoneme, Set<String> lines) throws IOException {
        grapheme = grapheme.replace("#", "");
        grapheme = grapheme.replace("-", "");
        if(grapheme.length() == 0 || phoneme.length() == 0){
            return false;
        }
        String path = "C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\simplified\\psychoGLAFF-correspondances-simplified-1.0.txt";
        HashSet<String> otherCorrespondances = new HashSet<>();
        boolean isUnique = true;
        switch (type) {
            case "infix":
                for (String nextLine: lines) {
                    if (nextLine.split("\\|")[0].contains(grapheme)
                            && !nextLine.split("\\|")[0].contains("#" + grapheme)
                            && !nextLine.split("\\|")[0].contains(grapheme + "#")){
                        if (!nextLine.split("\\|")[1].contains(phoneme)){
                            otherCorrespondances.add(nextLine);
                            isUnique =  false;
                        }
                    }
                }
            case "prefix":
                for (String nextLine: lines) {
                    if (nextLine.split("\\|")[0].startsWith(grapheme)) {
                        if (!nextLine.split("\\|")[1].startsWith(phoneme)){
                            otherCorrespondances.add(nextLine);
                            isUnique = false;
                        }
                    }
                }
            case "suffix":
                for (String nextLine: lines) {
                    if (nextLine.split("\\|")[0].endsWith(grapheme)) {
                        if(!nextLine.split("\\|")[1].endsWith(phoneme)){
                            otherCorrespondances.add(nextLine);
                            isUnique = false;
                        }
                    }
                }
            case "affix":
                for (String nextLine: lines) {
                    if (nextLine.split("\\|")[0].contains(grapheme)) {
                        if(!nextLine.split("\\|")[1].contains(phoneme)){
                            otherCorrespondances.add(nextLine);
                            isUnique = false;
                        }
                    }
                }
        }
        if(!isUnique && otherCorrespondances.size()<5) {
            //phonemesLoader.addCustomRules(otherCorrespondances);
            isUnique = true;
        } else if (!isUnique&& otherCorrespondances.size()<100){
            System.out.println("few exceptions for "+ grapheme + " "+ phoneme + " " + "\n" + otherCorrespondances);
        } else if (!isUnique&& otherCorrespondances.size()>100){
            System.out.println("check the "+ grapheme +" rule better !");
        }
        return isUnique;
    }

    public static List<String> getAllSplits(String input){
        List<String> output = new ArrayList<>();
        output.add(input);
        String[] array = input.split("\\|");
        if (array.length==2 && array[0].length() == array[1].length()){
            IntStream.range(0,array[0].length()-1)
                    .forEach(len -> IntStream.range(0,array[0].length()-len)
                            .forEach(startIndex -> output.add(
                                      array[0].substring(startIndex, startIndex+len+1)
                                    + "|"
                                    + array[1].substring(startIndex, startIndex+len+1)))
                    );
        }
        return output;
    }

    public static void findPhonemesInfix() throws IOException {
        HashMap <String, List<String>> prefixes = PhonemesLoader.loadPhonemesSimple("prefix", true);
        List<String> prefixesSortedKeys = prefixes.keySet().stream().sorted((a,b) -> Integer.compare(b.length(), a.length())).toList();
        HashMap <String, List<String>> suffixes = PhonemesLoader.loadPhonemesSimple("suffix", true);
        List<String> suffixesSortedKeys = suffixes.keySet().stream().sorted((a,b) -> Integer.compare(b.length(), a.length())).toList();
        String path = "C:\\Users\\garri\\Desktop\\PsychoGLAFF-txt\\PsychoGLAFF-txt\\psychoGLAFF-correspondances-1.0.txt";
        Set<String> fileLines = FileUtils.readFileToList(path, true);
        Set<String> newRules = new HashSet<>();
        fileLines.forEach(line ->{
            String grapheme = line.split("\\|")[0];
            String phoneme = line.split("\\|")[1];
            String suffixRuleApplied;
            String prefixRuleApplied;
            try {
                suffixRuleApplied = suffixesSortedKeys
                        .stream()
                        .filter(grapheme::endsWith)
                        .filter(suffix -> suffixes.get(suffix)
                                .stream()
                                .anyMatch(phoneme::endsWith))
                        .map(suffix -> suffix + "|" + suffixes.get(suffix)
                                .stream()
                                .filter(phoneme::endsWith)
                                .findFirst().orElse(""))
                        .findFirst()
                        .get();
                String tempGrapheme = grapheme.substring(0, grapheme.length() - suffixRuleApplied.split("\\|")[0].length());
                String tempPhoneme = phoneme.substring(0, phoneme.length() - suffixRuleApplied.split("\\|")[1].length());
                prefixRuleApplied = prefixesSortedKeys
                        .stream()
                        .filter(tempGrapheme::startsWith)
                        .filter(prefix -> prefixes.get(prefix)
                                .stream()
                                .anyMatch(tempPhoneme::startsWith))
                        .map(prefix -> prefix + "|" + prefixes.get(prefix)
                                .stream()
                                .filter(tempPhoneme::startsWith)
                                .findFirst().orElse(""))
                        .findFirst()
                        .get();
                String remainingGrapheme = tempGrapheme.substring(prefixRuleApplied.split("\\|")[0].length());
                String remainingPhoneme = tempPhoneme.substring(prefixRuleApplied.split("\\|")[1].length());
                if(!newRules.contains(remainingGrapheme+"|"+remainingPhoneme)){
                    boolean unique = findIfUnique("infix", remainingGrapheme, remainingPhoneme, fileLines);
                    if (unique){
                        newRules.add(remainingGrapheme+"|"+remainingPhoneme);
                        System.out.println(remainingGrapheme + " -> " + remainingPhoneme + " unique: " + unique);
                    }
                }
            } catch(NoSuchElementException | IOException e) {
                System.out.println("Could not process " + line);
            }
        });
    }

}
