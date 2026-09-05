package net.thevpc.samples.petstore.modules.order.dal.jpa.entity;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class JpaOrderItemEntity {
    private String petId;
    private int quantity;
    private BigDecimal price;

    public JpaOrderItemEntity() {
    }

    public JpaOrderItemEntity(String petId, int quantity, BigDecimal price) {
        this.petId = petId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
