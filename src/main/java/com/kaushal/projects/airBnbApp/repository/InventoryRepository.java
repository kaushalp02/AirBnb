package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.Inventory;
import com.kaushal.projects.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Void deleteByDateAfterAndRoom(LocalDate date, Room room);

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
}