# Software Requirements Specification (SRS) for Cooking Book Application

## 1. Introduction

### 1.1 Purpose
The purpose of this document is to define the requirements for the Cooking Book application, which allows users to manage their recipes. The application will be built using Java 21, Maven, JUnit 5, Spring Boot, PostgreSQL, and Docker.

### 1.2 Scope
The Cooking Book application will allow users to:
- Add recipes
- Delete recipes
- List recipes
- Search recipes by name

The application will be built as a REST API using Spring Boot, and will store recipe data in a PostgreSQL database. The application will be containerized using Docker for easy deployment and scaling.

### 1.3 Definitions
- **Recipe**: A set of instructions for preparing a dish, including ingredients and steps.
- **REST API**: A set of rules for communication between client and server.
- **Docker**: A platform for containerizing applications for easy deployment and scaling.

## 2. Overall Description

### 2.1 User Characteristics
The application will be used by users who want to manage their recipes. Users can add, delete, list, and search recipes.

### 2.2 System Features
The application will have the following features:
- Add recipe
- Delete recipe
- List recipes
- Search recipe by name

### 2.3 User Interfaces
The application will have a REST API for communication with clients. There will be no user interface for the application itself.

## 3. Specific Requirements

### 3.1 Functional Requirements
1. **Add Recipe**
   - Users should be able to add a new recipe to the database.
   - The recipe should include the name, ingredients, and steps.
   - The API should return a success message if the recipe is added successfully.

2. **Delete Recipe**
   - Users should be able to delete an existing recipe from the database.
   - The API should return a success message if the recipe is deleted successfully.

3. **List Recipes**
   - Users should be able to list all recipes from the database.
   - The API should return a list of all recipes.

4. **Search Recipe by Name**
   - Users should be able to search for recipes by name.
   - The API should return a list of recipes that match the search query.

### 3.2 Non-Functional Requirements
1. **Performance**
   - The application should respond to requests within 1 second.

2. **Reliability**
   - The application should be available 99.9% of the time.

3. **Security**
   - The application should have authentication and authorization mechanisms to protect user data.

4. **Scalability**
   - The application should be able to handle up to 10,000 users at the same time.

## 4. Future Roadmap

### 4.1 Java 21
- Use Java 21 as the programming language for the application.

### 4.2 Maven
- Use Maven as the build tool for the application.

### 4.3 JUnit 5
- Use JUnit 5 for writing and running unit tests.

### 4.4 File Storage
- Implement file storage for recipe images and other media.

### 4.5 Spring Boot REST API
- Use Spring Boot to build the REST API for the application.

### 4.6 PostgreSQL
- Use PostgreSQL as the database for storing recipe data.

### 4.7 Docker
- Use Docker to containerize the application for easy deployment and scaling.

## 5. Conclusion
This document defines the requirements for the Cooking Book application. The application will allow users to manage their recipes, and will be built using Java 21, Maven, JUnit 5, Spring Boot, PostgreSQL, and Docker. The application will have a REST API for communication with clients, and will store recipe data in a PostgreSQL database. The application will be containerized using Docker for easy deployment and scaling.