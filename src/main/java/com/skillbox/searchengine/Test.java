package com.skillbox.searchengine;

import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.WebsiteIndex;
import com.skillbox.searchengine.repository.CustomRepository;


public class Test {
    public static void main(String[] args) throws Exception{
        String f_word = "\"тест\"";
        String t_word = "т123ест123";
        System.out.println(cleaner(f_word));
    }

    private static void check(String word) {
        System.out.println( word.matches("[а-яА-Я0-9]*") );
    }

    private static String cleaner(String word) {
        return word.replaceAll("[^a-zA-Zа-яА-Я0-9]", "");
    }
}
