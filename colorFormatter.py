import logging
import sys

class ColorFormatter(logging.Formatter):
    """A custom formatter to use with logging that colors the output"""

    def format(self, record):
        """Format a log record and color the output"""
        log_fmt = f"{record.levelname}: {record.getMessage()}"
        if record.levelno == logging.WARNING:
            log_fmt = f"\033[1;31m{log_fmt}\033[0m"  # Make the text red
        elif record.levelno == logging.INFO:
            log_fmt = f"\033[1;32m{log_fmt}\033[0m"  # Make the text green
        elif record.levelno == logging.DEBUG:
            log_fmt = f"\033[1;35m{log_fmt}\033[0m"  # Make the text purple
        return log_fmt