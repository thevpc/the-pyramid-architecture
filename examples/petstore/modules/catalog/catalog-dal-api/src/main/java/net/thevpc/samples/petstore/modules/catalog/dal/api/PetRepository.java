package net.thevpc.samples.petstore.modules.catalog.dal.api;

import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;

import java.util.List;
import java.util.Optional;

public interface PetRepository {
    Pet save(Pet pet);
    Optional<Pet> findById(String id);
    List<Pet> findAll();
    List<Pet> findByStatus(PetStatus status);
    boolean deleteById(String id);
}
