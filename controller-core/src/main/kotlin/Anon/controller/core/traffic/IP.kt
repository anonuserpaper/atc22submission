package Anon.controller.core.traffic

import java.net.InetAddress


/**
 * IP address class
 */

class IP {
    val address: InetAddress

    constructor(ipStr: String) {
        this.address = InetAddress.getByName(ipStr)
    }

    constructor(ipAddress: InetAddress) {
        this.address = ipAddress
    }

    fun toLong(): Long {
        val address = address.hostAddress
        val spl = address.trim().split("/".toRegex())
        val addrArray = spl[0].trim().split("\\.".toRegex())
        var num: Long = 0
        for (i in addrArray.indices) {
            val power = 3 - i
            num += (Integer.parseInt(addrArray[i]) % 256 * Math.pow(256.0, power.toDouble())).toLong()
        }
        return num
    }

    override fun toString(): String {
        return address.hostAddress
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }
}
