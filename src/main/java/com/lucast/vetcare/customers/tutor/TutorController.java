package com.lucast.vetcare.customers.tutor;

import com.lucast.vetcare.customers.tutor.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutors")
@Tag(
        name = "Tutors",
        description = "Operations related to tutor management"
)
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create tutor",
            description = "Create a new tutor"
    )
    public TutorResponse create(@RequestBody @Valid CreateTutorRequest req) {
        return tutorService.create(req);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get tutor by ID",
            description = "Retrieve a tutor by its ID"
    )
    public TutorResponse getById(@PathVariable Long id) {
        return tutorService.getById(id);
    }

    @GetMapping
    @Operation(
            summary = "List tutors",
            description = "List all tutors with pagination, sorting and optional search query"
    )
    public Page<TutorListItemResponse> list(
            @RequestParam(required = false) String query,
            @ParameterObject
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable
    ) {
        return tutorService.list(query, pageable);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update tutor",
            description = "Update an existing tutor by ID"
    )
    public TutorResponse update(@PathVariable Long id, @RequestBody @Valid UpdateTutorRequest req) {
        return tutorService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete tutor",
            description = "Logically delete a tutor by ID"
    )
    public void deleteLogical(@PathVariable Long id) {
        tutorService.deleteLogical(id);
    }
}
