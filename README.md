# 🖼️ Image Gallery Web Application

A full-stack **Image Gallery** web application built with **Java Spring Boot** and **MySQL**, featuring user authentication, cloud image storage, and an admin panel.

---

## 🚀 Features

- ✅ **User Registration & Login** — Secure authentication using Spring Security & BCrypt
- ✅ **Image Upload** — Upload images directly to Cloudinary cloud storage
- ✅ **Private Gallery** — Each user sees only their own images
- ✅ **Search & Filter** — Search images by name instantly
- ✅ **Pagination** — Browse images 6 per page with Next/Previous buttons
- ✅ **Rename & Download** — Rename or download any image
- ✅ **Recycle Bin** — Soft delete with restore and permanent delete options
- ✅ **Profile Page** — View username, total images, and storage used
- ✅ **Admin Panel** — Manage users with role-based access control
- ✅ **Privacy Protected** — Admin cannot view user photos
- ✅ **Image Compression** — Auto-compress images on upload using Thumbnailator
- ✅ **Cloud Storage** — Images stored on Cloudinary (not local disk)

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 25, Spring Boot 3.5.0 |
| Security | Spring Security, BCrypt |
| Database | MySQL, Spring Data JPA, Hibernate |
| Storage | Cloudinary (Cloud Image Storage) |
| Frontend | HTML, CSS, JavaScript (Vanilla) |
| Architecture | MVC (Model-View-Controller) |
| Build Tool | Maven |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/example/gallery/
│   │   ├── config/
│   │   │   ├── CloudinaryConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   ├── ImageController.java
│   │   │   └── ProfileController.java
│   │   ├── dto/
│   │   │   └── ImageDTO.java
│   │   ├── entity/
│   │   │   ├── Image.java
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   ├── ImageRepository.java
│   │   │   └── UserRepository.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── ImageService.java
│   │   └── GalleryApplication.java
│   └── resources/
│       ├── static/
│       │   ├── login.html
│       │   ├── gallery.html
│       │   ├── profile.html
│       │   ├── trash.html
│       │   └── admin.html
│       └── application.properties
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 25
- MySQL 8.0+
- Maven
- Cloudinary Account (free at cloudinary.com)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/your-username/ImageGallery.git
cd ImageGallery
```

**2. Create MySQL Database**
```sql
CREATE DATABASE gallerydb;
```

**3. Configure application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gallerydb
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret
```

**4. Run the application**
```bash
mvn spring-boot:run
```

**5. Open in browser**
```
http://localhost:8080/login.html
```

---

## 👤 How to Use

### Normal User
1. Register a new account at `/login.html`
2. Login with your credentials
3. Upload images from the gallery page
4. Search, rename, download, or delete images
5. Deleted images go to **Recycle Bin** — restore or permanently delete

### Admin User
Create admin account by calling this endpoint once:
```
POST /api/auth/register-admin
Params: username, password, secretKey=ADMIN_SECRET_2024
```
Admin can view all users, their image counts, and storage used.

---

## 🗄️ Database Schema

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255)
);

CREATE TABLE images (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  path VARCHAR(255),
  cloudinary_public_id VARCHAR(255),
  file_type VARCHAR(255),
  file_size BIGINT,
  upload_time DATETIME(6),
  deleted BOOLEAN DEFAULT FALSE,
  deleted_at DATETIME(6),
  user_id BIGINT,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 🔐 Security Features

- Passwords hashed using **BCrypt**
- Session-based authentication via **Spring Security**
- Users can only access **their own images**
- Admin restricted to **user management only** — cannot view photos
- Role-based access control (`USER` / `ADMIN`)

---

## 📸 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login |
| GET | `/api/images` | Get user's images |
| POST | `/api/images/upload` | Upload image |
| DELETE | `/api/images/delete/{id}` | Move to trash |
| PUT | `/api/images/rename/{id}` | Rename image |
| GET | `/api/images/download/{id}` | Download image |
| GET | `/api/images/trash` | Get trash images |
| PUT | `/api/images/trash/restore/{id}` | Restore image |
| DELETE | `/api/images/trash/permanent/{id}` | Permanent delete |
| GET | `/api/profile` | Get profile stats |
| GET | `/api/admin/users` | Get all users (Admin) |

---

## 👨‍💻 Author

**Thirushika Vediyappan** — Java Full Stack Developer

---

## 📄 License

This project is for educational purposes.
