# Connectix App

Spring Boot backend for the shopkeeper/dealer workflows defined in `API-Design.xlsx`.

This document is the **developer reference** for the current codebase. Use it to understand:

- how to run the project locally
- which files control runtime behavior
- how the `/api/v1/**` APIs map to controllers and services
- which entities/repositories are involved in each flow
- where the current gaps are if you continue development

---

## 1. Verified Current State

### Verified locally

The following were verified against the current workspace:

```bash
./gradlew clean build -x test
```

The application also starts successfully with PostgreSQL running from `docker-compose.yml`, and the health endpoint responds on:

```text
http://localhost:8081/actuator/health
```

### Runtime summary

- Java: **21**
- Spring Boot: **3.3.5**
- Default app port: **8081**
- Default local Postgres host port: **5433**
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`

---

## 2. Source of Truth Files

Start with these files before changing behavior.

| Area | File | Why it matters |
|---|---|---|
| App entrypoint | `src/main/java/com/example/usermanagement/UserManagementApplication.java` | Main Spring Boot application class |
| Build | `build.gradle` | Java version, dependencies, Spring Boot version |
| Local DB container | `docker-compose.yml` | Local PostgreSQL setup |
| Container image | `Dockerfile` | Multi-stage image build |
| App runtime config | `src/main/resources/application.properties` | Datasource, JPA, server port, JWT defaults |
| Security rules | `src/main/java/com/example/usermanagement/config/SecurityConfig.java` | Public vs protected endpoints |
| JWT helper | `src/main/java/com/example/usermanagement/config/JwtUtil.java` | Token generation and parsing |
| JWT filter | `src/main/java/com/example/usermanagement/config/JwtAuthFilter.java` | Request authentication |
| Swagger/OpenAPI | `src/main/java/com/example/usermanagement/config/OpenApiConfig.java` | Bearer auth + API docs config |
| Seed behavior | `src/main/java/com/example/usermanagement/config/DataSeeder.java` | Minimal bootstrap users |
| API contract reference | `API-Design.xlsx` | Business-facing contract source |
| CI | `.github/workflows/ci.yml` | Build and test workflow |
| CD | `.github/workflows/cd.yml` | Docker image publishing workflow |

---

## 3. Local Setup

### Prerequisites

- Java 21
- Docker / Docker Compose
- Gradle wrapper (`./gradlew`)

### Start PostgreSQL locally

Verified command:

```bash
cd /Users/Deepak_Dubey/Documents/springs-demo/connectix-app

docker-compose up -d
```

### Current local PostgreSQL defaults

These are aligned between `docker-compose.yml` and `application.properties` defaults:

| Setting | Value |
|---|---|
| DB name | `connectix` |
| DB user | `postgres` |
| DB password | `admin@123` |
| Host | `localhost` |
| Host port | `5433` |
| Container port | `5432` |

### Run the app

```bash
cd /Users/Deepak_Dubey/Documents/springs-demo/connectix-app

./gradlew bootRun
```

### Health check

```bash
curl http://localhost:8081/actuator/health
```

### Stop services

```bash
docker-compose down
```

If you want to remove the DB volume too:

```bash
docker-compose down -v
```

---

## 4. Environment Overrides

`application.properties` is now environment-driven for the important runtime settings.

### Current defaults from `application.properties`

| Property | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5433/connectix` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `admin@123` |
| `SERVER_PORT` | `8081` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `SPRING_JPA_SHOW_SQL` | `true` |
| `APP_LOG_LEVEL` | `DEBUG` |
| `JWT_SECRET` | in-file default present |
| `JWT_EXPIRATION` | `86400000` |

### Example override

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/otherdb \
SPRING_DATASOURCE_USERNAME=otheruser \
SPRING_DATASOURCE_PASSWORD=otherpass \
SERVER_PORT=8082 \
./gradlew bootRun
```

---

## 5. Project Structure

```text
src/main/java/com/example/usermanagement
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
├── service/
└── util/
```

### Package responsibilities

| Package | Responsibility |
|---|---|
| `config` | security, JWT, Swagger, seed logic |
| `controller` | REST API entrypoints |
| `dto` | request/response contracts |
| `entity` | JPA entities and enums |
| `exception` | domain-specific exceptions |
| `repository` | Spring Data persistence layer |
| `service` | business logic |
| `util` | helper utilities |

---

## 6. API Contract Mapping from `API-Design.xlsx`

The `Shopkeeper Flow` sheet currently contains the APIs that matter for the implemented `/api/v1/**` flow.

The `Dealer Flow` sheet in the workbook is currently only a header row, so the codebase is still mostly driven by the `Shopkeeper Flow` sheet plus internal extensions.

### `/api/v1/**` implementation map

| Excel API # | Endpoint | Controller | Primary Service | Status |
|---|---|---|---|---|
| 1 | `POST /api/v1/auth/request-otp` | `V1AuthController` | `UserService` | Implemented |
| 2 | `POST /api/v1/auth/verify-otp` | `V1AuthController` | `UserService` | Implemented |
| 3 | `POST /api/v1/shopkeepers` | `ShopkeeperController` | `ShopDetailsService` | Implemented |
| 4 | `PUT /api/v1/shopkeepers/{shopkeeperId}` | `ShopkeeperController` | `ShopDetailsService` | Implemented |
| 5 | `GET /api/v1/dealers` | `DealerController` | `DealerService` | Implemented |
| 6 | `GET /api/v1/dealers/{dealerId}/products` | `DealerController` | `DealerService` | Implemented |
| 7 | `POST /api/v1/orders` | `OrderController` | `OrderService` | Implemented |
| 8 | `GET /api/v1/orders/{orderId}` | `OrderController` | `OrderService` | Implemented |
| 9 | `GET /api/v1/shopkeepers/{shopkeeperId}/orders` | `OrderController` | `OrderService` | Implemented |

### Extra `/api/v1/**` endpoints added in code

| Endpoint | Controller | Service | Purpose |
|---|---|---|---|
| `POST /api/v1/dealers/{dealerId}/bookmark` | `DealerController` | `DealerService` | Save dealer bookmark |
| `DELETE /api/v1/dealers/{dealerId}/bookmark` | `DealerController` | `DealerService` | Remove dealer bookmark |

---

## 7. Controller Reference Map

### v1 controllers

| Controller | Base Path | Main Responsibility |
|---|---|---|
| `V1AuthController` | `/api/v1/auth` | OTP request + verify flow aligned to Excel |
| `ShopkeeperController` | `/api/v1/shopkeepers` | Shopkeeper create/update |
| `DealerController` | `/api/v1/dealers` | Dealer discovery, products, bookmarks |
| `OrderController` | `/api/v1` | Orders and shopkeeper order history |

### legacy controllers still present

| Controller | Base Path | Notes |
|---|---|---|
| `AuthController` | `/api/auth` | Older register/otp/login flow |
| `ShopDetailsController` | `/api/shop-details` | Older CRUD around shop details |
| `ProductDetailsController` | `/api/products` | Product master CRUD |
| `DealerProductItemController` | `/api/dealer/product-items` | Dealer product item CRUD |
| `CommonAttributeController` | `/api/common/attributes` | Common attribute APIs |
| `CommonController` | `/api/common` | Placeholder controller |
| `UploadImageController` | controller-specific path | Image upload flow |

If you are extending the Excel contract, prefer the `v1` controllers first.

---

## 8. API-to-Code Reference Map

This section is meant to answer: **“If I need to change an endpoint, which files should I open?”**

### 8.1 Auth: `POST /api/v1/auth/request-otp`

Open these files:

- `src/main/java/com/example/usermanagement/controller/V1AuthController.java`
- `src/main/java/com/example/usermanagement/service/UserService.java`
- `src/main/java/com/example/usermanagement/service/OtpService.java`
- `src/main/java/com/example/usermanagement/dto/OtpV1RequestDto.java`
- `src/main/java/com/example/usermanagement/dto/OtpV1ResponseDto.java`
- `src/main/java/com/example/usermanagement/entity/User.java`
- `src/main/java/com/example/usermanagement/repository/UserRepository.java`

### 8.2 Auth: `POST /api/v1/auth/verify-otp`

Open these files:

- `src/main/java/com/example/usermanagement/controller/V1AuthController.java`
- `src/main/java/com/example/usermanagement/service/UserService.java`
- `src/main/java/com/example/usermanagement/config/JwtUtil.java`
- `src/main/java/com/example/usermanagement/dto/VerifyOtpRequestDto.java`
- `src/main/java/com/example/usermanagement/dto/VerifyOtpResponseDto.java`
- `src/main/java/com/example/usermanagement/entity/User.java`
- `src/main/java/com/example/usermanagement/exception/InvalidOtpException.java`
- `src/main/java/com/example/usermanagement/exception/InvalidTransactionException.java`
- `src/main/java/com/example/usermanagement/exception/OtpExpiredException.java`

### 8.3 Shopkeeper: `POST /api/v1/shopkeepers`

Open these files:

- `src/main/java/com/example/usermanagement/controller/ShopkeeperController.java`
- `src/main/java/com/example/usermanagement/service/ShopDetailsService.java`
- `src/main/java/com/example/usermanagement/dto/ShopDetailsRequestDto.java`
- `src/main/java/com/example/usermanagement/dto/ShopkeeperResponseDto.java`
- `src/main/java/com/example/usermanagement/entity/ShopDetails.java`
- `src/main/java/com/example/usermanagement/entity/User.java`
- `src/main/java/com/example/usermanagement/repository/ShopDetailsRepository.java`
- `src/main/java/com/example/usermanagement/repository/UserRepository.java`

### 8.4 Shopkeeper: `PUT /api/v1/shopkeepers/{shopkeeperId}`

Open these files:

- `src/main/java/com/example/usermanagement/controller/ShopkeeperController.java`
- `src/main/java/com/example/usermanagement/service/ShopDetailsService.java`
- `src/main/java/com/example/usermanagement/entity/ShopDetails.java`
- `src/main/java/com/example/usermanagement/exception/ForbiddenException.java`
- `src/main/java/com/example/usermanagement/exception/ShopDetailsNotFoundException.java`

### 8.5 Dealers: `GET /api/v1/dealers`

Open these files:

- `src/main/java/com/example/usermanagement/controller/DealerController.java`
- `src/main/java/com/example/usermanagement/service/DealerService.java`
- `src/main/java/com/example/usermanagement/dto/DealerListResponseDto.java`
- `src/main/java/com/example/usermanagement/dto/DealerInfoDto.java`
- `src/main/java/com/example/usermanagement/dto/ShopkeeperOrderSummaryDto.java`
- `src/main/java/com/example/usermanagement/entity/User.java`
- `src/main/java/com/example/usermanagement/entity/BookmarkedDealer.java`
- `src/main/java/com/example/usermanagement/entity/Order.java`
- `src/main/java/com/example/usermanagement/repository/UserRepository.java`
- `src/main/java/com/example/usermanagement/repository/BookmarkedDealerRepository.java`
- `src/main/java/com/example/usermanagement/repository/OrderRepository.java`

### 8.6 Dealer products: `GET /api/v1/dealers/{dealerId}/products`

Open these files:

- `src/main/java/com/example/usermanagement/controller/DealerController.java`
- `src/main/java/com/example/usermanagement/service/DealerService.java`
- `src/main/java/com/example/usermanagement/dto/DealerProductPageResponseDto.java`
- `src/main/java/com/example/usermanagement/dto/DealerProductItemDto.java`
- `src/main/java/com/example/usermanagement/entity/DealerProduct.java`
- `src/main/java/com/example/usermanagement/entity/DealerProductItem.java`
- `src/main/java/com/example/usermanagement/entity/ProductDetails.java`
- `src/main/java/com/example/usermanagement/repository/DealerProductRepository.java`
- `src/main/java/com/example/usermanagement/repository/DealerProductItemRepository.java`

### 8.7 Orders: `POST /api/v1/orders`

Open these files:

- `src/main/java/com/example/usermanagement/controller/OrderController.java`
- `src/main/java/com/example/usermanagement/service/OrderService.java`
- `src/main/java/com/example/usermanagement/dto/OrderRequestDto.java`
- `src/main/java/com/example/usermanagement/dto/OrderItemRequestDto.java`
- `src/main/java/com/example/usermanagement/dto/OrderResponseDto.java`
- `src/main/java/com/example/usermanagement/dto/OrderItemResponseDto.java`
- `src/main/java/com/example/usermanagement/entity/Order.java`
- `src/main/java/com/example/usermanagement/entity/OrderItem.java`
- `src/main/java/com/example/usermanagement/entity/OrderStatus.java`
- `src/main/java/com/example/usermanagement/repository/OrderRepository.java`
- `src/main/java/com/example/usermanagement/repository/ProductDetailsRepository.java`
- `src/main/java/com/example/usermanagement/repository/UserRepository.java`

### 8.8 Orders: `GET /api/v1/orders/{orderId}`

Open these files:

- `src/main/java/com/example/usermanagement/controller/OrderController.java`
- `src/main/java/com/example/usermanagement/service/OrderService.java`
- `src/main/java/com/example/usermanagement/repository/OrderRepository.java`
- `src/main/java/com/example/usermanagement/exception/OrderNotFoundException.java`

### 8.9 Orders: `GET /api/v1/shopkeepers/{shopkeeperId}/orders`

Open these files:

- `src/main/java/com/example/usermanagement/controller/OrderController.java`
- `src/main/java/com/example/usermanagement/service/OrderService.java`
- `src/main/java/com/example/usermanagement/dto/ShopkeeperOrdersPageDto.java`
- `src/main/java/com/example/usermanagement/repository/OrderRepository.java`
- `src/main/java/com/example/usermanagement/repository/UserRepository.java`

---

## 9. Service Reference Map

| Service | Main Concern | Common Files Around It |
|---|---|---|
| `UserService` | registration, OTP, JWT login support | `User`, `UserRepository`, `OtpService`, auth DTOs |
| `ShopDetailsService` | shopkeeper details create/update/read | `ShopDetails`, `ShopDetailsRepository`, `UserRepository` |
| `DealerService` | dealer search, bookmarks, dealer products | `User`, `DealerProduct`, `DealerProductItem`, `BookmarkedDealer`, `Order` |
| `OrderService` | place order, fetch order, list shopkeeper orders | `Order`, `OrderItem`, `ProductDetails`, `OrderRepository` |
| `ProductDetailsService` | product master CRUD | `ProductDetails`, `ProductDetailsRepository` |
| `DealerProductItemService` | dealer-owned product item CRUD | `DealerProductItem`, `DealerProductRepository`, `ProductDetailsRepository` |
| `CommonAttributeService` | common attribute management | `CommonAttribute`, `CommonAttributeRepository` |
| `ImageService` | upload/image processing flow | `Image`, `ImageRepository`, `ImageCompressor`, `FileUtils` |
| `OtpService` | OTP generation/send abstraction | OTP-related auth flows |

---

## 10. Entity Reference Map

### Core auth / user

| Entity | Purpose |
|---|---|
| `User` | phone-based user, role, OTP state, shop-related profile fields |
| `Role` | `USER`, `DEALER`, `SHOPKEEPER`, `ADMIN` |

### Shopkeeper / dealer domain

| Entity | Purpose |
|---|---|
| `ShopDetails` | shopkeeper detail record linked to `User` |
| `DealerProduct` | dealer-owned product collection/container |
| `DealerProductItem` | dealer-specific quantity/price for a product |
| `BookmarkedDealer` | shopkeeper bookmark relationship to dealer |

### Product domain

| Entity | Purpose |
|---|---|
| `ProductDetails` | product master definition |
| `ProductType` | broad category enum |
| `ProductSubType` | subcategory enum |
| `QuantityType` | unit enum |
| `CommonAttribute` | shared attribute model used by common APIs |
| `Image` | uploaded image metadata |

### Order domain

| Entity | Purpose |
|---|---|
| `Order` | order header between shopkeeper and dealer |
| `OrderItem` | line item under an order |
| `OrderStatus` | order lifecycle enum |

---

## 11. Repository Reference Map

| Repository | Primary Entity | Notes |
|---|---|---|
| `UserRepository` | `User` | user lookup, dealer filtering query |
| `ShopDetailsRepository` | `ShopDetails` | supports `findByUser_Id(...)` |
| `DealerProductRepository` | `DealerProduct` | dealer-owned product containers |
| `DealerProductItemRepository` | `DealerProductItem` | items under a dealer product |
| `ProductDetailsRepository` | `ProductDetails` | product master lookup |
| `OrderRepository` | `Order` | shopkeeper orders and order lookup |
| `BookmarkedDealerRepository` | `BookmarkedDealer` | bookmark add/remove/list |
| `CommonAttributeRepository` | `CommonAttribute` | common attributes |
| `ImageRepository` | `Image` | image persistence |

---

## 12. Security Reference Map

### Main files

- `src/main/java/com/example/usermanagement/config/SecurityConfig.java`
- `src/main/java/com/example/usermanagement/config/JwtAuthFilter.java`
- `src/main/java/com/example/usermanagement/config/JwtUtil.java`
- `src/main/java/com/example/usermanagement/controller/ApiExceptionHandler.java`
- `src/main/java/com/example/usermanagement/service/UserService.java`

### Current public endpoints

From `SecurityConfig`:

- `/api/auth/**`
- `/api/v1/auth/**`
- `/h2-console/**`
- `/swagger-ui.html`
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/v3/api-docs.yaml`
- `/actuator/health`
- `/api/common/attributes`
- `GET /api/v1/dealers`
- `GET /api/v1/dealers/*/products`

Everything else is authenticated unless method-level rules say otherwise.

---

## 13. Swagger / OpenAPI

### References

- Config file: `src/main/java/com/example/usermanagement/config/OpenApiConfig.java`
- UI: `http://localhost:8081/swagger-ui/index.html`
- Docs JSON: `http://localhost:8081/v3/api-docs`

`OpenApiConfig` registers Bearer token security for Swagger.

---

## 14. Build, CI, and Container References

### Build locally

```bash
./gradlew clean build -x test
```

### CI workflow

- File: `.github/workflows/ci.yml`
- Uses Java 21
- Runs:

```bash
./gradlew clean build --no-daemon
```

### CD workflow

- File: `.github/workflows/cd.yml`
- Builds and publishes a container image to GHCR

### Docker image build

```bash
docker build -t connectix-app .
```

### Dockerfile reference

- Build stage: Gradle bootJar on JDK 21
- Runtime stage: JRE 21
- Exposes app port `8081`

---

## 15. Manual Developer Test Flow

A reasonable manual validation flow for new developers is:

1. start Postgres with `docker-compose up -d`
2. run `./gradlew bootRun`
3. check `GET /actuator/health`
4. request OTP with `POST /api/v1/auth/request-otp`
5. read the OTP from the dummy sender/logs
6. verify OTP with `POST /api/v1/auth/verify-otp`
7. use the JWT for protected APIs
8. create or update shopkeeper data
9. list dealers
10. fetch dealer products
11. place an order
12. fetch the order and shopkeeper order history

---

## 16. Current Gaps / Things to Know Before Extending

These are still important for developers.

1. **Excel-style IDs vs numeric request IDs**
   - Responses often use values like `sk_`, `dl_`, `ord_`
   - Controllers mostly accept numeric `Long` path/request values

2. **`shopkeeper_id` query parameter in dealer listing is not the real source of truth**
   - `DealerService` primarily uses the authenticated user when available

3. **OTP sender is still a dummy implementation**
   - `OtpService` is not connected to real SMS/email infrastructure

4. **Order authorization still needs tightening**
   - especially for reading another shopkeeper’s orders or arbitrary order IDs

5. **Order totals are not recalculated server-side**
   - request payload amounts are still trusted too much

6. **Dealer category values from Excel do not perfectly align with enum values**
   - see `ProductType`

7. **Dealer product paging/sorting is in-memory**
   - okay for small data sets, not ideal for scale

8. **Legacy APIs and v1 APIs coexist**
   - be deliberate before removing or changing older endpoints

9. **No `src/test` suite is present yet**
   - compile/build success is currently the main validation path

10. **JWT secret still has an in-file default**
   - move to secrets/env for production usage

---

## 17. Best Starting Points for New Developers

If you are new to the codebase, start in this order:

1. `API-Design.xlsx`
2. `README.md`
3. `build.gradle`
4. `docker-compose.yml`
5. `src/main/resources/application.properties`
6. `src/main/java/com/example/usermanagement/config/SecurityConfig.java`
7. `src/main/java/com/example/usermanagement/controller/V1AuthController.java`
8. `src/main/java/com/example/usermanagement/controller/ShopkeeperController.java`
9. `src/main/java/com/example/usermanagement/controller/DealerController.java`
10. `src/main/java/com/example/usermanagement/controller/OrderController.java`
11. `src/main/java/com/example/usermanagement/service/UserService.java`
12. `src/main/java/com/example/usermanagement/service/ShopDetailsService.java`
13. `src/main/java/com/example/usermanagement/service/DealerService.java`
14. `src/main/java/com/example/usermanagement/service/OrderService.java`

---

## 18. Quick Reference Commands

### Build

```bash
./gradlew clean build -x test
```

### Run app

```bash
./gradlew bootRun
```

### Start DB

```bash
docker-compose up -d
```

### Stop DB

```bash
docker-compose down
```

### Health check

```bash
curl http://localhost:8081/actuator/health
```

### Swagger

```text
http://localhost:8081/swagger-ui/index.html
```

---

## 19. Final Note

This project now has a better local development baseline, and the README is intended to be the **navigation map** for developers.

When you add or change an endpoint, update both:

- the relevant controller/service/DTO references in code
- the endpoint mapping and reference sections in this `README.md`
