# A Distributed Booking Engine

This is a robust, production-grade backend for hotel booking platform built with Java 21 and Spring Boot 3.x. Inspired by Airbnb, This project demonstrates high-concurrency handling secure financial transactions and modern cloud-native architecture.

## 🚀 Key Features 
#### 🔐 Advanced Security 
- Implemented Spring Security with stateless JWT authorization and Google OAuth2 social login.
- Role-Based Access Distinct permission tiers for admin and users.

#### 💳 Payment Integration 
- Stripe Integration for payment

#### 🏨 Booking & Management
- Dynamic Inventory: 25+ RESTful APIs managing property listings, room availability, and guest profiles.
- Intelligent Search: Date-based filtering with pricing logic and availability checks.

#### 🧠 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security (JWT)
- Stripe API – for handling payments
- PostgresSQL – flexible database support
- Lombok – for reducing boilerplate code
- Scheduled Tasks – for automated operations like expiring unpaid bookings and updating room pricing

## 📦 Modules and APIs

##### - Authentication & Authorization 
`POST /auth/signup` - To sign up using email id and password, also handled necessary input validations    
`POST /auth/login` - To login using email id and password  
`POST /auth/refresh` - To get the access token using your refresh token    
`GET /oauth2/authorization/google` - Login/Sign Up using google 

##### - Admin Hotel 
`POST /admin/hotels` - Create new Hotel   
`GET /admin/hotels/{hotelId}` - Get Hotel   
`PUT /admin/hotels/{hotelId}` - Update Hotel   
`DELETE /admin/hotels/{hotelId}` - Delete Hotel  
`PATCH /admin/hotels/{hotelId}` - To activate the Hotel   
`GET /admin/hotels/` - Get all the hotels of the logged in admin

##### - Health Check
`GET /` - When the application is deployed you can call this end point to check if the application is running or not
