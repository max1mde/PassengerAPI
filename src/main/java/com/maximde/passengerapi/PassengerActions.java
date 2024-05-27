package com.maximde.passengerapi;

import java.util.List;

public interface PassengerActions {

    /*
    These methods only affect the passengers on an entity set by your plugin
    passengers by other plugins are not affected.
     */

    void addPassenger(int targetEntity, int passengerEntity);
    void addPassengers(int targetEntity, List<Integer> passengerIDs);
    void removePassenger(int targetEntity, int passengerID);
    void removePassengers(int targetEntity, List<Integer> passengerIDs);
    void removeAllPassengers(int targetEntity);
    List<Integer> getPassengers(int targetEntity);


    // ----------------------------------


    /** WARNING
     * This method removes ALL specified passengers from an entity. Not only the passengers set by your plugin
     * @param targetEntity The entity on which the passengers are set
     * @param passengerIDs The passengers which should be removed
     */
    void removeGlobalPassengers(int targetEntity, List<Integer> passengerIDs);
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
    List<Integer> getGlobalPassengers(int targetEntity);
}
