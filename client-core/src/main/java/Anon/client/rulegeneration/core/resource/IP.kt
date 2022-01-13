package Anon.client.rulegeneration.core.resource

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

    /*
    fun getBits(): CharArray {
        // TODO: performance issue
        val bitsStr = BigInteger(1, address.address).toString(2)
        return String.format("%32s", bitsStr).replace(" ", "0").toCharArray()
    }
    */

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