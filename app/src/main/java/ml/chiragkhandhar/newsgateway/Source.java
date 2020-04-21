package ml.chiragkhandhar.newsgateway;

import androidx.annotation.NonNull;

import java.io.Serializable;

class Source implements Serializable
{
    private String id;
    private String name;
    private String category;

    Source()
    {
        id = "";
        name = "";
        category = "";
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getCategory() {
        return category;
    }

    void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    public String toString() {
        return name;
    }
}
