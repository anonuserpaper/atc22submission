package Anon.controller.core.utils

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream


fun getGzipReader(filename: String) =
        BufferedReader(InputStreamReader(GZIPInputStream(FileInputStream(filename))))

fun main(args: Array<String>) {
    val br = getGzipReader("data/15169-paths.txt.gz")
    br.readLines().forEach { line ->
        val path = line.split(",").asReversed().map { it.toInt() }
        println(path)
    }
}
