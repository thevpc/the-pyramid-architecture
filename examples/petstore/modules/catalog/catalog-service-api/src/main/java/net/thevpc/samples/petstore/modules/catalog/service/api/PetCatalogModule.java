package net.thevpc.samples.petstore.modules.catalog.service.api;

import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;

import java.util.List;
import java.util.Optional;

/**
 * Published Facade Contract for the Pet Catalog Module.
 * Other modules interact with Catalog strictly apex-to-apex via this interface.
 */
public interface PetCatalogModule {

    Pet addPet(Pet pet);

    Pet updatePet(Pet pet);

    Optional<Pet> findPetById(String id);

    List<Pet> findPetsByStatus(PetStatus status);

    List<Pet> findAllPets();

    Pet updatePetStatus(String petId, PetStatus status);

    boolean removePet(String id);
}
