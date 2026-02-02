package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.EditUserRequest;
import com.mch.unicoursehub.model.dto.NewUserRequest;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.utils.pagination.Pagination;

import java.util.List;

/**
 * Service interface for managing users.
 * <p>
 * Provides operations to create, edit, and retrieve users,
 * including pagination and filtering by role or user number.
 * </p>
 */
public interface UserService {

    /**
     * Create a new user in the system.
     * Typically used by administrators.
     *
     * @param newUser the request DTO containing user details
     */
    void createUser(NewUserRequest newUser);

    /**
     * Get a list of all users, optionally filtered by role or user number.
     *
     * @param role       the role to filter by (optional)
     * @param userNumber the user number to filter by (optional)
     * @return list of matching User entities
     */
    List<User> getAllUsers(Role role, String userNumber);

    /**
     * Get a paginated list of users with optional filters.
     *
     * @param role       the role to filter by (optional)
     * @param size       the number of items per page
     * @param page       the page number (starting from 1)
     * @param userNumber the user number to filter by (optional)
     * @return paginated response containing UserListResponse DTOs
     */
    Pagination<UserListResponse> getAllUsers(Role role, int size, int page, String userNumber);

    /**
     * Edit an existing user's details.
     * Only fields provided in the EditUserRequest will be updated.
     *
     * @param userNumber       the user number of the user to edit
     * @param editUserRequest  the DTO containing updated fields
     */
    void editUser(String userNumber, EditUserRequest editUserRequest);
}
