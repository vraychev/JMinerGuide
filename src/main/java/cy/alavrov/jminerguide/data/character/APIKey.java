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

import java.util.LinkedHashMap;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Account's API key
 * @author alavrov
 */
public class APIKey {
    private final static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    
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
            EVECharacter character = new EVECharacter(charEl);
            newChars.put(character.getID(), character);
        }
        
        chars = newChars;
    }
    
    /**
     * Returns XML Element with API key data inside, to be used in saving.
     * @return 
     */
    public Element getXMLElement() {
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
    public String getExpires() {
        if (expires == null) return "Never";
        
        return expires.toString(fmt);
    }
    
    @Override
    public String toString() {
        return id+" ("+chars.size()+" pilots)";
    }
}
