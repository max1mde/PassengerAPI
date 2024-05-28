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

Then you can get the passenger actions instance like that:

> [!NOTE]   
> All entities are always defined here by the entity ID (Not the UUID)

```java
PassengerActions passengerActions = PassengerAPI.getAPI(/*<your plugin main class instance>*/);

passengerActions.addPassenger(/*<target entity>*/, /*<passenger entity>*/);
passengerActions.removePassenger(/*<target entity>*/, /*<passenger entity>*/);
```

More can be found here:  
https://github.com/max1mde/PassengerAPI/blob/main/src/main/java/com/maximde/passengerapi/PassengerActions.java
