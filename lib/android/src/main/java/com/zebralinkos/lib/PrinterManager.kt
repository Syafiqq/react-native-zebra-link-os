import android.content.Context
import com.zebralinkos.lib.printer.PrinterManager
import com.zebralinkos.lib.printer.util.PrintJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object PrinterManager {
    private var manager = PrinterManager()
    private val mutex = Mutex()
    private var isProcessing = false

    suspend fun print(
        context: Context,
        jobs: Map<String, PrintJob>,
        defaultAddresses: List<String>,
    ): Map<String, Boolean> {
        return mutex.withLock {
            if (isProcessing) {
                throw IllegalStateException("A print process is already ongoing.")
            }
            isProcessing = true
            try {
                manager.print(context, jobs, defaultAddresses)
            } finally {
                isProcessing = false
            }
        }
    }
}