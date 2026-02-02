package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.EditUserRequest;
import com.mch.unicoursehub.model.dto.NewUserRequest;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.service.impl.UserServiceImpl;
import com.mch.unicoursehub.utils.pagination.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing users.
 *
 * <p>
 * Provides endpoints for admins to create new users, edit existing users,
 * and retrieve a paginated list of users with optional filtering by role
 * and user number.
 * </p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    /**
     * Service responsible for user-related business logic.
     */
    private final UserServiceImpl userService;

    /**
     * Creates a new user.
     *
     * <p>
     * This endpoint is restricted to ADMIN users. The request body must
     * contain the details of the new user.
     * </p>
     *
     * @param newUser the request containing new user data
     */
    @Operation(
            summary = "Create a user by Admin.",
            description = "This method can only be used by admins."
    )
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void newUser(@Valid @RequestBody NewUserRequest newUser) {
        userService.createUser(newUser);
    }


    /**
     * Retrieves a paginated list of users with optional filtering.
     *
     * <p>
     * This endpoint is restricted to ADMIN users. Filters can include
     * role and user number. Pagination is supported with page number
     * and page size parameters.
     * </p>
     *
     * @param role       optional filter by user role
     * @param page       page number (default is 1)
     * @param size       page size (default is 8)
     * @param userNumber optional filter by user number
     * @return a paginated list of users matching the filters
     */
    @Operation(
            summary = "Get list of users.",
            description = "This method can only used by admins with role & user number filters."
    )
    @GetMapping()
    public ResponseEntity<Pagination<UserListResponse>> getUsers(@RequestParam(required = false, defaultValue = "")Role role,

                                                                 @RequestParam(defaultValue = "1", required = false, name = "p")
                                                                 @Parameter(name = "p", in= ParameterIn.DEFAULT, allowEmptyValue = true, description = "page number")int page,

                                                                 @RequestParam(defaultValue = "8", required = false, name = "s")
                                                                 @Parameter(name = "s", in = ParameterIn.DEFAULT, allowEmptyValue = true, description = "size of page") int size,

                                                                 @RequestParam(required = false, name = "userNumber")
                                                                 @Parameter(name = "userNumber", in = ParameterIn.QUERY, allowEmptyValue = true, description = "User number filter.")String userNumber){

        Pagination<UserListResponse> users = userService.getAllUsers(role, size, page, userNumber);
        return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user.
     *
     * <p>
     * This endpoint is restricted to ADMIN users. Only the fields provided
     * in the request body will be updated.
     * </p>
     *
     * @param userNumber       the user number identifying the user to update
     * @param editUserRequest  request containing updated user data
     */
    @Operation(summary = "Update user.",
    description = "Edit user information. Only provided fields will be changed.")
    @PutMapping("/{userNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable String userNumber, @Valid @RequestBody EditUserRequest editUserRequest) {
        userService.editUser(userNumber,editUserRequest);
    }
}
