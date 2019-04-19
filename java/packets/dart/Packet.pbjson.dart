///
//  Generated code. Do not modify.
//  source: Packet.proto
///
// ignore_for_file: camel_case_types,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name

const SelfMessageType$json = const {
  '1': 'SelfMessageType',
  '2': const [
    const {'1': 'FillPasswordPacket', '2': 0},
    const {'1': 'LostServerConnectionPacket', '2': 1},
    const {'1': 'RegisterPacket', '2': 2},
    const {'1': 'TimedOutRegistrationPacket', '2': 3},
    const {'1': 'PongPacket', '2': 4},
    const {'1': 'PingReceive', '2': 5},
    const {'1': 'PingPacket', '2': 6},
    const {'1': 'AuthenticateMessage', '2': 7},
  ],
};

const Packet$json = const {
  '1': 'Packet',
  '5': const [
    const {'1': 1, '2': 536870912},
  ],
};

const MessagePacket$json = const {
  '1': 'MessagePacket',
  '2': const [
    const {'1': 'message', '3': 1, '4': 2, '5': 9, '10': 'message'},
  ],
  '6': const [
    const {'1': 'packetType', '2': '.lightclient.packets.Packet', '3': 100, '4': 1, '5': 11, '6': '.lightclient.packets.Packet', '10': 'packetType'},
  ],
};

const AutoCompletePacket$json = const {
  '1': 'AutoCompletePacket',
  '2': const [
    const {'1': 'candidateList', '3': 2, '4': 3, '5': 11, '6': '.lightclient.packets.LightCandidateData', '10': 'candidateList'},
    const {'1': 'wordsListJson', '3': 3, '4': 3, '5': 9, '10': 'wordsListJson'},
  ],
  '6': const [
    const {'1': 'packetType', '2': '.lightclient.packets.Packet', '3': 101, '4': 1, '5': 11, '6': '.lightclient.packets.Packet', '10': 'packetType'},
  ],
};

const ConnectedPacket$json = const {
  '1': 'ConnectedPacket',
  '2': const [
    const {'1': 'name', '3': 4, '4': 2, '5': 9, '10': 'name'},
    const {'1': 'os', '3': 5, '4': 2, '5': 9, '10': 'os'},
    const {'1': 'uuid', '3': 6, '4': 2, '5': 9, '10': 'uuid'},
    const {'1': 'privateKey', '3': 7, '4': 2, '5': 9, '10': 'privateKey'},
  ],
  '6': const [
    const {'1': 'packetType', '2': '.lightclient.packets.Packet', '3': 102, '4': 1, '5': 11, '6': '.lightclient.packets.Packet', '10': 'packetType'},
  ],
};

const IllegalConnectionPacket$json = const {
  '1': 'IllegalConnectionPacket',
  '2': const [
    const {'1': 'message', '3': 8, '4': 2, '5': 9, '10': 'message'},
  ],
  '6': const [
    const {'1': 'packetType', '2': '.lightclient.packets.Packet', '3': 103, '4': 1, '5': 11, '6': '.lightclient.packets.Packet', '10': 'packetType'},
  ],
};

const RequestInfoPacket$json = const {
  '1': 'RequestInfoPacket',
  '2': const [
    const {'1': 'encryptionKey', '3': 9, '4': 2, '5': 9, '10': 'encryptionKey'},
  ],
  '6': const [
    const {'1': 'packetType', '2': '.lightclient.packets.Packet', '3': 104, '4': 1, '5': 11, '6': '.lightclient.packets.Packet', '10': 'packetType'},
  ],
};

const SelfMessagePacket$json = const {
  '1': 'SelfMessagePacket',
  '2': const [
    const {'1': 'messageType', '3': 10, '4': 2, '5': 14, '6': '.lightclient.packets.SelfMessageType', '10': 'messageType'},
  ],
  '6': const [
    const {'1': 'packetType', '2': '.lightclient.packets.Packet', '3': 105, '4': 1, '5': 11, '6': '.lightclient.packets.Packet', '10': 'packetType'},
  ],
};
