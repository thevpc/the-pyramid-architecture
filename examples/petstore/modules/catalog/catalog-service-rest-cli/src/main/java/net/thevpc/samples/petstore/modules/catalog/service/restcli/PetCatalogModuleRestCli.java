package net.thevpc.samples.petstore.modules.catalog.service.restcli;

import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import net.thevpc.samples.petstore.modules.catalog.service.api.PetCatalogModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("petCatalogModuleRestCli")
@Generated("pyramid")
public class PetCatalogModuleRestCli implements PetCatalogModule {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public PetCatalogModuleRestCli(
            @Autowired(required = false) RestTemplate restTemplate,
            @Value("${petstore.modules.catalog.url:http://localhost:8080/api/catalog}") String baseUrl) {
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
        this.baseUrl = baseUrl;
    }

    @Override
    public Pet addPet(Pet pet) {
        return restTemplate.postForObject(baseUrl + "/pets", pet, Pet.class);
    }

    @Override
    public Pet updatePet(Pet pet) {
        restTemplate.put(baseUrl + "/pets", pet);
        return pet;
    }

    @Override
    public Optional<Pet> findPetById(String id) {
        try {
            Pet pet = restTemplate.getForObject(baseUrl + "/pets/" + id, Pet.class);
            return Optional.ofNullable(pet);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Pet> findPetsByStatus(PetStatus status) {
        Pet[] pets = restTemplate.getForObject(baseUrl + "/pets?status=" + status.name(), Pet[].class);
        return pets != null ? Arrays.asList(pets) : List.of();
    }

    @Override
    public List<Pet> findAllPets() {
        Pet[] pets = restTemplate.getForObject(baseUrl + "/pets", Pet[].class);
        return pets != null ? Arrays.asList(pets) : List.of();
    }

    @Override
    public Pet updatePetStatus(String petId, PetStatus status) {
        restTemplate.put(baseUrl + "/pets/" + petId + "/status?status=" + status.name(), null);
        return findPetById(petId).orElse(null);
    }

    @Override
    public boolean removePet(String id) {
        try {
            restTemplate.delete(baseUrl + "/pets/" + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
