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

import cy.alavrov.jminerguide.App;
import cy.alavrov.jminerguide.log.JMGLogger;
import cy.alavrov.jminerguide.util.HTTPClient;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.HttpGet;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Pilot character. 
 * @author alavrov
 */
public class EVECharacter {
    /**
     * Parent API key - we need its parameters to make API requests 
     * and to distinct same characters from different APIs.
     */
    private final APIKey parentKey;
    private final Integer id;
    private final String name;
    
    private volatile HashMap<Integer, Integer> skills;
    private final Object blocker = new Object();
    
    /**
     * Constructor for a new character.
     * @param id
     * @param name
     * @param parentKey 
     */
    public EVECharacter(Integer id, String name, APIKey parentKey) {
        this.id = id;
        this.name = name;
        this.parentKey = parentKey;
        
        this.skills = new HashMap<>();
    }
    
    /**
     * Constructor to load character's data from the XML.
     * @param root root element for character's XML data.
     * @param parentKey parent API key.
     * @throws Exception 
     */
    public EVECharacter(Element root, APIKey parentKey) throws Exception {
        Attribute attr = root.getAttribute("id");
        id = attr.getIntValue();
        name = root.getChildText("name");
        
        HashMap<Integer, Integer> newSkills = new HashMap<>();
        Element skillSet = root.getChild("skills");
        if (skillSet != null) {
            List<Element> skillList = skillSet.getChildren("skill");
            for(Element skill : skillList) {
                int skid = skill.getAttribute("id").getIntValue();
                int skvalue = skill.getAttribute("value").getIntValue();
                newSkills.put(skid, skvalue);
            }
        }
        
        this.skills = newSkills;
        this.parentKey = parentKey;                
    }
    
    /**
     * Returns XML Element with character's data inside, to be used in saving.
     * @return 
     */
    public Element getXMLElement() {
        Element root = new Element("character");
        root.setAttribute(new Attribute("id", String.valueOf(id)));    
        root.addContent(new Element("name").setText(name));
        
        Element skillSet = new Element("skills");
        for(Map.Entry<Integer, Integer> skill : skills.entrySet()) {
            Element skillElem = new Element("skill");
            skillElem.setAttribute("id", skill.getKey().toString());
            skillElem.setAttribute("value", skill.getValue().toString());
            skillSet.addContent(skillElem);
        }
        
        root.addContent(skillSet);
        return root;
    }
    
    /**
     * Returns character's ID.
     * @return 
     */
    public Integer getID() {
        return id;        
    }
    
    /**
     * Returns character's name.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    public Integer getSkillValue(Integer skillID) {
        synchronized(blocker) {
            return skills.get(skillID);
        }
    }
    
    /**
     * Loads API data into this object.
     * Either completes 100% succesfully or doesn't change
     * anything at all.
     * 
     * 
     * 
     * @throws APIException thrown when something fails. Exception message
     * contains human-readable text, that can be passed to end-user
     */
    public void loadAPIData() throws APIException {
        String keyCharProfileURL = "https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyID="
                +parentKey.getID()+"&vCode="+parentKey.getVerification()+"&characterID="+id;
        
        // we're doing this instead of just passing URI into the builder because 
        // we need to provide an User-Agent header.
        HTTPClient client;
        try {
            client = new HTTPClient();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            JMGLogger.logSevere("Unable to create http client", e);
            throw new APIException("Critical error, please see logs.");
        }
        
        HttpGet req = new HttpGet(keyCharProfileURL);
        // CCP is asking us to pass useragent, so we'll do that.
        req.addHeader("User-Agent", "JMinerGuide "+App.getVersion());
        String xml = client.getStringFromURL(req);
        if (xml == null) {
            // logging will be done in a client already.
            throw new APIException("Unable to fetch char data, please see logs.");        
        }
        
        SAXBuilder builder = new SAXBuilder();
        try {            
            Document doc = builder.build(new StringReader(xml));
            Element rootNode = doc.getRootElement();            
            Element error = rootNode.getChild("error");
            if (error != null) {
                // TODO: better error handling. 
                int errorid = error.getAttribute("code").getIntValue();
                String errortext = error.getText();
                JMGLogger.logWarning("Unable to fetch character sheet, error #"
                        +errorid+": "+errortext+", key: "+parentKey.getID()
                        +", verification: "+parentKey.getVerification()
                        +", char id:"+id);
                throw new APIException("API Error: "+errortext);      
            }
            
            Element result = rootNode.getChild("result");      
            
            Element skillRowset = null;
            
            // now, there will be several "rowset" tags.
            // we need one with name "skills".
            List<Element> rowsets = result.getChildren("rowset");
            for (Element curRowset : rowsets) {
                if (curRowset.getAttributeValue("name").equals("skills")) {
                    skillRowset = curRowset;
                }
            }
            
            HashMap<Integer, Integer> newSkills = new HashMap<>();
            if (skillRowset != null) {
                List<Element> rows = skillRowset.getChildren("row");                
                for (Element row : rows) {
                    int skid = row.getAttribute("typeID").getIntValue();
                    int skval = row.getAttribute("level").getIntValue();
                    newSkills.put(skid, skval);
                }
            } else {
                throw new APIException("Unable to fetch "+name+"'s skills");
            }      
            
            synchronized(blocker) {
                skills = newSkills;
            }
            
        } catch (JDOMException | IOException | IllegalArgumentException | NullPointerException e ) {
            JMGLogger.logSevere("Cricical failure during API parsing", e);
            throw new APIException("Unable to parse data, please see logs.");        
        } 
    }
    
    @Override
    public EVECharacter clone() {
        EVECharacter out = new EVECharacter(id, name, parentKey);
        return out;
    }
    
    /**
     * Makes a full object clone, but with a different parent.
     * @param parentKey
     * @return 
     */
    public EVECharacter clone(APIKey parentKey) {
        EVECharacter out = new EVECharacter(id, name, parentKey);
        return out;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
