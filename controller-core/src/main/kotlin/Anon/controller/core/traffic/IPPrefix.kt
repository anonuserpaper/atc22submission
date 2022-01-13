package Anon.controller.core.traffic

/**
 * IP Prefix class
 */
data class IPPrefix(var prefix: String) {
    init {
        if(!prefix.contains("/")){
            // handle IP address as input
            prefix += "/32"
        }
    }
    override fun toString(): String = prefix
    fun getLowIpString(): String = prefix.split("/")[0]
    fun getMaskInt(): Int = prefix.split("/")[1].toInt()
}
