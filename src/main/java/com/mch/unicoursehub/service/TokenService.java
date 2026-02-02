package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.entity.User;

/**
 * Service interface for managing JWT tokens.
 * <p>
 * Responsible for generating, revoking, and managing user tokens.
 * Typically used for authentication and session management.
 * </p>
 */
public interface TokenService {

    /**
     * Generate a JWT token for a given user.
     *
     * @param user the user entity for whom the token is generated
     * @return a signed JWT token as a String
     */
    String generateToken(User user);

    /**
     * Revoke all active tokens associated with a given user.
     * <p>
     * Typically called on logout or security events to invalidate existing sessions.
     * </p>
     *
     * @param uid the unique ID of the user whose tokens should be revoked
     */
    void revokeUserToken(String uid);
}
