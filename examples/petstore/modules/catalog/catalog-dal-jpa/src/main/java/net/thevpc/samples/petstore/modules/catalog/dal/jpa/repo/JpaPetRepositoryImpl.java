package net.thevpc.samples.petstore.modules.catalog.dal.jpa.repo;

import net.thevpc.samples.petstore.modules.catalog.dal.api.PetRepository;
import net.thevpc.samples.petstore.modules.catalog.dal.jpa.entity.JpaPetEntity;
import net.thevpc.samples.petstore.modules.catalog.infra.Category;
import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaPetRepositoryImpl implements PetRepository {

    private final SpringDataPetRepository springRepo;

    @Autowired
    public JpaPetRepositoryImpl(SpringDataPetRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public Pet save(Pet pet) {
        JpaPetEntity entity = toEntity(pet);
        JpaPetEntity saved = springRepo.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Pet> findById(String id) {
        return springRepo.findById(id).map(this::toModel);
    }

    @Override
    public List<Pet> findAll() {
        return springRepo.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Pet> findByStatus(PetStatus status) {
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

    private JpaPetEntity toEntity(Pet pet) {
        String catId = pet.getCategory() != null ? pet.getCategory().getId() : null;
        String catName = pet.getCategory() != null ? pet.getCategory().getName() : null;
        String status = pet.getStatus() != null ? pet.getStatus().name() : PetStatus.AVAILABLE.name();
        return new JpaPetEntity(pet.getId(), pet.getName(), catId, catName, status, pet.getPrice());
    }

    private Pet toModel(JpaPetEntity e) {
        Category cat = (e.getCategoryId() != null || e.getCategoryName() != null)
                ? new Category(e.getCategoryId(), e.getCategoryName()) : null;
        PetStatus status = e.getStatus() != null ? PetStatus.valueOf(e.getStatus()) : PetStatus.AVAILABLE;
        return new Pet(e.getId(), e.getName(), cat, status, e.getPrice());
    }
}
