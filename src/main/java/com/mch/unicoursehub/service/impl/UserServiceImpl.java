package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.exceptions.UnAuthorizedException;
import com.mch.unicoursehub.model.dto.EditUserRequest;
import com.mch.unicoursehub.model.dto.NewUserRequest;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.repository.UserRepository;
import com.mch.unicoursehub.service.UserService;
import com.mch.unicoursehub.utils.pagination.Pagination;
import com.mch.unicoursehub.utils.pagination.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.mch.unicoursehub.ConstErrors.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user as an admin.
     *
     * @param newUser the details of the new user to create.
     * @throws BadRequestException if the phone number or national ID already exists.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public void createUser(NewUserRequest newUser){

        userRepository.findByUserNumber(newUser.userNumber())
                .ifPresent(user -> {
                    throw new BadRequestException(existingUser);
                });

        if (newUser.nationalCode() != null && !newUser.nationalCode().isBlank())
            userRepository.findUserByNationalCode(newUser.nationalCode())
                    .ifPresent(user -> {
                        throw new BadRequestException(nationalCodeExists);
                    });
        
        if (newUser.role() == Role.ADMIN){
            throw new BadRequestException(createAdmin);
        }

        User user = User.builder()
                .role(newUser.role())
                .firstName(newUser.firstName())
                .lastName(newUser.lastName())
                .userNumber(newUser.userNumber())
                .nationalCode(newUser.nationalCode())
                .password(passwordEncoder.encode(newUser.nationalCode()))
                .phoneNumber(newUser.phoneNumber())
                .isAccountLocked(false)
                .build();

        userRepository.saveAndFlush(user);
    }

    public List<User> getAllUsers(Role role, String userNumber){

        List<User> users = userRepository.findAll();

        if (role != null) {
            users = users.stream()
                    .filter(user -> user.getRole() == role)
                    .toList();
        }

        if (userNumber != null) {
            users = users.stream()
                    .filter(user -> user.getUserNumber().equals(userNumber))
                    .toList();
        }
        return users;
    }
    public Pagination<UserListResponse> getAllUsers(Role role, int size, int page, String userNumber){

        User userLoggedInRef = getUserLoggedInRef();

        List<UserListResponse> list = getAllUsers(role, userNumber)
                .parallelStream()
                .filter(user -> !user.getUid().toString().equals(userLoggedInRef.getUid().toString()))
                .map(User::convertToUserListResponse)
                .sorted(Comparator.comparing(UserListResponse:: lastName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return PaginationUtil.pagination(list,page,size);
    }

    public User getUserLoggedInRef() {
        String username = getUsernameLoggedIn();
        return userRepository.findByUsernameRef(username)
                .orElseThrow(() -> new NotFoundException(userNotFound)); //user not logged in...
    }

    /**
     * Retrieves the username of the currently logged-in user.
     *
     * @return the username of the logged-in user
     */
    private String getUsernameLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            throw new UnAuthorizedException("");

        return authentication.getName();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public void editUser(String userNumber, EditUserRequest editUserRequest) {

        User user = getUserByUserNumber(userNumber)
                .orElseThrow(() -> new NotFoundException(userNotFound));

        User loggedInUser = getUserLoggedInRef();

        if (user.getUid().toString().equals(loggedInUser.getUid().toString()) && user.getRole() == Role.ADMIN)
            throw new BadRequestException(notEditInThisWay);

        // First name
        if (editUserRequest.firstName() != null
                && !editUserRequest.firstName().equals(user.getFirstName())) {

            user.setFirstName(editUserRequest.firstName());
        }

        // Last name
        if (editUserRequest.lastName() != null
                && !editUserRequest.lastName().equals(user.getLastName())) {

            user.setLastName(editUserRequest.lastName());
        }

        // Phone number
        if (editUserRequest.phoneNumber() != null
                && !editUserRequest.phoneNumber().equals(user.getPhoneNumber())) {

            user.setPhoneNumber(editUserRequest.phoneNumber());
        }

        // National code
        if (editUserRequest.nationalCode() != null
                && !editUserRequest.nationalCode().equals(user.getNationalCode())) {

            user.setNationalCode(editUserRequest.nationalCode());
        }

        // User number (needs uniqueness check)
        if (editUserRequest.userNumber() != null
                && !editUserRequest.userNumber().equals(user.getUserNumber())) {

            Optional<User> target = getUserByUserNumber(editUserRequest.userNumber());
            if (target.isPresent() && !target.get().getUid().equals(user.getUid())) {
                throw new BadRequestException(existingUser);
            }

            user.setUserNumber(editUserRequest.userNumber());
        }

        // Role
        if (editUserRequest.role() != null) {

            Role newRole = editUserRequest.role();
            Role currentRole = user.getRole();

            if (currentRole != Role.ADMIN && newRole == Role.ADMIN) {
                throw new BadRequestException(convertToAdmin);
            }

            if (newRole != currentRole) {
                user.setRole(newRole);
            }
        }

        // Lock / unlock
        if (editUserRequest.isUserLocked() != null
                && editUserRequest.isUserLocked() != user.isAccountLocked()) {

            user.setAccountLocked(editUserRequest.isUserLocked());
        }

        // Password
        if (editUserRequest.password() != null && !editUserRequest.password().isBlank()) {

            if (!passwordEncoder.matches(editUserRequest.password(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(editUserRequest.password()));
            }
        }
    }

    /**
     * Retrieves a user by their full ID.
     *
     * @param userNumber the unique identifier of the user
     * @return an {@code Optional<User>} containing the user if found, or an empty {@code Optional} if no user exists
     */
    public Optional<User> getUserByUserNumber(String userNumber) {
        return userRepository.findByUserNumber(userNumber);
    }
}
