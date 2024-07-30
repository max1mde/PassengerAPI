package com.maximde.passengerapi;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface PassengerActions {

    /*
    These methods only affect the passengers on an entity set by your plugin
    passengers by other plugins are not affected.
     */

    void addPassenger(boolean async, int targetEntity, int passengerEntity);
    void addPassengers(boolean async, int targetEntity, @NotNull Set<Integer> passengerIDs);
    void addPassengers(boolean async, int targetEntity, int[] passengerIDs);
    void removePassenger(boolean async, int targetEntity, int passengerID);
    /**
     * This method needs more performance!
     * Please use removePassenger(int targetEntity, int passengerID) instead
     * @param passengerID
     */
    void removePassenger(boolean async, int passengerID);
    void removePassengers(boolean async, int targetEntity, @NotNull Set<Integer> passengerIDs);
    void removePassengers(boolean async, int targetEntity, int[] passengerIDs);
    /**
     * This method needs more performance!
     * Please use removePassengers(int targetEntity, int[] passengerIDs) instead
     * @param passengerIDs
     */
    void removePassengers(boolean async, int[] passengerIDs);

    /**
     * This method needs more performance!
     * Please use removePassengers(int targetEntity, @NotNull Set<Integer> passengerIDs) instead
     * @param passengerIDs
     */
    void removePassengers(boolean async, @NotNull Set<Integer> passengerIDs);
    void removeAllPassengers(boolean async, int targetEntity);
    Set<Integer> getPassengers(boolean async, int targetEntity);


    /** WARNING
     * This method removes ALL specified passengers from an entity. Not only the passengers set by your plugin
     * @param targetEntity The entity on which the passengers are set
     * @param passengerIDs The passengers which should be removed
     */
    void removeGlobalPassengers(boolean async, int targetEntity, @NotNull Set<Integer> passengerIDs);
    /** WARNING
     * This method removes ALL passengers from an entity. Not only the passengers set by your plugin
     * @param targetEntity The entity on which the passengers are set
     */
    void removeAllGlobalPassengers(boolean async, int targetEntity);
    /**
     * Returns all passengers set by all plugins
     * @param targetEntity
     * @return a list os passengers
     */
    Set<Integer> getGlobalPassengers(boolean async, int targetEntity);
}
