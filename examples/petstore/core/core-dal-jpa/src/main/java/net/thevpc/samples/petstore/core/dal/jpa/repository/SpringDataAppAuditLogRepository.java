package net.thevpc.samples.petstore.core.dal.jpa.repository;

import net.thevpc.samples.petstore.core.dal.jpa.entity.JpaAppAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataAppAuditLogRepository extends JpaRepository<JpaAppAuditLogEntity, String> {

    List<JpaAppAuditLogEntity> findBySourceModule(String sourceModule);
}
