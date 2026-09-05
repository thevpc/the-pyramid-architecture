package net.thevpc.samples.petstore.modules.order.ws.rest;

import net.thevpc.samples.petstore.core.infra.annotation.AppModuleWS;
import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderStatus;
import net.thevpc.samples.petstore.modules.order.service.api.PetOrderModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AppModuleWS(PetOrderModule.class)
@Generated("pyramid")
public class PetOrderWS {

    @Autowired
    private PetOrderModule petOrderModule;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        return ResponseEntity.ok(petOrderModule.placeOrder(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable String id) {
        return petOrderModule.findOrderById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Order>> findOrders(@RequestParam(required = false) OrderStatus status) {
        if (status != null) {
            return ResponseEntity.ok(petOrderModule.findOrdersByStatus(status));
        }
        return ResponseEntity.ok(petOrderModule.findAllOrders());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(petOrderModule.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id) {
        boolean cancelled = petOrderModule.cancelOrder(id);
        return cancelled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/findAllOrders")
    public ResponseEntity<List<Order>> findAllOrders() {
        return ResponseEntity.ok(petOrderModule.findAllOrders());
    }

    @GetMapping("/findOrdersByStatus")
    public ResponseEntity<List<Order>> findOrdersByStatus(@RequestBody() OrderStatus status) {
        return ResponseEntity.ok(petOrderModule.findOrdersByStatus(status));
    }
}
