# Zomato Full Stack (Spring Boot + Thymeleaf + MySQL)
Extends the basic version with login, roles, order persistence, coupons, restaurant & admin dashboards and food images.

## Run
1. Install JDK 17+, Maven 3.8+, MySQL 8 (running).
2. `export DB_USER=root DB_PASS=yourpassword` (or edit `application.properties`)
3. `mvn spring-boot:run` then open http://localhost:8080
The database `zomato_basic` and all tables are created automatically; sample restaurants, foods (with images), coupons and users are seeded on first start.

## Features
Customer: register/login, search by name/cuisine/location, category chips, veg/dish filter, cart with +/-, coupons, GST + delivery fee, COD or demo online payment, order history, cancel (while PLACED), live status tracking (auto-refresh), reviews, favourites.
Owner: dashboard, sales totals, accept/reject/prepare/dispatch/deliver orders, add/edit/delete food, set price/availability. Register as "Restaurant owner" to get your own restaurant.
Admin: totals, revenue, orders-by-status chart, coupon management, lists of users/restaurants/orders.

## Food images
Seed data uses Unsplash image URLs (needs internet). To use your own, put files in `src/main/resources/static/images/` and set the image URL to `/images/name.jpg` (owners can do this in the food form). Broken links fall back to a placeholder.

## Deploy
`mvn clean package` then `java -jar target/zomato-basic-2.0.0.jar` with `DB_USER`, `DB_PASS` and `SPRING_DATASOURCE_URL` set. Put it behind HTTPS (nginx/ALB). For production set `spring.jpa.hibernate.ddl-auto=validate` after first run.

## Not included
Real payment gateway, email/OTP verification, delivery-partner app, GPS tracking, Redis, separate React frontend.
