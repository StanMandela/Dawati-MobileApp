package com.ke.dawaati.widgets.reader.log

/**
 * class that holds the [Logger] for this library, defaults to [LoggerDefault] to send logs to android [Log]
 */
object LogManager {
    var logger: Logger = LoggerDefault()
}
