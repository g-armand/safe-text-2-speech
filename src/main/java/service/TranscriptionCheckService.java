package service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class TranscriptionCheckService {

    @Test
    public String getTranscription() throws IOException {
        Document doc = Jsoup.connect("https://fr.wiktionary.org/wiki/linguistique").get();
        String transcript = doc.select("span.API").first().text();
        return transcript;
    }

}
