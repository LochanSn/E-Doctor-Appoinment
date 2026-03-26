package com.hotel.management.service;

import com.hotel.management.entity.Hotel;
import com.hotel.management.repository.HotelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Hotel updateHotel(Long id, Hotel hotel) {
        Hotel existing = hotelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        existing.setName(hotel.getName());
        existing.setCity(hotel.getCity());
        existing.setAddress(hotel.getAddress());
        existing.setPricePerNight(hotel.getPricePerNight());
        return hotelRepository.save(existing);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
}
