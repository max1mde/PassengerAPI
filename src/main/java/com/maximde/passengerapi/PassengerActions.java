package com.maximde.passengerapi;

import java.util.List;

public interface PassengerActions {
    void addPassenger(int targetEntity, int passengerEntity);
    void addPassengers(int targetEntity, List<Integer> passengerIDs);
    void removePassenger(int targetEntity, int passengerID);
    void removePassengers(int targetEntity, List<Integer> passengerIDs);
    void removeAllPassengers(int targetEntity);
    List<Integer> getPassengers(int targetEntity);

    void removeGlobalPassengers(int targetEntity, List<Integer> passengerIDs);
    void removeAllGlobalPassengers(int targetEntity);
    List<Integer> getGlobalPassengers(int targetEntity);
}
