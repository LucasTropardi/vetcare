package com.lucast.vetcare.customers.pet;

import com.lucast.vetcare.customers.pet.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@Tag(
        name = "Pets",
        description = "Endpoints for managing pets and their relationship with tutors"
)
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create pet",
            description = "Creates a new pet associated with a tutor"
    )
    public PetResponse create(
            @RequestBody @Valid CreatePetRequest req
    ) {
        return petService.create(req);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get pet by ID",
            description = "Returns pet details for the given ID"
    )
    public PetResponse getById(@PathVariable Long id) {
        return petService.getById(id);
    }


    @GetMapping
    @Operation(
            summary = "List pets",
            description = "Lists pets with optional filters by tutor and search query"
    )
    public Page<PetListItemResponse> list(
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        return petService.list(tutorId, query, pageable);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update pet",
            description = "Updates pet information such as name, breed or age"
    )
    public PetResponse update(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePetRequest req
    ) {
        return petService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete pet",
            description = "Soft deletes a pet"
    )
    public void deleteLogical(@PathVariable Long id) {
        petService.deleteLogical(id);
    }
}