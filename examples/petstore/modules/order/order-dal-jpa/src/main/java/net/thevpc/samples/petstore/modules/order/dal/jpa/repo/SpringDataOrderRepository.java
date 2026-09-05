package net.thevpc.samples.petstore.modules.order.dal.jpa.repo;

import net.thevpc.samples.petstore.modules.order.dal.jpa.entity.JpaOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<JpaOrderEntity, String> {
    List<JpaOrderEntity> findByStatus(String status);
}
