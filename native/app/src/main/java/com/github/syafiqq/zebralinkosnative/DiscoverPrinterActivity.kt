package com.github.syafiqq.zebralinkosnative

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.zebralinkos.lib.PrinterDiscoverer
import com.zebralinkos.lib.discoverer.util.PrinterDiscovererDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DiscoverPrinterActivity : AppCompatActivity() {
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_printer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }

        job = lifecycleScope.launch {
            println("CurrentLog : Prepare discover")
            var printerDiscoverer = PrinterDiscoverer.discover(
                this@DiscoverPrinterActivity,
                PrinterDiscovererDto(
                    bluetooth = true,
                )
            )
            println("CurrentLog : ${printerDiscoverer.size}")
            println("CurrentLog : $printerDiscoverer")
            println("CurrentLog : finish discover")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DiscoverPrinterActivity::class.java)
        }
    }
}