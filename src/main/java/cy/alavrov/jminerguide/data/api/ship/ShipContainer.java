/*
 * Copyright (c) 2015, Andrey Lavrov <lavroff@gmail.com>
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
package cy.alavrov.jminerguide.data.api.ship;

import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class ShipContainer {
    private LinkedHashMap<String, Ship> ships;
    
    private final String path;
    
    private final Object blocker = new Object();
    
    public ShipContainer(String path) {
        this.path = path;
        ships = new LinkedHashMap<>();
    }
        
    
    /**
     * Loads ships from a configuration file.
     */
    public void load() {        
        JMGLogger.logWarning("Loading ships...");
        File src = new File(path+File.separator+"ships.dat");
        if (!src.exists()) {            
            JMGLogger.logWarning("No ship file found, creating new.");
            save();
            return;
        }
        
        LinkedHashMap<String, Ship> newShips = new LinkedHashMap<>();
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            List<Element> shipList = rootNode.getChildren("ship");
            for (Element shipEl : shipList) {
                Ship ship = new Ship(shipEl);
                newShips.put(ship.getName(), ship);
            }
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load a configuration file for ships", e);
        } 
        
        ships = newShips;
    }
    
    /**
     * Saves all the ships into a file.
     */
    public void save() {
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
        
        synchronized(blocker) {
            for (Ship ship : ships.values()) {
                Element elem = ship.getXMLElement();
                root.addContent(elem);
            }
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try (FileWriter fw = new FileWriter(path+File.separator+"ships.dat")){
            xmlOutput.output(doc, fw);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to save "+path+File.separator+"ships.dat", e);
        }
    }
    
    /**
     * Returns a combo box model with ships for a Swing list. Ships are sorted by insertion order.
     * @return 
     */
    public DefaultComboBoxModel<Ship> getShipModel() {
        DefaultComboBoxModel<Ship> out = new DefaultComboBoxModel<>();
                                        
        synchronized(blocker) {
            if (ships.isEmpty()) return out;
            
            for (Ship ship : ships.values()) {
                out.addElement(ship);
            }
        }
        
        return out;
    }
}
