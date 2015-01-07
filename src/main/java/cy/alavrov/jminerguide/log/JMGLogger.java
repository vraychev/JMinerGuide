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

import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger.
 * @author alavrov
 */
public class JMGLogger {
    private static Logger l;
    public static void init(String location) {
        l = Logger.getLogger(JMGLogger.class.getName());
        l.setLevel(Level.WARNING);
        try {
            FileHandler fhandler = new FileHandler(location+"application.log");
            JMGLogFormatter lfmt = new JMGLogFormatter();
            fhandler.setFormatter(lfmt);
            l.addHandler(fhandler);
        } catch (Exception e) {
            l.log(Level.SEVERE, "Unable to init file logging:", e);
            // carry on like nothing happened!
        }
    }
    
    /**
     * Sets a logging level for application events logging.
     * @param level 
     */
    public static void setLoggingLevel(String level) {
        Level lvl;    
        try {
            lvl = Level.parse(level.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            lvl = Level.WARNING;
        }
        l.setLevel(lvl);
    }
    
    public static void logSevere(String msg) {
        l.severe(msg);
    }
    
    public static void logSevere(String msg, Throwable e) {
        l.log(Level.SEVERE, msg, e);
    }
    
    public static void logWarning(String msg) {
        l.warning(msg);
    }
    
    public static void logWarning(String msg, Throwable e) {
        l.log(Level.WARNING, msg, e);
    }
    
    public static void logInfo(String msg) {
        l.info(msg);
    }
    
    public static void logInfo(String msg, Throwable e) {
        l.log(Level.INFO, msg, e);
    }
    
    public static boolean isInfo() {
        return l.isLoggable(Level.INFO);
    }
    
    /**
     * Returns a logging level in a string format.
     * @return 
     */
    public static String getLogLevel() {
        return l.getLevel().getName();
    }
}
