package de.uulm.einhoernchen.flashcardsapp.Model;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class Category {

    private long id;
    private long parentId;
    private String name;
    private String description;

    public Category(long id, long parentId, String name, String description) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
    }

    // Construct for jsons parser
    public Category(long id, long parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
