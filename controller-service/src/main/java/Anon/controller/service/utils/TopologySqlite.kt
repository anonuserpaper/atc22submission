package Anon.controller.service.utils

import java.sql.Connection
import java.sql.DriverManager

fun getConnection(): Connection? {
    var c: Connection? = null

    try {
        Class.forName("org.sqlite.JDBC")
        c = DriverManager.getConnection("jdbc:sqlite:aspaths.sqlite")
        return c
    } catch (e: Exception) {
        System.err.println(e.javaClass.name + ": " + e.message)
        System.exit(0)
    }
    return c
}

fun getAsPathsMap(c: Connection): Map<String, String> {
    val map = hashMapOf<String, String>()
    val stmt = c.createStatement()
    val sql = "SELECT * FROM ddos2007flowpath;"
    val res = stmt.executeQuery(sql)
    while (res.next()) {
        val srcip = res.getString("srcip")
        val aspath = res.getString("aspaths")
        // println("$srcip -> $aspath")
        map.put(srcip, aspath)
    }

    return map
}

fun main(args: Array<String>) {
    getConnection()?.let { getAsPathsMap(it) }
}
