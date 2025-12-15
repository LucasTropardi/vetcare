package com.lucast.vetcare.customers.pet;

import com.lucast.vetcare.customers.pet.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@RequestBody @Valid CreatePetRequest req) {
        return petService.create(req);
    }

    @GetMapping("/{id}")
    public PetResponse getById(@PathVariable Long id) {
        return petService.getById(id);
    }

    @GetMapping
    public Page<PetListItemResponse> list(
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        return petService.list(tutorId, query, pageable);
    }

    @PutMapping("/{id}")
    public PetResponse update(@PathVariable Long id, @RequestBody @Valid UpdatePetRequest req) {
        return petService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLogical(@PathVariable Long id) {
        petService.deleteLogical(id);
    }
}
