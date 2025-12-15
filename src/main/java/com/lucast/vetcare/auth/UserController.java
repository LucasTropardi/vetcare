package com.lucast.vetcare.auth;

import com.lucast.vetcare.auth.dto.CreateUserRequest;
import com.lucast.vetcare.auth.dto.UpdateMeRequest;
import com.lucast.vetcare.auth.dto.UpdateUserRequest;
import com.lucast.vetcare.auth.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid CreateUserRequest req) {
        return userService.createUser(req);
    }

    @GetMapping
    public Page<UserResponse> list(Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/me")
    public UserResponse me() {
        return userService.me();
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest req) {
        return userService.update(id, req);
    }

    @PutMapping("/me")
    public UserResponse updateMe(@RequestBody @Valid UpdateMeRequest req) {
        return userService.updateMe(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteLogical(id);
    }
}
