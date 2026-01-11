package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.Inventory;
import com.kaushal.projects.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Modifying
    @Transactional
    int deleteByDateAfterAndRoom(LocalDate date, Room room);

    @Query(value = "SELECT h.* FROM hotel h\n" +
            "        JOIN room r ON h.id = r.hotel_id\n" +
            "        JOIN (\n" +
            "            SELECT\n" +
            "                hotel_id,\n" +
            "                room_id,\n" +
            "                min(total_count - booked_count - reserved_count) AS room_count\n" +
            "            FROM inventory\n" +
            "            WHERE\n" +
            "                date BETWEEN :checkinDate AND :checkoutDate\n" +
            "                AND closed = false\n" +
            "            GROUP BY hotel_id, room_id\n" +
            "            HAVING count(room_id) = DATEDIFF(:checkoutDate,:checkinDate) + 1\n" +
            "        ) AS available_rooms ON h.id = available_rooms.hotel_id AND r.id = available_rooms.room_id\n" +
            "        WHERE\n" +
            "            h.active = true\n" +
            "            AND r.active = true\n" +
            "            AND h.city = :city\n" +
            "        GROUP BY h.id\n" +
            "        HAVING SUM(available_rooms.room_count * r.capacity) >= :numberOfGuests\n" +
            "        \n" +
            "        ",
            countQuery = "SELECT count(*) FROM hotel h\n" +
                    "        JOIN room r ON h.id = r.hotel_id\n" +
                    "        JOIN (\n" +
                    "            SELECT\n" +
                    "                hotel_id,\n" +
                    "                room_id,\n" +
                    "                min(total_count - booked_count) AS room_count\n" +
                    "            FROM inventory\n" +
                    "            WHERE\n" +
                    "                date BETWEEN :checkinDate AND :checkoutDate\n" +
                    "                AND closed = false\n" +
                    "            GROUP BY hotel_id, room_id\n" +
                    "            HAVING count(room_id) = DATEDIFF(:checkoutDate,:checkinDate) + 1\n" +
                    "        ) AS available_rooms ON h.id = available_rooms.hotel_id AND r.id = available_rooms.room_id\n" +
                    "        WHERE\n" +
                    "            h.active = true\n" +
                    "            AND r.active = true\n" +
                    "            AND h.city = :city\n" +
                    "        GROUP BY h.id\n" +
                    "        HAVING SUM(available_rooms.room_count * r.capacity) >= :numberOfGuests\n" +
                    "        \n" +
                    "        "
            ,nativeQuery = true)
    Page<Hotel> findHotelWithAvailableInventory(
            @Param("city") String city,
            @Param("checkinDate") LocalDate fromDate,
            @Param("checkoutDate") LocalDate toDate,
            @Param("numberOfGuests") Integer guestCount,
            Pageable pageable
    );

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query("""
             UPDATE Inventory i
             SET i.reservedCount = i.reservedCount - :numberOfRooms,
                i.bookedCount = i.bookedCount + :numberOfRooms
             WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                AND i.reservedCount >= :numberOfRooms
                AND i.closed = false
            """)
    void confirmBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount) >= :roomsCount
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") int roomsCount
    );

    @Modifying
    @Query("""
             UPDATE Inventory i
             SET i.bookedCount = i.bookedCount - :numberOfRooms
             WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.bookedCount >= :numberOfRooms
                AND i.closed = false
            """)
    void cancelBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND i.bookedCount >= :roomsCount
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventoryForCancellation(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") int roomsCount
    );

    @Modifying
    @Query("""
             UPDATE Inventory i
             SET i.reservedCount = i.reservedCount + :numberOfRooms
             WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
                AND i.closed = false
            """)
    void initBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );

    List<Inventory> findByRoomOrderByDate(Room room);

    //query for locking before updating the inventory
    @Query("""
        SELECT i
        FROM Inventory i
        WHERE
            i.room.id = :roomId
            AND i.date BETWEEN :startDate AND :endDate
        """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> lockInventoryToBeUpdated(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE Inventory i
        SET
            i.price = CASE
                         WHEN :price IS NOT NULL AND :price > 0 THEN :price
                         ELSE i.price
                       END,
            i.surgeFactor = CASE
                              WHEN :surgeFactor IS NOT NULL AND :surgeFactor > 0 THEN :surgeFactor
                              ELSE i.surgeFactor
                            END,
            i.closed = :closed
        WHERE
            i.room.id = :roomId
            AND i.date BETWEEN :startDate AND :endDate
        """)
    void updateInventory(
            @Param("roomId") Long roomId,
            @Param("price") BigDecimal price,
            @Param("surgeFactor") BigDecimal surgeFactor,
            @Param("closed") Boolean closed,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}