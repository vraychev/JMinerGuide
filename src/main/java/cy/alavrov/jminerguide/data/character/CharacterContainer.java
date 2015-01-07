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

package cy.alavrov.jminerguide.data.character;

import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultListModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author alavrov
 */
public class CharacterContainer {
    
    private final String path;
    
    private LinkedHashMap<Integer, APIKey> keys;
    
    /**
     * Constructor.
     * @param path path to the directory with configuration files.
     */
    public CharacterContainer(String path) {
        this.path = path;
        keys = new LinkedHashMap<>();
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
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            List<Element> keyList = rootNode.getChildren("apikey");
            for (Element keyEl : keyList) {
                APIKey key = new APIKey(keyEl);
                newkeys.put(key.getID(), key);
            }
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load a configuration file for characters", e);
        } 
        
        keys = newkeys;
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
        
        synchronized(keys) {
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
     * Returns a list model for a Swing list. Keys are sorted by insertion order.
     * @return 
     */
    public DefaultListModel<APIKey> getListModel() {
        DefaultListModel<APIKey> out = new DefaultListModel<>();
                
        if (keys.isEmpty()) return out;
                
        synchronized(keys) {
            for (APIKey key : keys.values()) {
                out.addElement(key);
            }
        }
        
        return out;
    }
    
    /**
     * Adds a new API key to the storage.
     * If there's already a API key with same key ID, does nothing.
     * @param key
     */
    public void addAPIKey(APIKey key) {
        if (key == null) return;
        
        synchronized(keys) {
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
        
        synchronized(keys) {
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
        
        synchronized(keys) {
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
        
        synchronized(keys) {
            if (keys.containsKey(key.getID())) {
                keys.put(key.getID(), key);
            }
        }
    }
}
