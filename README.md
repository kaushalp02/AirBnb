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
`GET /admin/hotels/{hotelId}/bookings` - Get all the bookings for hotel

##### - Admin rooms 
`POST /admin/hotels/{hotelId}/rooms` - Create room   
`GET /admin/hotels/{hotelId}/rooms/{roomId}` - Get Room   
`POST /admin/hotels/{hotelId}/rooms` - Get all rooms for hotel  
`DELETE /admin/hotels/{hotelId}/rooms/{roomId}` - Delete room   
`PUT /admin/hotels/{hotelId}/rooms/{roomdId}` - Update room       
`GET /admin/hotels/{hotelId}/reports` - Admin dashboard showing bookings and revenue

##### - Admin Inventory  
`GET /inventory/rooms/{roomId}` - Get Inventory for room    
`PATCH /inventory/rooms/{roomId}` - Update inventory  

##### - Bookings   
`POST /bookings/init` - Create Booking  
`POST /bookings/{bookingId}/addGuests` - Add guests in the booking   
`POST /bookings/{bookingId}/payments` - Payment for booking   
`GET /bookings/{bookingId}/status` - Get Status of your booking   
`POST /bookings/{bookingId}/cancel` - Cancel booking, your payment will be refunded  

##### - Search 
`GET /hotels/search` - To Search the hotel by date, no of guests and city   
`GET /hotels/{hotelId}/info` - To get hotel and all its rooms  

##### - User 
`GET /users/myBookings` - Get all booking of logged in user   
`POST /users/{userId}/roles` - Update User Roles   
`PUT /users` - Update User info  
`GET /users` - Get User info  


##### - Health Check
`GET /` - When the application is deployed you can call this end point to check if the application is running or not


### Additional Information   
- Cron job to update pricing every hour : I have added a dynamic pricing module which will give you different prices based on existing bookings or holidays, I have schduled a task every hour to update the pricing in the inventory every hour.
- Inventory generation : when you activate a hotel then inventory will be automatically generated for the next 1 year for all it's active rooms, inventory will also be generated on creating new room.
- Stripe Setup : when you perform a payment then in order to update the booking status you will have to install and run a stripe cli so that payment information can be updated using webhook. 
