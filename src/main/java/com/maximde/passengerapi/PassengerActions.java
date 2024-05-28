package com.maximde.passengerapi;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface PassengerActions {

    /*
    These methods only affect the passengers on an entity set by your plugin
    passengers by other plugins are not affected.
     */

    void addPassenger(int targetEntity, int passengerEntity);
    void addPassengers(int targetEntity, @NotNull Set<Integer> passengerIDs);
    void addPassengers(int targetEntity, int[] passengerIDs);
    void removePassenger(int targetEntity, int passengerID);
    /**
     * This method needs more performance!
     * Please use removePassenger(int targetEntity, int passengerID) instead
     * @param passengerID
     */
    void removePassenger(int passengerID);
    void removePassengers(int targetEntity, @NotNull Set<Integer> passengerIDs);
    void removePassengers(int targetEntity, int[] passengerIDs);
    /**
     * This method needs more performance!
     * Please use removePassengers(int targetEntity, int[] passengerIDs) instead
     * @param passengerIDs
     */
    void removePassengers(int[] passengerIDs);

    /**
     * This method needs more performance!
     * Please use removePassengers(int targetEntity, @NotNull Set<Integer> passengerIDs) instead
     * @param passengerIDs
     */
    void removePassengers(@NotNull Set<Integer> passengerIDs);
    void removeAllPassengers(int targetEntity);
    Set<Integer> getPassengers(int targetEntity);


    /** WARNING
     * This method removes ALL specified passengers from an entity. Not only the passengers set by your plugin
     * @param targetEntity The entity on which the passengers are set
     * @param passengerIDs The passengers which should be removed
     */
    void removeGlobalPassengers(int targetEntity, @NotNull Set<Integer> passengerIDs);
    /** WARNING
     * This method removes ALL passengers from an entity. Not only the passengers set by your plugin
     * @param targetEntity The entity on which the passengers are set
     */
    void removeAllGlobalPassengers(int targetEntity);
    /**
     * Returns all passengers set by all plugins
     * @param targetEntity
     * @return a list os passengers
     */
    Set<Integer> getGlobalPassengers(int targetEntity);
}
