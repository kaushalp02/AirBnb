package com.kaushal.projects.airBnbApp.service;
import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.dto.HotelSearchRequest;
import com.kaushal.projects.airBnbApp.dto.InventoryDto;
import com.kaushal.projects.airBnbApp.dto.UpdateInventoryRequestDto;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.Inventory;
import com.kaushal.projects.airBnbApp.entity.Room;
import com.kaushal.projects.airBnbApp.entity.User;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.exceptions.UnAuthorizedException;
import com.kaushal.projects.airBnbApp.repository.InventoryRepository;
import com.kaushal.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.kaushal.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;

    private final ModelMapper modelMapper;

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
                    .reservedCount(0)
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

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {

        log.info("Searching for the hotel in city : {} from : {} to : {} for {} Guests."
                ,hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()
                ,hotelSearchRequest.getGuestCount());
        Pageable pageable = PageRequest.of(
                hotelSearchRequest.getPage(), hotelSearchRequest.getSize());

        Page<Hotel> hotelPage = inventoryRepository.findHotelWithAvailableInventory(hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(), hotelSearchRequest.getGuestCount(), pageable);

        return hotelPage.map((hotel) -> modelMapper.map(hotel, HotelDto.class));
    }

    @Override
    public List<InventoryDto> getInventoryByRoom(long roomId) {

        log.info("Getting all the inventory for room : {}",roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id : "+roomId));

        User user = getCurrentUser();
        if (!user.equals(room.getHotel().getOwner())) throw new UnAuthorizedException("Only hotel owner can access the inventory for selected room.");

        return inventoryRepository.findByRoomOrderByDate(room)
                .stream()
                .map((element) -> modelMapper.map(element, InventoryDto.class)).toList();

    }

    @Override
    @Transactional
    public void updateInventory(long roomId, UpdateInventoryRequestDto inventoryRequestDto) {
        log.info("Updating the inventory for room : {}, starting from : {} to {}",roomId, inventoryRequestDto.getStartDate(), inventoryRequestDto.getEndDate());

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id : "+roomId));

        User user = getCurrentUser();
        if (!user.equals(room.getHotel().getOwner())) throw new UnAuthorizedException("Only hotel owner can update the inventory for selected room.");

        //lock the inventory before updating
        inventoryRepository.lockInventoryToBeUpdated(
                roomId,
                inventoryRequestDto.getStartDate(),
                inventoryRequestDto.getEndDate()
        );

        //updating the inventory
        inventoryRepository.updateInventory(
                roomId,
                inventoryRequestDto.getPrice(),
                inventoryRequestDto.getSurgeFactor(),
                inventoryRequestDto.getClosed(),
                inventoryRequestDto.getStartDate(),
                inventoryRequestDto.getEndDate()
        );
    }
}
