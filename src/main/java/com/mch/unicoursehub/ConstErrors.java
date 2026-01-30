package com.mch.unicoursehub;

public class ConstErrors {

    //======================= USER ==================================
    public static final Error existingUser = new Error("This userNumber exists", 1001);
    public static final Error nationalCodeExists = new Error("This nationalCode is exists", 1002);
    public static final Error createAdmin = new Error("You can't create an admin", 1003);
    public static final Error userNotFound = new Error("User not found", 1004);
    public static final Error notEditInThisWay = new Error("Admin user can't edit in this way ", 1005);
    public static final Error convertToAdmin = new Error("You can't change anyone's role to admin.", 1006);

    //======================= SEMESTER ==============================
    public static final Error notFoundSemester = new Error("Semester not found", 2001);
    public static final Error maxUnit = new Error("Maximum allowed units exceeded", 2002);

    //======================= CourseOffering ========================
    public static final Error courseOfferingNotFound = new Error("Course offering not found", 3001);
    public static final Error fullCapacity = new Error("Course offering not found", 3002);
    public static final Error taken = new Error("You have already taken this course in this semester", 3003);
    public static final Error examDateConflict = new Error("Exam date conflict detected", 3004);
    public static final Error classTimeConflict = new Error("Class time conflict detected", 3005);

    //======================= Prerequisites =========================
    public static final Error notPassed = new Error("Prerequisite not passed", 4001);

    //======================= ENROLLMENT ============================
    public static final Error notFoundEnrollment = new Error("Enrollment not found for this course", 5001);
    public static final Error nonSelectedStatus = new Error("You can only drop courses with SELECTED status", 5002);
    public static final Error droppedCourse = new Error("You have already dropped this course in this semester", 5003);

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
