# TrackerPro - User Management System

A complete full-stack user management system built with Spring Boot 3.3.5, Java 17, and H2/MySQL database.

## Features

### 🎯 **Core Functionality**
- **Multi-Role Authentication**: Admin, Student, Faculty, HR roles
- **Admin Dashboard**: Complete user management interface
- **Role-Based Registration**: Users can register for different roles
- **Approval Workflow**: Admin approval required for new registrations
- **Secure Authentication**: BCrypt password encryption

### 🏗️ **Architecture**
- **Backend**: Spring Boot 3.3.5 with layered architecture
- **Frontend**: Pure HTML/CSS/JavaScript (no frameworks)
- **Database**: H2 (development) / MySQL (production)
- **Security**: Spring Security with BCrypt
- **API**: RESTful APIs with comprehensive error handling

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (for production)

### Development Setup (H2 Database)

1. **Clone and Build**
   ```bash
   git clone <repository-url>
   cd trackerpro
   mvn clean compile
   ```

2. **Run Application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access Application**
   - Frontend: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - API Base: http://localhost:8080/api

### Production Setup (MySQL Database)

1. **Create MySQL Database**
   ```sql
   CREATE DATABASE trackerpro;
   CREATE USER 'trackerpro_user'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON trackerpro.* TO 'trackerpro_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Update Configuration**
   Edit `/src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/trackerpro
       driver-class-name: com.mysql.cj.jdbc.Driver
       username: trackerpro_user
       password: your_password
     
     jpa:
       database: mysql
       hibernate:
         ddl-auto: update
       properties:
         hibernate:
           dialect: org.hibernate.dialect.MySQL8Dialect
   ```

3. **Run Application**
   ```bash
   mvn spring-boot:run
   ```

## 👤 Default Admin Credentials

- **Email**: admin@trackerpro.com
- **Password**: admin123

## 📱 User Interface

### Login Portal
- **Student Login**: For registered students
- **Employer/Users**: For Faculty, HR, and Admin users
- **Responsive Design**: Works on desktop and mobile

### Admin Dashboard
- **Statistics Cards**: User counts and pending requests
- **User Management**: Approve/reject registrations
- **Role-Based Filtering**: View users by role
- **Real-time Updates**: Dashboard updates automatically

### Registration System
- **Multi-Role Registration**: Students, Faculty, HR
- **Form Validation**: Client-side and server-side validation
- **Email Uniqueness**: Prevents duplicate registrations
- **Mobile Validation**: 10-digit mobile number validation

## 🔧 API Endpoints

### Authentication APIs
```http
POST /api/auth/register          # User registration
POST /api/auth/login             # User login
POST /api/auth/admin/login       # Admin login
POST /api/auth/forgot-password   # Password reset
```

### Admin APIs
```http
GET  /api/admin/dashboard-stats        # Dashboard statistics
GET  /api/admin/pending-registrations  # Pending user registrations
POST /api/admin/approve-user/{id}      # Approve user registration
POST /api/admin/reject-user/{id}       # Reject user registration
GET  /api/admin/users                  # Get all users (with role filter)
POST /api/admin/toggle-user-status/{id} # Enable/disable user
DELETE /api/admin/users/{id}           # Delete user
```

## 🗃️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    mobile VARCHAR(10),
    role ENUM('ADMIN', 'STUDENT', 'FACULTY', 'HR') NOT NULL,
    status ENUM('PENDING', 'ACTIVE', 'INACTIVE', 'REJECTED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_email (email),
    INDEX idx_user_role (role),
    INDEX idx_user_status (status)
);
```

## 🛡️ Security Features

- **Password Encryption**: BCrypt with salt
- **Role-Based Access**: Different access levels for roles
- **Input Validation**: Server-side validation for all inputs
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **CORS Configuration**: Configurable cross-origin requests

## 📊 Sample Data

The application automatically creates sample data for testing:

### Sample Users
- **John Smith** (Student, Pending) - john.s@example.com
- **Sarah Johnson** (Faculty, Pending) - sarah.j@example.com  
- **Mike Williams** (HR, Pending) - mike.w@example.com
- **Alice Brown** (Student, Active) - alice.b@example.com
- **Robert Davis** (Faculty, Active) - robert.d@example.com
- **Emily Wilson** (HR, Active) - emily.w@example.com

All sample users have password: `password123`

## 🔄 Workflow

1. **User Registration** → User fills registration form
2. **Pending Status** → Registration awaits admin approval
3. **Admin Review** → Admin approves/rejects from dashboard
4. **User Activation** → Approved users can login
5. **Role-Based Access** → Users access appropriate features

## 🏗️ Project Structure

```
src/main/java/com/webapp/trackerpro/
├── config/               # Configuration classes
│   ├── DataInitializer   # Sample data creation
│   ├── SecurityConfig    # Security configuration
│   └── WebConfig         # Web configuration
├── controller/           # REST API controllers
│   ├── AdminController   # Admin management APIs
│   └── AuthController    # Authentication APIs
├── dto/                  # Data Transfer Objects
│   ├── LoginDto         # Login request DTO
│   ├── UserRegistrationDto # Registration request DTO
│   └── UserResponseDto   # User response DTO
├── exception/            # Exception handling
│   ├── BusinessException # Custom business exception
│   └── GlobalExceptionHandler # Global error handler
├── mapper/               # Entity-DTO mappers
│   └── UserMapper       # User entity mapper
├── model/                # JPA entities
│   ├── Role             # Role enumeration
│   ├── User             # User entity
│   └── UserStatus       # User status enumeration
├── repository/           # Data access layer
│   └── UserRepository   # User data repository
└── service/              # Business logic layer
    └── UserService      # User business logic

src/main/resources/
├── application.yml       # Application configuration
└── static/              # Frontend files
    ├── index.html       # Login page
    ├── Registration.html # Registration page
    ├── adminDashboard.html # Admin dashboard
    ├── success.html     # Success page
    ├── forget.html      # Forgot password page
    └── assets/          # Images and static files
```

## 🧪 Testing

### Manual Testing
1. **Access Application**: http://localhost:8080
2. **Test Admin Login**: Use admin@trackerpro.com / admin123
3. **Test Registration**: Register new user with unique email/mobile
4. **Test Approval**: Approve user from admin dashboard
5. **Test User Login**: Login with approved user credentials

### API Testing with cURL
```bash
# Test user registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","password":"password123","confirmPassword":"password123","mobileNo":"9876543210","roleCategory":"Student"}'

# Test admin login
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@trackerpro.com","password":"admin123"}'

# Test dashboard stats
curl http://localhost:8080/api/admin/dashboard-stats
```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
server:
  port: 8080

app:
  admin:
    email: admin@trackerpro.com
    password: admin123

spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Use 'update' for production
    show-sql: true           # Set false for production
  
  h2:
    console:
      enabled: true          # Disable for production
```

## 📈 Future Enhancements

- **JWT Authentication**: Token-based authentication
- **Email Integration**: Send approval notifications
- **Advanced Role Management**: Role-based permissions
- **Audit Logging**: Track user actions
- **Password Reset**: Complete forgot password functionality
- **User Profile Management**: Allow users to update profiles
- **Batch Operations**: Bulk user approval/rejection
- **Export Functionality**: Export user data
- **Dashboard Analytics**: Advanced reporting

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact: support@trackerpro.com

---

**TrackerPro** - *Built with ❤️ using Spring Boot and modern web technologies*