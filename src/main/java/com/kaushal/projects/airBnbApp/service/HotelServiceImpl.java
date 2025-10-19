package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.Room;
import com.kaushal.projects.airBnbApp.exceptions.ResourceInUseException;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;


    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        log.info("Started creation of new hotel with name {}", hotelDto.getName());

        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);

        log.info("Hotel created with id : {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel by id : {}",id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+id));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto replaceHotel(long id, HotelDto hotelDto) {
        log.info("Replacing Hotel with new information (Hotel id : {})",id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+id+" While replacing hotel information."));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        log.info("Successfully replaced Hotel details for Hotel with id : {}",id);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(long id) {
        log.info("Start of Deletion of Hotel with id : {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+id+" While deleting the hotel"));

        if (hotel.getRooms() != null && !hotel.getRooms().isEmpty())
        {
            log.info("Cannot delete hotel since there are rooms associated with this hotel.");
            throw new ResourceInUseException("Cannot Delete Hotel Since there are rooms associated with it. Kindly delete all the rooms in order to delete this hotel.");
        }

        hotelRepository.deleteById(id);
        log.info("Successfully Delete the hotel with ID : {}",id);
    }

    @Override
    @Transactional
    public void activateHotel(long id) {
        log.info("Start of Hotel Activation of hotel with id : {}", id);

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : " + id + " While hotel activation."));

        //create all the inventory for this hotel Assuming that inventory does not exist already
        for (Room room : hotel.getRooms())
        {
            inventoryService.initializeRoomInvForYear(room);
        }

        hotel.setActive(true);
        hotelRepository.save(hotel);
        log.info("Successfully Activated the hotel with ID : {}",id);
    }
}
