package com.emaintec.lib.network

class ConnInfo<T : TransmitterBase, R : ReceiverBase<*>>(val transmitter: T, val receiver: R)
