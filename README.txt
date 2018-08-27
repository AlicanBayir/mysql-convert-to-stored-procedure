This project provides mysql sql convert to stored procedure for basic crud operations.

Connection Field Definitions:
-DB: DB name that you want to connect.
-URL: Your mysql server url.
-User Name: Your mysql username.
-Password: Your mysql password.
-Connect: Connect for mysql server.

Operations Definitions:
-Read: This operation creates stored procedure that corresponding sql read  for selected table. 
-Create: This operation creates stored procedure that corresponding sql create  for selected table. 
        -Returns inserted recordId.
-Update: This operation creates stored procedure that corresponding sql update for selected table. 
-Delete: This operation creates stored procedure that corresponding sql delete for selected table.
If selected table includes status ENUM('active', 'deleted') column , update query will be created according to status column instead of delete query.
-Type JS File: This operation creates node.js entity class for selected table.  
  
