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
package cy.alavrov.jminerguide.data.character;

import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Container for simple characters.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class SimpleCharacterCointainer {
    
    private final String path;
    
    private HashMap<String, SimpleCharacter> charMap;

    public SimpleCharacterCointainer(String path) {
        this.path = path;
        this.charMap = new HashMap<>();
    }
    
    /**
     * Loads simple characters from a configuration file.
     */
    public synchronized void load() {        
        JMGLogger.logWarning("Loading simple characters...");
        File src = new File(path+File.separator+"simplecharacters.dat");
        if (!src.exists()) {            
            JMGLogger.logWarning("No simple character file found, creating new.");
            save();
            return;
        }
        
        HashMap<String, SimpleCharacter> newchars = new HashMap<>();
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            
            List<Element> simpleChars = rootNode.getChildren("simplecharacter"); 
            for (Element charElem : simpleChars) {
                SimpleCharacter sChar = new SimpleCharacter(charElem);
                newchars.put(sChar.getName(), sChar);
            }
            charMap = newchars;

        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load a configuration file for simple characters", e);
        } 
    }
    
    /**
     * Saves all the keys and characters into a file.
     */
    public synchronized void save() {
        File src = new File(path+File.separator+"simplecharacters.dat");
        if (!src.exists()) {
            try {
                if (!src.createNewFile()) {
                    JMGLogger.logSevere("Unable to create a configuration file for simple characters");
                    return;
                }
            } catch (IOException e) {
                JMGLogger.logSevere("Unable to create a configuration file for simple characters", e);
                return;
            }
        }
        
        
        Element root = new Element("simplecharacters");
        Document doc = new Document(root);
        
        for (SimpleCharacter sChar : charMap.values()) {
            Element elem = sChar.getXMLElement();
            root.addContent(elem);
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try (FileOutputStream fos = new FileOutputStream(path+File.separator+"simplecharacters.dat")){
            xmlOutput.output(doc, fos);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to save "+path+File.separator+"simplecharacters.dat", e);
        }
    }
    
    /**
     * Returns a simple character by it's name.
     * If there is none stored, creates a new one.
     * @param name
     * @return 
     */
    public synchronized SimpleCharacter getCharacterByName(String name) {        
        SimpleCharacter out = charMap.get(name);
        if (out == null) {
            out = new SimpleCharacter(name);
            charMap.put(out.getName(), out);
        }
        
        return out;
    }
}
