package net.thevpc.samples.petstore.modules.order.dal.jpa.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ord_order")
public class JpaOrderEntity {

    @Id
    private String id;

    private String customerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ord_order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<JpaOrderItemEntity> items = new ArrayList<>();

    private String status;

    private BigDecimal totalAmount;

    private Instant orderDate;

    public JpaOrderEntity() {
    }

    public JpaOrderEntity(String id, String customerId, List<JpaOrderItemEntity> items, String status, BigDecimal totalAmount, Instant orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.items = items != null ? items : new ArrayList<>();
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<JpaOrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<JpaOrderItemEntity> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }
}
