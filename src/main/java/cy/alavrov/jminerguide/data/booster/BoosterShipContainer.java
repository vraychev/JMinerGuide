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
package cy.alavrov.jminerguide.data.booster;

import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileOutputStream;
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
 * Container for booster ships.
 * At this moment - there's only one current booster.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class BoosterShipContainer {
    private LinkedHashMap<String, BoosterShip> boosterShips;
    private final BoosterShip notABoosterShip = new NoBoosterShip();
    private final String path;
    
    private boolean useBoosterShip;
    private String selectedBoosterShip;
    
    
    public BoosterShipContainer(String path) {
        boosterShips = new LinkedHashMap<>();
        BoosterShip booster = new BoosterShip("Generic Ship");
        boosterShips.put(booster.getName(), booster);
        
        this.path = path;
        useBoosterShip = false;
    }
    
    /**
     * Loads booster ships from a configuration file.
     */
    public synchronized void load() {        
        JMGLogger.logWarning("Loading booster ships...");
        File src = new File(path+File.separator+"boosters.dat");
        if (!src.exists()) {            
            JMGLogger.logWarning("No booster ship file found, creating new.");
            save();
            return;
        }
        
        LinkedHashMap<String, BoosterShip> newBoosterShips = new LinkedHashMap<>();
        String lastSelectedBoosterShip = null;
        boolean doUseBoosterShip = false;
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            doUseBoosterShip = rootNode.getAttribute("useship").getBooleanValue();
            lastSelectedBoosterShip = rootNode.getChildText("lastselectedboostership");
            List<Element> shipList = rootNode.getChildren("booster");
            for (Element shipEl : shipList) {
                BoosterShip ship = new BoosterShip(shipEl);
                newBoosterShips.put(ship.getName(), ship);
            }
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load a configuration file for booster ships", e);
        }        

        useBoosterShip = doUseBoosterShip;
        boosterShips = newBoosterShips;
        if (boosterShips.isEmpty()) {
            BoosterShip booster = new BoosterShip("Generic Ship");
            boosterShips.put(booster.getName(), booster);
        }
        // booster ships is never empty, so it's ok.
        if (lastSelectedBoosterShip == null) lastSelectedBoosterShip = boosterShips.values().iterator().next().getName();
        selectedBoosterShip = lastSelectedBoosterShip;
    }
    
    /**
     * Saves all the booster ships into a file.
     */
    public synchronized void save() {
        File src = new File(path+File.separator+"boosters.dat");
        if (!src.exists()) {
            try {
                if (!src.createNewFile()) {
                    JMGLogger.logSevere("Unable to create a configuration file for booster ships");
                    return;
                }
            } catch (IOException e) {
                JMGLogger.logSevere("Unable to create a configuration file for booster ships", e);
                return;
            }
        }
        
        
        Element root = new Element("boosters");
        Document doc = new Document(root);
        
        root.setAttribute("useship", String.valueOf(useBoosterShip));

        String lastBoosterShip = selectedBoosterShip;
        if (lastBoosterShip == null) lastBoosterShip = boosterShips.values().iterator().next().getName();            
        root.addContent(new Element("lastselectedboostership").setText(lastBoosterShip));

        for (BoosterShip booster : boosterShips.values()) {
            Element elem = booster.getXMLElement();
            root.addContent(elem);
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try (FileOutputStream fos = new FileOutputStream(path+File.separator+"boosters.dat")){
            xmlOutput.output(doc, fos);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to save "+path+File.separator+"boosters.dat", e);
        }
    }
    
    /**
     * Returns a combo box model with booster ships for a Swing combo box. 
     * Ships are sorted by insertion order.
     * @return 
     */
    public synchronized DefaultComboBoxModel<BoosterShip> getBoosterShipModel() {
        DefaultComboBoxModel<BoosterShip> out = new DefaultComboBoxModel<>();
                                  
        if (boosterShips.isEmpty()) return out;

        for (BoosterShip ship : boosterShips.values()) {
            out.addElement(ship);
        }
        
        return out;
    }
    
    public synchronized BoosterShip getBoosterShip(String name) {
        if (name == null) return null;
        
        return boosterShips.get(name);
    }
    
    /**
     * Generic hull without any booster links.
     * @return 
     */
    public BoosterShip getNoBooster() {
        return notABoosterShip;
    }
    
    /**
     * Should we use boosting ship for boosting?
     * @return 
     */
    public synchronized boolean isUsingBoosterShip() {
        return useBoosterShip;
    }
    
    /**
     * Sets if we should use boosting ships for boosting.
     * @param what 
     */
    public synchronized void setUsingBoosterShip(boolean what) {
        useBoosterShip = what;
    }        
    
    /**
     * Creates a new booster ship with a given name. If the name is used already,
     * null returned. Same with null and whitespace-only names.
     * @param name
     * @return 
     */
    public synchronized BoosterShip createNewBoosterShip(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        
        if (getBoosterShip(name) != null) return null;

        BoosterShip newShip = new BoosterShip(name);
        boosterShips.put(name, newShip);

        return newShip;
    }
    
    /**
     * How many booster ships are there?
     * @return 
     */
    public synchronized int getBoosterShipCount() {
        return boosterShips.size();
    }
    
    /**
     * Deletes the booster ship from container. Can't delete last ship!
     * @param ship
     * @return true, if successfully deleted.
     */
    public synchronized boolean deleteBoosterShip(BoosterShip ship) {
        if (ship == null) return false;
        
        if (getBoosterShipCount() < 2) return false;

        BoosterShip res = boosterShips.remove(ship.getName());
        return (res != null);
    }
    
    /**
     * Changes booster ship's name and moves it to the appropriate key.
     * New name shouldn't be used by another ship.
     * New name shouldn't be empty or whitespace-only.
     * New name should be different from the current one.
     * Null parameters lead to false.
     * @param oldName name of existing booster ship.
     * @param newName desired new name.
     * @return true, if renamed successfully, false otherwise.
     */
    public synchronized boolean renameBoosterShip(String oldName, String newName) {
        if (oldName == null || newName == null) return false;
        if (newName.trim().isEmpty()) return false;
        if (oldName.equals(newName)) return false;
        
        if (boosterShips.containsKey(newName)) return false;

        BoosterShip oldship = boosterShips.remove(oldName);
        if (oldship == null) return false;
        oldship.setName(newName);
        boosterShips.put(newName, oldship);

        return true;
    }
    
    /**
     * Sets the name of a last selected booster ship.
     * @param name 
     */
    public synchronized void setSelectedBoosterShip(String name) {
        selectedBoosterShip = name;
    }
    
    /**
     * Returns last selected booster ship.
     * @return 
     */
    public synchronized BoosterShip getLastSelectedBoosterShip() {
        BoosterShip ret = boosterShips.get(selectedBoosterShip);
        // ships is never empty, so it's safe
        if (ret == null) ret = boosterShips.values().iterator().next();
        return ret;
    }
}
