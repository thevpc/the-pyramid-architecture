package net.thevpc.samples.petstore.core.dal.jpa.repository;

import net.thevpc.samples.petstore.core.dal.api.AppAuditLogRepository;
import net.thevpc.samples.petstore.core.dal.jpa.entity.JpaAppAuditLogEntity;
import net.thevpc.samples.petstore.core.infra.model.AppAuditLog;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaAppAuditLogRepositoryImpl implements AppAuditLogRepository {

    private final SpringDataAppAuditLogRepository repository;

    public JpaAppAuditLogRepositoryImpl(SpringDataAppAuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public AppAuditLog save(AppAuditLog log) {
        return repository.save(JpaAppAuditLogEntity.fromModel(log)).toModel();
    }

    @Override
    public Optional<AppAuditLog> findById(String id) {
        return repository.findById(id).map(JpaAppAuditLogEntity::toModel);
    }

    @Override
    public List<AppAuditLog> findAll() {
        return repository.findAll().stream()
                .map(JpaAppAuditLogEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppAuditLog> findBySourceModule(String sourceModule) {
        return repository.findBySourceModule(sourceModule).stream()
                .map(JpaAppAuditLogEntity::toModel)
                .collect(Collectors.toList());
    }
}
