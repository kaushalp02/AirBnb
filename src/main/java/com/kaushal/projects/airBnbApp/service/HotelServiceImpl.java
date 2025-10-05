package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;

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
    public void deleteHotelById(long id) {
        log.info("Start of Deletion of Hotel with id : {}", id);
        boolean exists = hotelRepository.existsById(id);

        if (!exists)
        {
            throw new ResourceNotFoundException("Hotel Not Found with id : "+id+" While deleting the hotel");
        }
        //TODO : delete all the inventory for this hotel
        hotelRepository.deleteById(id);
        log.info("Successfully Delete the hotel with ID : {}",id);
    }

    @Override
    public void activateHotel(long id) {
        log.info("Start of Hotel Activation of hotel with id : {}", id);

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+id+" While hotel activation."));

        //TODO : create all the inventory for this hotel

        hotel.setActive(true);
        hotelRepository.save(hotel);
        log.info("Successfully Activated the hotel with ID : {}",id);
    }
}
