package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.BookingDto;
import com.kaushal.projects.airBnbApp.dto.BookingRequestDto;
import com.kaushal.projects.airBnbApp.dto.GuestDto;
import com.kaushal.projects.airBnbApp.dto.HotelReportDto;
import com.kaushal.projects.airBnbApp.entity.*;
import com.kaushal.projects.airBnbApp.entity.enums.BookingStatus;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.exceptions.UnAuthorizedException;
import com.kaushal.projects.airBnbApp.repository.*;
import com.kaushal.projects.airBnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import static com.kaushal.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;

    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequestDto bookingRequestDto) {
        log.info("Starting Booking process for hotel {} from {} to {}", bookingRequestDto.getHotelId(),
                bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequestDto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : " + bookingRequestDto.getHotelId()));

        Room room = roomRepository.findById(bookingRequestDto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found with id : " + bookingRequestDto.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequestDto.getCheckInDate(),
                bookingRequestDto.getCheckOutDate(),
                bookingRequestDto.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate()) + 1;

        if (inventoryList.size() != daysCount) {
            log.error("Room which you're trying to book does not have enough inventory(room not available).");
            throw new IllegalStateException("Room is not available anymore.");
        }

//        for (Inventory inventory : inventoryList) {
//            if (bookingRequestDto.getRoomsCount() + inventory.getBookedCount() + inventory.getReservedCount() > inventory.getTotalCount()) {
//                log.error("Number of room you're trying to book are not available.");
//                throw new IllegalStateException("Number of room you're trying to book are not available.");
//            }
//            inventory.setReservedCount(inventory.getReservedCount() + bookingRequestDto.getRoomsCount());
//        }
//
//        inventoryRepository.saveAll(inventoryList);
        inventoryRepository.initBooking(room.getId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate(), bookingRequestDto.getRoomsCount());

        //Calculate the total price of order
        BigDecimal oneRoomPrice = pricingService.calculateTotalPricing(inventoryList);
        BigDecimal totalPrice = oneRoomPrice.multiply(BigDecimal.valueOf(bookingRequestDto.getRoomsCount()));


        Booking booking = Booking.builder().bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkinDate(bookingRequestDto.getCheckInDate())
                .checkoutDate(bookingRequestDto.getCheckOutDate())
                .user(getCurrentUser())
                .roomCount(bookingRequestDto.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestsDtoList) {

        log.info("Adding guests for the booking with booking id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found with ID : " + bookingId));

        User user = getCurrentUser();

        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorizedException("Booking does not belong to this user with id :" + user.getId());
        }

        if (hasBookingExpired(booking)) {
            log.error("Booking is expired while trying to add guests.");
            throw new IllegalStateException("Your Booking is expired. Please book again.");
        }

        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            log.error("Booking is not in Reserved state. Unable to proceed with adding guests. Current Booking state : {}", booking.getBookingStatus());
            throw new IllegalStateException("Booking is in the " + booking.getBookingStatus() + " state. Cannot add guests.");
        }

        for (GuestDto guestDto : guestsDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        log.info("Sucessfully added guests in the booking : {}", bookingId);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiateBooking(Long bookingId) {

        log.info("Initializing booking payment");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found with the booking id : " + bookingId));

        User user = getCurrentUser();

        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Cannot proceed with payment because Booking does not belong to this user with id :" + user.getId());
        }

        if (hasBookingExpired(booking)) {
            log.error("Cannot proceed with payment because booking is expired. Please try again.");
            throw new IllegalStateException("Cannot proceed with payment because Your Booking is expired. Please book again.");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking, frontendUrl + "/payment/success", frontendUrl + "/payment/failed");

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType()))
        {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("booking not found for session id : "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            log.info("Booking confirmed with booking id : {}",booking.getId());

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                    booking.getCheckinDate(), booking.getCheckoutDate(), booking.getRoomCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(),
                    booking.getCheckinDate(), booking.getCheckoutDate(), booking.getRoomCount());

            log.info("inventory has been updated after the booking reservation.");
        }
        else {
            log.warn("unhandled event type : {}", event.getType());
        }

    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {

        //get the booking and user
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found with the booking id : " + bookingId));

        User user = getCurrentUser();

        //check if user himself is cancelling the booking
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Cannot proceed with payment because Booking does not belong to this user with id :" + user.getId());
        }

        //Add check to only cancel confirmed bookings
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED)
        {
            throw new IllegalStateException("Only confirmed bookings can be cancelled. Your booking status is "+booking.getBookingStatus());
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking has been marked as cancelled.");

        //lock the inventory rows and update the count to reduce booked count from the inventory
        inventoryRepository.findAndLockReservedInventoryForCancellation(booking.getRoom().getId(),
                booking.getCheckinDate(), booking.getCheckoutDate(), booking.getRoomCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(),
                booking.getCheckinDate(), booking.getCheckoutDate(), booking.getRoomCount());

        log.info("Updated the inventory after booking cancellation.");

        //handle the payment refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund refund = Refund.create(refundParams);
            log.info("Payment successfully refunded.");

        } catch (StripeException e) {
            log.error("Error while refunding the payment.");
            throw new RuntimeException(e);
        }


    }

    @Override
    public String getBookingStatus(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found with ID : " + bookingId));

        User user = getCurrentUser();

        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorizedException("Booking does not belong to this user with id :" + user.getId());
        }

        return booking.getBookingStatus().name();
    }

    @Override
    public List<BookingDto> getBookingsByHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("No Hotel Found with id "+hotelId));

        User user = getCurrentUser();

        if (!user.equals(hotel.getOwner()))
        {
            throw new AccessDeniedException("Only hotel owner can request for the hotel rooms");
        }
        log.info("finding all the booking for the hotel with id {}",hotelId);
        List<Booking> bookings = bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long id, LocalDate startDate, LocalDate endDate) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Hotel Found with id "+id));

        User user = getCurrentUser();

        if (!user.equals(hotel.getOwner()))
        {
            throw new AccessDeniedException("Only hotel owner can request for the hotel rooms");
        }

        log.info("Generating report for hotel with id {} starting from : {} to {}", id, startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Booking> bookings = bookingRepository.findByHotelIdAndCreatedDateBetween(id,startDateTime, endDateTime);

        long totalConfirmBooking = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenue = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmBooking == 0 ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(totalConfirmBooking), RoundingMode.HALF_UP);

        return new HotelReportDto(totalConfirmBooking, totalRevenue, avgRevenue);
    }

    @Override
    public List<BookingDto> gerUserBookings() {
        User user = getCurrentUser();

        log.info("Getting all the bookings for the user");
        return bookingRepository.findByUser(user).stream()
                .map((element) -> modelMapper.map(element, BookingDto.class)).collect(Collectors.toList());

    }

    public Boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedDate().plusMinutes(10).isBefore(LocalDateTime.now());

    }
}
