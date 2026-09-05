package net.thevpc.samples.petstore.modules.catalog.service.impl;

import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import net.thevpc.samples.petstore.modules.catalog.service.api.PetCatalogModule;
import net.thevpc.samples.petstore.modules.catalog.service.impl.entity.PetCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Local implementation of PetCatalogModule facade.
 * Delegates to internal CRUD services while acting as the apex entry point.
 */
@Service("petCatalogModule")
public class PetCatalogModuleImpl implements PetCatalogModule {

    private final PetCrudService petCrudService;

    @Autowired
    public PetCatalogModuleImpl(PetCrudService petCrudService) {
        this.petCrudService = petCrudService;
    }

    @Override
    public Pet addPet(Pet pet) {
        return petCrudService.addPet(pet);
    }

    @Override
    public Pet updatePet(Pet pet) {
        return petCrudService.updatePet(pet);
    }

    @Override
    public Optional<Pet> findPetById(String id) {
        return petCrudService.findPetById(id);
    }

    @Override
    public List<Pet> findPetsByStatus(PetStatus status) {
        return petCrudService.findPetsByStatus(status);
    }

    @Override
    public List<Pet> findAllPets() {
        return petCrudService.findAllPets();
    }

    @Override
    public Pet updatePetStatus(String petId, PetStatus status) {
        return petCrudService.updatePetStatus(petId, status);
    }

    @Override
    public boolean removePet(String id) {
        return petCrudService.removePet(id);
    }
}
