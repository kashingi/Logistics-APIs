# Distribution and Logistics Management Platform

A Spring Boot RESTful API for Distributions and Logistics management

## Features

- User registration and authentication with JWT
- Category management and reports
- Product management and reports
- Cart management and reports
- Order management and reports
- Sales management and reports
- Logistics and delivery
- riders management and reporting
- Customer management and reporting
- Inventory management and Warehousing
- MySQL database integration

## Tech Stack

- Java 25
- Spring Boot 3.4+
- Spring Security
- Spring Data JPA
- MySQL
- JWT (JSON Web Tokens)
- JUnit for testing
- Postman as the testing tool

## Project Structure

```
src/main/java/com/expensetracker/
├── controllers/       # REST controllers
├── config/            # Configuration classes
├── constants/         # Application constants
├── dto/               # Data Transfer Objects
├── entity/            # JPA entities
├── enums/             # Application enums
├── repository/        # JPA repositories
├── security/          # JWT utilities and filters
├── service/           # Service interfaces
├── servicesimpl/      # Service implementations
└── utils/             # Utility classes
```

## Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE logisticdb;
```

2. Update `application.properties` with your MySQL credentials:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## API Endpoints

### Authentication
- `POST /v1/api/auth/signup` - Sign up new user
- `POST /v1/api/auth/login` - Login user
- `POST /v1/api/auth/verify-email?token=26dc436` - Verify email before login
- `POST /v1/api/auth/resend-verificationToken` - Resend email verification token
- `POST /api/auth/forgot-password` - Forget user password
- `POST /v1/api/auth/reset-password` - Reset user password
- `POST v1/api/auth/change-password` - Change user password

### Categories (Requires JWT token)
- `POST /api/v1//category/addCategory` - add new category
- `GET /api/v1/category/getAllCategories` - Get all categories
- `POST /api/v1/category/updateCategory{id}` - Update category
- `PUT /api/v1/category/updateCategoryStatus/{id}` - Update category status
- `DELETE /api/category/deleteCategory/{id}` - Delete category

### Expenses (Requires JWT token)
- `POST /api/v1/product/addProduct` - Add a new product
- `GET /api/v1/product/getAllProducts` - Add new expense
- `PUT /api/v1/product/updateProduct/{id}` - Update a product
- `GET /api/v1/product/getProductByCategory/{id}` - Get a product br category id
- `PUT /api/v1/product/updateProductStatus` - Update product status
- `DELETE /api/v1/product/deleteProduct` - Delete a product

## Authentication

### Signup User

POST http://localhost:8084/v1/api/auth/signup
Content-Type: application/json
```Json
{
  "email": "jamestest@mailinator.com",
  "fullName": "James Test",
  "password": "Test@12345",
  "role": "USER"
}
```
### Verify Email
POST http://localhost:8084/v1/api/auth/verify-email?token=26dc4360-0dcb

### Resend email verification token
POST http://localhost:8084/v1/api/auth/resend-verificationToken
```json
{
  "email": "jamestest@mailinator.com"
}
```

### Forgot password
POST http://localhost:8084/v1/api/auth/forgot-password
```json
{
  "email": "jamestest@mailinator.com"
}
```

### Reset Password
POST http://localhost:8084/v1/api/auth/reset-password
```json
{
    "token" : "26dc4360-0dcb-4592-bfc4-cc36489892aa",
    "newPassword" : "1234567"
}
```

### Change password
POST http://localhost:8084/v1/api/auth/change-password
```json
{
    "currentPassword" : "Test@12345",
    "newPassword" : "123456"
}
```

### Login User
POST http://localhost:8084/v1/api/auth/login
```json
{
    "email": "john@example.com",
    "password": "password123"
}
```

### Get all users
GET http://localhost:8081/api/v1/auth/getAllUsers
Authorization: Bearer <jwt_token>
```json
[
  {
    "id": 2,
    "name": "User User",
    "email": "user@test.com",
    "contact": "0990487504",
    "role": "User"
  }
]
```

### Update user
PUT http://localhost:8081/api/v1/auth/updateUser/id
Authorization: Bearer <jwt_token>
```json
{
  "contact" : "0746350811"
}
```

### Update user role
PUT http://localhost:8081/api/v1/auth/updateRole/id
Authorization: Bearer <jwt_token>
```json
{
  "role" : "admin"
}
```
### Delete user
DELETE http://localhost:8081/api/v1/auth/deleteUser/id
Authorization: Bearer <jwt_token>
```json
{
  "Message":"User deleted successfully."
}
```
### Add category
POST http://localhost:8081/api/v1/category/addCategory
Authorization: Bearer <jwt_token>
```json
{
    "name" : "Legumes",
    "discount" : 12,
    "status" : false,
    "categoryImage" : "base64"
}
```

### Get all categories
Authorization: <jwt_token>
```json
[
    {
        "id": 1,
        "name": "Stools",
        "discount" : 12,
        "status": true,
        "categoryImage" : "base64"
    },
    {
        "id": 2,
        "name": "Beds",
        "discount" : 12,
        "status": false,
        "categoryImage" : "base64"
    }
]
```

### Update category
Authorization: <jwt_token>
PUT http://localhost:8081/api/v1/category/updateCategory/id
```json
{
  "name" : "Legumes",
  "discount" : 12,
  "status" : true,
  "categoryImage" : "base64"
}
```

### Update category status
PUT http://localhost:8081/api/v1/category/updateCategoryStatus/id
Authorization: <jwt_token>
```json
{
    "status" : true
}
```

### Delete category
DELETE http://localhost:8081/api/v1/category/deleteCategory/id
Authorization: <jwt_token>
```json
{
  "Message":"Category deleted successfully."
}
```
### Add product
POST: http://localhost:8081/api/v1/product/addProduct
Authorization: <jwt_token>
```json
{
    "name" : "Misumari",
    "description" : "Build Kenya",
    "price" : 125,
    "status" : false,
    "productImage" : "bwhdudadiasds64",
    "stock" : 210,
    "rating" : 2.5,
    "categoryId" : 5
}
```

### Get Products
GET: http://localhost:8081/api/v1/product/getAllProducts
```json
[
    {
        "id": 1,
        "name": "Misumari",
        "description": "Build Kenya",
        "price": 125.0,
        "status": false,
        "productImage": "AAAAIGZ0eXBhdmlmAA",
        "stock": 210.0,
        "rating": 2.5,
        "categoryName": "Construction materials"
    },
    {
        "id": 2,
        "name": "Hammers",
        "description": "Build Kenya",
        "price": 125.0,
        "status": true,
        "productImage": "/9j/4AAQSkZJRgAB",
        "stock": 1150.0,
        "rating": 1.5,
        "categoryName": "Construction materials"
    }
]
```

### Update Product
PUT: http://localhost:8081/api/v1/product/updateProduct/id
```json
{
  "name" : "Misumari",
  "description" : "Build Kenya",
  "price" : 125,
  "status" : false,
  "productImage" : "bwhdudadiasds64",
  "stock" : 210,
  "rating" : 2.5,
  "categoryId" : 5
}
```

### Update Product Status
PATCH: http://localhost:8081/api/v1/product/updateProductStatus/id
```json
{
    "status" : false
}
```

### Delete Product
DELETE: http://localhost:8081/api/v1/product/deleteProduct/id
```json
{
  "Message" : "Product deleted successfully"
}
```

### Add to cart
POST http://localhost:8081/api/v1/cart/addToCart
Authorization: <jwt_token>
```json
{
    "productId" : 9,
    "quantity" : 5
}
```

### Get all cart items
GET http://localhost:8081/api/v1/cart/getCart
```json
[
  {
    "id": 6,
    "userName": "Admin Admin",
    "userEmail": "admin@test.com",
    "productName": "Kitchen Toolz",
    "productDescription": "Confortable seat",
    "productPrice": 15000.0,
    "quantity": 5
  }
]
```
### Update cart item
PUT http://localhost:8081/api/v1/cart/updateCart/id
Authorization: <jwt_token>
```json
{
    "quantity" : 2
}
```

### Remove from cart
DELETE http://localhost:8081/api/v1/cart/removeFromCart/id
Authorization: <jwt_token>
```json
{
  "Message":"Cart item deleted successfully"
}
```
### Place order
POST http://localhost:8081/api/v1/order/placeOrder
Authorization: <jwt_token>
```json
{
    "cartId" : 6,
    "paymentMethod" : "CASH"
}
```

### Get orders
GET http://localhost:8081/api/v1/order/getOrders
Authorization: <jwt_token>
```json
[
    {
        "id": 5,
        "userName": "Admin Admin",
        "userEmail": "admin@test.com",
        "productName": "Kitchen Toolz",
        "productDescription": "Confortable seat",
        "productPrice": 15000.0,
        "quantity": 2,
        "totalAmount": 30000.0,
        "paymentMethod": "CASH",
        "paymentStatus": "PENDING",
        "orderStatus": "PENDING",
        "orderDate": "2025-09-26T22:10:33"
    }
]
```
### Update order status
PUT http://localhost:8081/api/v1/order/updateOrderStatus/id
Authorization: <jwt_token>
```json
{
  "status" : "CONFIRMED"
}
```
### Lipa na mpesa
Install ngrok, unzip and paste it in C drive
Run it using this command
ngrok http 8081
Copy and paste Forwarding in call.back-url before
/api/v1/payment/callback

![img.png](img.png)

### stk push
POST http://localhost:8081/api/v1/payment/stkpush
Authorization: <jwt_token>
```json

{
  "orderId": 5,
  "phoneNumber": "254712345678"
}

```

### Call back url
POST https://c438eabaf72c.ngrok-free.app/api/v1/payment/callback
Authorization: <no token required>
```json
{
  "Body": {
    "stkCallback": {
      "MerchantRequestID": "2e9c-429c-a95c-b4cd653531a322302",
      "CheckoutRequestID": "ws_CO_05102025193733441713408025",
      "ResultCode": 1032,
      "ResultDesc": "Request cancelled by user"
    }
  }
}
```
## Running the Application

1. Clone the repository
2. Set up MySQL database
3. Update database credentials in `application.properties`
4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Testing

Run unit tests:
```bash
mvn test
```

## Security

- JWT tokens are required for all category and expense operations
- Users can only access their own data
- Passwords are encrypted using BCrypt
- CORS is configured for cross-origin requests

## Postman Testing

Import the API endpoints into Postman and test:
1. Register a new user
2. Login to get JWT token
3. Use the token in Authorization header for protected endpoints
4. Test all CRUD operations for categories and expenses