
import 'dart:collection';

import 'package:lightchat_client/backend/packets/handshake_packets.dart';
import 'package:lightchat_client/backend/packets/latency_packets.dart';
import 'package:lightchat_client/backend/packets/other_packets.dart';
import 'package:lightchat_client/backend/packets/packets.dart';

class PacketRegistry {

  _PacketRegistry() {}

  static final Map<String, Packet> packetRegistry = Map();

  static bool _defaultPacketRegistered = false;

  static Packet getPacketInstanceFromRegistry(String name, Map<String, dynamic> json) {
    if (!packetRegistry.containsKey(name)) throw new Exception("The packet registry does not contain packet \"" + name + "\" in the registry. Make sure it is spelled correctly and is case-sensitive.");

    return packetRegistry[name].fromJson(json);
  }

  static Type registerPacket(Packet packet) {
    if(packetRegistry.containsKey(packet.packetName) && packetRegistry[packet.packetName] != packet) throw ("The packet ${packet.runtimeType} tried to use packet name \"${packet.packetName}\" which is already taken by the packet ${packetRegistry[packet.packetName].runtimeType}");

    packetRegistry[packet.packetName] = packet;

    return packet.runtimeType;
  }



  static RegisteredReturnValues checkIfRegistered(Packet packet) {
    if (!packetRegistry.containsKey(packet.packetName)) return RegisteredReturnValues.NOT_IN_REGISTRY;

    return packetRegistry[packet.packetName].runtimeType == packet.runtimeType ? RegisteredReturnValues.IN_REGISTRY : RegisteredReturnValues.IN_REGISTRY_DIFFERENT_PACKET;
  }

  static RegisteredReturnValues checkIfRegisteredIdentifier(String packetIdentifier) {
    if (!packetRegistry.containsKey(packetIdentifier)) return RegisteredReturnValues.NOT_IN_REGISTRY;

    return RegisteredReturnValues.IN_REGISTRY;
  }

  static void registerDefaultPackets() {
    if(_defaultPacketRegistered) return;

    List<Packet> packets = new List.of({
      InitialHandshakePacket.constant,
      KeyResponsePacket.constant,
      ConnectedPacket.constant,

      CommandPacket.constant,
      IllegalConnection.constant,
      MessagePacket.constant,
      SelfMessagePacket.constant,

      PingPacket.constant,
      PingReceive.constant,
      PongPacket.constant,
    });
    _defaultPacketRegistered = true;
    try {
      registerPackets(packets);
    } catch (e) {
      _defaultPacketRegistered = false;
      throw e;
    }
  }

  static void registerPackets(List<Packet> packets) {

    for (Packet packet in packets) {
      print("Registering ${packet.runtimeType} with name ${packet.packetName}");
      registerPacket(packet);
    }

//    for (Package packageT : Package.getPackages()) {
//    if (packageT.getName().startsWith(StaticHandler.PACKET_PACKAGE)) {
//    StaticHandler.getCore().getLogger().debug("Registering the package " + packageT.getName());
//    registerPacketPackage(packageT.getName());
//    }
//    }
  
  }

//  static void registerPacketPackage(String packageName) {
//    Set<Class<? extends Packet>> classes = new Reflections(packageName).getSubTypesOf(Packet.class);
//
//    for (Class<? extends Packet> packetClass : classes) {
//    StaticHandler.getCore().getLogger().debug("Registering the class {}", packetClass);
//
//    try {
//    registerPacket(packetClass);
//    } catch (Exception e) {
//    e.printStackTrace();
//    }
//    }
//  }


}

enum RegisteredReturnValues {
  NOT_IN_REGISTRY,
  IN_REGISTRY,
  IN_REGISTRY_DIFFERENT_PACKET
}