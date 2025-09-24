

Test_Cases - UserService


# Register

System: FineForms (User-Service @ http://localhost:8070/api/users/register),
Environment: Docker local (Postgres 15, User-Service 8070)

#### TestCase - Scenario - Expected - Result- {code} - API response

**1)** TC01 - ValidRegister(valid data), (email, password, firstName, lastName) - 201 - 201 - **{ "email": "qa_user1@gmail.com", "password": "Passw0rd!", "firstName": "QA", "lastName": "UserOne" }** - **{ "id": 1, "email": "qa_user1@gmail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"}**

**2)** TC02 - Valid Register with + in email - 201 - 201 - **{ "email": "qa.user+tag@gmail.com", "password": "Str0ngPass!", "firstName": "QA", "lastName": "UserTwo" }** - **{ "id": 2, "email": "qa.user+tag@gmail.com", "firstName": "QA", "lastName": "UserTwo", "role": "USER"}**

**3)** TC03 - Valid Register with Unicode name - 201 - 201 - **{ "email": "qa_user3@gmail.com", "password": "Qwerty123!", "firstName": "Đorđe", "lastName": "Živković" }** - **{ "id": 3, "email": "qa_user3@gmail.com", "firstName": "Đorđe", "lastName": "Živković", "role": "USER"}**

**4)** TC04 - Valid Register with whitespace trimming - 201 - 201 - **{ "email": "qa_user4@gmail.com", "password": "Valid123!", "firstName": "   Milan   ", "lastName": "   Jovanović   " }** - **{ "id": 4, "email": "qa_user4@gmail.com", "firstName": "   Milan   ", "lastName": "   Jovanović   ", "role": "USER"}**

**Backend developer team notified**: User successfully registered as **firstName: "   Milan   ", lastName: "   Jovanović   "**, when it should be **firstName: "Milan", lastName: "Jovanović"**

**5)** TC05 - Empty email - 400 - 400 - **{ "email": "", "password": "Valid123!", "firstName": "QA", "lastName": "UserFive" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**6)** TC06 - Invalid, email missing @ - 400 - 400 - **{ "email": "qa_user6gmail.com", "password": "Valid123!", "firstName": "QA", "lastName": "UserSix" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**7)** TC07 - Invalid email, missing domain - 400 - 400 - **{ "email": "qa_user7@", "password": "Valid123!", "firstName": "QA", "lastName": "UserSeven" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**8)** TC08 - Invalid email with special chars - 400 - 400 - **{ "email": "qa_user8@#$.com", "password": "Valid123!", "firstName": "QA", "lastName": "UserEight" }** **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**9)** TC09 - Duplicate email - 409 - 409 - **{ "email": "qa_user1@gmail.com", "password": "Valid123!", "firstName": "Duplicate", "lastName": "User" }** - **{"status": 409, "message": "User with email qa_user4@gmail.com already exists", "error": "Conflict"}**

**10)** TC10 - Empty password - 400 - 400 - **{ "email": "qa_user10@gmail.com", "password": "", "firstName": "QA", "lastName": "UserTen" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**11)** TC11 - Too short password - 400 - 400 - **{ "email": "qa_user11@gmail.com", "password": "Ab1", "firstName": "QA", "lastName": "UserEleven" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**12)** TC12 - Password with only whitespace - 400 - 400 - **{ "email": "qa_user15@gmail.com", "password": "     ", "firstName": "QA", "lastName": "UserFifteen" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**13)** TC13 - Empty firstName - 400 - 400 - **{ "email": "qa_user16@gmail.com", "password": "Valid123!", "firstName": "", "lastName": "UserSixteen" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**14)** TC14 - Empty lastName - 400 - 400 - **{ "email": "qa_user17@gmail.com", "password": "Valid123!", "firstName": "QA", "lastName": "" }** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

**15)** TC15 - Name(first/last) with numbers - 400 - **201** - **{ "email": "qa_user18@gmail.com", "password": "Valid123!", "firstName": "Milan123", "lastName": "QA" }** - **{ "id": 11, "email": "qa_user18@gmail.com", "firstName": "Milan123", "lastName": "QA", "role": "USER"}**

**Backend developer team notified**: User successfully registered as **firstName: "Milan123"**, when a users first name should never contain numbers.

**16)** TC16 - Name(first/last) with special characters - 400 - **201** - **{ "email": "qa_user19@gmail.com", "password": "Valid123!", "firstName": "@#!^", "lastName": "QA" }** - **{ "id": 12, "email": "qa_user19@gmail.com", "firstName": "@#!^", "lastName": "QA", "role": "USER"}**

**Backend developer team notified**: User successfully registered as **firstName: "@#!^"**, when a users first name should never contain special characters.

**17)** TC17 - Name(first/last) too long - 400 - **201**- **{ "email": "qa_user20@mail.com", "password": "Valid123!", "firstName": "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "lastName": "LongName" }** - **{ "email": "qa_user20@mail.com", "password": "Valid123!", "firstName": "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "lastName": "LongName" }**

**Backend developer team notified**: User successfully registered as **firstName: "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"**, when a users first name should never exceed 50 characters.

**18)** TC18 - Empty body - 400 - 400 - **{}** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

# Login

System: FineForms (User-Service @ http://localhost:8070/api/users/login),
Environment: Docker local (Postgres 15, User-Service 8070)

#### TestCase - Scenario - Expected - Result- {code} - API response

**1)** TC01 - Valid Login (correct email & password) - 200 - 200 - **{ "email": "qa_user1@mail.com", "password": "Passw0rd!" }** - **{"id": 3, "email": "qa_user1@mail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"}**

**2)** TC02 - Valid Login with uppercase email - 200 - 200 - **{ "email": "QA_USER1@MAIL.COM", "password": "Passw0rd!" }** - **{"id": 3, "email": "qa_user1@mail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"}**

**3)** TC03 - Valid Login with trimmed email - 200 - 200 - **{ "email": "   qa_user1@mail.com   ", "password": "Passw0rd!" }** - **{"id": 3, "email": "qa_user1@mail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"}**

**4)** TC04 - Empty email - 400 - 400 - **{ "email": "", "password": "Passw0rd!" }** - **{"error": "Bad Request", "status": 400, "message": "Email must be provided !"}**

**5)** TC05 - Invalid email format (missing @) - 400 - 400 - **{ "email": "qa_user2mail.com", "password": "Passw0rd!" }** - **{"error": "Bad Request", "status": 400, "message": "Provided email is not valid !"}**

**6)** TC06 - Invalid email format (missing domain) - 400 - **{ "email": "qa_user3@", "password": "Passw0rd!" }** - **{"error": "Bad Request", "status": 400, "message": "Provided email is not valid !"}**

**7)** TC07 - Email not registered - 401 - 401 - **{ "email": "not_exists@mail.com", "password": "Passw0rd!" }** - **{"error": "Unauthorized", "status": 401, "message": "Invalid email or password!"}**

**8)** TC08 - Empty password - 400 - 400 - **{ "email": "qa_user4@mail.com", "password": "" }** - **{"error": "Bad Request", "status": 400, "message": "Password must be provided !"}**

**9)** TC09 - Wrong password - 401 - 401 - **{ "email": "qa_user1@mail.com", "password": "WrongPass123" }** - **{"error": "Unauthorized", "status": 401, "message": "Invalid email or password!"}**

**10)** TC10 - Password too short - 400 - 400 - **{ "email": "qa_user5@mail.com", "password": "123" }** - **{"error": "Unauthorized", "status": 401, "message": "Invalid email or password!"}**

**11)** TC11 - Password only whitespace - 400 - 400 - **{ "email": "qa_user6@mail.com", "password": "    " }** - **{"error": "Unauthorized", "status": 401, "message": "Invalid email or password!"}**

# ChangeRole, GET, Update



### ChangeRole ( TestCase - Scenario - Expected - Result - Request - Response)

System: FineForms (User-Service @ http://localhost:8070/users/change-role/USER_ID),
Environment: Docker local (Postgres 15, User-Service 8070)

**1)** TC01 - Change role from USER to ADMIN - 200 - 200 - **localhost:8070/users/change-role/3** - **{"id": 3, "email": "qa_user1@gmail.com", "firstName": "QA", "lastName": "UserOne", "role": "ADMIN"}**

**2)** TC02 - Change role from ADMIN to USER - 200 - 200 - **localhost:8070/users/change-role/3** - **{"id": 3, "email": "qa_user1@gmail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"}**

**3)** TC03 - Change role for non-existing user - 404 - 404 - **localhost:8070/users/change-role/65** - **{"error": "Not Found", "message": "User with id 65 not found", "status": 404}**

### GET ( Register 3 new users, then show response after GET)

Expected to show 3 new registered users, with ID's: {20,21,22}

Three registered users: 
**{ "email": "qa_get_user1@gmail.com", "password": "Passw0rd!", "firstName": "QA", "lastName": "UserOne" } ; 
{ "email": "qa_get_user2@gmail.com", "password": "Passw0rd!", "firstName": "QA", "lastName": "UserTwo" } ; 
{ "email": "qa_get_user3@gmail.com", "password": "Passw0rd!", "firstName": "QA", "lastName": "UserThree" }**

Response after GET:
**{"id": 20, "email": "qa_get_user1@gmail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"};
{"id": 21, "email": "qa_get_user2@gmail.com", "firstName": "QA", "lastName": "UserTwo", "role": "USER"};
{"id": 22, "email": "qa_get_user3@gmail.com", "firstName": "QA", "lastName": "UserThree", "role": "USER"}**


### Update ( TestCase - Scenario - Expected - Result - {code} - Response)

System: FineForms (User-Service @ http://localhost:8070/users/edit/USER_ID),
Environment: Docker local (Postgres 15, User-Service 8070)

**1)** TC01 - Update firstName only - 200 - 200 - **{ "email": "qa_user1@gmail.com", "password": "", "firstName": "UpdatedFirstName", "lastName": "" }** - **{"id":3,"email":"qa_user1@gmail.com","firstName":"UpdatedFirstName", "lastName": "UserOne", "role": "USER"}**

**2)** TC02 - Update lastName only - 200 - 200 - **{ "email": "qa_user1@gmail.com", "password": "", "firstName": "", "lastName": "UpdatedLastName" }** - **{"id":3,"email":"qa_user1@gmail.com","firstName":"UpdatedFirstName", "lastName": "UpdatedLastName", "role": "USER"}**

**3)** TC03 - Update email only - 200 - 200 - **{ "email": "newemail@gmail.com", "password": "", "firstName": "", "lastName": "UpdatedLastName" }** - **{"id":3,"email":"newemail@gmail.com","firstName":"UpdatedFirstName", "lastName": "UpdatedLastName", "role": "USER"}**

**4)** TC04 - Update name to name with **valid** special characters - 200 - 200 - **{ "email": "qa_user2@mail.com", "password": "", "firstName": "Đorđe", "lastName": "Živković" }** - **{"id": 5, "email": "qa_user2@mail.com", "firstName": "ore", "lastName": "ivkovi", "role": "USER"}**

** **Backend developer team notified**: User successfully updated to **firstName: “ore”**, **lastName :  “ivkovi”** when he should've been named as prompted **firstName: “Đorđe”**, **lastName :  “Živković”**.

**5)** TC05 - Update name to name with **invalid** special characters - 400 - 400 - **{ "email": "qa_user2@mail.com", "password": "", "firstName": "!#@$", "lastName": "Živković" }** - **{"error": "Bad Request", "message": "Invalid input field First name", "status": 400}**

**6)** TC06 - Update email to an already existing email - 409 - 409 - USER_ID = 50 - **{ "email": "qa_user2@gmail.com", "password": "", "firstName": "QA", "lastName": "UserTwo" }** - **{"error": "Conflict", "message": "User with email qa_user2@gmail.com already exists", "status": 409}**
