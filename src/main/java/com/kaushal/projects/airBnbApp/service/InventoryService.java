package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.entity.Room;

public interface InventoryService {

    void initializeRoomInvForYear(Room room);

    void deleteFutureInventories(Room room);
}
