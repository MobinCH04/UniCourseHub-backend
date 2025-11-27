package com.mch.unicoursehub;

public class ConstVal {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String PREFIX_BEARER = "Bearer ";
    public static final String SECRET_KEY = "cd7a5c0dbec776ed6104df40fc846cec8ca086843b5e428068b96b08a3ab0962042e64869231213fea4ad46f4b3ee6a68956c6eeea0fe17943049480642acb9a";


    public static final long JWT_EXPIRATION_STUDENT = 43200000L; //12h -> using for normal user
    public static final long JWT_EXPIRATION_PROFESSOR = 21600000L;//6h
    public static final long JWT_EXPIRATION_ADMIN = 3600000L;//1h
    public static final long REFRESH_EXPIRATION = 259200000L;//3 day
    public static final String UUID_KEY = "uuid";
}