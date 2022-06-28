package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.entity.Site;
import lombok.Data;

@Data
public class AddressUtility {
    private String url;

    private String path;
    private String root;
    private String siteName;

    public AddressUtility (String url) {
        this.url = url;
        parse();
    }

    private void parse() {
        int index = 0;
        for (int i = 0; i < 3; i++) {
            index = url.indexOf('/', index + 1);
        }

        path = url.substring(index);
        root = url.substring(0, index+1);

        //site name
        index = 0;
        for (int i = 0; i < 2; i++) {
            index = root.indexOf('/', index + 1);
        }
        StringBuilder sb = new StringBuilder(root.substring(index+1));
        sb.deleteCharAt(sb.length() - 1);
        siteName = sb.toString();
    }
}
