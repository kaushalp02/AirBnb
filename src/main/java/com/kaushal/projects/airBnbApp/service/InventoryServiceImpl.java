package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.entity.Inventory;
import com.kaushal.projects.airBnbApp.entity.Room;
import com.kaushal.projects.airBnbApp.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void initializeRoomInvForYear(Room room) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        log.info("Creating inventory for Room : {} from : {} to : {}", room.getId(), today, endDate);

        for(; !today.isEqual(endDate);today = today.plusDays(1))
        {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .date(today)
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .price(room.getBasePrice())
                    .city(room.getHotel().getCity())
                    .closed(false)
                    .bookedCount(0)
                    .build();

            inventoryRepository.save(inventory);

        }
        log.info("Successfully created the inventory for room : {}", room.getId());
    }

    @Override
    public void deleteFutureInventories(Room room) {
        LocalDate today = LocalDate.now();
        log.info("Deleting inventory for the room : {}", room.getId());
        inventoryRepository.deleteByDateAfterAndRoom(today, room);
        log.info("Deleted inventory for the room : {}", room.getId());
    }
}
