# Warehouse Inventory Management System

A Spring Boot REST API for managing products, stock movements, and low-stock alerts across multiple warehouses.

## Tech Stack
- Java 17, Spring Boot 4.1.2
- Spring Web, Spring Data JPA, Validation, Spring Security
- MySQL 8
- JUnit 5, Mockito

## Features
- Create and list products, categories, suppliers, and warehouses
- Record stock-in and stock-out movements (stock is never overwritten, only changed by movements)
- Current stock per product and warehouse
- Insufficient-stock rejection (no negative stock)
- Configurable low-stock threshold per product, with a low-stock list
- Dated movement history per product
- Inventory summary
- Role-based access (Admin, Manager, Staff)
- Transactional stock updates with optimistic locking


## Setup
1. Install JDK 17 and MySQL 8.
2. Create the database:
   ```sql
   CREATE DATABASE warehouse_db;
   ```
3. In `src/main/resources`, copy `application.properties.example` to a new file named `application.properties`, then put your MySQL password in it.
4. Run `InventoryApplication`. Tables are created automatically.
5. The API runs at `http://localhost:8080`.

## Demo Users (for testing only)
| Role | Username | Password |
|---|---|---|
| Admin | admin | admin123 |
| Manager | manager | manager123 |
| Staff | staff | staff123 |

## Roles
| Role | Permissions |
|---|---|
| Admin | Everything |
| Manager | Create products, categories, suppliers, warehouses; change thresholds; stock movements; view data |
| Staff | Stock-in, stock-out; view data |

## API Endpoints
| Method | Endpoint | Description | Roles |
|---|---|---|---|
| POST | /api/products | Create product | Admin, Manager |
| GET | /api/products | List products | All |
| PATCH | /api/products/{id}/threshold | Update low-stock threshold | Admin, Manager |
| GET | /api/products/{id}/movements | Movement history | All |
| POST | /api/categories, /api/suppliers, /api/warehouses | Create master data | Admin, Manager |
| GET | /api/categories, /api/suppliers, /api/warehouses | List master data | All |
| GET | /api/inventory | Current stock per product and warehouse | All |
| GET | /api/inventory/low-stock | Items at or below threshold | All |
| GET | /api/inventory/summary | Totals and units per warehouse | All |
| POST | /api/inventory/stock-in | Record incoming stock | All |
| POST | /api/inventory/stock-out | Record outgoing stock | All |

### Example
```
POST /api/inventory/stock-in
{"productId":1,"warehouseId":1,"quantity":50,"remarks":"Initial"}
```

## Error Responses
| Status | Meaning |
|---|---|
| 400 | Validation failed (e.g. quantity not positive) |
| 401 | Not logged in |
| 403 | Role not allowed |
| 404 | Product or warehouse not found |
| 409 | Insufficient stock, duplicate SKU/name, or concurrent update |

## Business Rules
- Quantities must be positive.
- Stock changes are recorded as movements.
- Stock cannot go negative.
- Stock update and movement record happen in one transaction.

## Tests
Run `InventoryServiceTest` (JUnit 5 + Mockito) to verify stock-in and stock-out logic.
