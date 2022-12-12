package encryptdecrypt

import java.io.File
//import kotlin.math.abs


object rotaterEncrypter {
    // charArray is optional i think. but why not.
    private val alphabet = ('a'..'z').map { it }.toCharArray() //+ ('A'..'Z').map{ it }.toCharArray()
    private val reversedAlphabet = alphabet.reversed().toCharArray()
    fun encryptByAlphabet(msg: String, alp: CharArray): String
    {
        var returnValue: String = ""
        for (ch in msg)
        {
            val idx = alphabet.indexOf(ch.toString().lowercase()[0])

            if (idx == -1) returnValue += ch
            else {
                val newCh = if (ch.isUpperCase()) alp[idx].uppercase() else alp[idx].lowercase()
                returnValue += newCh
            }

        }
        return returnValue//.trim()
    }

    fun reverseText(msg: String): String {
        return encryptByAlphabet(msg, reversedAlphabet)
    }
    fun getAlphabet(offset: Int, isDecrypt: Boolean = false): CharArray
    {
        val ret = mutableListOf<Char>()
        for (idx in alphabet.indices)
        {
            val nextIdx = if (isDecrypt) {
                idx + (alphabet.size - offset)
            } else {
                idx + offset
            }
            ret.add(alphabet[(nextIdx) % alphabet.size])
        }
        //println(alphabet)
        //println(ret)
        return ret.toCharArray()
    }
    fun encByOffset(msg: String, offset: Int) = msg.map { it + offset }.joinToString("")
    fun decByOffset(msg: String, offset: Int) = msg.map { it - offset }.joinToString("")
}

fun main(args: Array<String>) {
    // maybe exists some like optarg in kotlin but for now is ok
    var isEncMode = true
    var keyOffset = 0
    var data = ""
    var inFileName = ""
    var outFileName = ""
    var alg = ""
    for (idx in 0 until args.size)
    {
        if('-' in args[idx]) {
            if ('-' in args[(idx + 1) % args.size])
                throw Exception("Error: bad argument ${args[(idx + 1) % args.size]} for ${args[idx]}")
            val opt = args[(idx + 1) % args.size]
            when (args[idx]) {
                "-mode" -> {
                    val m = args[(idx + 1) % args.size]
                    isEncMode = if (m == "enc") true
                    else if (m == "dec") false
                    else throw Exception("Unknown mode")
                }
                "-key" -> {
                    keyOffset = opt.toIntOrNull() ?: 0
                }

                "-data" -> {
                    data = opt
                }

                "-in" -> {
                    inFileName = opt
                }

                "-out" -> {
                    outFileName = opt
                }
                "-alg" -> {
                    alg = opt
                }
            }
        }
    }
    //val method = readln()
   // val msg = readln()
    //val offset = readln().toInt()
    // unicde by default?
    val isUnicodeMode = when(alg) {
        "shift" -> false
        else -> true
    }
    val RotEnc = rotaterEncrypter
    var encDecData = ""
    try {
        if (data.isEmpty() && !inFileName.isEmpty()) {
            val iFile = File(inFileName)
            if (iFile.exists() == false) throw Exception("Error: Can't to open input file ${inFileName}")
            data = iFile.readText() // for big file i would to use forEachLine
        }
        with(RotEnc)
        {

            when (isEncMode) {
                true -> {
                    val ourAlphabet = getAlphabet(keyOffset)
                    //println(encryptByAlphabet(msg,ourAlphabet))
                    if (!isUnicodeMode) {
                        encDecData = encryptByAlphabet(data, ourAlphabet)
                    } else {
                        encDecData = encByOffset(data, keyOffset)
                    }

                }
                false -> {
                    if (!isUnicodeMode) {
                        val ourAlphabet = getAlphabet(keyOffset, isDecrypt = true)
                        encDecData = encryptByAlphabet(data, ourAlphabet)
                    }else {
                        encDecData = decByOffset(data, keyOffset)
                    }
                }
            }
        }
        //println("Raw data: $data")
        //println("EncDecData: ${encDecData.trim()}")
        if (outFileName != "") {
            val outFile = File(outFileName)
            if (outFile.exists() == false) outFile.createNewFile()
            if (outFile.canWrite() == false)
                throw Exception("Error: Can't to open output file or(and) can't to write")
            outFile.writeText(encDecData.trim())
        }
    } catch (e: Exception) {
        println(e.toString().split(": ")[1])
    }
}
