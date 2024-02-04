import java.io.IOException
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class PortScanner(private val host: String, private val start: Int = 1, private val end: Int = 65535) {

    fun runScan(startPort: Int, endPort: Int) {
        for (port in startPort..endPort) {
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

            val portScanner = PortScanner(host, start, end)

            // Multi-threaded execution configuration
            val THREADS = 10
            val FAIL_CODE = 1
            val pool = Executors.newFixedThreadPool(THREADS)

            val step = (end - start + 1) / THREADS
            val tasks = (0 until THREADS).map { index ->
                val threadStart = start + index * step
                val threadEnd = if (index == THREADS - 1) end else threadStart + step - 1
                Runnable { portScanner.runScan(threadStart, threadEnd) }
            }

            tasks.forEach(pool::execute)

            pool.shutdown()

            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                println("Interrupted while waiting for termination, tasks unfinished")
                e.printStackTrace()
                exitProcess(FAIL_CODE)
            }
        }
    }
}

