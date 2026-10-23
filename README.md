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
- `POST /v1/api//category/addCategory` - add new category
- `GET /v1/api/category/getAllCategories` - Get all categories
- `GET /v1/api/category/getCategoryById/{id}` - Get category by id
- `PATCH /v1/api/category/updateCategory{id}` - Update category
- `PATCH /v1/api/category/updateCategoryStatus/{id}` - Update category status
- `GET /v1/api/category/getCategoriesByStatus/{id}` - Get category by status

### Products (Requires JWT token)
- `POST /v1/api/product/addProduct` - Add a new product
- `GET /v1/api/product/getAllProducts` - Add new expense
- `GET /v1/api/product/getProductById/{id}` Get product by id
- `PUT /v1/api/product/updateProduct/{id}` - Update a product
- `GET /v1/api/product/getProductByCategory/{id}` - Get a product br category id - Missing API
- `PUT /v1/api/product/updateProductStatus` - Update product status
- `GET /v1/api/product/getProductByStatus` - Get product by status

### Purchase Order (Requires JWT token)

- `POST /v1/api/purchase-orders/addPurchaseOrder` - Add purchase order
- `GET /v1/api/purchase-orders/getAllPurchaseOrders` - Get all purchase orders
- `GET /v1/api/purchase-orders/getPurchaseOrderById/{id}` Get purchase order by id
- `PATCH /v1/api/purchase-orders/approve/{id}` - Approve purchase order
- `PATCH /v1/api/purchase-orders/reject/{id}` - Reject purchase order


## Authentication APIs

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

## Category APIs

### Add category
POST http://localhost:8084/v1/api/category/addCategory
Authorization: Bearer <jwt_token>
```json
{
  "name": "Networking Equipment",
  "status": "ACTIVE"
}

```

### Get all categories
POST http://localhost:8084/v1/api/category/getAllCategories
Authorization: <jwt_token>
```json
{
  "message": "Categories retrieved successfully.",
  "payLoad": [
    {
      "createdAt": "2026-10-18T12:14:59Z",
      "id": 1,
      "name": "Electronic Equipment",
      "status": "INACTIVE",
      "updatedAt": "2026-10-18T13:10:27Z"
    },
    {
      "createdAt": "2026-10-18T12:17:38Z",
      "id": 2,
      "name": "Electronic Equipment",
      "status": "INACTIVE",
      "updatedAt": "2026-10-18T13:27:28Z"
    }
  ],
  "status": "SUCCESS"
}
```

### Update category
Authorization: <jwt_token>
PUT http://localhost:8084/v1/api/category/updateCategory/1
```json
{
  "name": "Electronic Equipment",
  "status": "INACTIVE"
}
```
### Get category by id
POST http://localhost:8084/v1/api/category/getCategoryById/1
Authorization: <jwt_token>
```json
{
  "message": "Category retrieved successfully.",
  "payLoad": {
    "createdAt": "2026-10-18T12:14:59Z",
    "id": 1,
    "name": "Electronic Equipment",
    "status": "INACTIVE",
    "updatedAt": "2026-10-18T13:10:27Z"
  },
  "status": "SUCCESS"
}
```

### Get category by status
POST http://localhost:8084/v1/api/category/getCategoriesByStatus/INACTIVE
Authorization: <jwt_token>
```json
{
    "message": "Categories retrieved successfully.",
    "payLoad": [
        {
            "createdAt": "2026-10-18T12:14:59Z",
            "id": 1,
            "name": "Electronic Equipment",
            "status": "INACTIVE",
            "updatedAt": "2026-10-18T13:10:27Z"
        },
        {
            "createdAt": "2026-10-18T12:17:38Z",
            "id": 2,
            "name": "Electronic Equipment",
            "status": "INACTIVE",
            "updatedAt": "2026-10-18T13:27:28Z"
        }
    ],
    "status": "SUCCESS"
}
```
### Update category status
PUT http://localhost:8084/v1/api/category/updateCategoryStatus/3
Authorization: <jwt_token>
```json
{
  "status": "INACTIVE"
}
```

## Products APIs
### Add product
POST: http://localhost:8084/v1/api/products/addProduct
Authorization: <jwt_token>
```json
{
  "name": "Red Onions Large",
  "serialNumber": "IT0127",
  "brand": "Farm Fresh",
  "description": "Grade A large red onions",
  "uom": "KG",
  "categoryId": 2,
  "packagingUnit": "BAG",
  "quantityPerUnit": 1,
  "purchasePrice": 50.00,
  "wholesalePrice": 55.00,
  "retailPrice": 65.00,
  "salesTaxRate": "STANDARD_16",
  "purchaseTaxRate": "STANDARD_16",
  "skuCode": "VEG-ONR-MMZ",
  "batchManaged": true,
  "shelfLifeDays": 14,
  "earlyWarningDays": 3,
  "imageUrls": [
    "https://example.com/images/onion1.jpg",
    "https://example.com/images/onion2.jpg"
  ]
}
```

### Get Products
GET: http://localhost:8081/api/v1/product/getAllProducts
```json
{
  "batchManaged": true,
  "brand": "Farm Fresh Premium",
  "category": {
    "createdAt": "2026-10-18T16:51:13Z",
    "id": 3,
    "name": "Networking Equipment",
    "status": "INACTIVE",
    "updatedAt": "2026-10-18T17:11:54Z"
  },
  "createdAt": "2026-10-18T15:16:38Z",
  "createdBy": null,
  "description": "Updated description - Grade A large red onions",
  "earlyWarningDays": 30,
  "id": 5,
  "images": [
    {
      "id": 15,
      "productImage": "https://example.com/images/onion1.jpg",
      "sortOrder": 0
    },
    {
      "id": 16,
      "productImage": "https://example.com/images/onion2.jpg",
      "sortOrder": 1
    }
  ],
  "name": "Red Onions Large - Updated",
  "packagingUnit": "BAG",
  "productNo": "IT0005",
  "purchasePrice": 52,
  "purchaseTaxRate": "STANDARD_16",
  "quantityPerUnit": 1,
  "retailPrice": 68,
  "salesTaxRate": "STANDARD_16",
  "serialNumber": "IT0127",
  "shelfLifeDays": 14,
  "skuCode": "VEG-ONR-MMZ",
  "status": "ACTIVE",
  "uom": "KG",
  "updatedAt": "2026-10-22T11:57:15Z",
  "updatedBy": "admintest@mailinator.com",
  "wholesalePrice": 58
}
```
### Get product by id
POST: http://localhost:8084/v1/api/products/getProductById/5
Authorization: <jwt_token>
```json
{
    "batchManaged": true,
    "brand": "Farm Fresh Premium",
    "category": {
        "createdAt": "2026-10-18T16:51:13Z",
        "id": 3,
        "name": "Networking Equipment",
        "status": "INACTIVE",
        "updatedAt": "2026-10-18T17:11:54Z"
    },
    "createdAt": "2026-10-18T15:16:38Z",
    "createdBy": null,
    "description": "Updated description - Grade A large red onions",
    "earlyWarningDays": 30,
    "id": 5,
    "images": [
        {
            "id": 15,
            "productImage": "https://example.com/images/onion1.jpg",
            "sortOrder": 0
        },
        {
            "id": 16,
            "productImage": "https://example.com/images/onion2.jpg",
            "sortOrder": 1
        }
    ],
    "name": "Red Onions Large - Updated",
    "packagingUnit": "BAG",
    "productNo": "IT0005",
    "purchasePrice": 52,
    "purchaseTaxRate": "STANDARD_16",
    "quantityPerUnit": 1,
    "retailPrice": 68,
    "salesTaxRate": "STANDARD_16",
    "serialNumber": "IT0127",
    "shelfLifeDays": 14,
    "skuCode": "VEG-ONR-MMZ",
    "status": "ACTIVE",
    "uom": "KG",
    "updatedAt": "2026-10-22T11:57:15Z",
    "updatedBy": "admintest@mailinator.com",
    "wholesalePrice": 58
}
```

### Get products by category id
GET : http://localhost:8084/v1/api/products/getProductsByCategory/1
Authorization: <jwt_token>
```json
{
    "message": "Products retrieved successfully",
    "payLoad": [
        {
            "batchManaged": true,
            "brand": "Farm Fresh",
            "category": {
                "createdAt": "2026-10-18T12:14:59Z",
                "id": 1,
                "name": "Electronic Equipment",
                "status": "INACTIVE",
                "updatedAt": "2026-10-18T13:10:27Z"
            },
            "createdAt": "2026-10-15T18:29:20Z",
            "createdBy": null,
            "description": "Grade A large red onions",
            "earlyWarningDays": 3,
            "id": 1,
            "images": [
                {
                    "id": 1,
                    "productImage": "https://example.com/images/onion1.jpg",
                    "sortOrder": 0
                },
                {
                    "id": 2,
                    "productImage": "https://example.com/images/onion2.jpg",
                    "sortOrder": 1
                },
                {
                    "id": 3,
                    "productImage": "https://example.com/images/onion3.jpg",
                    "sortOrder": 2
                }
            ],
            "name": "Red Onions Large",
            "packagingUnit": "BAG",
            "productNo": "IT0001",
            "purchasePrice": 50,
            "purchaseTaxRate": "STANDARD_16",
            "quantityPerUnit": 1,
            "retailPrice": 65,
            "salesTaxRate": "STANDARD_16",
            "serialNumber": "IT0021",
            "shelfLifeDays": 14,
            "skuCode": "VEG-ONR-MM4",
            "status": "ACTIVE",
            "uom": "KG",
            "updatedAt": "2026-10-15T18:29:20Z",
            "updatedBy": null,
            "wholesalePrice": 55
        }
    ],
    "status": "SUCCESS"
}
```
### Update Product
PUT: http://localhost:8084/v1/api/products/updateProduct/5
```json
{
  "name": "Red Onions Large - Updated",
  "brand": "Farm Fresh Premium",
  "description": "Updated description - Grade A large red onions",
  "uom": "KG",
  "categoryId": 3,
  "packagingUnit": "BAG",
  "quantityPerUnit": 1,
  "purchasePrice": 52.00,
  "wholesalePrice": 58.00,
  "retailPrice": 68.00,
  "salesTaxRate": "STANDARD_16",
  "purchaseTaxRate": "STANDARD_16",
  "batchManaged": true,
  "shelfLifeDays": 14,
  "earlyWarningDays": 30

}
```

### Update Product Status
PATCH: http://localhost:8084/v1/api/products/updateProductStatus/4898
```json
{
  "status": "INACTIVE"
}
```

### Get Product by status
GET: http://localhost:8084/v1/api/products/getProductByStatus/INACTIVE
```json
{
  "message": "Product retrieved successfully.",
  "payLoad": {
    "batchManaged": true,
    "brand": "Farm Fresh Premium",
    "category": {
      "createdAt": "2026-10-18T16:51:13Z",
      "id": 3,
      "name": "Networking Equipment",
      "status": "INACTIVE",
      "updatedAt": "2026-10-18T17:11:54Z"
    },
    "createdAt": "2026-10-18T15:16:38Z",
    "createdBy": null,
    "description": "Updated description - Grade A large red onions",
    "earlyWarningDays": 30,
    "id": 5,
    "images": [
      {
        "id": 15,
        "productImage": "https://example.com/images/onion1.jpg",
        "sortOrder": 0
      },
      {
        "id": 16,
        "productImage": "https://example.com/images/onion2.jpg",
        "sortOrder": 1
      }
    ],
    "name": "Red Onions Large - Updated",
    "packagingUnit": "BAG",
    "productNo": "IT0005",
    "purchasePrice": 52,
    "purchaseTaxRate": "STANDARD_16",
    "quantityPerUnit": 1,
    "retailPrice": 68,
    "salesTaxRate": "STANDARD_16",
    "serialNumber": "IT0127",
    "shelfLifeDays": 14,
    "skuCode": "VEG-ONR-MMZ",
    "status": "ACTIVE",
    "uom": "KG",
    "updatedAt": "2026-10-22T11:57:15Z",
    "updatedBy": "admintest@mailinator.com",
    "wholesalePrice": 58
  },
  "status": "SUCCESS"
}
```

## Warehouse APIs

### Add warehouse
POST http://localhost:8084/v1/api/warehouse/addWarehouse
Authorization: <jwt_token>
```json
{
  "name": "Imara Daima Subs Stores",
  "contactPerson": "Michael Juma",
  "phone": "0712340000",
  "email": "warehouse@company.co.ke",
  "street": "Enterprise Road",
  "city": "Nairobi",
  "postal": "00100",
  "category": "Main Warehouse",
  "allowNegativeInventory": false,
  "remark": "Day storage only"
}
```

### Get all warehouses
GET: http://localhost:8084/v1/api/warehouse/getAllWarehouses
```json
{
  "message": "Warehouses retrieved successfully.",
  "payLoad": [
    {
      "allowNegativeInventory": true,
      "category": "Main Warehouse",
      "city": "Nairobi",
      "contactPerson": "Michael Juma",
      "createdAt": "2026-10-20T08:08:14Z",
      "email": "warehouse@company.co.ke",
      "id": 2,
      "name": "Imara Daima Main Store",
      "phone": "0712340000",
      "postal": "00100",
      "remark": "Day storage only",
      "status": "INACTIVE",
      "street": "Enterprise Road",
      "updatedAt": "2026-10-20T10:08:24Z",
      "warehouseNo": "WH0002"
    }
  ],
  "status": "SUCCESS"
}
```
### Get warehouse by id
GET: http://localhost:8084/v1/api/warehouse/getWarehouseById/2
Authorization: <jwt_token>
```json
{
  "message": "Warehouse retrieved successfully.",
  "payLoad": {
    "allowNegativeInventory": true,
    "category": "Main Warehouse",
    "city": "Nairobi",
    "contactPerson": "Michael Juma",
    "createdAt": "2026-10-20T08:08:14Z",
    "email": "warehouse@company.co.ke",
    "id": 2,
    "name": "Imara Daima Main Store",
    "phone": "0712340000",
    "postal": "00100",
    "remark": "Day storage only",
    "status": "INACTIVE",
    "street": "Enterprise Road",
    "updatedAt": "2026-10-20T10:08:24Z",
    "warehouseNo": "WH0002"
  },
  "status": "SUCCESS"
}
```

### Update warehouse
PATCH http://localhost:8084/v1/api/warehouse/updateWarehouse/2
Authorization: <jwt_token>
```json
{
  "name": "Imara Daima Main Store",
  "contactPerson": "Michael Juma",
  "phone": "0712340000",
  "email": "warehouse@company.co.ke",
  "street": "Enterprise Road",
  "city": "Nairobi",
  "postal": "00100",
  "category": "Main Warehouse",
  "allowNegativeInventory": true,
  "status": "INACTIVE",
  "remark": "Day storage only"
}
```
### Update warehouse status
PATCH: http://localhost:8084/v1/api/warehouse/updateWarehouseStatus/2
Authorization: <jwt_token>
```json
{
  "status": "INACTIVE"
}
```
## Supplier APIs

### Add supplier
POST: http://localhost:8084/v1/api/supplers/addSupplier
Authorization: <jwt_token>
```json
{
  "name": "Meru Farms",
  "contactPerson": "James Doe",
  "phone": "0712345678",
  "email": "jamesdoe@merufarms.co.ke",
  "paymentTerms": "DUE_ON_RECEIPT",
  "street": "Limuru Road",
  "city": "Kiambu",
  "postal": "00217"
}
```
### Get all suppliers
GET http://localhost:8084/v1/api/supplers/getAllSuppliers
Authorization: <jwt_token>
```json
{
  "message": "Supplier retrieved successfully.",
  "payLoad": {
    "city": "Kiambu",
    "contactPerson": "James Doe",
    "createdAt": "2026-10-20T11:47:32Z",
    "email": "jamesdoe@limurufarms.co.ke",
    "id": 1,
    "name": "Limuru Farms Suppliers Limited",
    "paymentTerms": "DUE_ON_RECEIPT",
    "phone": "0712345678",
    "postal": "00217",
    "status": "ACTIVE",
    "street": "Limuru Road",
    "supplierNo": "SUP0001",
    "updatedAt": "2026-10-20T12:17:40Z"
  },
  "status": "SUCCESS"
}
```
### Get supplier by id
GET: http://localhost:8084/v1/api/supplers/getSupplierById/1
Authorization: <jwt_token>
```json
{
  "message": "Supplier retrieved successfully.",
  "payLoad": {
    "city": "Kiambu",
    "contactPerson": "James Doe",
    "createdAt": "2026-10-20T11:47:32Z",
    "email": "jamesdoe@limurufarms.co.ke",
    "id": 1,
    "name": "Limuru Farms Suppliers Limited",
    "paymentTerms": "DUE_ON_RECEIPT",
    "phone": "0712345678",
    "postal": "00217",
    "status": "ACTIVE",
    "street": "Limuru Road",
    "supplierNo": "SUP0001",
    "updatedAt": "2026-10-20T12:17:40Z"
  },
  "status": "SUCCESS"
}
```
### Update supplier
PATCH: http://localhost:8084/v1/api/supplers/updateSupplier/1
Authorization: <jwt_token>
```json
{
  "name": "Limuru Farms Suppliers Limited",
  "contactPerson": "James Doe",
  "phone": "0712345678",
  "email": "jamesdoe@limurufarms.co.ke",
  "paymentTerms": "DUE_ON_RECEIPT",
  "street": "Limuru Road",
  "city": "Kiambu",
  "postal": "00217",
  "status": "INACTIVE"
}
```
### Update supplier by status
PATCH: http://localhost:8084/v1/api/supplers/updateSupplierStatus/1
Authorization: <jwt_token>
```json
{
  "status": "ACTIVE"
}
```

### Get suppliers by status
GET: http://localhost:8084/v1/api/supplers/getSupplierByStatus/ACTIVE
```json
{
    "message": "Suppliers retrieved successfully.",
    "payLoad": [
        {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        }
    ],
    "status": "SUCCESS"
}
```
## Purchase order APIs
POST: http://localhost:8084/v1/api/purchase-orders/addPurchaseOrder
Authorization: <jwt_token>
```json
{
  "supplierId": 1,
  "orderDate": "2026-10-20",
  "expectedDeliveryDate": "2026-10-28",
  "lines": [
    {
      "productId": 5,
      "warehouseId": 1,
      "quantity": 100,
      "unitPrice": 50.00,
      "discount": 0,
      "taxRate": 16
    },
    {
      "productId": 3,
      "warehouseId": 1,
      "quantity": 50,
      "unitPrice": 80.00,
      "discount": 100,
      "taxRate": 16
    }
  ]
}
```

### Get all purchase orders
GET: http://localhost:8084/v1/api/purchase-orders/getAllPurchaseOrders
Authorization: <jwt_token>
```json
{
    "message": "Purchase order retrieved successfully.",
    "payLoad": {
        "createdAt": "2026-10-20T19:32:02Z",
        "createdBy": "admintest@mailinator.com",
        "expectedDeliveryDate": "2026-10-28",
        "id": 1,
        "invoiceStatus": "NOT_INVOICED",
        "lines": [
            {
                "amount": 5800,
                "discount": 0,
                "id": 1,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh",
                    "category": {
                        "createdAt": "2026-10-18T12:14:59Z",
                        "id": 1,
                        "name": "Electronic Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T13:10:27Z"
                    },
                    "createdAt": "2026-10-15T18:29:20Z",
                    "createdBy": null,
                    "description": "Grade A large red onions",
                    "earlyWarningDays": 3,
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "images": [
                        {
                            "id": 1,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 2,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        },
                        {
                            "id": 3,
                            "productImage": "https://example.com/images/onion3.jpg",
                            "sortOrder": 2
                        }
                    ],
                    "name": "Red Onions Large",
                    "packagingUnit": "BAG",
                    "productNo": "IT0001",
                    "purchasePrice": 50,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 65,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0021",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MM4",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-15T18:29:20Z",
                    "updatedBy": null,
                    "wholesalePrice": 55
                },
                "quantity": 100,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            },
            {
                "amount": 4524,
                "discount": 100,
                "id": 2,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh",
                    "category": {
                        "createdAt": "2026-10-18T12:17:38Z",
                        "id": 2,
                        "name": "Electronic Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T13:27:28Z"
                    },
                    "createdAt": "2026-10-15T18:36:53Z",
                    "createdBy": null,
                    "description": "Grade A large red onions",
                    "earlyWarningDays": 3,
                    "hibernateLazyInitializer": {},
                    "id": 2,
                    "images": [
                        {
                            "id": 4,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 5,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        },
                        {
                            "id": 6,
                            "productImage": "https://example.com/images/onion3.jpg",
                            "sortOrder": 2
                        }
                    ],
                    "name": "Red Onions Large",
                    "packagingUnit": "BAG",
                    "productNo": "IT0002",
                    "purchasePrice": 50,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 65,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0020",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMM",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-15T18:36:53Z",
                    "updatedBy": null,
                    "wholesalePrice": 55
                },
                "quantity": 50,
                "taxRate": 16,
                "unitPrice": 80,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "orderDate": "2026-10-20",
        "poNumber": "PO00001",
        "receiptStatus": "NOT_RECEIVED",
        "status": "REJECTED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 10324,
        "updatedAt": "2026-10-20T19:59:12Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```

### Get purchase order by id
GET: http://localhost:8084/v1/api/purchase-orders/getPurchaseOrderById/1
Authorization: <jwt_token>
```json
{
    "message": "Purchase order retrieved successfully.",
    "payLoad": {
        "createdAt": "2026-10-20T19:32:02Z",
        "createdBy": "admintest@mailinator.com",
        "expectedDeliveryDate": "2026-10-28",
        "id": 1,
        "invoiceStatus": "NOT_INVOICED",
        "lines": [
            {
                "amount": 5800,
                "discount": 0,
                "id": 1,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh",
                    "category": {
                        "createdAt": "2026-10-18T12:14:59Z",
                        "id": 1,
                        "name": "Electronic Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T13:10:27Z"
                    },
                    "createdAt": "2026-10-15T18:29:20Z",
                    "createdBy": null,
                    "description": "Grade A large red onions",
                    "earlyWarningDays": 3,
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "images": [
                        {
                            "id": 1,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 2,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        },
                        {
                            "id": 3,
                            "productImage": "https://example.com/images/onion3.jpg",
                            "sortOrder": 2
                        }
                    ],
                    "name": "Red Onions Large",
                    "packagingUnit": "BAG",
                    "productNo": "IT0001",
                    "purchasePrice": 50,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 65,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0021",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MM4",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-15T18:29:20Z",
                    "updatedBy": null,
                    "wholesalePrice": 55
                },
                "quantity": 100,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            },
            {
                "amount": 4524,
                "discount": 100,
                "id": 2,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh",
                    "category": {
                        "createdAt": "2026-10-18T12:17:38Z",
                        "id": 2,
                        "name": "Electronic Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T13:27:28Z"
                    },
                    "createdAt": "2026-10-15T18:36:53Z",
                    "createdBy": null,
                    "description": "Grade A large red onions",
                    "earlyWarningDays": 3,
                    "hibernateLazyInitializer": {},
                    "id": 2,
                    "images": [
                        {
                            "id": 4,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 5,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        },
                        {
                            "id": 6,
                            "productImage": "https://example.com/images/onion3.jpg",
                            "sortOrder": 2
                        }
                    ],
                    "name": "Red Onions Large",
                    "packagingUnit": "BAG",
                    "productNo": "IT0002",
                    "purchasePrice": 50,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 65,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0020",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMM",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-15T18:36:53Z",
                    "updatedBy": null,
                    "wholesalePrice": 55
                },
                "quantity": 50,
                "taxRate": 16,
                "unitPrice": 80,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "orderDate": "2026-10-20",
        "poNumber": "PO00001",
        "receiptStatus": "NOT_RECEIVED",
        "status": "REJECTED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 10324,
        "updatedAt": "2026-10-20T19:59:12Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```

### Approve purchase order
PATCH: http://localhost:8084/v1/api/purchase-orders/approve/2
Authorization: <jwt_token>
```json
{"Message":"Only pending approval orders can be approved"}
```

### Reject purchase order
PATCH: http://localhost:8084/v1/api/purchase-orders/reject/1
Authorization: <jwt_token>
```json
{"Message":"Purchase Order rejected"}
```

### Add purchase received note
POST: http://localhost:8084/v1/api/goods-received-notes/addGrn
Authorization: <jwt_token>
```json
{
  "supplierId": 1,
  "purchaseOrderId": 2,
  "receivedDate": "2026-10-21",
  "fulfilmentType": "PARTIAL",
  "lines": [
    {
      "productId": 5,
      "warehouseId": 1,
      "quantity": 100,
      "unitPrice": 50.00,
      "taxRate": 16
    },
    {
      "productId": 3,
      "warehouseId": 1,
      "quantity": 40,
      "unitPrice": 80.00,
      "taxRate": 16
    }
  ]
}
```
### Get all purchase received notes
GET: http://localhost:8084/v1/api/goods-received-notes/getAllGrns
Authorization: <jwt_token>
```json
{
    "message": "Goods received note retrieved successfully",
    "payLoad": {
        "createdAt": "2026-10-22T12:13:32Z",
        "createdBy": "admintest@mailinator.com",
        "fulfilmentType": "PARTIAL",
        "grnNumber": "GRN00003",
        "id": 3,
        "lines": [
            {
                "amount": 5800,
                "batchNo": "BN0003",
                "id": 5,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 100,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            },
            {
                "amount": 3712,
                "batchNo": "BN0003",
                "id": 6,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh",
                    "category": {
                        "createdAt": "2026-10-18T12:17:38Z",
                        "id": 2,
                        "name": "Electronic Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T13:27:28Z"
                    },
                    "createdAt": "2026-10-15T18:38:16Z",
                    "createdBy": null,
                    "description": "Grade A large red onions",
                    "earlyWarningDays": 3,
                    "hibernateLazyInitializer": {},
                    "id": 3,
                    "images": [
                        {
                            "id": 7,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 8,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        },
                        {
                            "id": 9,
                            "productImage": "https://example.com/images/onion3.jpg",
                            "sortOrder": 2
                        }
                    ],
                    "name": "Red Onions Large",
                    "packagingUnit": "BAG",
                    "productNo": "IT0003",
                    "purchasePrice": 50,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 65,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0027",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMK",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-15T18:38:16Z",
                    "updatedBy": null,
                    "wholesalePrice": 55
                },
                "quantity": 40,
                "taxRate": 16,
                "unitPrice": 80,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "purchaseOrder": {
            "createdAt": "2026-10-20T19:44:43Z",
            "createdBy": "admintest@mailinator.com",
            "expectedDeliveryDate": "2026-10-28",
            "hibernateLazyInitializer": {},
            "id": 2,
            "invoiceStatus": "NOT_INVOICED",
            "lines": [
                {
                    "amount": 5800,
                    "discount": 0,
                    "id": 3,
                    "product": {
                        "batchManaged": true,
                        "brand": "Farm Fresh Premium",
                        "category": {
                            "createdAt": "2026-10-18T16:51:13Z",
                            "id": 3,
                            "name": "Networking Equipment",
                            "status": "INACTIVE",
                            "updatedAt": "2026-10-18T17:11:54Z"
                        },
                        "createdAt": "2026-10-18T15:16:38Z",
                        "createdBy": null,
                        "description": "Updated description - Grade A large red onions",
                        "earlyWarningDays": 30,
                        "hibernateLazyInitializer": {},
                        "id": 5,
                        "images": [
                            {
                                "id": 15,
                                "productImage": "https://example.com/images/onion1.jpg",
                                "sortOrder": 0
                            },
                            {
                                "id": 16,
                                "productImage": "https://example.com/images/onion2.jpg",
                                "sortOrder": 1
                            }
                        ],
                        "name": "Red Onions Large - Updated",
                        "packagingUnit": "BAG",
                        "productNo": "IT0005",
                        "purchasePrice": 52,
                        "purchaseTaxRate": "STANDARD_16",
                        "quantityPerUnit": 1,
                        "retailPrice": 68,
                        "salesTaxRate": "STANDARD_16",
                        "serialNumber": "IT0127",
                        "shelfLifeDays": 14,
                        "skuCode": "VEG-ONR-MMZ",
                        "status": "ACTIVE",
                        "uom": "KG",
                        "updatedAt": "2026-10-22T11:57:15Z",
                        "updatedBy": "admintest@mailinator.com",
                        "wholesalePrice": 58
                    },
                    "quantity": 100,
                    "taxRate": 16,
                    "unitPrice": 50,
                    "warehouse": {
                        "allowNegativeInventory": false,
                        "category": "Main Warehouse",
                        "city": "Nairobi",
                        "contactPerson": "Michael Juma",
                        "createdAt": "2026-10-19T19:50:52Z",
                        "email": "warehouse@company.co.ke",
                        "hibernateLazyInitializer": {},
                        "id": 1,
                        "name": "Imara Daima Main Store",
                        "phone": "0712340000",
                        "postal": "00100",
                        "remark": "Day storage only",
                        "status": "ACTIVE",
                        "street": "Enterprise Road",
                        "updatedAt": "2026-10-19T19:50:52Z",
                        "warehouseNo": "WH0001"
                    }
                },
                {
                    "amount": 4524,
                    "discount": 100,
                    "id": 4,
                    "product": {
                        "batchManaged": true,
                        "brand": "Farm Fresh",
                        "category": {
                            "createdAt": "2026-10-18T12:17:38Z",
                            "id": 2,
                            "name": "Electronic Equipment",
                            "status": "INACTIVE",
                            "updatedAt": "2026-10-18T13:27:28Z"
                        },
                        "createdAt": "2026-10-15T18:38:16Z",
                        "createdBy": null,
                        "description": "Grade A large red onions",
                        "earlyWarningDays": 3,
                        "hibernateLazyInitializer": {},
                        "id": 3,
                        "images": [
                            {
                                "id": 7,
                                "productImage": "https://example.com/images/onion1.jpg",
                                "sortOrder": 0
                            },
                            {
                                "id": 8,
                                "productImage": "https://example.com/images/onion2.jpg",
                                "sortOrder": 1
                            },
                            {
                                "id": 9,
                                "productImage": "https://example.com/images/onion3.jpg",
                                "sortOrder": 2
                            }
                        ],
                        "name": "Red Onions Large",
                        "packagingUnit": "BAG",
                        "productNo": "IT0003",
                        "purchasePrice": 50,
                        "purchaseTaxRate": "STANDARD_16",
                        "quantityPerUnit": 1,
                        "retailPrice": 65,
                        "salesTaxRate": "STANDARD_16",
                        "serialNumber": "IT0027",
                        "shelfLifeDays": 14,
                        "skuCode": "VEG-ONR-MMK",
                        "status": "ACTIVE",
                        "uom": "KG",
                        "updatedAt": "2026-10-15T18:38:16Z",
                        "updatedBy": null,
                        "wholesalePrice": 55
                    },
                    "quantity": 50,
                    "taxRate": 16,
                    "unitPrice": 80,
                    "warehouse": {
                        "allowNegativeInventory": false,
                        "category": "Main Warehouse",
                        "city": "Nairobi",
                        "contactPerson": "Michael Juma",
                        "createdAt": "2026-10-19T19:50:52Z",
                        "email": "warehouse@company.co.ke",
                        "hibernateLazyInitializer": {},
                        "id": 1,
                        "name": "Imara Daima Main Store",
                        "phone": "0712340000",
                        "postal": "00100",
                        "remark": "Day storage only",
                        "status": "ACTIVE",
                        "street": "Enterprise Road",
                        "updatedAt": "2026-10-19T19:50:52Z",
                        "warehouseNo": "WH0001"
                    }
                }
            ],
            "orderDate": "2026-10-20",
            "poNumber": "PO00002",
            "receiptStatus": "PARTIALLY_RECEIVED",
            "status": "APPROVED",
            "supplier": {
                "city": "Kiambu",
                "contactPerson": "James Doe",
                "createdAt": "2026-10-20T11:47:32Z",
                "email": "jamesdoe@limurufarms.co.ke",
                "hibernateLazyInitializer": {},
                "id": 1,
                "name": "Limuru Farms Suppliers Limited",
                "paymentTerms": "DUE_ON_RECEIPT",
                "phone": "0712345678",
                "postal": "00217",
                "status": "ACTIVE",
                "street": "Limuru Road",
                "supplierNo": "SUP0001",
                "updatedAt": "2026-10-23T16:56:01Z"
            },
            "totalAmount": 10324,
            "updatedAt": "2026-10-22T12:23:58Z",
            "updatedBy": "admintest@mailinator.com"
        },
        "receivedDate": "2026-10-21",
        "status": "APPROVED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 9512,
        "updatedAt": "2026-10-22T12:23:58Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```

### Get purchase received note by id
GET: http://localhost:8084/v1/api/goods-received-notes/getGrnById/3
Authorization: <jwt_token>
```json
{
    "message": "Goods received note retrieved successfully",
    "payLoad": {
        "createdAt": "2026-10-22T12:13:32Z",
        "createdBy": "admintest@mailinator.com",
        "fulfilmentType": "PARTIAL",
        "grnNumber": "GRN00003",
        "id": 3,
        "lines": [
            {
                "amount": 5800,
                "batchNo": "BN0003",
                "id": 5,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 100,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            },
            {
                "amount": 3712,
                "batchNo": "BN0003",
                "id": 6,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh",
                    "category": {
                        "createdAt": "2026-10-18T12:17:38Z",
                        "id": 2,
                        "name": "Electronic Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T13:27:28Z"
                    },
                    "createdAt": "2026-10-15T18:38:16Z",
                    "createdBy": null,
                    "description": "Grade A large red onions",
                    "earlyWarningDays": 3,
                    "hibernateLazyInitializer": {},
                    "id": 3,
                    "images": [
                        {
                            "id": 7,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 8,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        },
                        {
                            "id": 9,
                            "productImage": "https://example.com/images/onion3.jpg",
                            "sortOrder": 2
                        }
                    ],
                    "name": "Red Onions Large",
                    "packagingUnit": "BAG",
                    "productNo": "IT0003",
                    "purchasePrice": 50,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 65,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0027",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMK",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-15T18:38:16Z",
                    "updatedBy": null,
                    "wholesalePrice": 55
                },
                "quantity": 40,
                "taxRate": 16,
                "unitPrice": 80,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "purchaseOrder": {
            "createdAt": "2026-10-20T19:44:43Z",
            "createdBy": "admintest@mailinator.com",
            "expectedDeliveryDate": "2026-10-28",
            "hibernateLazyInitializer": {},
            "id": 2,
            "invoiceStatus": "NOT_INVOICED",
            "lines": [
                {
                    "amount": 5800,
                    "discount": 0,
                    "id": 3,
                    "product": {
                        "batchManaged": true,
                        "brand": "Farm Fresh Premium",
                        "category": {
                            "createdAt": "2026-10-18T16:51:13Z",
                            "id": 3,
                            "name": "Networking Equipment",
                            "status": "INACTIVE",
                            "updatedAt": "2026-10-18T17:11:54Z"
                        },
                        "createdAt": "2026-10-18T15:16:38Z",
                        "createdBy": null,
                        "description": "Updated description - Grade A large red onions",
                        "earlyWarningDays": 30,
                        "hibernateLazyInitializer": {},
                        "id": 5,
                        "images": [
                            {
                                "id": 15,
                                "productImage": "https://example.com/images/onion1.jpg",
                                "sortOrder": 0
                            },
                            {
                                "id": 16,
                                "productImage": "https://example.com/images/onion2.jpg",
                                "sortOrder": 1
                            }
                        ],
                        "name": "Red Onions Large - Updated",
                        "packagingUnit": "BAG",
                        "productNo": "IT0005",
                        "purchasePrice": 52,
                        "purchaseTaxRate": "STANDARD_16",
                        "quantityPerUnit": 1,
                        "retailPrice": 68,
                        "salesTaxRate": "STANDARD_16",
                        "serialNumber": "IT0127",
                        "shelfLifeDays": 14,
                        "skuCode": "VEG-ONR-MMZ",
                        "status": "ACTIVE",
                        "uom": "KG",
                        "updatedAt": "2026-10-22T11:57:15Z",
                        "updatedBy": "admintest@mailinator.com",
                        "wholesalePrice": 58
                    },
                    "quantity": 100,
                    "taxRate": 16,
                    "unitPrice": 50,
                    "warehouse": {
                        "allowNegativeInventory": false,
                        "category": "Main Warehouse",
                        "city": "Nairobi",
                        "contactPerson": "Michael Juma",
                        "createdAt": "2026-10-19T19:50:52Z",
                        "email": "warehouse@company.co.ke",
                        "hibernateLazyInitializer": {},
                        "id": 1,
                        "name": "Imara Daima Main Store",
                        "phone": "0712340000",
                        "postal": "00100",
                        "remark": "Day storage only",
                        "status": "ACTIVE",
                        "street": "Enterprise Road",
                        "updatedAt": "2026-10-19T19:50:52Z",
                        "warehouseNo": "WH0001"
                    }
                },
                {
                    "amount": 4524,
                    "discount": 100,
                    "id": 4,
                    "product": {
                        "batchManaged": true,
                        "brand": "Farm Fresh",
                        "category": {
                            "createdAt": "2026-10-18T12:17:38Z",
                            "id": 2,
                            "name": "Electronic Equipment",
                            "status": "INACTIVE",
                            "updatedAt": "2026-10-18T13:27:28Z"
                        },
                        "createdAt": "2026-10-15T18:38:16Z",
                        "createdBy": null,
                        "description": "Grade A large red onions",
                        "earlyWarningDays": 3,
                        "hibernateLazyInitializer": {},
                        "id": 3,
                        "images": [
                            {
                                "id": 7,
                                "productImage": "https://example.com/images/onion1.jpg",
                                "sortOrder": 0
                            },
                            {
                                "id": 8,
                                "productImage": "https://example.com/images/onion2.jpg",
                                "sortOrder": 1
                            },
                            {
                                "id": 9,
                                "productImage": "https://example.com/images/onion3.jpg",
                                "sortOrder": 2
                            }
                        ],
                        "name": "Red Onions Large",
                        "packagingUnit": "BAG",
                        "productNo": "IT0003",
                        "purchasePrice": 50,
                        "purchaseTaxRate": "STANDARD_16",
                        "quantityPerUnit": 1,
                        "retailPrice": 65,
                        "salesTaxRate": "STANDARD_16",
                        "serialNumber": "IT0027",
                        "shelfLifeDays": 14,
                        "skuCode": "VEG-ONR-MMK",
                        "status": "ACTIVE",
                        "uom": "KG",
                        "updatedAt": "2026-10-15T18:38:16Z",
                        "updatedBy": null,
                        "wholesalePrice": 55
                    },
                    "quantity": 50,
                    "taxRate": 16,
                    "unitPrice": 80,
                    "warehouse": {
                        "allowNegativeInventory": false,
                        "category": "Main Warehouse",
                        "city": "Nairobi",
                        "contactPerson": "Michael Juma",
                        "createdAt": "2026-10-19T19:50:52Z",
                        "email": "warehouse@company.co.ke",
                        "hibernateLazyInitializer": {},
                        "id": 1,
                        "name": "Imara Daima Main Store",
                        "phone": "0712340000",
                        "postal": "00100",
                        "remark": "Day storage only",
                        "status": "ACTIVE",
                        "street": "Enterprise Road",
                        "updatedAt": "2026-10-19T19:50:52Z",
                        "warehouseNo": "WH0001"
                    }
                }
            ],
            "orderDate": "2026-10-20",
            "poNumber": "PO00002",
            "receiptStatus": "PARTIALLY_RECEIVED",
            "status": "APPROVED",
            "supplier": {
                "city": "Kiambu",
                "contactPerson": "James Doe",
                "createdAt": "2026-10-20T11:47:32Z",
                "email": "jamesdoe@limurufarms.co.ke",
                "hibernateLazyInitializer": {},
                "id": 1,
                "name": "Limuru Farms Suppliers Limited",
                "paymentTerms": "DUE_ON_RECEIPT",
                "phone": "0712345678",
                "postal": "00217",
                "status": "ACTIVE",
                "street": "Limuru Road",
                "supplierNo": "SUP0001",
                "updatedAt": "2026-10-23T16:56:01Z"
            },
            "totalAmount": 10324,
            "updatedAt": "2026-10-22T12:23:58Z",
            "updatedBy": "admintest@mailinator.com"
        },
        "receivedDate": "2026-10-21",
        "status": "APPROVED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 9512,
        "updatedAt": "2026-10-22T12:23:58Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```
### Approve goods received note
PATCH: http://localhost:8084/v1/api/goods-received-notes/approve/1212
Authorization: <jwt_token>
```json
{"Message":"Goods received note not found"}
```

### Reject good received note
PATCH: http://localhost:8084/v1/api/goods-received-notes/approve/1212
Authorization: <jwt_token>
```json
{"Message":"Goods received note not found"}
```
### Add purchase return
POST: http://localhost:8084/v1/api/purchase-returns/addPurchaseReturn
Authorization: <jwt_token>
```json
{
  "supplierId": 1,
  "returnDate": "2026-10-22",
  "remarks": "Damaged goods received",
  "lines": [
    {
      "productId": 5,
      "warehouseId": 1,
      "quantity": 20,
      "unitPrice": 50.00,
      "discount": 0,
      "taxRate": 16
    }
  ]
}
```
### Get all purchase return
GET: http://localhost:8084/v1/api/purchase-returns/getAllPurchaseReturns
Authorization: <jwt_token>
```json
{
    "message": "Purchase Return retrieved successfully",
    "payLoad": {
        "createdAt": "2026-10-22T15:00:00Z",
        "createdBy": "admintest@mailinator.com",
        "id": 4,
        "invoiceStatus": "NOT_INVOICED",
        "lines": [
            {
                "amount": 1160,
                "discount": 0,
                "id": 4,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 20,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "paymentStatus": "NOT_PAID",
        "prNumber": "PR00004",
        "remarks": "Damaged goods received",
        "returnDate": "2026-10-22",
        "status": "REJECTED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 1160,
        "updatedAt": "2026-10-22T15:02:11Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```

### Get purchase return by id
GET: http://localhost:8084/v1/api/purchase-returns/getPurchaseReturnById/4
Authorization: <jwt_token>
```json
{
    "message": "Purchase Return retrieved successfully",
    "payLoad": {
        "createdAt": "2026-10-22T15:00:00Z",
        "createdBy": "admintest@mailinator.com",
        "id": 4,
        "invoiceStatus": "NOT_INVOICED",
        "lines": [
            {
                "amount": 1160,
                "discount": 0,
                "id": 4,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 20,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "paymentStatus": "NOT_PAID",
        "prNumber": "PR00004",
        "remarks": "Damaged goods received",
        "returnDate": "2026-10-22",
        "status": "REJECTED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 1160,
        "updatedAt": "2026-10-22T15:02:11Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```

### Get purchase return by status
GET: http://localhost:8084/v1/api/purchase-returns/getPurchaseReturnsByStatus/REJECTED
```json
{
    "message": "Purchase Return retrieved successfully",
    "payLoad": {
        "createdAt": "2026-10-22T15:00:00Z",
        "createdBy": "admintest@mailinator.com",
        "id": 4,
        "invoiceStatus": "NOT_INVOICED",
        "lines": [
            {
                "amount": 1160,
                "discount": 0,
                "id": 4,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 20,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "paymentStatus": "NOT_PAID",
        "prNumber": "PR00004",
        "remarks": "Damaged goods received",
        "returnDate": "2026-10-22",
        "status": "REJECTED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 1160,
        "updatedAt": "2026-10-22T15:02:11Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```

### Get purchase return by id
GET: http://localhost:8084/v1/api/purchase-returns/getPurchaseReturnById/4
Authorization: <jwt_token>
```json
{
    "message": "Purchase Return retrieved successfully",
    "payLoad": {
        "createdAt": "2026-10-22T15:00:00Z",
        "createdBy": "admintest@mailinator.com",
        "id": 4,
        "invoiceStatus": "NOT_INVOICED",
        "lines": [
            {
                "amount": 1160,
                "discount": 0,
                "id": 4,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 20,
                "taxRate": 16,
                "unitPrice": 50,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "paymentStatus": "NOT_PAID",
        "prNumber": "PR00004",
        "remarks": "Damaged goods received",
        "returnDate": "2026-10-22",
        "status": "REJECTED",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalAmount": 1160,
        "updatedAt": "2026-10-22T15:02:11Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```
### Approve purchase return
PATCH: http://localhost:8084/v1/api/purchase-returns/approve/3
```json
{"Message":"Only pending approval returns can be approved"}
```
### Reject purchase return
PATCH: http://localhost:8084/v1/api/purchase-returns/reject/4
```json
{"Message":"Purchase Return rejected"}
```
## Stock in APIs

### Add stock in
POST: http://localhost:8084/v1/api/stock-ins/addStockIn
Authorization: <jwt_token>
```json
{
  "supplierId": 1,
  "date": "2026-10-22",
  "notes": "Manual stock adjustment - found extra stock",
  "lines": [
    {
      "productId": 5,
      "warehouseId": 1,
      "quantity": 150,
      //"batchNo": "LOT-20261022-VEGONR-WH001",
      "expense": 7500
    }
  ]
}
```
### Get all sock in
GET: http://localhost:8084/v1/api/stock-ins/getAllStockIns
Authorization: <jwt_token>
```json
{
    "message": "Stock In retrieved successfully",
    "payLoad": {
        "approvalStatus": "REJECTED",
        "createdAt": "2026-10-22T13:53:07Z",
        "createdBy": "admintest@mailinator.com",
        "date": "2026-10-22",
        "id": 2,
        "lines": [
            {
                "batchNo": "SIBN0002",
                "expense": 7500,
                "id": 2,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 150,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "notes": "Manual stock adjustment - found extra stock",
        "stockInNo": "IBDST00002",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalExpense": 7500,
        "updatedAt": "2026-10-22T14:34:21Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```
### Get stock by id
GET: http://localhost:8084/v1/api/stock-ins/getStockInById/2
Authorization: <jwt_token>
```json
{
    "message": "Stock In retrieved successfully",
    "payLoad": {
        "approvalStatus": "REJECTED",
        "createdAt": "2026-10-22T13:53:07Z",
        "createdBy": "admintest@mailinator.com",
        "date": "2026-10-22",
        "id": 2,
        "lines": [
            {
                "batchNo": "SIBN0002",
                "expense": 7500,
                "id": 2,
                "product": {
                    "batchManaged": true,
                    "brand": "Farm Fresh Premium",
                    "category": {
                        "createdAt": "2026-10-18T16:51:13Z",
                        "id": 3,
                        "name": "Networking Equipment",
                        "status": "INACTIVE",
                        "updatedAt": "2026-10-18T17:11:54Z"
                    },
                    "createdAt": "2026-10-18T15:16:38Z",
                    "createdBy": null,
                    "description": "Updated description - Grade A large red onions",
                    "earlyWarningDays": 30,
                    "hibernateLazyInitializer": {},
                    "id": 5,
                    "images": [
                        {
                            "id": 15,
                            "productImage": "https://example.com/images/onion1.jpg",
                            "sortOrder": 0
                        },
                        {
                            "id": 16,
                            "productImage": "https://example.com/images/onion2.jpg",
                            "sortOrder": 1
                        }
                    ],
                    "name": "Red Onions Large - Updated",
                    "packagingUnit": "BAG",
                    "productNo": "IT0005",
                    "purchasePrice": 52,
                    "purchaseTaxRate": "STANDARD_16",
                    "quantityPerUnit": 1,
                    "retailPrice": 68,
                    "salesTaxRate": "STANDARD_16",
                    "serialNumber": "IT0127",
                    "shelfLifeDays": 14,
                    "skuCode": "VEG-ONR-MMZ",
                    "status": "ACTIVE",
                    "uom": "KG",
                    "updatedAt": "2026-10-22T11:57:15Z",
                    "updatedBy": "admintest@mailinator.com",
                    "wholesalePrice": 58
                },
                "quantity": 150,
                "warehouse": {
                    "allowNegativeInventory": false,
                    "category": "Main Warehouse",
                    "city": "Nairobi",
                    "contactPerson": "Michael Juma",
                    "createdAt": "2026-10-19T19:50:52Z",
                    "email": "warehouse@company.co.ke",
                    "hibernateLazyInitializer": {},
                    "id": 1,
                    "name": "Imara Daima Main Store",
                    "phone": "0712340000",
                    "postal": "00100",
                    "remark": "Day storage only",
                    "status": "ACTIVE",
                    "street": "Enterprise Road",
                    "updatedAt": "2026-10-19T19:50:52Z",
                    "warehouseNo": "WH0001"
                }
            }
        ],
        "notes": "Manual stock adjustment - found extra stock",
        "stockInNo": "IBDST00002",
        "supplier": {
            "city": "Kiambu",
            "contactPerson": "James Doe",
            "createdAt": "2026-10-20T11:47:32Z",
            "email": "jamesdoe@limurufarms.co.ke",
            "hibernateLazyInitializer": {},
            "id": 1,
            "name": "Limuru Farms Suppliers Limited",
            "paymentTerms": "DUE_ON_RECEIPT",
            "phone": "0712345678",
            "postal": "00217",
            "status": "ACTIVE",
            "street": "Limuru Road",
            "supplierNo": "SUP0001",
            "updatedAt": "2026-10-23T16:56:01Z"
        },
        "totalExpense": 7500,
        "updatedAt": "2026-10-22T14:34:21Z",
        "updatedBy": "admintest@mailinator.com"
    },
    "status": "SUCCESS"
}
```
### Approve stock in
PATCH: http://localhost:8084/v1/api/stock-ins/approve/2
Authorization: <jwt_token>
```json
{"Message":"Only pending Stock In can be approved"}
```

### Reject stock in
PATCH: http://localhost:8084/v1/api/stock-ins/reject/2
Authorization: <jwt_token>
```json
{"Message":"Stock In rejected"}
```







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