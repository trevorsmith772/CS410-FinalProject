# GradeBook

GradeBook is a Java shell application that allows instructors to add their classes and their respective students, add assignments, and manage their students' grades. Since it's a shell application, users have access to a custom shell with a dynamic prompt and useful base-level shell features like autocomplete, usage documentation, etc.

This application was created using Spring Shell and Spring Boot in order to give us a great shell foundation to build off of. This app is currently configured to use a database and JDBC on Boise State University's Onyx server. This means that this application is only usable while connected to Onyx, but it can be easily configured to run anywhere by changing the specified database and JDBC sources in the application.properties file.

## Installation

Simply clone the repository using the following command

```bash
git clone https://github.com/trevorsmith772/CS410-FinalProject.git
```

## Usage

To run the application, simply use the provided `run.sh` script. Make sure you are in the root directory (the same directory as this README)
```bash
./run.sh
```
Usage for each command can be seen by running the `help` command while in the shell
