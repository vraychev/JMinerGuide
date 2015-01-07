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

import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 * Pilot character. 
 * @author alavrov
 */
public class EVECharacter {
    private Integer id;
    private String name;
    
    /**
     * Constructor to load character's data from the XML.
     * @param root root element for character's XML data.
     * @throws Exception 
     */
    public EVECharacter(Element root) throws Exception {
        Attribute attr = root.getAttribute("id");
        id = attr.getIntValue();
        name = root.getChildText("name");
    }
    
    /**
     * Returns XML Element with character's data inside, to be used in saving.
     * @return 
     */
    public Element getXMLElement() {
        Element root = new Element("character");
        root.setAttribute(new Attribute("id", String.valueOf(id)));    
        root.addContent(new Element("name").setText(name));
        
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
}
