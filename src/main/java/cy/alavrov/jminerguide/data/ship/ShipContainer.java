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
package cy.alavrov.jminerguide.data.ship;

import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
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
    private String selectedShip;
    
    public ShipContainer(String path) {
        this.path = path;
        ships = new LinkedHashMap<>();
        Ship ship = new Ship("T1 Venture");
        ships.put(ship.getName(), ship);
        selectedShip = ship.getName();
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
        String lastSelectedShip = null;
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            lastSelectedShip = rootNode.getChildText("lastselectedship");
            List<Element> shipList = rootNode.getChildren("ship");
            for (Element shipEl : shipList) {
                Ship ship = new Ship(shipEl);
                newShips.put(ship.getName(), ship);
            }
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load a configuration file for ships", e);
        } 
        
        synchronized(blocker) {
            ships = newShips;
            if (ships.isEmpty()) {
                Ship ship = new Ship("T1 Venture");
                ships.put(ship.getName(), ship);
            }
            // ships is never empty, so it's ok.
            if (lastSelectedShip == null) lastSelectedShip = ships.values().iterator().next().getName();
            selectedShip = lastSelectedShip;
        }
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
            String lastShip = selectedShip;
            if (lastShip == null) lastShip = ships.values().iterator().next().getName();            
            root.addContent(new Element("lastselectedship").setText(lastShip));
            
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
     * Returns a combo box model with ships for a Swing combo box. Ships are sorted by insertion order.
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
    
    /**
     * Returns a ship under that name or null, if there isn't one. Null name
     * results in null.
     * @param name
     * @return 
     */
    public Ship getShip(String name) {
        if (name == null) return null;
        
        synchronized(blocker) {
            return ships.get(name);
        }
    }
    
    /**
     * Creates new ship with a given name. If the name is used already,
     * null returned. Same with null and whitespace-only names.
     * @param name
     * @return 
     */
    public Ship createNewShip(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        
        synchronized(blocker) {
            if (getShip(name) != null) return null;
            
            Ship newShip = new Ship(name);
            ships.put(name, newShip);
            
            return newShip;
        }
    }
    
    /**
     * How many ships are there?
     * @return 
     */
    public int getShipCount() {
        synchronized(blocker) {
            return ships.size();
        }
    }
    
    /**
     * Deletes the ship from container. Can't delete last ship!
     * @param ship
     * @return true, if successfully deleted.
     */
    public boolean deleteShip(Ship ship) {
        if (ship == null) return false;
        
        synchronized(blocker) {
            if (getShipCount() < 2) return false;
            
            Ship res = ships.remove(ship.getName());
            return (res != null);
        }
    }
    
    /**
     * Changes ship's name and moves it to the appropriate key.
     * New name shouldn't be used by another ship.
     * New name shouldn't be empty or whitespace-only.
     * New name should be different from the current one.
     * Null parameters lead to false.
     * @param oldName name of existing ship.
     * @param newName desired new name.
     * @return true, if renamed successfully, false otherwise.
     */
    public boolean renameShip(String oldName, String newName) {
        if (oldName == null || newName == null) return false;
        if (newName.trim().isEmpty()) return false;
        if (oldName.equals(newName)) return false;
        
        synchronized(blocker) {
            if (ships.containsKey(newName)) return false;
            
            Ship oldship = ships.remove(oldName);
            if (oldship == null) return false;
            oldship.setName(newName);
            ships.put(newName, oldship);
            
            return true;
        }
    }
    
    /**
     * Sets the name of a last selected ship.
     * @param name 
     */
    public void setSelectedShip(String name) {
        synchronized(blocker) {
            selectedShip = name;
        }
    }
    
    /**
     * Returns last selected ship.
     * @return 
     */
    public Ship getLastSelectedShip() {
        synchronized(blocker) {
            Ship ret = ships.get(selectedShip);
            // ships is never empty, so it's safe
            if (ret == null) ret = ships.values().iterator().next();
            return ret;
        }
    }
}
