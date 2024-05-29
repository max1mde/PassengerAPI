# PassengerAPI
PassengerAPI is a Java API that allows you to manage passengers (entities attached to other entities) for multiple plugins.
It works as a plugin and provides an easy-to-use interface for adding, removing, and retrieving passengers for any entity,
regardless of whether the entity exists or not (NPC's, packet based entities...).

# Why Use PassengerAPI?
It solves compatibility issues that may arise when different plugins create entities    
by sending packets to players and setting them as passengers.  
This can lead to conflicts and unintended behavior like unmounting of previous set passengers by other plugins.

# Showcase

https://github.com/max1mde/PassengerAPI/assets/114857048/224a9df1-3b22-4176-bfce-40a555fc71a2



> [!IMPORTANT]  
> This plugin **works out-of-the-box!**  
> Just put it into your plugins folder and restart your server.  
> _It should fix most entity passenger compatibility bugs with other plugins_
>
> BUT if you are a **developer** you can still add/access/remove passengers using this API!  
> For example if you want to remove a passenger from an entity, without killing it, this could be usefull.

# Getting Started

1. Add PassengerAPI as a compile-only dependency to your plugin.

Gradle:
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.max1mde:PassengerAPI:1.0.1'
}
```
(For maven: https://jitpack.io/#max1mde/PassengerAPI/1.0.0)  

2. Add the following line to your `plugin.yml` file:
```
depend:
- PassengerAPI
```

Also add the plugin [Packet Events](https://www.spigotmc.org/resources/packetevents-api.80279/) to your server
which is required by this plugin!

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

Keep in mind that you can only retrive and remove passengers, which you have set here by using the addPassenger... methods.  
**Except:** when using the "global" methods like getGlobalPassengers.

```java
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
