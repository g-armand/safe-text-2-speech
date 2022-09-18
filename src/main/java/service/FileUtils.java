package service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileUtils {
    public static Set<String> readFileToList(String path, boolean simplify) throws IOException {
        Set<String> output = new HashSet<>();
        File file = new File(path);
        if(Files.exists(file.toPath())){
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String nextLine;
            if (simplify){
                while ((nextLine = bufferedReader.readLine()) != null) {
                    nextLine = nextLine
                            .replace("ɛ̃", "1")
                            .replace("ẽ", "1")
                            .replace("ɛ", "e")
                            .replace("ə", "e")
                            .replace("ɑ̃", "3")
                            .replace("ã", "3")
                            .replace("ɑ", "a")
                            .replace("ø", "œ")
                            .replace("ɔ̃", "2")
                            .replace("õ", "2")
                            .replace("ɔ", "o")
                            .replace("œ̃", "4");
                    output.add(nextLine);
                }
            } else {
                while ((nextLine = bufferedReader.readLine()) != null) {;
                    nextLine = nextLine.replace("ɛ̃", "1")
                            .replace("ɔ̃", "2")
                            .replace("ɑ̃", "3")
                            .replace("œ̃", "4");
                    output.add(nextLine);
                }
            }
        }
        return output;
    }

    public static HashMap<String, List<String>> readFileToJson(String path, boolean simplify) throws IOException {
        String text;
        HashMap<String, List<String>> output = new HashMap<>();
        if (simplify){
            text = Files.readString(Paths.get(path)).replace("ẽ", "1")
                    .replace("õ", "2")
                    .replace("ã", "3")
                    .replace("œ̃", "4");;
        } else {
            text = Files.readString(Paths.get(path)).replace("ɛ̃", "1")
                    .replace("ɔ̃", "2")
                    .replace("ɑ̃", "3")
                    .replace("œ̃", "4");
        }

        JSONObject json = new JSONObject(text);
        json = new JSONObject(text);

        JSONObject finalJson = json;
        JSONObject finalJson1 = json;
        json.keySet().forEach(key -> {
            JSONArray array = finalJson1.getJSONArray(key);
            List<String> listToAssign = new ArrayList<String>();
            array.forEach(value -> listToAssign.add(value.toString()));
            output.put(key, listToAssign);
    });
    return output;
    }
}
