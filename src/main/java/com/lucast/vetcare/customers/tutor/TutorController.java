package com.lucast.vetcare.customers.tutor;

import com.lucast.vetcare.customers.tutor.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TutorResponse create(@RequestBody @Valid CreateTutorRequest req) {
        return tutorService.create(req);
    }

    @GetMapping("/{id}")
    public TutorResponse getById(@PathVariable Long id) {
        return tutorService.getById(id);
    }

    @GetMapping
    public Page<TutorListItemResponse> list(
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        return tutorService.list(query, pageable);
    }

    @PutMapping("/{id}")
    public TutorResponse update(@PathVariable Long id, @RequestBody @Valid UpdateTutorRequest req) {
        return tutorService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLogical(@PathVariable Long id) {
        tutorService.deleteLogical(id);
    }
}
