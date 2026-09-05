package net.thevpc.samples.petstore.modules.order.infra;

import java.math.BigDecimal;

public class OrderItem {
    private String petId;
    private int quantity = 1;
    private BigDecimal price;

    public OrderItem() {
    }

    public OrderItem(String petId, int quantity, BigDecimal price) {
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
