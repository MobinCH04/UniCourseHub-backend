package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.entity.User;

public interface TokenService {

    String generateToken(User user);

    void revokeUserToken(String uid);
}
