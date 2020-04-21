package ml.chiragkhandhar.newsgateway;

import java.io.Serializable;

class Article implements Serializable
{
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    Article()
    {
        author = "";
        title = "";
        description = "";
        url = "";
        urlToImage = "";
        publishedAt = "";
    }

    String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = removeRegex(description);
    }

    String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    String getUrlToImage() {
        return urlToImage;
    }

    void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    String getPublishedAt() {
        return publishedAt;
    }

    void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    private String removeRegex(String s)
    {
        return s.replaceAll("\\<(/?[^\\>]+)\\>", "\\ ").replaceAll("\\s+", " ").trim();
    }

}
