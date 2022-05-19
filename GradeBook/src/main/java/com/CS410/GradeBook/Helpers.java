package com.CS410.GradeBook;

/**
 * Helper class to mainly provide current selected
 * course that is used by other commands.
 */
class Helpers {
	
	// This int represents the class_id of the current selected course.
	private static int selectedCourse = 0; // 0 is default value if no class is selected

	/**
	 * Getter for the selected course.
	 * @return selectedCourse
	 */
	public static int getSelectedCourse() {
		return selectedCourse;
	}

	/**
	 * Setter for the selected course.
	 * @param selectedCourse - class_id of the selected course
	 */
	public static void setSelectedCourse(int selectedCourse) {
		Helpers.selectedCourse = selectedCourse;
	}
}
