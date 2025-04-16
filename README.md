🔔 NotificationAndContact-svc

NotificationAndContact-svc is a dedicated RESTful microservice designed to manage user notification preferences and process contact messages. It works in seamless integration with RetroClubKit, ensuring effective communication and smooth handling of notifications across the platform.

🛠 Tech Stack (Back-End):

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **MySQL**

🧪 Testing:

- JUnit
- Mockito
- Spring Boot Test
- MockMvc

🚀 Deployment:

- Maven

🧩 Architecture:

- RESTful Microservice
- Separate database for notifications and contact messages
- Designed to integrate with the main RetroClubKit application (transitioning towards a full microservices architecture)

🎯 About NotificationAndContact-svc

This microservice enables RetroClubKit to:
  
✅ Manage user notification settings and preferences  
✅ Receive and store contact messages sent by users  
✅ Support secure and efficient user-to-admin communication  
✅ Provide a scalable solution for notification handling as the platform evolves

📦 Key Features

🔔 **Notifications Management**
- Configure and update notification preferences per user
- Enable or disable notifications for different message types

📬 **Contact Message Processing**
- Receive and store messages from the integrated contact form
- Provide a reliable channel for users to contact the admin

🔄 **REST API Endpoints**
- Create, read, update, and delete (CRUD) operations for notifications and contact messages
- Secure endpoints to maintain data integrity and confidentiality

🔌 **Integration with RetroClubKit**
- Seamless communication between the microservice and RetroClubKit
- Real-time updates for enhanced user experience
