package com.skillbox.searchengine.entity;

import com.skillbox.searchengine.repository.SiteRepository;
import com.skillbox.searchengine.utils.AddressUtility;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.persistence.*;

@Entity
@Table(name = "page")
public class Page implements BaseEntity{
    private static final String SQL_PARAMS = "page (code, content, path) VALUES ";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Lob
    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    @Getter @Setter
    private Site site;

    public Page() {}

    public Page(String url, int code) {
        this.code = code;
        AddressUtility addressUtility = new AddressUtility(url);
        site = SiteRepository.get(url);
        path = addressUtility.getPath();
    }

    /**
     * Parses content and returns it as a Jsoup Document.
     * @return content as a Jsoup Document.
     */
    public Document getDocument() {
        return Jsoup.parse(content);
    }

    //getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}