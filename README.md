# What is this?
This is a Java API to use in your own plugins which also works as a plugin.
Its made for easily managing passengers of any entities on any entities for multiple plugins.
It does not matter if you entity exists or not as long as you provide an entity ID.

# Why?
Because of compatibility problems with other plugins
which for example create entities by sending packets to a player and setting it as a passenger,
which can cause problems with other plugins.

# How to use?

Add this plugin as compile only to your plugin and also
```
depend:
- PassengerAPI
```
to your `plugin.yml` file.

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
