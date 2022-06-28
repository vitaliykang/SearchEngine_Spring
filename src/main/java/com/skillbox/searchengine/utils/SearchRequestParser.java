package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.FoundPage;
import com.skillbox.searchengine.entity.Lemma;
import com.skillbox.searchengine.entity.Page;
import com.skillbox.searchengine.entity.Site;
import com.skillbox.searchengine.entity.WebsiteIndex;
import com.skillbox.searchengine.repository.IndexRepository;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.SiteRepository;

import java.util.*;

public class SearchRequestParser {
    private String request;
    private Site site;

    public SearchRequestParser(String request, String url) {
        this.request = request;
        AddressUtility addressUtility = new AddressUtility(url);
        String siteName = addressUtility.getSiteName();
        site = SiteRepository.get(siteName);
    }

    public SearchRequestParser(String request, Site site) {
        this.request = request;
        this.site = site;
    }

    public void parse() {
        List<FoundPage> foundPages = new ArrayList<>();

        List<Lemma> sortedRequestLemmas = getSortedLemmas();
        List<Page> pages = getFilteredPages(sortedRequestLemmas);
        Map<Page, Double> pageAbsRankMap = getPageAbsRank(sortedRequestLemmas, pages);
        Map<Page, Double> pageCompRankMap = getPageCompRank(pageAbsRankMap);

        //Sorting the pages in descending order
        List<Map.Entry<Page, Double>> entryList = new ArrayList<>(pageCompRankMap.entrySet());
        entryList.sort(Map.Entry.<Page,Double>comparingByValue().reversed());

        entryList.forEach(entry -> {
            foundPages.add(new FoundPage(entry.getKey(), entry.getValue(), sortedRequestLemmas));
        });

        foundPages.forEach(System.out::println);
    }


    private Map<Page, Double> getPageCompRank(Map<Page, Double> pageAbsRankMap) {
        Map<Page, Double> pageCompRank = new HashMap<>();

        List<Double> absRankList = new ArrayList<>(pageAbsRankMap.values());
        Set<Double> absRanksSet = new HashSet<>(absRankList);
        absRankList = new ArrayList<>(absRanksSet);
        absRankList.sort(Collections.reverseOrder());

        //Absolute ranks and their comparative value
        Map<Double, Double> absCompRankMap = new HashMap<>();
        double absVal;
        try {
            absVal = absRankList.get(0);
        } catch (IndexOutOfBoundsException e) {
            return pageCompRank;
        }
        absRankList.forEach(absRank -> {
            double compRank = absRank / absVal;
            compRank = Math.round(compRank * 100d) / 100d;
            absCompRankMap.put(absRank, compRank);
        });

        //page and its comparative rank
        pageAbsRankMap.forEach((page, absRank) -> {
            Double compRank = absCompRankMap.get(absRank);
            pageCompRank.put(page, compRank);
        });

        return pageCompRank;
    }

    // Creates a map containing pages and their absolute ranks
    private Map<Page, Double> getPageAbsRank(List<Lemma> lemmas, List<Page> pages) {
        Map<Page, Double> pageRankMap = new HashMap<>();

        for (Page page : pages) {
            double absRank = 0;
            for (Lemma lemma : lemmas) {
                WebsiteIndex index = IndexRepository.findIndexByLemmaAndPage(lemma, page);
                absRank += index.getRank();
            }
            absRank = Math.round(absRank * 100d) / 100d;
            pageRankMap.put(page, absRank);
        }

        return pageRankMap;
    }

    /**
     * Returns a filtered list of pages where all the given lemmas were found.
     */
    private List<Page> getFilteredPages(List<Lemma> sortedLemmas) {
        List<Page> result = new ArrayList<>();
        Lemma rarestLemma;
        try {
            rarestLemma = sortedLemmas.get(0);
        } catch (IndexOutOfBoundsException e) {
            return result;
        }
        List<WebsiteIndex> indexList = IndexRepository.findIndexesByLemma(rarestLemma);

        for (int i = 1; i < sortedLemmas.size(); i++) {
            Lemma lemma = sortedLemmas.get(i);
            List<WebsiteIndex> sortedIndexes = new ArrayList<>();

            indexList.forEach(index -> {
                WebsiteIndex newIndex = IndexRepository.findIndexByLemmaAndPage(lemma, index.getPage());
                if (newIndex != null) {
                    sortedIndexes.add(newIndex);
                }
            });
            indexList = sortedIndexes;
        }

        indexList.forEach(index -> result.add(index.getPage()));
        return result;
    }

    //Breakes request into lemmas and sorts them in ascending order based on their ranks
    private List<Lemma> getSortedLemmas() {
        List<Lemma> requestLemmas = new ArrayList<>(LemmaCounter.countLemmas(request, site).keySet());
        List<Lemma> sortedLemmas = new ArrayList<>();
        for (Lemma requestLemma : requestLemmas){
            Lemma repoLemma = LemmaRepository.get(requestLemma.getLemma(), requestLemma.getSiteId());
            if (repoLemma == null) {
                continue;
            } else {
                sortedLemmas.add(repoLemma);
            }
        }
        Collections.sort(sortedLemmas);
        return sortedLemmas;
    }
}
