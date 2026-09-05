package net.thevpc.samples.petstore.drivers.notification.memory.dal.jpa.repo;

import net.thevpc.samples.petstore.drivers.notification.memory.dal.api.NotificationLogRepo;
import net.thevpc.samples.petstore.drivers.notification.memory.dal.jpa.entity.JpaNotificationLogEntity;
import net.thevpc.samples.petstore.drivers.notification.memory.infra.NotificationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaNotificationLogRepoImpl implements NotificationLogRepo {

    private final SpringDataNotificationLogRepository springRepo;

    @Autowired
    public JpaNotificationLogRepoImpl(SpringDataNotificationLogRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public NotificationLog save(NotificationLog log) {
        JpaNotificationLogEntity entity = new JpaNotificationLogEntity(
                log.getId(), log.getRecipient(), log.getSubject(), log.getBody(),
                log.getChannel(), log.getTimestamp()
        );
        JpaNotificationLogEntity saved = springRepo.save(entity);
        return toModel(saved);
    }

    @Override
    public List<NotificationLog> findAll() {
        return springRepo.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<NotificationLog> findById(String id) {
        return springRepo.findById(id).map(this::toModel);
    }

    private NotificationLog toModel(JpaNotificationLogEntity e) {
        return new NotificationLog(e.getId(), e.getRecipient(), e.getSubject(), e.getBody(), e.getChannel(), e.getTimestamp());
    }
}
