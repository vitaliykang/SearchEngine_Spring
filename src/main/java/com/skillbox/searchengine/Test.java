package com.skillbox.searchengine;

import com.skillbox.searchengine.entity.Index;
import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.repository.CustomRepository;
import com.skillbox.searchengine.utils.PageProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

import static com.skillbox.searchengine.utils.PageProcessor.generateMap;


public class Test {
    public static void main(String[] args) throws Exception{
        Document doc = Jsoup.connect("https://skillbox.ru/").get();
        Map<Lemma, Double> map = generateMap(doc);
        map.forEach((k, v) -> {
            System.out.printf("%s - %f%n", k.getLemma(), v);
        });
    }

    private static void flush() {
        CustomRepository.dropTable(Lemma.class);
        CustomRepository.dropTable(Page.class);
        CustomRepository.dropTable(Index.class);
    }

    public static <T> void test(Class<T> tClass) {
        System.out.println(tClass.getSimpleName());
    }
}
