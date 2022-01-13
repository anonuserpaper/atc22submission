package Anon.client.rulegeneration.core.resource

/**
 * IP Prefix class
 */
data class IPPrefix(var prefix: String) {
    override fun toString(): String = prefix
    fun getLowIpString(): String = prefix.split("/")[0]
    fun getMaskInt(): Int = prefix.split("/")[1].toInt()
}