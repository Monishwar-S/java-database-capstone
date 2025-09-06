Admin Login
Title:
As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely.

Acceptance Criteria:

Admin provides a valid username and password.

The system verifies credentials against the MySQL database.

On success, the admin is redirected to the dashboard; on failure, an error message is displayed.

Priority: High
Story Points: 3
Notes:

Consider password encryption and session management.

Admin Logout
Title:
As an admin, I want to log out of the portal, so that I can protect system access when I am not using it.

Acceptance Criteria:

Logout option is visible on the admin dashboard.

Clicking logout ends the active session.

Admin is redirected to the login page.

Priority: High
Story Points: 2
Notes:

Ensure session tokens are invalidated after logout.

Add Doctor
Title:
As an admin, I want to add doctors to the portal, so that they can access their dashboard and manage appointments.

Acceptance Criteria:

Admin can fill in required doctor details (name, email, specialization, etc.).

System validates inputs before saving.

Doctor profile is stored in the MySQL database and confirmation is displayed.

Priority: High
Story Points: 5
Notes:

Handle duplicate entries for email/username.

Delete Doctor Profile
Title:
As an admin, I want to delete a doctor’s profile from the portal, so that inactive or incorrect records are removed.

Acceptance Criteria:

Admin can select the doctor profile to delete.

System prompts for confirmation before deletion.

Doctor’s profile is removed from the MySQL database and a success message is shown.

Priority: Medium
Story Points: 3
Notes:

Consider preventing deletion if the doctor has active appointments.

Run Usage Statistics Procedure
Title:
As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics.

Acceptance Criteria:

A stored procedure exists in the database to count appointments per month.

Admin can execute the procedure via MySQL CLI.

The result displays the total number of appointments grouped by month.

Priority: Medium
Story Points: 2
Notes:

Optionally, integrate this with the dashboard for easier access.



------------------------------------------

