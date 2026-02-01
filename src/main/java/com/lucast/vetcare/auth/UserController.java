package com.lucast.vetcare.auth;

import com.lucast.vetcare.auth.dto.CreateUserRequest;
import com.lucast.vetcare.auth.dto.UpdateMeRequest;
import com.lucast.vetcare.auth.dto.UpdateUserRequest;
import com.lucast.vetcare.auth.dto.UserResponse;
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
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description = "Operations related to user management"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create user",
            description = "Create a new user"
    )
    public UserResponse create(@RequestBody @Valid CreateUserRequest req) {
        return userService.createUser(req);
    }

    @GetMapping
    @Operation(
            summary = "List users",
            description = "List all users with pagination and sorting"
    )
    public Page<UserResponse> list(
            @ParameterObject
            @PageableDefault(size = 50, sort = { "name" })
            Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a user by its ID"
    )
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Retrieve data of the authenticated user"
    )
    public UserResponse me() {
        return userService.me();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user",
            description = "Update an existing user by ID"
    )
    public UserResponse update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest req) {
        return userService.update(id, req);
    }

    @PutMapping("/me")
    @Operation(
            summary = "Update current user",
            description = "Update data of the authenticated user"
    )
    public UserResponse updateMe(@RequestBody @Valid UpdateMeRequest req) {
        return userService.updateMe(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete user",
            description = "Logically delete a user by ID"
    )
    public void delete(@PathVariable Long id) {
        userService.deleteLogical(id);
    }

    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete user",
            description = "Delete a user by ID"
    )
    public void deleteOldSchool(@PathVariable Long id) {
        userService.deleteOldSchool(id);
    }
}
