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
import cy.alavrov.jminerguide.data.DataContainer;
import cy.alavrov.jminerguide.log.JMGLogger;
import cy.alavrov.jminerguide.util.HTTPClient;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultListModel;
import org.apache.http.client.methods.HttpGet;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Account's API key
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class APIKey {
    private final static DateTimeFormatter fmt = DateTimeFormat.forPattern("d MMMM yyyy, HH:mm");
    private final static DateTimeFormatter APIfmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    
    private final Integer id;
    private final String verification;
    private DateTime expires = null;
    
    private LinkedHashMap<Integer, EVECharacter> chars;
    
    /**
     * Constructor for the new API key.
     * @param id key's ID
     * @param verif key's verification code
     */
    public APIKey(Integer id, String verif) {
        this.id = id;
        this.verification = verif;
        this.chars = new LinkedHashMap<>();
    }
    
    /**
     * Constructor for loading API key data from the XML.
     * @param root root element for key's XML data.
     * @throws Exception 
     */
    public APIKey(Element root) throws Exception {
        Attribute attr = root.getAttribute("id");
        id = attr.getIntValue();
        verification = root.getChildText("verification");
        String expiresText = root.getChildText("expires"); 
        if (expiresText != null) {
            DateTime time = new DateTime(Long.parseLong(expiresText));
            expires = time;
        }
        
        LinkedHashMap<Integer, EVECharacter> newChars = new LinkedHashMap<>();
        
        List<Element> charList = root.getChildren("character");
        for (Element charEl : charList) {
            EVECharacter character = new EVECharacter(charEl, this);
            newChars.put(character.getID(), character);
        }
        
        chars = newChars;
    }
    
    /**
     * Returns XML Element with API key data inside, to be used in saving.
     * @return 
     */
    public synchronized Element getXMLElement() {
        Element root = new Element("apikey");
        root.setAttribute(new Attribute("id", String.valueOf(id)));    
        root.addContent(new Element("verification").setText(verification));
        if (expires != null) {
            root.addContent(new Element("expires").setText(String.valueOf(expires.getMillis())));
        }

        for (EVECharacter character : chars.values()) {
            Element elem = character.getXMLElement();
            root.addContent(elem);
        }

        return root;
    }
    
    /**
     * Returns API key's ID.
     * @return 
     */
    public Integer getID() {
        return id;        
    }
    
    /**
     * Returns API key's verification code.
     * @return 
     */
    public String getVerification() {
        return verification;
    }
    
    /**
     * Returns API key's expiration date and time in a string form, or "Never",
     * if the key have no expiration date.
     * @return 
     */
    public synchronized String getExpires() {
        if (expires == null) return "Never";        
        return expires.toString(fmt);
    }
    
    @Override
    public synchronized String toString() {
        return id+" ("+chars.size()+" pilots)";
    }
    
    /**
     * Loads API data into this object.
     * Either completes 100% succesfully or doesn't change
     * anything at all.
     * 
     * @throws APIException thrown when something fails. Exception message
     * contains human-readable text, that can be passed to end-user
     */
    public synchronized void loadAPIData() throws APIException {
        String keyVerifyURL = DataContainer.baseURL+"/account/APIKeyInfo.xml.aspx?keyID="
                +id+"&vCode="+verification;
        
        // we're doing this instead of just passing URI into the builder because 
        // we need to provide an User-Agent header.
        HTTPClient client;
        try {
            client = new HTTPClient();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            JMGLogger.logSevere("Unable to create http client", e);
            throw new APIException("Critical error, please see logs.");
        }
        
        HttpGet req = new HttpGet(keyVerifyURL);
        // CCP is asking us to pass useragent, so we'll do that.
        req.addHeader("User-Agent", "JMinerGuide "+App.getVersion()+", https://github.com/alavrov/JMinerGuide");
        String xml = client.getStringFromURL(req);
        if (xml == null) {
            // logging will be done in a client already.
            throw new APIException("Unable to fetch API key data, please see logs.");        
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
                JMGLogger.logWarning("Unable to fetch API key status, error #"
                        +errorid+": "+errortext+", key: "+id
                        +", verification: "+verification);
                throw new APIException("API Error: "+errortext);      
            }
            
            Element result = rootNode.getChild("result");
            Element key = result.getChild("key");
            
            int accessMask = key.getAttribute("accessMask").getIntValue();
            if ((accessMask & 8) == 0) {
                JMGLogger.logWarning("Unable to use API key, bad access mask ("
                        +accessMask+"), "+id+", verification: "+verification);
                throw new APIException("Unable to use API key, bad access mask.");
            }
            
            String expiresStr = key.getAttributeValue("expires");
            DateTime expiresNew;
                    
            if (expiresStr.isEmpty()) {
                expiresNew = null;
            } else {
                expiresNew = APIfmt.parseDateTime(expiresStr);
            }
            
            Element rowset = key.getChild("rowset");
            List<Element> rows = rowset.getChildren("row");
            LinkedHashMap<Integer, EVECharacter> newChars = new LinkedHashMap<>();
            
            for(Element row : rows) {
                int charid = row.getAttribute("characterID").getIntValue();
                EVECharacter theChar = chars.get(charid);
                
                if (theChar == null) {                    
                    String name = row.getAttributeValue("characterName");
                    theChar = new EVECharacter(charid, name, this);
                } else {
                    // we'd better use a clone here, so we will be able to safely
                    // discard it if something will go wrong.
                    theChar = theChar.clone();
                }
                
                theChar.loadAPIData();
                
                newChars.put(theChar.getID(), theChar);
            }
            
            // if we got there, there was no errors on the way.
            expires = expiresNew;
            chars = newChars;                        
        } catch (JDOMException | IOException | IllegalArgumentException | NullPointerException e ) {
            JMGLogger.logSevere("Critical failure during API parsing", e);
            throw new APIException("Unable to parse data, please see logs.");        
        } 
    }
    
    
    
    /**
     * Returns a list model for a Swing list. Characters are sorted by insertion order.
     * Contains hidden characters.
     * @return 
     */
    public synchronized DefaultListModel<EVECharacter> getListModel() {
        DefaultListModel<EVECharacter> out = new DefaultListModel<>();
        
        for (EVECharacter theChar : chars.values()) {
            out.addElement(theChar);
        }
        
        return out;
    }
    
    /**
     * Returns a list of all characters. Characters are sorted by insertion order.
     * @return 
     */
    public synchronized List<EVECharacter> getCharacters() {
        ArrayList<EVECharacter> out = new ArrayList<>();
          
        for (EVECharacter chr : chars.values()) {
            out.add(chr);
        }
        
        return out;
    }
    
    @Override
    public synchronized APIKey clone() {
        APIKey out = new APIKey(id, verification);
        out.expires = expires;
        out.chars = new LinkedHashMap<>();
        for (EVECharacter character : chars.values()) {
            out.chars.put(character.getID(), character.clone(out));
        }
        
        return out;
    }
}
