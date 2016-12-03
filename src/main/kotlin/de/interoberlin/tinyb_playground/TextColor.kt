package de.interoberlin.tinyb_playground

open class TextColor {
    companion object {
        val ANSI_RESET = "\u001B[0m"
        val ANSI_RED = "\u001B[31m"
        val ANSI_GREEN = "\u001B[32m"
        val ANSI_YELLOW = "\u001B[33m"
        val ANSI_PURPLE = "\u001B[35m"
        val ANSI_CYAN = "\u001B[36m"

        val DEBUG = "${ANSI_CYAN}DEBUG$ANSI_RESET "
        val INFO = "$ANSI_GREEN INFO$ANSI_RESET "
        // val WARN = "$ANSI_YELLOW WARN$ANSI_RESET "
        val ERROR = "${ANSI_RED}ERROR$ANSI_RESET "
        val INPUT = "${ANSI_PURPLE}INPUT$ANSI_RESET "
    }
}