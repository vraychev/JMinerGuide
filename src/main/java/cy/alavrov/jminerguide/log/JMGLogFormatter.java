/*
 * Copyright (c) 2014, alavrov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package cy.alavrov.jminerguide.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Log entry formatter.
 * @author alavrov
 */
public class JMGLogFormatter extends Formatter {

    Date dat = new Date();
    private final static String format = "{0,date} {0,time} {1}: {2}";
    private MessageFormat formatter;

    private Object args[] = new Object[3];

    private String lineSeparator = System.getProperty("line.separator");

    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
	// Minimize memory allocations here.
	dat.setTime(record.getMillis());
	args[0] = dat;
        args[1] = record.getLevel().getName();
        String message = formatMessage(record);
        args[2] = message;
        
	StringBuffer text = new StringBuffer();
	if (formatter == null) {
	    formatter = new MessageFormat(format);
	}
	formatter.format(args, text, null);
	
	text.append(lineSeparator);
        Throwable thrown = record.getThrown();
        
	if (thrown != null) {
	    try {
	        StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw)) {
                    thrown.printStackTrace(pw);
                }
		text.append(sw.toString());
	    } catch (Exception ex) {
	    }
	}
	return text.toString();
    }
}
