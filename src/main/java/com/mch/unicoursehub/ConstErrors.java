package com.mch.unicoursehub;

public class ConstErrors {

    public static final Error existingUser = new Error("This userNumber exists", 1001);
    public static final Error nationalCodeExists = new Error("This nationalCode is exists", 1002);
    public static final Error createAdmin = new Error("You can't create an admin", 1003);
    public static final Error userNotFound = new Error("user not found", 1004);
    public static final Error notEditInThisWay = new Error("Admin user can't edit in this way ", 1005);
    public static final Error convertToAdmin = new Error("You can't change anyone's role to admin.", 1006);

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
