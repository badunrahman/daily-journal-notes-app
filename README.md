#  Daily Journal & Notes App

A **JavaFX-based journaling application** with **Notion-style blocks**, **theme switching**, and **multi-language support (EN/FR)**.  
Each user has their own secure account with journals stored in a SQLite database.

---

##  Features

- **User Authentication**
  - Auto-generated unique **User ID** on registration
  - Login using **User ID + Password**
  - Validation and error handling for security

- **Dashboard**
  - Displays all journals with **Title, Date Created, Date Modified, Tags**
  - Search journals
  - Delete entries
  - User info and logout

- **Journal Editor**
  - Full-screen editor with **auto-save**
  - Slash (`/`) commands for blocks:
    - `/todo` → To-do lists
    - `/toggle` → Toggle blocks
    - `/bullet` → Bullet lists
    - `/heading` → Headings (H1, H2, H3)
  - Text formatting: **Bold, Italic, Underline**
  - Tagging system with colors
  - Nesting blocks (e.g., Todo inside Toggle)

- **Customization**
  - Themes: Light, Dark, Blue, Green, Purple
  - Languages: English 🇺🇸 & French 🇫🇷

- **Persistence**
  - SQLite database ensures user data is **saved securely**

---

##  Screenshots

### Login & Register
<img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 26 29 PM" src="https://github.com/user-attachments/assets/1ccf1da0-62a2-4d40-b3ec-87e92fa5685a" />

<img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 26 49 PM" src="https://github.com/user-attachments/assets/e4eba2dc-9894-4515-8a69-b580e19900b1" />

![Register Success] <img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 26 57 PM" src="https://github.com/user-attachments/assets/17993b82-6909-48dd-8c9e-e5c33699b5e8" />


### Dashboard
![Dashboard Light] <img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 27 11 PM" src="https://github.com/user-attachments/assets/d8d17048-914d-4648-8bf5-08c9fbfbfd5a" />

![Dashboard Green]<img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 27 24 PM" src="https://github.com/user-attachments/assets/23ff0f3e-b361-4b5c-b2e1-f44ce1e414a3" />


### Editor
![Editor Empty] <img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 27 33 PM" src="https://github.com/user-attachments/assets/6ed78831-02bc-4162-95a4-f8759e2fdca6" />

![Editor Blocks] <img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 30 44 PM" src="https://github.com/user-attachments/assets/f5ad48ce-c54c-4884-a6d6-d9aaa05f38df" />

<img width="1710" height="1107" alt="Screenshot 2025-09-01 at 9 30 53 PM" src="https://github.com/user-attachments/assets/321f209e-d08d-48d0-9bad-259452d6035c" />

---

## ⚙ Technologies Used

- Java 23  
- JavaFX 21  
- Maven 3.9+  
- SQLite  

---

## 🛠 Installation & Setup

1. Install **Java JDK 23+** and **Maven 3.9+**
   ```bash
   java -version
   mvn -version
