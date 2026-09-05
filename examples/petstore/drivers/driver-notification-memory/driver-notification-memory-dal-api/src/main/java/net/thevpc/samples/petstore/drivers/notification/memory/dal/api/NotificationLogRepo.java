package net.thevpc.samples.petstore.drivers.notification.memory.dal.api;

import net.thevpc.samples.petstore.drivers.notification.memory.infra.NotificationLog;

import java.util.List;
import java.util.Optional;

public interface NotificationLogRepo {
    NotificationLog save(NotificationLog log);
    List<NotificationLog> findAll();
    Optional<NotificationLog> findById(String id);
}
