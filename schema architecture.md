section 1: Architecture summary
This Spring Boot application follows a layered architecture that combines both MVC and REST design patterns. 
The Admin and Doctor dashboards are implemented using Thymeleaf templates, enabling dynamic rendering of server-side views.
For other modules, such as patient management, appointments, and prescriptions, the application exposes RESTful APIs that allow seamless interaction with the system.
The application integrates with two databases: MySQL and MongoDB. MySQL stores relational data such as patients, doctors, appointments, and admin records, which are managed using JPA entities.
MongoDB is used to store prescriptions as document models, offering flexibility in handling unstructured medical data. All incoming requests pass through controllers, which interact with a dedicated service layer.
The service layer encapsulates business logic and delegates persistence tasks to the appropriate repositories, ensuring a clean separation of concerns and easier maintainability.

Section 2: Numbered flow of data and control
1.User Request – A request is initiated either from the Admin/Doctor dashboards (via Thymeleaf pages) or from external clients through REST APIs.
2.Controller Handling – The request is received by the appropriate controller (MVC or REST), which acts as the entry point into the application.
3.Service Layer Processing – The controller forwards the request to the service layer, where the core business logic is executed.
4.Repository Delegation – The service layer interacts with the repository layer to retrieve, save, or update data.
5.Database Access – Depending on the request type, the repository communicates with either MySQL (for structured data such as users, appointments, and admin records) or MongoDB (for unstructured prescription documents).
6.Response Assembly – The retrieved or updated data is returned from the repository to the service layer, which processes and prepares the response.
7.User Response – Finally, the controller sends the response back to the client: rendered HTML via Thymeleaf for dashboards, or JSON/XML output via REST APIs for other modules.
