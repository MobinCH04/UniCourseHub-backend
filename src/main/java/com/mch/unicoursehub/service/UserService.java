package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.EditUserRequest;
import com.mch.unicoursehub.model.dto.NewUserRequest;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.utils.pagination.Pagination;

import java.util.List;

public interface UserService {

    void createUser(NewUserRequest newUser);

    List<User> getAllUsers(Role role, String userNumber);

    Pagination<UserListResponse> getAllUsers(Role role, int size, int page, String userNumber);

    void editUser(String userNumber, EditUserRequest editUserRequest);
}
