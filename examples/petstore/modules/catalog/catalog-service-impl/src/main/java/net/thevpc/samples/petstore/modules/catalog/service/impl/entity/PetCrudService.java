package net.thevpc.samples.petstore.modules.catalog.service.impl.entity;

import net.thevpc.samples.petstore.core.infra.exception.NotFoundException;
import net.thevpc.samples.petstore.core.infra.util.AppValidation;
import net.thevpc.samples.petstore.core.interceptor.api.LifecyclePhase;
import net.thevpc.samples.petstore.core.interceptor.impl.InterceptorDispatcher;
import net.thevpc.samples.petstore.modules.catalog.dal.api.PetRepository;
import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Exclusive owner of PetRepository within the Catalog module.
 * Dispatches entity lifecycle interceptor events for every CRUD operation.
 */
@Service
public class PetCrudService {

    private final PetRepository petRepository;
    private final InterceptorDispatcher interceptorDispatcher;

    @Autowired
    public PetCrudService(PetRepository petRepository, InterceptorDispatcher interceptorDispatcher) {
        this.petRepository = petRepository;
        this.interceptorDispatcher = interceptorDispatcher;
    }

    public Pet addPet(Pet pet) {
        AppValidation.requireNonNull(pet, "Pet must not be null");
        AppValidation.requireNonBlank(pet.getName(), "Pet name must not be blank");
        if (pet.getId() == null || pet.getId().isBlank()) {
            pet.setId(UUID.randomUUID().toString());
        }
        if (pet.getStatus() == null) {
            pet.setStatus(PetStatus.AVAILABLE);
        }

        // BEFORE_ADD phase
        interceptorDispatcher.dispatch(LifecyclePhase.BEFORE_ADD, pet, null);

        Pet saved = petRepository.save(pet);

        // AFTER_ADD phase
        interceptorDispatcher.dispatch(LifecyclePhase.AFTER_ADD, saved, null);

        return saved;
    }

    public Pet updatePet(Pet pet) {
        AppValidation.requireNonNull(pet, "Pet must not be null");
        AppValidation.requireNonBlank(pet.getId(), "Pet ID must not be blank");

        Pet existing = petRepository.findById(pet.getId())
                .orElseThrow(() -> new NotFoundException("Pet not found with ID: " + pet.getId()));

        // BEFORE_UPDATE phase
        interceptorDispatcher.dispatch(LifecyclePhase.BEFORE_UPDATE, pet, existing);

        Pet saved = petRepository.save(pet);

        // AFTER_UPDATE phase
        interceptorDispatcher.dispatch(LifecyclePhase.AFTER_UPDATE, saved, existing);

        return saved;
    }

    public Optional<Pet> findPetById(String id) {
        return petRepository.findById(id);
    }

    public List<Pet> findPetsByStatus(PetStatus status) {
        return petRepository.findByStatus(status != null ? status : PetStatus.AVAILABLE);
    }

    public List<Pet> findAllPets() {
        return petRepository.findAll();
    }

    public Pet updatePetStatus(String petId, PetStatus status) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet not found with ID: " + petId));
        Pet oldCopy = new Pet(pet.getId(), pet.getName(), pet.getCategory(), pet.getStatus(), pet.getPrice());
        pet.setStatus(status);

        interceptorDispatcher.dispatch(LifecyclePhase.BEFORE_UPDATE, pet, oldCopy);
        Pet saved = petRepository.save(pet);
        interceptorDispatcher.dispatch(LifecyclePhase.AFTER_UPDATE, saved, oldCopy);
        return saved;
    }

    public boolean removePet(String id) {
        Optional<Pet> existing = petRepository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        Pet pet = existing.get();

        interceptorDispatcher.dispatch(LifecyclePhase.BEFORE_REMOVE, pet, null);
        boolean deleted = petRepository.deleteById(id);
        if (deleted) {
            interceptorDispatcher.dispatch(LifecyclePhase.AFTER_REMOVE, pet, null);
        }
        return deleted;
    }
}
