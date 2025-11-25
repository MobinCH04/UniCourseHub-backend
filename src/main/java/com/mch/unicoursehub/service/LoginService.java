package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.AuthRequestResponse;
import com.mch.unicoursehub.model.dto.UserLogin;

public interface LoginService {
    /**
     * Authenticates a user with phone number and password.
     *
     * @param userLogin the login details.
     * @return a response containing login details such as token.
     */
    AuthRequestResponse pwdUserLogin(UserLogin userLogin);

}
