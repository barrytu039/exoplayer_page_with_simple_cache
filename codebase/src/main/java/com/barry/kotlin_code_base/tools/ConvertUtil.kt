package com.barry.kotlin_code_base.tools

class ConvertUtil {
    companion object {
        fun extraBytes(originalBytes : ByteArray, start : Int, length : Int) : ByteArray {
            var newBytes = ByteArray(length)
            System.arraycopy(originalBytes, start, newBytes, 0, length)
            return newBytes
        }

        fun hexToString(hex : String) : String {
            var sb  = StringBuilder()
            var temp =  StringBuilder()

            for (i in 0 .. hex.length - 1 step 2) {
                var output =  hex.substring(i, (i + 2))
                if (output != "00") {
                    var decimal = Integer.parseInt(output,16)
                    sb.append(decimal.toChar())
                    temp.append(decimal)
                }
            }
            return sb.toString()
        }

        private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

        fun bytesToHex(bytes: ByteArray): String? {
            try {
                val hexChars = CharArray(bytes.size * 2)
                for (j in bytes.indices) {
                    val v = bytes[j].toInt() and 0xFF // Here is the conversion
                    hexChars[j * 2] = HEX_ARRAY[v.ushr(4)]
                    hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
                }
                return String(hexChars)
            } catch (e : Exception) {
                return null
            }
        }

        fun hexToByteArray(hex : String) : ByteArray? {
            try {
                if (hex != null) {
                    var length  = hex.length
                    if (length % 2 == 0) {
                        var data = ByteArray(length/2)
                        for (i in 0 .. length-1 step 2) {
                            data[i/2] = ((Character.digit(hex[i], 16) shl 4)
                                    + Character.digit(hex[i + 1], 16)).toByte()
                        }
                        return data
                    }
                }
                return null
            } catch (e : Exception) {
                return null
            }
        }

    }
}