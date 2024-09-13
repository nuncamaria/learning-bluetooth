package com.nuncamaria.learningbluetooth.domain

import java.util.UUID

/**
 * UUID for the service, should be the same for both devices, so it can not be randomly created
 * And it has to be in the format of a UUID
 * https://www.uuidgenerator.net/version4
 */
val SERVICE_UUID: UUID = UUID.fromString("1ab1711a-2e34-4b74-911c-6f99379e6787")

val SERVICE_NAME: String = "Chat-service"
