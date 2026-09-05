package net.thevpc.samples.petstore.modules.catalog.infra;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Pet {
    private String id;
    private String name;
    private Category category;
    private List<Tag> tags = new ArrayList<>();
    private PetStatus status = PetStatus.AVAILABLE;
    private BigDecimal price;

    public Pet() {
    }

    public Pet(String id, String name, Category category, PetStatus status, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status != null ? status : PetStatus.AVAILABLE;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public PetStatus getStatus() {
        return status;
    }

    public void setStatus(PetStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
