# ElectroMart — Backend (Spring Boot 4.1.0 / Java 21)

REST API for the ElectroMart e-commerce frontend. Package base: `com.electromart.ecommerce`.

## Kept exactly as provided

These three files (plus the Mongo lines in `application.properties`) were dropped in
unchanged, as requested — everything else was adapted around them:

- `config/SpringSecurity.java` — `DaoAuthenticationProvider` + stateless JWT security
- `filter/JwtFilter.java` — reads `Authorization: Bearer <token>`, loads the user, sets auth context
- `utils/JwtUtils.java` — token generation/validation (subject = email only, no embedded role/claims)
- `application.properties` Mongo block (`spring.mongodb.host/port/database`)

**Two things worth knowing about the kept files:**
1. `application.properties` uses `spring.mongodb.*` (missing `data.`). The real Spring Boot
   property is `spring.data.mongodb.host` / `.port` / `.database`. As written, Spring Boot
   won't bind these and will fall back to `mongodb://localhost:27017/test`. Since the
   defaults (`localhost:27017`) match what's written anyway, this only matters if you deploy
   to a different host/port/db name — in that case add `spring.data.mongodb.*` as well (or
   instead).
2. `JwtUtils`'s expiration is `1000 * 60 * 60 * 60` ms = **60 hours**, even though the code
   comment says "5 minutes." Left as-is per your file; adjust if you want a shorter session.

Because the token only carries the user's email (no role claim), `JwtFilter` re-loads the
full user (including current role) from MongoDB on every request via
`CustomUserDetailServiceImp` — so role changes take effect immediately without needing a
new token.

## Project Structure

```
com.electromart.ecommerce
├── config          SpringSecurity, CorsConfig, CloudinaryConfig, DataSeeder
├── controller       AuthController, ProductController, AdminController,
│                     CartController, WishlistController, OrderController
├── dto              Request/response payloads
├── entity           MongoDB documents (@Document)
├── exception        Custom exceptions + @RestControllerAdvice
├── filter           JwtFilter
├── repository       Spring Data MongoDB repositories
├── services         AuthService, ProductService, CartService, WishlistService,
│                     OrderService, CloudinaryService, CustomUserDetailServiceImp, UserPrincipal
├── utils            JwtUtils, AuthUtils
└── EcommerceApplication.java
```

## Route prefixes (must match `SpringSecurity`'s matchers)

| Prefix       | Access                     | Used by                                   |
|--------------|------------------------------|--------------------------------------------|
| `/public/**` | open to everyone            | `AuthController`, `ProductController`      |
| `/reg/**`    | any authenticated user      | `CartController`, `WishlistController`, `OrderController` |
| `/admin/**`  | requires `ROLE_ADMIN`       | `AdminController`                          |

## Prerequisites

- Java 21, Maven 3.9+
- MongoDB running locally on `27017` (or update the Mongo properties)
- A Cloudinary account for product image uploads

## Configuration

`src/main/resources/application.properties` — set your real Cloudinary keys and,
if needed, a different CORS origin:

```properties
cloudinary.cloud-name=your-cloud-name
cloudinary.api-key=your-api-key
cloudinary.api-secret=your-api-secret
app.cors.allowed-origins=http://localhost:4200
```

## Running

```bash
mvn spring-boot:run
```

API starts on `http://localhost:8080`. On first run, `DataSeeder` creates:
- Default admin: **admin@electromart.com / Admin@123**
- The same 16 sample electronics used in the Angular frontend

## API Overview

### Public
| Method | Endpoint                       | Description                                       |
|--------|----------------------------------|----------------------------------------------------|
| POST   | `/public/auth/signup`          | Register a new user                                |
| POST   | `/public/auth/login`           | Log in, returns JWT                                |
| GET    | `/public/products`             | List, with `?category=&brand=&search=&maxPrice=&sort=` |
| GET    | `/public/products/{id}`        | Get one product                                     |
| GET    | `/public/products/categories`  | Distinct category list                              |
| GET    | `/public/products/brands`      | Distinct brand list                                 |

### Authenticated (`Authorization: Bearer <token>`)
| Method | Endpoint                                | Description                       |
|--------|--------------------------------------------|-------------------------------------|
| GET    | `/reg/cart`                                | Get current user's cart            |
| POST   | `/reg/cart/add`                            | Add item `{productId, quantity}`   |
| PUT    | `/reg/cart/update/{productId}?quantity=`   | Update quantity                    |
| DELETE | `/reg/cart/remove/{productId}`             | Remove item                        |
| DELETE | `/reg/cart/clear`                          | Empty the cart                     |
| GET    | `/reg/wishlist`                            | Get wishlist                       |
| POST   | `/reg/wishlist/toggle/{productId}`         | Add/remove from wishlist           |
| POST   | `/reg/orders`                              | Place order `{shippingAddress:{...}}` |
| GET    | `/reg/orders`                              | Current user's order history       |

### Admin (`ROLE_ADMIN`)
| Method | Endpoint                    | Description                                                     |
|--------|--------------------------------|--------------------------------------------------------------------|
| POST   | `/admin/products`             | Create product (multipart: `product` JSON part + `image` file)    |
| PUT    | `/admin/products/{id}`        | Update product (same multipart shape)                              |
| DELETE | `/admin/products/{id}`        | Delete product                                                     |
| GET    | `/admin/orders`               | All orders                                                          |
| PUT    | `/admin/orders/{id}/status`   | Update order status `{status}`                                     |

## Notes

- Passwords are hashed with BCrypt.
- This project was **not compiled in the sandboxed build environment** (no Maven Central
  access there) — run `mvn compile` locally as your first step.

## Dashboard endpoints (added for the admin/customer dashboards)

| Method | Endpoint          | Access        | Description                                    |
|--------|--------------------|---------------|--------------------------------------------------|
| GET    | `/reg/account`     | authenticated | Current user's profile (name, email, role, joined) |
| PUT    | `/reg/account`     | authenticated | Update display name `{name}`                    |
| GET    | `/admin/stats`     | `ROLE_ADMIN`  | Dashboard cards: total products/orders/users/revenue |
