package net.thevpc.samples.petstore.modules.catalog.ws.rest;

import net.thevpc.samples.petstore.core.infra.annotation.AppModuleWS;
import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import net.thevpc.samples.petstore.modules.catalog.service.api.PetCatalogModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@AppModuleWS(PetCatalogModule.class)
@Generated("pyramid")
public class PetCatalogWS {

    @Autowired
    private PetCatalogModule petCatalogModule;

    @PostMapping("/pets")
    public ResponseEntity<Pet> addPet(@RequestBody Pet pet) {
        return ResponseEntity.ok(petCatalogModule.addPet(pet));
    }

    @PutMapping("/pets")
    public ResponseEntity<Pet> updatePet(@RequestBody Pet pet) {
        return ResponseEntity.ok(petCatalogModule.updatePet(pet));
    }

    @GetMapping("/pets/{id}")
    public ResponseEntity<Pet> findPetById(@PathVariable String id) {
        return petCatalogModule.findPetById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pets")
    public ResponseEntity<List<Pet>> findPets(@RequestParam(required = false) PetStatus status) {
        if (status != null) {
            return ResponseEntity.ok(petCatalogModule.findPetsByStatus(status));
        }
        return ResponseEntity.ok(petCatalogModule.findAllPets());
    }

    @PutMapping("/pets/{id}/status")
    public ResponseEntity<Pet> updatePetStatus(@PathVariable String id, @RequestParam PetStatus status) {
        return ResponseEntity.ok(petCatalogModule.updatePetStatus(id, status));
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> removePet(@PathVariable String id) {
        boolean removed = petCatalogModule.removePet(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/findPetsByStatus")
    public ResponseEntity<List<Pet>> findPetsByStatus(@RequestBody() PetStatus status) {
        return ResponseEntity.ok(petCatalogModule.findPetsByStatus(status));
    }

    @GetMapping("/findAllPets")
    public ResponseEntity<List<Pet>> findAllPets() {
        return ResponseEntity.ok(petCatalogModule.findAllPets());
    }
}
