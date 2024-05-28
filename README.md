# PassengerAPI
PassengerAPI is a Java API that allows you to manage passengers (entities attached to other entities) for multiple plugins.
It works as a plugin and provides an easy-to-use interface for adding, removing, and retrieving passengers for any entity,
regardless of whether the entity exists or not (NPC's, packet based entities...).

# Why Use PassengerAPI?
It solves compatibility issues that may arise when different plugins create entities  
by sending packets to players and setting them as passengers.
This can lead to conflicts and unintended behavior.

# Getting Started

1. Add PassengerAPI as a compile-only dependency to your plugin.
2. Add the following line to your `plugin.yml` file:
```
depend:
- PassengerAPI
```

# Usage

### Obtaining the PassengerActions Instance

To access the PassengerAPI functionality, you need to obtain the PassengerActions instance:

```java
PassengerActions passengerActions = PassengerAPI.getAPI(yourPluginInstance);
```
Replace yourPluginInstance with the instance of your plugin's main class.
(For example with `this` if you use it in your main class)

### Managing Passengers

Here are some examples of how to use the `PassengerActions` interface:

```
// Add a single passenger
passengerActions.addPassenger(targetEntityId, passengerEntityId);

// Add multiple passengers
passengerActions.addPassengers(targetEntityId, Set.of(passenger1Id, passenger2Id, ...));

// Remove a single passenger
passengerActions.removePassenger(targetEntityId, passengerEntityId);

// Remove multiple passengers
passengerActions.removePassengers(targetEntityId, Set.of(passenger1Id, passenger2Id, ...));

// Remove all passengers for a target entity
passengerActions.removeAllPassengers(targetEntityId);

// Get all passengers for a target entity
Set<Integer> passengers = passengerActions.getPassengers(targetEntityId);

// Remove global passengers (passengers set by all plugins)
passengerActions.removeGlobalPassengers(targetEntityId, Set.of(passenger1Id, passenger2Id, ...));

// Remove all global passengers for a target entity (passengers set by all plugins)
passengerActions.removeAllGlobalPassengers(targetEntityId);

// Get all global passengers for a target entity (passengers set by all plugins)
Set<Integer> globalPassengers = passengerActions.getGlobalPassengers(targetEntityId);
```

> [!NOTE]   
> All entities are identified by their entity ID (not UUID).

### Listening to events

PassengerAPI also provides events that you can listen to and handle accordingly:

```java
@EventHandler
public void onAddPassenger(AddPassengerEvent event) {
    // The name of the plugin which tries to add these passengers
    String pluginName = event.getPluginName();
    int targetEntity = event.getTargetEntityID();
    Set<Integer> passengers = event.getPassengerList();
    // Perform actions with these properties
}

@EventHandler
public void onRemovePassenger(RemovePassengerEvent event) {
    // The name of the plugin which tries to remove these passengers
    String pluginName = event.getPluginName();
    int targetEntity = event.getTargetEntityID();
    Set<Integer> removedPassengers = event.getPassengerList();
    // Perform actions with these properties
}

@EventHandler
public void onPassengerPacket(PassengerPacketEvent event) {
    int targetEntity = event.getTargetEntityID();
    Set<Integer> passengers = event.getPassengerList();
    // Which players should receive the packet (You can modify that list)
    List<Player> receivers = event.getPacketReceivers();
    // Perform actions with these properties
}
```
Don't forget to register your event class 
