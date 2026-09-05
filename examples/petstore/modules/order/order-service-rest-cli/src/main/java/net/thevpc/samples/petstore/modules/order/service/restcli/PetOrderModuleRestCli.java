package net.thevpc.samples.petstore.modules.order.service.restcli;

import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderStatus;
import net.thevpc.samples.petstore.modules.order.service.api.PetOrderModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("petOrderModuleRestCli")
@Generated("pyramid")
public class PetOrderModuleRestCli implements PetOrderModule {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public PetOrderModuleRestCli(
            @Autowired(required = false) RestTemplate restTemplate,
            @Value("${petstore.modules.order.url:http://localhost:8080/api/orders}") String baseUrl) {
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
        this.baseUrl = baseUrl;
    }

    @Override
    public Order placeOrder(Order order) {
        return restTemplate.postForObject(baseUrl, order, Order.class);
    }

    @Override
    public Optional<Order> findOrderById(String id) {
        try {
            Order order = restTemplate.getForObject(baseUrl + "/" + id, Order.class);
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findAllOrders() {
        Order[] orders = restTemplate.getForObject(baseUrl, Order[].class);
        return orders != null ? Arrays.asList(orders) : List.of();
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus status) {
        Order[] orders = restTemplate.getForObject(baseUrl + "?status=" + status.name(), Order[].class);
        return orders != null ? Arrays.asList(orders) : List.of();
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus status) {
        restTemplate.put(baseUrl + "/" + orderId + "/status?status=" + status.name(), null);
        return findOrderById(orderId).orElse(null);
    }

    @Override
    public boolean cancelOrder(String orderId) {
        try {
            restTemplate.delete(baseUrl + "/" + orderId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
