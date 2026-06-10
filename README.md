# 🚌 Cloud Bus Pass Management System

A full-stack web application developed using **Spring Boot**, **Thymeleaf**, and **MySQL** to automate the process of bus pass application, approval, route management, and pass generation for students.

---

## 📌 Project Overview

The Cloud Bus Pass Management System digitizes the traditional bus pass process by allowing students to apply for bus passes online while enabling administrators to manage applications, routes, and users through a dedicated dashboard.

The system includes secure authentication, PDF pass generation, QR code integration, email notifications, route fare management, and analytics dashboards.

---

## 🚀 Features

### 👨‍🎓 Student Module

* User Registration
* Secure Login & Logout
* Apply for Bus Pass
* View Application Status
* Application History
* Download Approved Pass as PDF
* Profile Management
* Change Password
* Dashboard Analytics

### 👨‍💼 Admin Module

* View All Applications
* Approve Applications
* Reject Applications
* Manage Routes

    * Add Route
    * Edit Route
    * Delete Route
* Manage Users

    * View Users
    * Edit Users
    * Delete Users
* Dashboard Analytics

### 🚌 Route Management

* Dynamic Route Selection
* Source & Destination Management
* Fare Management
* Route CRUD Operations

### 📄 PDF & QR Code Features

* Generate Bus Pass PDF
* QR Code Embedded Pass
* Download Approved Pass

### 📧 Email Notifications

* Approval Notification Email
* Rejection Notification Email
* PDF Attachment Support

### 🔐 Security Features

* BCrypt Password Encryption
* Session-Based Authentication
* Role-Based Access Control

---

## 🛠️ Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* Hibernate

### Frontend

* Thymeleaf
* HTML5
* CSS3
* Bootstrap 5
* JavaScript
* Chart.js

### Database

* MySQL

### Additional Libraries

* iText PDF
* QR Code Generator
* JavaMailSender
* BCrypt Password Encoder

---

## 📊 Modules Implemented

### Authentication Module

* Registration
* Login
* Logout
* Password Encryption

### Student Dashboard

* Application Statistics
* Latest Application Card
* Application History
* Pie Chart Analytics

### Admin Dashboard

* Total Applications
* Approved Applications
* Pending Applications
* Rejected Applications
* Pie Chart Analytics

### Route Management

* Add Route
* Edit Route
* Delete Route
* Route Fare Management

### User Management

* View Users
* Edit User Details
* Delete Users

### Bus Pass Management

* Apply Pass
* Approve Pass
* Reject Pass
* Generate Pass PDF

---

## 📸 Screenshots

### Home Page

*Add screenshot here*

### Login Page

*Add screenshot here*

### Student Dashboard

*Add screenshot here*

### Apply Bus Pass

*Add screenshot here*

### Admin Dashboard

*Add screenshot here*

### Route Management

*Add screenshot here*

### User Management

*Add screenshot here*

### Generated PDF Pass

*Add screenshot here*

### Email Notification

*Add screenshot here*

---

## ⚙️ Installation & Setup

### Clone Repository

```bash
git clone https://github.com/HetGangani/codealpha_cloud_bus_pass_system.git
```

### Navigate to Project

```bash
cd codealpha_cloud_bus_pass_system
```

### Configure MySQL

Create a database:

```sql
CREATE DATABASE bus_pass_system;
```

### Configure application.properties

Update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bus_pass_system
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
```

### Run Application

```bash
mvn spring-boot:run
```

---

## 🔮 Future Enhancements

* Cloud Deployment
* Student Profile Photo
* Pass Expiry Management
* Online Payment Integration
* Mobile Application Support
* Advanced Reporting Dashboard

---

## 👨‍💻 Author

**Het Gangani**

* GitHub: https://github.com/HetGangani
* LinkedIn: [www.linkedin.com/in/het-gangani-80b34929b](http://www.linkedin.com/in/het-gangani-80b34929b)

---

## ⭐ Acknowledgement

Developed as part of a learning and practical implementation project to demonstrate Spring Boot, Database Management, PDF Generation, Email Integration, and Full Stack Web Development concepts.
