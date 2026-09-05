package net.thevpc.samples.petstore.drivers.notification.memory.dal.jpa.repo;

import net.thevpc.samples.petstore.drivers.notification.memory.dal.jpa.entity.JpaNotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataNotificationLogRepository extends JpaRepository<JpaNotificationLogEntity, String> {
}
