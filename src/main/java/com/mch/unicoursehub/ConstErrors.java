package com.mch.unicoursehub;

public class ConstErrors {

    public static class Error {

        private int errorCode;
        private String message;

        public Error(String message, int errorCode) {
            this.errorCode = errorCode;
            this.message = message;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }
    }

}
