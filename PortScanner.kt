import java.io.IOException
import java.net.Socket

class PortScanner(private val host: String, private val start: Int = 1, private val end: Int = 65535) {

    fun runScan() {
        for (port in start..end) {
            try {
                Socket(host, port).use { sock ->
                    println("Connected to port $port with local address ${sock.localPort}${sock.localAddress}")
                }
            } catch (e: IOException) {
                // Handle exception if necessary
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 3) {
                System.err.println("Usage: java PortScanner <host> <start-port> <end-port>")
                System.exit(1)
            }
            val host = args[0]
            val start = args[1].toInt()
            val end = args[2].toInt()
            val whiteHat = PortScanner(host, start, end)
            whiteHat.runScan()
        }
    }
}


