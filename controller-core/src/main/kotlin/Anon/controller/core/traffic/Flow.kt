package Anon.controller.core.traffic

import Anon.controller.core.topology.AS
import Anon.controller.core.topology.AsTopology

/**
 * Representation of attack flows in a DDoS attack.
 */
data class Flow (
        var ip: IP,
        var bitRate: Int = 1,
        var filteredAt: AS? = null,
        val timeStamp: Int = 0
)

