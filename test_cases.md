

# Test_Cases - UserService


## Register

System: FormApp (User-Service @ http://localhost:8070/api/users/register),
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

**FIXED!**

**16)** TC16 - Name(first/last) with special characters - 400 - **201** - **{ "email": "qa_user19@gmail.com", "password": "Valid123!", "firstName": "@#!^", "lastName": "QA" }** - **{ "id": 12, "email": "qa_user19@gmail.com", "firstName": "@#!^", "lastName": "QA", "role": "USER"}**

**Backend developer team notified**: User successfully registered as **firstName: "@#!^"**, when a users first name should never contain special characters.

**FIXED!**

**17)** TC17 - Name(first/last) too long - 400 - **201**- **{ "email": "qa_user20@mail.com", "password": "Valid123!", "firstName": "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "lastName": "LongName" }** - **{ "email": "qa_user20@mail.com", "password": "Valid123!", "firstName": "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "lastName": "LongName" }**

**Backend developer team notified**: User successfully registered as **firstName: "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"**, when a users first name should never exceed 50 characters.

**FIXED!**

**18)** TC18 - Empty body - 400 - 400 - **{}** - **{"status": 400, "error": "Bad Request", "path": "/api/users/register"}**

## Login

System: FormApp (User-Service @ http://localhost:8070/api/users/login),
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



## ChangeRole

System: FormApp (User-Service @ http://localhost:8070/users/change-role/USER_ID),
Environment: Docker local (Postgres 15, User-Service 8070)

#### TestCase - Scenario - Expected - Result - Request - Response

**1)** TC01 - Change role from USER to ADMIN - 200 - 200 - **localhost:8070/users/change-role/3** - **{"id": 3, "email": "qa_user1@gmail.com", "firstName": "QA", "lastName": "UserOne", "role": "ADMIN"}**

**2)** TC02 - Change role from ADMIN to USER - 200 - 200 - **localhost:8070/users/change-role/3** - **{"id": 3, "email": "qa_user1@gmail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"}**

**3)** TC03 - Change role for non-existing user - 404 - 404 - **localhost:8070/users/change-role/65** - **{"error": "Not Found", "message": "User with id 65 not found", "status": 404}**

## GET ( Register 3 new users, then show response after GET)

Expected to show 3 new registered users, with ID's: {20,21,22}

Three registered users: 
**{ "email": "qa_get_user1@gmail.com", "password": "Passw0rd!", "firstName": "QA", "lastName": "UserOne" } ; 
{ "email": "qa_get_user2@gmail.com", "password": "Passw0rd!", "firstName": "QA", "lastName": "UserTwo" } ; 
{ "email": "qa_get_user3@gmail.com", "password": "Passw0rd!", "firstName": "QA", "lastName": "UserThree" }**

Response after GET:
**{"id": 20, "email": "qa_get_user1@gmail.com", "firstName": "QA", "lastName": "UserOne", "role": "USER"};
{"id": 21, "email": "qa_get_user2@gmail.com", "firstName": "QA", "lastName": "UserTwo", "role": "USER"};
{"id": 22, "email": "qa_get_user3@gmail.com", "firstName": "QA", "lastName": "UserThree", "role": "USER"}**


## Update

System: FormApp (User-Service @ http://localhost:8070/users/edit/USER_ID),
Environment: Docker local (Postgres 15, User-Service 8070)

 ### TestCase - Scenario - Expected - Result - {code} - Response

**1)** TC01 - Update firstName only - 200 - 200 - **{ "email": "qa_user1@gmail.com", "password": "", "firstName": "UpdatedFirstName", "lastName": "" }** - **{"id":3,"email":"qa_user1@gmail.com","firstName":"UpdatedFirstName", "lastName": "UserOne", "role": "USER"}**

**2)** TC02 - Update lastName only - 200 - 200 - **{ "email": "qa_user1@gmail.com", "password": "", "firstName": "", "lastName": "UpdatedLastName" }** - **{"id":3,"email":"qa_user1@gmail.com","firstName":"UpdatedFirstName", "lastName": "UpdatedLastName", "role": "USER"}**

**3)** TC03 - Update email only - 200 - 200 - **{ "email": "newemail@gmail.com", "password": "", "firstName": "", "lastName": "UpdatedLastName" }** - **{"id":3,"email":"newemail@gmail.com","firstName":"UpdatedFirstName", "lastName": "UpdatedLastName", "role": "USER"}**

**4)** TC04 - Update name to name with **valid** special characters - 200 - 200 - **{ "email": "qa_user2@mail.com", "password": "", "firstName": "Đorđe", "lastName": "Živković" }** - **{"id": 5, "email": "qa_user2@mail.com", "firstName": "ore", "lastName": "ivkovi", "role": "USER"}**

**Backend developer team notified**: User successfully updated to **firstName: “ore”**, **lastName :  “ivkovi”** when he should've been named as prompted **firstName: “Đorđe”**, **lastName :  “Živković”**.

**FIXED!**

**5)** TC05 - Update name to name with **invalid** special characters - 400 - 400 - **{ "email": "qa_user2@mail.com", "password": "", "firstName": "!#@$", "lastName": "Živković" }** - **{"error": "Bad Request", "message": "Invalid input field First name", "status": 400}**

**6)** TC06 - Update email to an already existing email - 409 - 409 - USER_ID = 50 - **{ "email": "qa_user2@gmail.com", "password": "", "firstName": "QA", "lastName": "UserTwo" }** - **{"error": "Conflict", "message": "User with email qa_user2@gmail.com already exists", "status": 409}**

# Test_Cases - FormService

## Create

System: FineForms (Form-Service @ http://localhost:8060/form?usedId=Id),
Environment: Docker local (Postgres 15, Form-Service 8060)

### TestCase - Scenario - Expected - Result - {code} - Response

**1)** TC01 - Valid create form - 201 - 201 - **{"ownerId": 2, "title": "Druga test forma", "description": "Ovo je forma drugog vlasnika", "requiresAuth": true, "questions": [{"text": "Koji je vaš email?", "type": "email", "required": true}]}** - 
**[{"id": 1, "ownerId": 2, "title": "Druga test forma", "description": "Ovo je forma drugog vlasnika", "requiresAuth": true, "questions": [{"id": 1, "text": "Koji je vaš email?", "requiredQuestion": true, "type": "email", "position": 0, "imageUrl": null, "numberMin": 0, "numberMax": 0, "numberStep": 0, "options": []}], "collaborators": []}]**

**2)** TC02 - Create form without title - 201 - 201 - **{"ownerId": 2, "title": "", "description": "Forma bez naslova" , "requiresAuth": true, "questions": []}** - **{"id": 2, "ownerId": 2, "title": "Untitled Form", "description": "Forma bez naslova", "requiresAuth": true, "questions": [], "collaborators": null}**

**3)** TC03 - Create form with invalid question type - 400 - **201** - **{"ownerId": 2, "title": "Forma sa losim tipom", "description": "Ovo je forma loseg tipa odgovora", "requiresAuth": true, "questions": [{"text": "Koliko imate godina?", "type": "numbeerrr", "required": true}]}** - 
**[{"id": 1, "ownerId": 2, "title": "Forma sa losim tipom", "description": "Ovo je forma loseg tipa odgovora", "requiresAuth": true, "questions": [{"id": 1, "text": "Koliko imate godina?", "requiredQuestion": true, "type": "numbeerrr", "position": 0, "imageUrl": null, "numberMin": 0, "numberMax": 0, "numberStep": 0, "options": []}], "collaborators": []}]**

**Backend developer team notified**: Form successfully created with **type: "numbeerrr"** when only **type: "number"** for this field should be acceptable.

**FIXED!**

**4)** TC04 - Create form with non-existing owner - 403 - 403 - **{"ownerId": 2, "title": "Koje je vaše ime?", "description": "OwnerId ne postoji", "requiresAuth": true, "questions": [{"text": "Kako se zovete?", "type": "text", "required": true}]}** - **{"status": 403, "error": "Forbidden", "message": "Invalid ownerId", "path": "/form"}**

**5)** TC05 - Create form without provided ownerId - 201 - 201 (ownerId set to request creating userId[1]) - **{"ownerId": "", "title": "Prazan ownerId", "description": "OwnerId polje je prazno", "requiresAuth": true, "questions": [{"text": "Koja Vam je omiljena boja?", "type": "text", "required": true}]}** - 
**[{"id": 1, "ownerId": 1, "title": "Prazan ownerId", "description": "OwnerId polje je prazno", "requiresAuth": true, "questions": [{"id": 1, "text": "Prazan ownerId", "requiredQuestion": true, "type": "numbeerrr", "position": 0, "imageUrl": null, "numberMin": 0, "numberMax": 0, "numberStep": 0, "options": []}], "collaborators": []}]**

**6)** TC06 - Get public form, valid - 200 - 200 - **localhost:8060/form/public/2** - **{"id": 2, "title": "Forma 1 ", "description": "Forma 1 test", "questions": [
 {
            "id": 2,
            "text": "Pitanje 1 ",
            "type": "LONG_ANSWER",
            "options": [],
            "imageUrl": null,
            "required": false,
            "numberMin": 0,
            "numberMax": 0,
            "numberStep": 1,
            "minRequiredAnswers": null,
            "maxAllowedAnswers": null,
            "numericValues": null,
            "formId": null
        }
    ], "collaborators": [], "requiresAuth": false}**

**7)** TC07 - Get public form, invalid - 403 - 403 - **localhost:8060/form/public/1** - **{"status": 403, "error": "Forbidden", "message": "Form with id 1 requires authentication and is not publicly accessible", "path": "/form/public/1"}**



## Update

System: FineForms (Form-Service @ http://localhost:8060/form/Id?userId),
Environment: Docker local (Postgres 15, Form-Service 8060)

### TestCase - Scenario - Expected - Result - {code} - Response

**1)** TC01 - Valid update form - 200 - 200 - **{"ownerId": 1, "title": "AŽURIRANI NASLOV FORME", "description": "Ovo je ažurirani opis", "requiresAuth": true}** - 
**[{"id": 1, "ownerId": 1, "title": "AŽURIRANI NASLOV FORME", "description": "Ovo je ažurirani opis", "requiresAuth": true, "questions": [{"id": 1, "text": "Koja Vam je omiljena boja?", "requiredQuestion": true, "type": "text", "position": 0, "imageUrl": null, "numberMin": 0, "numberMax": 0, "numberStep": 0, "options": []}], "collaborators": []}]**

**2)** TC02 - Update ownerId - 200 - 200 - **{"ownerId": 5, "title": "AŽURIRANI NASLOV FORME", "description": "Ovo je ažurirani opis", "requiresAuth": true}** - 
**[{"id": 1, "ownerId": 1, "title": "AŽURIRANI NASLOV FORME", "description": "Ovo je ažurirani opis", "requiresAuth": true, "questions": [{"id": 1, "text": "Koja Vam je omiljena boja?", "requiredQuestion": true, "type": "text", "position": 0, "imageUrl": null, "numberMin": 0, "numberMax": 0, "numberStep": 0, "options": []}], "collaborators": []}]**

**Backend developer team notified**: Form successfully updated, however, "ownerId" never gets updated to a value that's not provided in request parameter **userId** : localhost:8060/form/1?**userId**.

**FIXED!**

**3)** TC03 - Update form tittle to empty tittle - 200 - 200 - **{"ownerId": "1", "title": "", "description": "Ovo je ažurirani opis", "requiresAuth": true}** - 
**{"id": 2, "ownerId": 1, "title": "", "description": "Ovo je ažurirani opis", "requiresAuth": true, "questions": [{"id": 2, "text": "Koja Vam je omiljena boja?", "requiredQuestion": true, "type": "text", "position": 0, "imageUrl": null, "numberMin": 0, "numberMax": 0, "numberStep": 0, "options": []}], "collaborators": []}**

**Backend developer team notified**: Form successfully updated, however, **tittle** should remain as it was prior to the update **(AŽURIRANI NASLOV FORME)**.

**FIXED!**


## Delete

System: FineForms (Form-Service @ http://localhost:8060/form/Id?userId),
Environment: Docker local (Postgres 15, Form-Service 8060)

### TestCase - Scenario - Expected - Result - Request - Response

**1)** TC01 - Valid form delete - 200 - 200 - **localhost:8060/form/1?userId=1** - {}

**2)** TC02 - Form delete, current session userId different from ownerId - 403 - 403 - **localhost:8060/form/2?userId=534** - **{"status": 403, "error": "Forbidden", "message": "Only the owner can delete the form.", "path": "/form/2"}**

**FIXED!**

**3)** TC03 - Delete form with wrong formId - 500 - 500 - **localhost:8060/form/4?userId=534** - **{"status": 500, "error": "Internal Server Error", "message": "Form not found: 4", "path": "/form/4"}**

## Collaborators

System: 
FineForms (Form-Service @ http://localhost:8060/form/Id/collab?userId=Id&role=EDITOR&currentUserId=Id),
Environment: Docker local (Postgres 15, Form-Service 8060)

### TestCase - Scenario - Expected - Result - Request - Response

**1)** TC01 - Create collaborator, valid - 200 - 200 - 
**localhost:8060/form/1/collab?userId=5&collaboratorRole=EDITOR&currentUserId=2** - 
**{"id": 2, "userId": 5, "formId": 1, "email": null, "role": "EDITOR}**

**2)** TC02 - Create collaborator, non-owner - 500 - 500 - 
**localhost:8060/form/1/collab?userId=5&collaboratorRole=EDITOR&currentUserId=1** - 
**{"status": 500, "error": "Internal Server Error", "message": "Only the owner can add collaborators.", "path": "/form/1/collab"}**

**3)** TC03 - Get all collaborators - 200 - 200 - **localhost:8060/form/1/collab** - 
**[{"id": 1, "userId": 2, "role": "EDITOR"}, {"id": 2, "userId": 5, "role": "EDITOR"}]**

**4)** TC04 - Get all collaborators, non-existing form - 400 - **200** - **localhost:8060/form/5/collab** - **[]**

**Backend developer team notified**: Response shows an empty collaborators list **[]** for a form that does **not** exist. Expected an error with appropriate message.

**FIXED!**

**5)** TC05 - Update collaborator role, valid - 200 - 200 - 
**localhost:8060/form/1/collab/5/update-role?role=VIEWER&currentUserId=2** - **{"id": 2, "userId": 5, "role": "VIEWER"}**

**6)** TC06 - Update collaborator, non-owner - 500 - 500 - 
**localhost:8060/form/1/collab/5/update-role?role=VIEWER&currentUserId=5** - 
**{"status": 500, "error": "Internal Server Error", "message": "Only the owner can update collaborators.", "path": "/form/1/collab"}**

**7)** TC07 - Update collaborator role, non existing collaborator - 500 - 500 - 
**localhost:8060/form/1/collab/3/update-role?role=VIEWER&currentUserId=2** - 
**{"status": 500, "error": "Internal Server Error", "message": "Collaborator not found for user: 3", "path": "/form/1/collab/3/update-role"}**

**8)** TC08 - Update collaborator role, non existing form - 500 - 500 - **localhost:8060/form/3/collab/5/update-role?role=VIEWER&currentUserId=2** - 
**{"status": 500, "error": "Internal Server Error", "message": "Form not found with id: 3", "path": "/form/3/collab/5/update-role"}**

**9)** TC09 - Delete collaborator, valid - 204 - 204 - **localhost:8060/form/1/collab/5?currentUserId=2**

**10)** TC10 - Delete collaborator, non-owner - 500 - 500 - **localhost:8060/form/1/collab/2?currentUserId=2** - **{"status": 500, "error": "Internal Server Error", "message": "Only the owner can remove collaborators.", "path": "/form/1/collab"}**

# Test_Cases - ResponseService

## Create

System: FineForms (Response-Service @ [http://localhost:8050/response](http://localhost:8050/response "http://localhost:8050/response")),
Environment: Docker local (Postgres 15, Response-Service 8050)

### TestCase - Scenario - Expected - Result - Request - Response

**1)** TC01 - Create response - 201 - 201 - **{"formId": 1, "userId": 123, "userEmail": "account@example.com", "answers": {"q1": "Da"}}** - **{"id": 1, "formId": 1, "userId": 123, "userEmail": "account@example.com", "answers": {"q1": "Da"}, "submittedAt": "2025-09-26 19:11:44", "isAuthenticated": true}**

**2)** TC02 - Create anonymous response - 201 - 201 - **{"formId": 1, "answers": {"q1": "Ne"}}** - **{"id": 2, "formId": 1, "userId": 0, "userEmail": null, "answers": {"q1": "Ne"}, "submittedAt": "2025-09-26 19:15:26", "isAuthenticated": false}**

**3)** TC03 - Get all form responses - 201 - 201 - **localhost:8050/response/1?currentUserId=123** - 
**[{"id": 1, "formId": 1, "userId": 123, "userEmail": "account@example.com", "answers": {"q1": "Da"}, "submittedAt": "2025-09-26 19:11:44", "isAuthenticated": true}, {"id": 2, "formId": 1, "userId": 0, "userEmail": null, "answers": {"q1": "Ne"}, "submittedAt": "2025-09-26 19:15:26", "isAuthenticated": false}]**

**4)** TC04 - Get specific response - 201 - 201 - **localhost:8050/response/1/5?currentUserId=15** - **{"id": 5, "formId": 1, "userId": 15, "userEmail": "account@example.com", "answers": {"q1": "Da"}, "submittedAt": "2025-09-26 19:29:23","isAuthenticated": true}**

**5)** TC05 - Delete response - 204 - 204 - **localhost:8050/response/1?currentUserId=123**

**6)** TC06 - Export CSV - 200 - 200 - **localhost:8050/response/export?formId=1**
