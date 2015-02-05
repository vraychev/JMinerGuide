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

package cy.alavrov.jminerguide.data.character;

import cy.alavrov.jminerguide.data.implant.Implant;
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
 * Container class for API keys and, subsequently, their characters.
 * @author alavrov
 */
public class CharacterContainer {
    
    private final EVECharacter all5booster = new All5Character(" - booster");
    private final EVECharacter all5miner = new All5Character(" - miner");
    private final EVECharacter all0 = new All0Character();
    
    private final String path;
    
    private LinkedHashMap<Integer, APIKey> keys;
    private HashMap<String, EVECharacter> charMap;
    
    private final Object blocker = new Object();
    
    private String selectedMiner;
    private String selectedBooster;
    
    /**
     * Constructor.
     * @param path path to the directory with configuration files.
     */
    public CharacterContainer(String path) {
        this.path = path;
        keys = new LinkedHashMap<>();
        selectedMiner = all5miner.getName();
        selectedBooster = all0.getName();
        all5booster.setSlot10Implant(Implant.MFMINDLINK);
        reloadCharMap();
    }
    
    /**
     * Loads API keys and their characters from a configuration file.
     */
    public void load() {        
        JMGLogger.logWarning("Loading characters...");
        File src = new File(path+File.separator+"characters.dat");
        if (!src.exists()) {            
            JMGLogger.logWarning("No character file found, creating new.");
            save();
            return;
        }
        
        LinkedHashMap<Integer, APIKey> newkeys = new LinkedHashMap<>();
        String lastSelectedMiner = null;
        String lastSelectedBooster = null;
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            
            lastSelectedMiner = rootNode.getChildText("lastselectedminer");
            lastSelectedBooster = rootNode.getChildText("lastselectedbooster");
            
            List<Element> keyList = rootNode.getChildren("apikey");
            for (Element keyEl : keyList) {
                APIKey key = new APIKey(keyEl);
                newkeys.put(key.getID(), key);
            }
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load a configuration file for characters", e);
        } 
        
        if (lastSelectedMiner == null) lastSelectedMiner = all5miner.getName();
        if (lastSelectedBooster == null) lastSelectedBooster = all0.getName();
        
        all5booster.setSlot10Implant(Implant.MFMINDLINK);
        selectedMiner = lastSelectedMiner;
        selectedBooster = lastSelectedBooster;
        keys = newkeys;
        reloadCharMap();
    }
    
    /**
     * Saves all the keys and characters into a file.
     */
    public void save() {
        File src = new File(path+File.separator+"characters.dat");
        if (!src.exists()) {
            try {
                if (!src.createNewFile()) {
                    JMGLogger.logSevere("Unable to create a configuration file for characters");
                    return;
                }
            } catch (IOException e) {
                JMGLogger.logSevere("Unable to create a configuration file for characters", e);
                return;
            }
        }
        
        
        Element root = new Element("apikeys");
        Document doc = new Document(root);
                
        synchronized(blocker) {
            String lastMiner = selectedMiner;
            if (lastMiner == null) lastMiner = all5miner.getName();
            root.addContent(new Element("lastselectedminer").setText(lastMiner));

            String lastBooster = selectedBooster;
            if (lastBooster == null) lastBooster = all0.getName();
            root.addContent(new Element("lastselectedbooster").setText(lastBooster));        
        
            for (APIKey key : keys.values()) {
                Element elem = key.getXMLElement();
                root.addContent(elem);
            }
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try (FileWriter fw = new FileWriter(path+File.separator+"characters.dat")){
            xmlOutput.output(doc, fw);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to save "+path+File.separator+"characters.dat", e);
        }
    }
    
    /**
     * Returns a list model with API keys for a Swing list. Keys are sorted by insertion order.
     * @return 
     */
    public DefaultListModel<APIKey> getListModel() {
        DefaultListModel<APIKey> out = new DefaultListModel<>();
                                        
        synchronized(blocker) {
            if (keys.isEmpty()) return out;
            
            for (APIKey key : keys.values()) {
                out.addElement(key);
            }
        }
        
        return out;
    }
    
    /**
     * Reloads character name to character map. Stores only one character per name.
     * Does not contain hidden characters.
     */
    public void reloadCharMap() {
        synchronized(blocker) {
            HashMap<String, EVECharacter> newCharMap = new HashMap<>();
            
            newCharMap.put(all5miner.getName(), all5miner);
            newCharMap.put(all5booster.getName(), all5booster);
            newCharMap.put(all0.getName(), all0);
            for (APIKey key : keys.values()) {
                for (EVECharacter eveChar : key.getCharacters()) {
                    if (!eveChar.isHidden()) {
                        newCharMap.put(eveChar.getName(), eveChar);
                    }
                }
            }
            
            charMap = newCharMap;
        }
    }
    
    /**
     * Returns an EVE character by a name. If there is more than one character
     * in a storage, returns only one.
     * @param name
     * @return 
     */
    public EVECharacter getCharacterByName(String name) {
        if (name == null) return null;
        synchronized(blocker) {
            return charMap.get(name);
        }
    }
    
    /**
     * Adds a new API key to the storage.
     * If there's already a API key with same key ID, does nothing.
     * @param key
     */
    public void addAPIKey(APIKey key) {
        if (key == null) return;
        
        synchronized(blocker) {
            if (keys.containsKey(key.getID())) return;
            
            keys.put(key.getID(), key);
        }
    }
    
    /**
     * Removes an API key from the storage.
     * @param key 
     */
    public void removeAPIKey(APIKey key) {
        if (key == null) return;
        
        synchronized(blocker) {
            keys.remove(key.getID());
        }
    }
    
    /**
     * Returns an API key with given id, or null, if there's no such key.
     * Null id returns null.
     * @param id
     * @return 
     */
    public APIKey getAPIKey(Integer id) {
        if (id == null) return null;
        
        synchronized(blocker) {
            return keys.get(id);
        }
    }
    
    /**
     * Updates API storage with this key.
     * If there is a key with same id, as the given one, it's replaced with
     * the given one.
     * If there is no such key, nothing happens.
     * @param key 
     */
    public void updateAPIKey(APIKey key) {
        if (key == null) return;
        
        synchronized(blocker) {
            if (keys.containsKey(key.getID())) {
                keys.put(key.getID(), key);
            }
        }
    }
    
    /**
     * Returns a combo box model with all chars of all keys for a Swing combo box. 
     * Keys are sorted by insertion order. Always contains at least "All 5" and "All 0"
     * characters.
     * Does not contain hidden characters.
     * @return 
     */
    public DefaultComboBoxModel<EVECharacter> getCharModel() {
        DefaultComboBoxModel<EVECharacter> out = new DefaultComboBoxModel<>();
        
        out.addElement(all5miner);
        out.addElement(all5booster);
        out.addElement(all0);
        
        synchronized(blocker) {            
            for (APIKey key : keys.values()) {
                List<EVECharacter> chrs = key.getCharacters();
                for (EVECharacter chr : chrs) {
                    if (!chr.isHidden()) {
                        out.addElement(chr);
                    }
                }
            }
        }
        
        return out;
    }
    
    /**
     * Sets name of selected miner.
     * @param name 
     */
    public void setSelectedMiner(String name) {
        synchronized(blocker) {
            selectedMiner = name;
        }
    }
    
    /**
     * Returns last selected miner (all5, if there were none).
     * @return 
     */
    public EVECharacter getLastSelectedMiner() {
        synchronized(blocker) {
            EVECharacter ret = charMap.get(selectedMiner);
            if (ret == null) ret = all5miner;
            
            return ret;
        }
    }
    
    /**
     * Sets name of selected miner.
     * @param name 
     */
    public void setSelectedBooster(String name) {
        synchronized(blocker) {
            selectedBooster = name;
        }
    }
    
    /**
     * Returns last selected miner (all5, if there were none).
     * @return 
     */
    public EVECharacter getLastSelectedBooster() {
        synchronized(blocker) {
            EVECharacter ret = charMap.get(selectedBooster);
            if (ret == null) ret = all0;
            
            return ret;
        }
    }

    /**
     * Returns default "All 0" character.
     * @return 
     */
    public EVECharacter getAll0() {
        return all0;
    }

    /**
     * Returns default "All 5" booster character.
     * @return 
     */
    public EVECharacter getAll5booster() {
        return all5booster;
    }

    /**
     * Returns default "All 5" miner character.
     * @return 
     */
    public EVECharacter getAll5miner() {
        return all5miner;
    }
        
}
