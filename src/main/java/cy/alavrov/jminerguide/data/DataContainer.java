/*
 * Copyright (c) 2014, Andrey Lavrov <lavroff@gmail.com>
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

package cy.alavrov.jminerguide.data;

import cy.alavrov.jminerguide.data.api.APIKeyLoader;
import cy.alavrov.jminerguide.data.character.CharacterContainer;
import cy.alavrov.jminerguide.log.JMGLogger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A container for all the data.
 * @author alavrov
 */
public class DataContainer {
    /**
     * Path to the directory with configuration files with a leading slash.
     */
    private final String path;
    
    private CharacterContainer chars;
    
    private ExecutorService pool;
    
    /**
     * Constructor.
     * @param path path to the directory with configuration files with a leading slash.
     */
    public DataContainer(String path) {
        this.path = path;
        chars = new CharacterContainer(path);
        pool = Executors.newCachedThreadPool();
    }
    
    public CharacterContainer getCharacterContainer() {
        return chars;
    }
    
    public void load() {
        JMGLogger.logWarning("Loading data...");
        chars.load();
    }
    
    public void save() {
        JMGLogger.logWarning("Saving data...");
        chars.save();
    }
    
    public void startAPILoader(APIKeyLoader loader) {
        pool.submit(loader);
    }
}
