USE GradeBook;

CREATE TABLE classes (
	class_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    course_number VARCHAR(7) NOT NULL,
    term VARCHAR(4) NOT NULL,
    section_number int NOT NULL,
    description VARCHAR(50)
);

CREATE TABLE students (
	student_id int NOT NULL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    name VARCHAR(70) NOT NULL
);

CREATE TABLE categories (
	category_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(15) NOT NULL
);

CREATE TABLE assignments (
	assignment_id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    point_value int NOT NULL,
    categories_id int NOT NULL,
    
    FOREIGN KEY (categories_id) REFERENCES categories(category_ID)
);

CREATE TABLE enrolled_in (
	student_id int NOT NULL,
    class_id int NOT NULL,
    
    PRIMARY KEY(student_id, class_id),
    FOREIGN KEY(student_id) REFERENCES students(student_id),
    FOREIGN KEY(class_id) REFERENCES classes(class_id)
);

CREATE TABLE grades (
	assignment_id int NOT NULL,
    student_id int NOT NULL,
    grade decimal NOT NULL,
    
	PRIMARY KEY(assignment_id, student_id),
    FOREIGN KEY(assignment_id) REFERENCES assignments(assignment_id),
    FOREIGN KEY(student_id) REFERENCES students(student_id)
);

CREATE TABLE weights (
	category_id int NOT NULL,
    class_id int NOT NULL,
    weight decimal NOT NULL,
    
    PRIMARY KEY(category_id, class_id),
	FOREIGN KEY(category_id) REFERENCES categories(category_id),
	FOREIGN KEY(class_id) REFERENCES classes(class_id)
);