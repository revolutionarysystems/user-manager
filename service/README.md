User Manager Service  [![Build Status](https://travis-ci.org/revolutionarysystems/user-manager.svg?branch=master)](https://travis-ci.org/revolutionarysystems/user-manager)
====================

* [Accounts](#accounts)
* [Users](#users)
* [Applications](#applications)
* [Roles](#roles)
* [Permissions](#permissions)

## Accounts

### Properties

* name
* status
* applications
* attributes

### Create an Account

##### Example Request

```sh
curl -X POST -d '{"name": "Test Account 1", "status": "enabled", "attributes": {"p1": "v1"}}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/accounts/
```

##### Example Response

```json
{"id":"536a00f4b16dba135714d14a","name":"Test Account 1","status":"enabled","attributes":{"p1":"v1"}}
```

### Retreive an Account

##### Example Request

```sh
curl http://localhost:8080/user-manager-service/accounts/{accountId}
```

##### Example Response

```json
{"id":"536a00f4b16dba135714d14a","name":"Test Account 1","status":"enabled","attributes":{"p1":"v1"}}
```

### Update an Account

##### Updatable Properties

* name
* status
* applications
* attributes

##### Example Request

```sh
curl -X POST -d '{"status": "disabled"}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/accounts/{accountId}
```

##### Example Response

```json
{"id":"536a00f4b16dba135714d14a","name":"Test Account 2","status":"disabled","attributes":{"p1":"v1"}}
```

### Delete an Account

##### Example Request

```sh
curl -X DELETE http://localhost:8080/user-manager-service/accounts/{accountId}
```

## Users

### Properties

* name
* username
* password
* status
* account
* roles
* attributes

### Create a User

##### Example Request

```sh
curl -X POST -d '{"name": "Test User 1", "username": "me@email.com", "password": "password123", "status": "enabled", "account": "536a00f4b16dba135714d14a", "attributes": {"p1": "v1"}}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/users/
```

##### Example Response

```json
{"id":"536a06c7b16d038a9d300c31","name":"Test User 1","username":"me@email.com","password":"password123","status":"enabled","attributes":{"p1":"v1"},"account":"536a00f4b16dba135714d14a","roles":null}
```

### Retreive a User

##### Example Request

```sh
curl http://localhost:8080/user-manager-service/users/{userId}
```

##### Example Response

```json
{"id":"536a06c7b16d038a9d300c31","name":"Test User 1","username":"me@email.com","password":"password123","status":"enabled","attributes":{"p1":"v1"},"account":"536a00f4b16dba135714d14a","roles":null}
```

### Update a User

##### Updatable Properties

* name
* username
* password
* status
* account
* roles
* attributes

##### Example Request

```sh
curl -X POST -d '{"status": "disabled"}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/users/{userId}
```

##### Example Response

```json
{"id":"536a06c7b16d038a9d300c31","name":"Test User 1","username":"me@email.com","password":"password123","status":"disabled","attributes":{"p1":"v1"},"account":"536a00f4b16dba135714d14a","roles":null}
```

### Delete a User

##### Example Request

```sh
curl -X DELETE http://localhost:8080/user-manager-service/users/{userId}
```

## Applications

### Properties

* name
* description
* roles
* attributes

### Create an Application

##### Example Request

```sh
curl -X POST -d '{"name": "Test Application", "description": "This is a test application", "attributes": {"p1": "v1"}}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/applications/
```

##### Example Response

```json
{"id":"536a0b2cb16d038a9d300c32","name":"Test Application","description":"This is a test application","roles":null,"attributes":{"p1":"v1"}}
```

### Retreive an Application

##### Example Request

```sh
curl http://localhost:8080/user-manager-service/applications/{applicationId}
```

##### Example Response

```json
{"id":"536a0b2cb16d038a9d300c32","name":"Test Application","description":"This is a test application","roles":null,"attributes":{"p1":"v1"}}
```

### Update an Application

##### Updatable Properties

* name
* description
* roles
* attributes

##### Example Request

```sh
curl -X POST -d '{"attributes": {"p1": "v2"}}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/applications/{applicationId}
```

##### Example Response

```json
{"id":"536a0b2cb16d038a9d300c32","name":"Test Application","description":"This is a test application","roles":null,"attributes":{"p1":"v2"}}
```

### Delete a User

##### Example Request

```sh
curl -X DELETE http://localhost:8080/user-manager-service/applications/{applicationId}
```

## Roles

### Properties

* name
* description
* permissions

### Create a Role

##### Example Request

```sh
curl -X POST -d '{"name": "Test Role", "description": "This is a test role"}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/roles/
```

##### Example Response

```json
{"id":"536a4f17e6b975274927df1d","name":"Test Role","description":"This is a test role","permissions":null}
```

### Retreive an Role

##### Example Request

```sh
curl http://localhost:8080/user-manager-service/roles/{roleId}
```

##### Example Response

```json
{"id":"536a4f17e6b975274927df1d","name":"Test Role","description":"This is a test role","permissions":null}
```

### Update an Role

##### Updatable Properties

* name
* description
* permissions

##### Example Request

```sh
curl -X POST -d '{"description": "This is still a test role"}}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/roles/{roleId}
```

##### Example Response

```json
{"id":"536a4f17e6b975274927df1d","name":"Test Role","description":"This is still a test role","permissions":null}
```

### Delete a Role

##### Example Request

```sh
curl -X DELETE http://localhost:8080/user-manager-service/roles/{roleId}
```

## Permissions

### Properties

* name
* description

### Create a Permission

##### Example Request

```sh
curl -X POST -d '{"name": "Test Permission", "description": "This is a test role"}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/permissions/
```

##### Example Response

```json
{"id":"536a4fe2e6b98b6bd4dbd1ea","name":"Test Permission","description":"This is a test permission"}
```

### Retreive a Permission

##### Example Request

```sh
curl http://localhost:8080/user-manager-service/permissions/{permissionId}
```

##### Example Response

```json
{"id":"536a4fe2e6b98b6bd4dbd1ea","name":"Test Permission","description":"This is a test role"}
```

### Update a Permission

##### Updatable Properties

* name
* description

##### Example Request

```sh
curl -X POST -d '{"description": "This is still a test permission"}}' -H "Content-Type: application/json" http://localhost:8080/user-manager-service/permissions/{permissionId}
```

##### Example Response

```json
{"id":"536a4fe2e6b98b6bd4dbd1ea","name":"Test Permission","description":"This is still a test role"}
```

### Delete a Role

##### Example Request

```sh
curl -X DELETE http://localhost:8080/user-manager-service/permissions/{permissionId}
```
