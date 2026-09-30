# 🏪 Shree Ganesh Enterprises

### Industrial Products Catalogue & Business Enquiry Management Platform

A full-stack web application built with **Java and Spring Boot** to manage industrial product catalogues, categories, customer enquiries, inventory operations, and administrative workflows. The application includes secure authentication, database integration, email services, and an AI-powered administrative assistant using Spring AI and Ollama.

---

## 🚀 Overview

Shree Ganesh Enterprises is a business web application designed to showcase industrial products and simplify product enquiry management through a centralized administrative dashboard.

### Key Highlights

* 🛍️ **Product Catalogue:** Display industrial products with categories, descriptions, and images.
* 📂 **Category Management:** Organize and manage product categories.
* 🛒 **Shopping Cart:** Add products to a cart and manage quantities.
* 📝 **Enquiry Management:** Allow customers to submit product enquiries without requiring an online payment workflow.
* 📦 **Stock Management:** Administrative stock management functionality.
* 🔐 **Secure Authentication:** Spring Security with role-based access control.
* 🔑 **OAuth2 Login:** Third-party authentication integration.
* 📲 **Two-Factor Authentication:** Additional authentication using TOTP.
* 📧 **Email Integration:** Support for email-based OTPs and notifications.
* 🖥️ **Admin Dashboard:** Manage products, categories, enquiries, banners, users, and application settings.
* 🤖 **AI Assistant:** Integrated a locally hosted language model using Spring AI and Ollama.
* 🗄️ **Database Integration:** Persistent storage using MySQL, Spring Data JPA, and Hibernate.
* 📱 **Responsive Interface:** Web pages built with Thymeleaf, HTML, CSS, and JavaScript.

---

## 🛠️ Technology Stack

### Backend

* Java 21
* Spring Boot 3
* Spring MVC
* Spring Data JPA
* Hibernate ORM
* Spring Security
* Spring Validation
* REST APIs
* Maven

### Frontend

* Thymeleaf
* HTML5
* CSS3
* JavaScript
* Responsive UI components

### Database

* MySQL

### Authentication & Security

* Spring Security
* OAuth2 Client
* Role-Based Access Control (RBAC)
* TOTP-based Two-Factor Authentication
* BCrypt password hashing
* Session management
* CSRF protection

### AI Integration

* Spring AI
* Ollama
* Llama 3.2 (1B)
* Local Large Language Model (LLM) integration
* REST-based AI chat endpoint

### Development & Deployment Tools

* Git
* GitHub
* IntelliJ IDEA
* Maven
* Docker
* Docker Compose
* Spring Boot Actuator

---

## ✨ Features

### 🛍️ Product Catalogue

* Browse industrial products.
* View product details and descriptions.
* Display product images.
* Organize products by categories.
* Manage product information through the admin panel.

### 🛒 Cart & Enquiry Workflow

* Add products to the cart.
* Update item quantities.
* Remove products from the cart.
* Submit product enquiries.
* Manage enquiries through the administrative interface.

### 📦 Inventory Management

* Administrative stock management.
* Stock addition and reduction workflows.
* Centralized product administration.

### 🖥️ Administrative Dashboard

* Manage products and categories.
* Manage hero banners and website content.
* Review customer and product enquiries.
* Manage users and selected application settings.
* Access administrative notifications and operational tools.

### 🔐 Authentication & Application Security

* Secure login using Spring Security.
* OAuth2 client integration.
* TOTP-based two-factor authentication.
* Password hashing using BCrypt.
* Session-based access control.
* CSRF protection for applicable requests.
* Input validation and exception handling.

### 📧 Email Services

* Email-based OTP functionality.
* Application email integration.
* Support for administrative notifications.

### 🤖 AI-Powered Administrative Assistant

The project includes an AI chat interface integrated with Spring AI and a locally hosted Ollama model.

Current capabilities:

* Send questions through the administrative AI chat interface.
* Generate responses using a locally hosted Llama 3.2 model.
* Communicate with the AI service through a Spring Boot REST endpoint.
* Validate incoming chat requests.
* Handle AI provider errors.

**Planned enhancement:** Connect approved AI tools to MySQL-backed product, category, and enquiry data so the assistant can answer business questions using actual database results. This functionality is still in development.

---

## 🏗️ Application Architecture

The application follows a layered Spring Boot architecture.

```text
┌──────────────────────────────────────┐
│       Thymeleaf Web Interface        │
│    HTML · CSS · JavaScript           │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│       Spring MVC Controllers         │
│       REST API Endpoints             │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│          Service Layer               │
│     Business Logic & Validation      │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│     Spring Data JPA / Hibernate      │
│         Repository Layer             │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│             MySQL                    │
└──────────────────────────────────────┘
```

### AI Integration Architecture

```text
Admin AI Chat Interface
          │
          ▼
Spring Boot REST Controller
          │
          ▼
Spring AI Chat Service
          │
          ▼
Ollama Local LLM
          │
          ▼
AI Response
```

The database-aware AI workflow is a planned extension and will use explicitly approved Java methods to retrieve business data.

---

## 📂 Project Structure

```text
src/
├── main/
│   ├── java/com/shreeganesh/enterprises/
│   │   ├── ai/
│   │   │   ├── AiChatController.java
│   │   │   ├── AiChatService.java
│   │   │   ├── AiProviderUnavailableException.java
│   │   │   ├── AiApiExceptionHandler.java
│   │   │   └── dto/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   ├── js/
│       │   └── Images/
│       ├── templates/
│       │   └── admin/
│       ├── application.properties
│       └── application-ai.properties
└── test/
    └── java/
```

*The structure above is representative; individual packages and files may evolve as development continues.*

---

## ⚙️ Prerequisites

Install the following before running the application:

* JDK 21
* Maven
* MySQL Server
* Git
* IntelliJ IDEA or another Java IDE

For the optional AI assistant:

* Ollama
* A compatible local language model, such as `llama3.2:1b`

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd enterprises
```

Replace `YOUR_GITHUB_REPOSITORY_URL` with your actual GitHub repository URL.

### 2. Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE shree_ganesh_enterprises;
```

Configure the following environment variables using your local environment or a private `.env` file:

```properties
DB_URL=jdbc:mysql://localhost:3306/shree_ganesh_enterprises
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
```

Use the exact variable names expected by your existing `application.properties`.

**Security:** Never commit database passwords, `.env` files, API keys, or other secrets to GitHub.

### 3. Run the Application

From the project root:

```bash
mvn spring-boot:run
```

If your local configuration uses port `2330`, open:

```text
http://localhost:2330
```

Otherwise, use the port configured in your application.

### 4. Run the AI Assistant (Optional)

Install Ollama and download the model:

```bash
ollama pull llama3.2:1b
```

Ensure Ollama is running and the model is available:

```bash
ollama list
```

Start Spring Boot with the AI profile enabled.

**PowerShell:**

```powershell
$env:OLLAMA_CHAT_MODEL = "llama3.2:1b"
$env:SPRING_PROFILES_ACTIVE = "ai"
mvn spring-boot:run
```

If the application needs database or other environment variables, configure them as described in the previous step.

The AI profile uses the local Ollama service by default. Refer to `application-ai.properties` for the actual configuration.

---

## 🧪 Testing

Run the project's automated tests using:

```bash
mvn test
```

If tests depend on external services, local configuration, or a particular JDK, configure those prerequisites first.

The project includes AI controller and service-related tests, along with tests for selected security and application services.

---

## 📸 Screenshots

Add your screenshots to the `screenshots/` directory in the repository.

### 🏠 Home Page

![Home Page](screenshots/img_1.png)

### 🛍️ Product Categories

![Product Categories](screenshots/img.png)

### 🔐 Login Page

![Login Page](screenshots/img_2.png)

### 📝 Sign-Up Page

![Sign-Up Page](screenshots/img_3.png)

### 🛒 Product Catalogue

![Product Catalogue](screenshots/img_4.png)

![Product Catalogue](screenshots/img_5.png)

![Product Catalogue](screenshots/img_6.png)

### 🧺 Cart

![Cart View](screenshots/img_7.png)

### ℹ️ About Page

![About Page](screenshots/img_8.png)

### 📌 Website Footer

![Website Footer](screenshots/img_9.png)

### 🤖 AI Assistant

<img width="1366" height="768" alt="Screenshot (907)" src="https://github.com/user-attachments/assets/dd4782a6-7f57-4982-b5a0-79bedca7e074" />

---

## 🔒 Security Considerations

* Store credentials in environment variables or another secure configuration mechanism.
* Protect administrative endpoints using the existing authentication and authorization configuration.
* Preserve CSRF protection where applicable.
* Validate user input and handle exceptions safely.
* Restrict AI capabilities to explicitly approved operations.
* Never give an LLM unrestricted database credentials or unrestricted SQL execution.
* Avoid exposing sensitive business or customer information in AI prompts and logs.

---

## 🗺️ Future Improvements

* Integrate the AI assistant with approved, read-only business data tools.
* Add database-grounded answers for product, category, and enquiry questions.
* Explore Retrieval-Augmented Generation (RAG) for product documentation and company PDFs.
* Expand automated testing and integration testing.
* Improve deployment automation and production monitoring.
* Continue improving accessibility and responsive design.

These items represent potential enhancements, not necessarily completed features.

---

## 👨‍💻 Author

**Deepen Mandve**

Java Backend Developer | Spring Boot | REST APIs | MySQL | AI Integration

* GitHub: [Your GitHub Profile](YOUR_GITHUB_PROFILE_URL)
* LinkedIn: [Your LinkedIn Profile](YOUR_LINKEDIN_PROFILE_URL)

---

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.

Contributions, suggestions, and feedback are welcome!

---

*Built with Java, Spring Boot, MySQL, and a focus on secure business application development.*
