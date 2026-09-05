package net.thevpc.samples.petstore.modules.catalog.dal.jpa.repo;

import net.thevpc.samples.petstore.modules.catalog.dal.jpa.entity.JpaPetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataPetRepository extends JpaRepository<JpaPetEntity, String> {
    List<JpaPetEntity> findByStatus(String status);
}
