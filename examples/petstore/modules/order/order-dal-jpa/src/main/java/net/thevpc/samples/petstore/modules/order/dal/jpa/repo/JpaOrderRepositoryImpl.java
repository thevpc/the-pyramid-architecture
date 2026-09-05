package net.thevpc.samples.petstore.modules.order.dal.jpa.repo;

import net.thevpc.samples.petstore.modules.order.dal.api.OrderRepository;
import net.thevpc.samples.petstore.modules.order.dal.jpa.entity.JpaOrderEntity;
import net.thevpc.samples.petstore.modules.order.dal.jpa.entity.JpaOrderItemEntity;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderItem;
import net.thevpc.samples.petstore.modules.order.infra.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaOrderRepositoryImpl implements OrderRepository {

    private final SpringDataOrderRepository springRepo;

    @Autowired
    public JpaOrderRepositoryImpl(SpringDataOrderRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public Order save(Order order) {
        JpaOrderEntity entity = toEntity(order);
        JpaOrderEntity saved = springRepo.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Order> findById(String id) {
        return springRepo.findById(id).map(this::toModel);
    }

    @Override
    public List<Order> findAll() {
        return springRepo.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return springRepo.findByStatus(status.name()).stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(String id) {
        if (springRepo.existsById(id)) {
            springRepo.deleteById(id);
            return true;
        }
        return false;
    }

    private JpaOrderEntity toEntity(Order order) {
        List<JpaOrderItemEntity> itemEntities = order.getItems() != null ? order.getItems().stream()
                .map(i -> new JpaOrderItemEntity(i.getPetId(), i.getQuantity(), i.getPrice()))
                .collect(Collectors.toList()) : List.of();
        String status = order.getStatus() != null ? order.getStatus().name() : OrderStatus.PLACED.name();
        return new JpaOrderEntity(order.getId(), order.getCustomerId(), itemEntities, status, order.getTotalAmount(), order.getOrderDate());
    }

    private Order toModel(JpaOrderEntity e) {
        List<OrderItem> items = e.getItems() != null ? e.getItems().stream()
                .map(i -> new OrderItem(i.getPetId(), i.getQuantity(), i.getPrice()))
                .collect(Collectors.toList()) : List.of();
        OrderStatus status = e.getStatus() != null ? OrderStatus.valueOf(e.getStatus()) : OrderStatus.PLACED;
        return new Order(e.getId(), e.getCustomerId(), items, status, e.getTotalAmount(), e.getOrderDate());
    }
}
