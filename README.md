# Spring Boot E-Commerce Application

A comprehensive e-commerce application built with Spring Boot, MongoDB, and JWT authentication featuring dynamic pricing and inventory synchronization.

## Features

- **JWT Authentication**: Secure user registration and login
- **Business Line Management**: Products filtered by user's business line
- **Dynamic Pricing**: Real-time discount calculations based on inventory levels
- **Shopping Cart**: Full cart management with quantity updates
- **Inventory Synchronization**: Thread-safe concurrent order processing
- **MongoDB Database**: NoSQL database for scalability

## Business Requirements

### Users & Business Lines
- **userA**: Business Line Buss1, 15% personal discount
- **userB**: Business Line Buss1, 10% personal discount
- **userC**: Business Line Buss2, 20% personal discount

### Products
**Business Line 1 (Buss1) - Humanities Books:**
- PhilosophyBooks ($25.99, 10% base discount)
- SpiritualityBooks ($29.99, 12% base discount)
- SociologyBooks ($34.99, 8% base discount)
- HistoryBooks ($27.99, 15% base discount)

**Business Line 2 (Buss2) - Physics Books:**
- QuantumMathsBook ($45.99, 5% base discount)
- TheoryOfRelativityBook ($39.99, 7% base discount)
- QuantumMechanicsFundamentals ($49.99, 6% base discount)
- StringTheory ($52.99, 4% base discount)

### Dynamic Discount Algorithm
**Rule**: If product quantity decreases by 10%, discount rate decreases by 20% of initial discount rate

**Formula**: 
```
currentDiscountRate = initialDiscountRate * (1 - (0.2 * (quantityDecrease / 0.1)))
```

### Inventory Synchronization
- Thread-safe inventory updates using synchronized methods
- Product-level locking to prevent overselling
- Handles concurrent orders when inventory is limited

## Technology Stack

- **Backend**: Spring Boot 3.1.5, Java 17
- **Database**: MongoDB
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **Authentication**: BCrypt password encryption

## Quick Start

### Prerequisites
- Java 17 or higher
- MongoDB (running on localhost:27017)
- Maven 3.6 or higher

### Installation

1. **Clone and navigate to project**
   ```bash
   cd spring-boot-ecommerce
   ```

2. **Start MongoDB**
   ```bash
   # On Linux/Mac
   sudo systemctl start mongod

   # On Windows
   net start MongoDB
   ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - API Base URL: http://localhost:8080
   - MongoDB Database: `ecommerce_db`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/check-username?username={username}` - Check username availability
- `GET /api/auth/check-email?email={email}` - Check email availability

### Products
- `GET /api/products` - Get all products (with current discounts)
- `GET /api/products/business-line/{businessLineId}` - Get products by business line
- `GET /api/products/{id}` - Get single product
- `GET /api/products/{id}/availability?quantity={qty}` - Check stock availability

### Shopping Cart
- `GET /api/cart` - Get user's cart items
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/update` - Update cart item quantity
- `DELETE /api/cart/remove/{productId}` - Remove item from cart
- `DELETE /api/cart/clear` - Clear entire cart
- `GET /api/cart/count` - Get cart item count
- `GET /api/cart/validate` - Validate cart inventory

### Orders
- `POST /api/orders/place` - Place order (with inventory sync)
- `GET /api/orders` - Get user's order history
- `GET /api/orders/{orderId}` - Get specific order details

## Testing the Application

### Sample User Credentials
- **Username**: `userA`, **Password**: `password123`
- **Username**: `userB`, **Password**: `password123`
- **Username**: `userC`, **Password**: `password123`

### Testing Scenarios

1. **User Authentication**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"username": "userA", "password": "password123"}'
   ```

2. **Get Products by Business Line**
   ```bash
   curl -X GET http://localhost:8080/api/products/business-line/{businessLineId} \
   -H "Authorization: Bearer {jwt_token}"
   ```

3. **Add to Cart**
   ```bash
   curl -X POST http://localhost:8080/api/cart/add \
   -H "Authorization: Bearer {jwt_token}" \
   -H "Content-Type: application/json" \
   -d '{"productId": "{productId}", "quantity": 2}'
   ```

4. **Place Order**
   ```bash
   curl -X POST http://localhost:8080/api/orders/place \
   -H "Authorization: Bearer {jwt_token}"
   ```

### Testing Concurrent Orders
The application handles concurrent inventory updates. To test:

1. Use two different users (userA and userB)
2. Add the same product to both carts when stock is limited
3. Try to place orders simultaneously
4. Only one order should succeed

## Configuration

### MongoDB Configuration
```properties
spring.data.mongodb.database=ecommerce_db
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
```

### JWT Configuration
```properties
jwt.secret=mySecretKeyForECommerceApplication2024SpringBootMongoDBVerySecureKey123
jwt.expiration=86400000
```

### Custom Configuration
- Server runs on port 8080
- CORS enabled for development
- Debug logging enabled for com.ecommerce package

## Data Initialization

The application automatically initializes sample data on startup:
- Business lines (Buss1, Buss2)
- Products with initial inventory and discount rates
- Test user accounts

## Architecture Overview

### Entities
- **User**: Authentication and business line association
- **BusinessLine**: Product categorization
- **Product**: Inventory and pricing management
- **CartItem**: Shopping cart functionality
- **Order**: Transaction records

### Services
- **AuthService**: User authentication and registration
- **ProductService**: Product management with synchronized inventory
- **CartService**: Shopping cart operations
- **OrderService**: Order processing with inventory updates
- **DiscountCalculationService**: Dynamic pricing calculations

### Security
- JWT-based authentication
- Role-based access control
- Password encryption with BCrypt
- CORS configuration for API access

## Development Notes

### Synchronization Strategy
- Uses `ReentrantLock` per product to prevent overselling
- Atomic MongoDB operations for inventory updates
- Transaction management for order processing

### Error Handling
- Global exception handler for consistent error responses
- Custom exceptions for business logic violations
- Input validation using Bean Validation

### Performance Considerations
- Product-level locking instead of global synchronization
- Efficient MongoDB queries with proper indexing
- Optimized discount calculations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
