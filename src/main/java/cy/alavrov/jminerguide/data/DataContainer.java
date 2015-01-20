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

import cy.alavrov.jminerguide.data.api.ship.Ship;
import cy.alavrov.jminerguide.data.character.CharacterContainer;
import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * A container for all the data.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class DataContainer {
    public final static String baseURL = "https://api.eveonline.com";
    //public final static String baseURL = "https://api.testeveonline.com";
    // TODO: make it configurable per-key.
    
    
    /**
     * Path to the directory with configuration files with a leading slash.
     */
    private final String path;
    
    private CharacterContainer chars;
    
    private volatile Ship ship;
    
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
    
    /**
     * Loads all the data from configuration files.
     * Should normally be called only on the start of the application lifecycle.
     */
    public void load() {
        JMGLogger.logWarning("Loading data...");
        chars.load();
        loadShip();
    }
    
    /**
     * Loads saved ship.
     * Later, when we'll have multiple ship loadouts with saving/loading, we'll
     * move this out of root container.
     */
    private void loadShip() {
        JMGLogger.logWarning("Loading ship...");
        File src = new File(path+File.separator+"ships.dat");
        if (!src.exists()) {            
            JMGLogger.logWarning("No ship file found, creating new.");
            ship = new Ship();
            saveShip();
            return;
        }
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            Element shipNode = rootNode.getChild("ship");
            ship = new Ship(shipNode);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load a ship", e);
            ship = new Ship();
        }
    }
    
    /**
     * Saves all the data to the configuration files.
     */
    public void save() {
        JMGLogger.logWarning("Saving data...");
        chars.save();
        saveShip();
    }
    
    /**
     * Saves a ship.
     * Later, when we'll have multiple ship loadouts with saving/loading, we'll
     * move this out of root container.
     */
    private void saveShip() {
        File src = new File(path+File.separator+"ships.dat");
        if (!src.exists()) {
            try {
                if (!src.createNewFile()) {
                    JMGLogger.logSevere("Unable to create a configuration file for ships");
                    return;
                }
            } catch (IOException e) {
                JMGLogger.logSevere("Unable to create a configuration file for ships", e);
                return;
            }
        }
        
        
        Element root = new Element("ships");
        Document doc = new Document(root);
        
        Element elem = ship.getXMLElement();
        root.addContent(elem);
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try (FileWriter fw = new FileWriter(path+File.separator+"ships.dat")){
            xmlOutput.output(doc, fw);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to save "+path+File.separator+"ships.dat", e);
        }
    }
    
    /**
     * Submits API loader to the executor pool.
     * Or, well, any runnable.
     * Can be called from any context or thread.
     * @param loader 
     */
    public void startAPILoader(Runnable loader) {
        pool.submit(loader);
    }        
    
    public Ship getShip() {
        return ship;
    }
}
