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
import cy.alavrov.jminerguide.data.implant.Implant;
import cy.alavrov.jminerguide.log.JMGLogger;
import cy.alavrov.jminerguide.util.HTTPClient;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class EVECharacter {
    
    public final static int SKILL_ASTROGEOLOGY = 3410;
    public final static int SKILL_DRONE_INTERFACING = 3442;
    public final static int SKILL_DRONES = 3436;
    public final static int SKILL_EXHUMERS = 22551;
    public final static int SKILL_EXPEDITION_FRIGATES = 33856;
    public final static int SKILL_GAS_CLOUD_HARVESTING = 25544;
    public final static int SKILL_ICE_HARVESTING = 16281;
    public final static int SKILL_MINING = 3386;
    public final static int SKILL_MINING_BARGE = 17940;
    public final static int SKILL_MINING_DRONE_OPERATION = 3438;
    public final static int SKILL_MINING_FRIGATE = 32918;
    
    public final static int SKILL_MINING_FOREMAN = 22536;
    public final static int SKILL_MINING_DIRECTOR = 22552;
    public final static int SKILL_WARFARE_LINK_SPECIALIST = 3354;
    public final static int SKILL_INDUSTRIAL_RECONFIGURATION = 28585;
    public final static int SKILL_INDUSTRIAL_COMMAND_SHIPS = 29637;
    public final static int SKILL_CAPITAL_INDUSTRIAL_SHIPS = 28374;
    
    /**
     * Parent API key - we need its parameters to make API requests 
     * and to distinct same characters from different APIs.
     */
    private final APIKey parentKey;
    private final Integer id;
    private final String name;
    
    private volatile Implant slot7;
    private volatile Implant slot8;
    private volatile Implant slot10;
    
    private volatile HashMap<Integer, Integer> skills;
    private final Object blocker = new Object();
    
    private volatile String monitorShip;
    private volatile String monitorBooster;
    private volatile String monitorBoosterShip;
    private volatile boolean monitorUseBoosterShip = false;
    
    private volatile boolean hidden;
    
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
        
        this.slot7 = Implant.NOTHING;
        this.slot8 = Implant.NOTHING;
        this.slot10 = Implant.NOTHING;
        hidden = false;
    }
    
    /**
     * Constructor to load character's data from the XML.
     * @param root root element for character's XML data.
     * @param parentKey parent API key.
     * @throws Exception 
     */
    public EVECharacter(Element root, APIKey parentKey) throws Exception {
        // if we can't get ID and name, it's a failure.
        Attribute attr = root.getAttribute("id");
        id = attr.getIntValue();
        name = root.getChildText("name");
        
        try {
            attr = root.getAttribute("hidden");
            hidden = attr.getBooleanValue();
        } catch (Exception e) {
            hidden = false;
        }
        
        this.slot7 = Implant.NOTHING;
        this.slot8 = Implant.NOTHING;
        this.slot10 = Implant.NOTHING;
        
        // skills and implants on the other hand are expendable.
        // they can be pulled via API anyway.
        HashMap<Integer, Integer> newSkills = new HashMap<>();
        Element skillSet = root.getChild("skills");
        try {
            List<Element> skillList = skillSet.getChildren("skill");
            for(Element skill : skillList) {
                int skid = skill.getAttribute("id").getIntValue();
                int skvalue = skill.getAttribute("value").getIntValue();
                newSkills.put(skid, skvalue);
            }
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load character skills for "+name, e);
        }
        
        Element implantSet = root.getChild("implants");
        try {
            List<Element> impList = implantSet.getChildren("implant");
            for(Element impElem : impList) {
                int impid = impElem.getAttribute("id").getIntValue();
                Implant imp = Implant.implants.get(impid);
                if (imp != null) {
                    switch(imp.getSlot()) {
                        case 7:
                            slot7 = imp;
                            break;
                        case 8:
                            slot8 = imp;
                            break;
                        case 10:
                            slot10 = imp;
                            break;
                    }
                }                
            }
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load character implants for "+name, e);
        }
        
        Element monitorConf = root.getChild("monitor");
        
        try {
            monitorBooster = monitorConf.getChildText("booster");
            monitorShip = monitorConf.getChildText("ship");
            monitorBoosterShip = monitorConf.getChildText("boostership");
            monitorUseBoosterShip = "true".equals(monitorConf.getChildText("useboostership"));
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load monitor settings for "+name, e);
        }                
        
        this.skills = newSkills;
        this.parentKey = parentKey;                
    }
    
    /**
     * Returns XML Element with character's data inside, to be used in saving.
     * @return 
     */
    public Element getXMLElement() {
        synchronized(blocker) {
            Element root = new Element("character");
            root.setAttribute(new Attribute("id", String.valueOf(id)));    
            root.setAttribute(new Attribute("hidden", String.valueOf(hidden)));    
            root.addContent(new Element("name").setText(name));

            Element skillSet = new Element("skills");
            for(Map.Entry<Integer, Integer> skill : skills.entrySet()) {
                Element skillElem = new Element("skill");
                skillElem.setAttribute("id", skill.getKey().toString());
                skillElem.setAttribute("value", skill.getValue().toString());
                skillSet.addContent(skillElem);
            }        
            root.addContent(skillSet);

            Element implantSet = new Element("implants");        
            Implant[] imps = {slot7, slot8, slot10};        
            for(Implant imp : imps) {        
                if (imp != Implant.NOTHING) {
                    Element implantElem = new Element("implant");
                    implantElem.setAttribute("id", String.valueOf(imp.getID()));
                    implantSet.addContent(implantElem);
                }
            }
            root.addContent(implantSet);

            Element monitorConf = new Element("monitor");
            if (monitorBooster != null) {
               Element boosterElem = new Element("booster");
               boosterElem.setText(monitorBooster);
               monitorConf.addContent(boosterElem);
            }            
            if (monitorShip != null) {
               Element shipElem = new Element("ship");
               shipElem.setText(monitorShip);
               monitorConf.addContent(shipElem);
            }           
            if (monitorBoosterShip != null) {
               Element bShipElem = new Element("boostership");
               bShipElem.setText(monitorBoosterShip);
               monitorConf.addContent(bShipElem);
            }            
            
            Element useBShipElem = new Element("useboostership");
            useBShipElem.setText(String.valueOf(monitorUseBoosterShip));
            monitorConf.addContent(useBShipElem);
            root.addContent(monitorConf);
            
            return root;
        }
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
    
    /**
     * Returns level of a skill with a given id.
     * @param skillID
     * @return skill level or 0 for unknown skill.
     */
    public Integer getSkillLevel(Integer skillID) {
        synchronized(blocker) {
            Integer ret = skills.get(skillID);
            return (ret == null ? 0 : ret);
        }
    }
    
    /**
     * Sets level of a skill. 
     * Ingores bad level values. Null-safe (does nothing on nulls).
     * @param skillID ID of a skill
     * @param level desired level
     */
    public void setSkillLevel(Integer skillID, Integer level) {
        if (skillID == null || level == null || level < 0 || level > 5) return;
        synchronized(blocker) {
            skills.put(skillID, level);
        }
    }
    
    /**
     * Returns the implant in the 7th implant slot.
     * Implant.NOTHING is returned if there's nothing.
     * @return 
     */
    public Implant getSlot7Implant() {
        synchronized(blocker) {
            return slot7 == null ? Implant.NOTHING : slot7;
        }
    }
    
    /**
     * Sets implant to the 7th slot.
     * Implant should be insertable to that slot, or nothing will happen.
     * @param imp 
     */
    public void setSlot7Implant(Implant imp) {
        if (imp == null || (imp.getSlot() != 7 && imp != Implant.NOTHING)) return;
        synchronized(blocker) {
            slot7 = imp;
        }
    }
    
    /**
     * Returns the implant in the 8th implant slot.
     * Implant.NOTHING is returned if there's nothing.
     * @return 
     */
    public Implant getSlot8Implant() {
        synchronized(blocker) {
            return slot8 == null ? Implant.NOTHING : slot8;
        }
    }
    
    /**
     * Sets implant to the 8th slot.
     * Implant should be insertable to that slot, or nothing will happen.
     * @param imp 
     */
    public void setSlot8Implant(Implant imp) {
        if (imp == null || (imp.getSlot() != 8 && imp != Implant.NOTHING)) return;
        synchronized(blocker) {
            slot8 = imp;
        }
    }
    
    /**
     * Returns the implant in the 10th implant slot.
     * Implant.NOTHING is returned if there's nothing.
     * @return 
     */
    public Implant getSlot10Implant() {
        synchronized(blocker) {
            return slot10 == null ? Implant.NOTHING : slot10;
        }
    }
    
    /**
     * Sets implant to the 10th slot.
     * Implant should be insertable to that slot, or nothing will happen.
     * @param imp 
     */
    public void setSlot10Implant(Implant imp) {
        if (imp == null || (imp.getSlot() != 10 && imp != Implant.NOTHING)) return;
        synchronized(blocker) {
            slot10 = imp;
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
        String keyCharProfileURL = DataContainer.baseURL+"/char/CharacterSheet.xml.aspx?keyID="
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
        req.addHeader("User-Agent", "JMinerGuide "+App.getVersion()+", https://github.com/alavrov/JMinerGuide");
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
            Element implantRowset = null;
            
            // now, there will be several "rowset" tags.
            // we need one with name "skills" and one with name "implants".
            List<Element> rowsets = result.getChildren("rowset");
            for (Element curRowset : rowsets) {
                if (curRowset.getAttributeValue("name").equals("skills")) {
                    skillRowset = curRowset;
                }
                if (curRowset.getAttributeValue("name").equals("implants")) {
                    implantRowset = curRowset;
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
            
            Implant newSlot7 = Implant.NOTHING, newSlot8 = Implant.NOTHING, 
                    newSlot10 = Implant.NOTHING;
            
            if (implantRowset != null) {
                List<Element> rows = implantRowset.getChildren("row");                
                for (Element row : rows) {
                    int impid = row.getAttribute("typeID").getIntValue();
                    Implant imp = Implant.implants.get(impid);
                    if (imp != null) {
                        switch(imp.getSlot()) {
                            case 7:
                                newSlot7 = imp;
                                break;
                            case 8:
                                newSlot8 = imp;
                                break;
                            case 10:
                                newSlot10 = imp;
                                break;
                        }
                    }
                }
            } else {
                throw new APIException("Unable to fetch "+name+"'s implants");
            }
            
            synchronized(blocker) {
                skills = newSkills;
                slot7 = newSlot7;
                slot8 = newSlot8;
                slot10 = newSlot10;
            }
            
        } catch (JDOMException | IOException | IllegalArgumentException | NullPointerException e ) {
            JMGLogger.logSevere("Cricical failure during API parsing", e);
            throw new APIException("Unable to parse data, please see logs.");        
        } 
    }
    
    @Override
    public EVECharacter clone() {
        synchronized(blocker) {
            EVECharacter out = new EVECharacter(id, name, parentKey);
            out.slot7 = slot7;
            out.slot8 = slot8;
            out.slot10 = slot10;   
            out.hidden = hidden;     
            return out;
        }
    }
    
    /**
     * Makes a full object clone, but with a different parent.
     * @param parentKey
     * @return 
     */
    public EVECharacter clone(APIKey parentKey) {
        synchronized(blocker) {
            EVECharacter out = new EVECharacter(id, name, parentKey);
            out.slot7 = slot7;
            out.slot8 = slot8;
            out.slot10 = slot10;
            out.hidden = hidden;
            return out;
        }
    }
    
    @Override
    public String toString() {
        return name + (hidden? " - hidden" : "");
    }
    
    /**
     * Returns true for preset characters (ALL 5, ALL 0), false otherwise.
     * @return 
     */
    public boolean isPreset() {
        return false;
    }
    
    /**
     * Returns bonus modifier to mining yield, granted by skills and implants.
     * Warning! Modifiers aren't in percents, you can directly multiply by them.
     * @return 
     */
    public float getMiningYieldModifier () {
        float out = 1;
        
        synchronized(blocker) {
            int miningLevel = this.getSkillLevel(SKILL_MINING);
            if (miningLevel > 0) {
                out = out * (1f + 0.05f*miningLevel);
            }

            int astroLevel = this.getSkillLevel(SKILL_ASTROGEOLOGY);
            if (astroLevel > 0) {
                out = out * (1f + 0.05f*astroLevel);
            }

            int slot7Yield = this.slot7.getMiningYieldBonus();
            if (slot7Yield > 0) {
                out = out * (1f + 0.01f*slot7Yield);
            }

            // slot 8 contains gas implants only, so we may pass it.

            int slot10Yield = this.slot10.getMiningYieldBonus();
            if (slot10Yield > 0) {
                out = out * (1f + 0.01f*slot10Yield);
            }
        }
        
        return out;
    }
    
    /**
     * Returns bonus modifier to ice harvester cycle time, granted by skills and implants.     
     * Warning! Modifiers aren't in percents, you can directly multiply by them.
     * @return 
     */
    public float getIceCycleModifier() {
        float out = 1;
        
        synchronized(blocker) {
            int iceLevel = this.getSkillLevel(SKILL_ICE_HARVESTING);
            if (iceLevel > 0) {
                out = out * (1f - 0.05f*iceLevel);
            }

            // ice bonus only in slot 10
            int slot10CycleBonus = this.slot10.getIceCycleBonus();
            if (slot10CycleBonus > 0) {
                out = out * (1f - 0.01f*slot10CycleBonus);
            }
        }
        
        return out;
    }
    
    /**
     * Returns bonus modifier to gas harvester cycle time, granted by implants.
     * Warning! Modifiers aren't in percents, you can directly multiply by them.
     * @return 
     */
    public float getGasCycleModifier() {
        float out = 1;
        
        synchronized(blocker) {
            // gas bonus only in slot 8
            int slot8CycleBonus = this.slot8.getGasCycleBonus();
            if (slot8CycleBonus > 0) {
                out = out * (1f - 0.01f*slot8CycleBonus);
            }
        }
        
        return out;
    }
    
    
    
    /**
     * Returns bonus modifier to drone yield, granted by skills.
     * Warning! Modifiers aren't in percents, you can directly multiply by them.
     * @return 
     */
    public float getDroneYieldModifier() {
        float out = 1;
        
        synchronized(blocker) {
            int droneOperBonus = this.getSkillLevel(SKILL_MINING_DRONE_OPERATION);
            if (droneOperBonus > 0) {
                out = out * (1f + 0.05f*droneOperBonus);
            }

            int droneIntBonus = this.getSkillLevel(SKILL_DRONE_INTERFACING);
            if (droneIntBonus > 0) {
                out = out * (1f + 0.1f*droneIntBonus);
            }
        }
        
        return out;
    }
    
    /**
     * Returns bonus modifier to drone yield, granted by skills.
     * Warning! Modifiers aren't in percents, you can directly multiply by them.
     * @return 
     */
    public float getBoosterLinkModifier() {
        float out = 1;
        
        synchronized(blocker) {
            int miningDirectorBonus = this.getSkillLevel(SKILL_MINING_DIRECTOR);
            if (miningDirectorBonus > 0) {
                out = out * (1f + 0.2f*miningDirectorBonus);
            }

            int warfareLinkSpecBonus = this.getSkillLevel(SKILL_WARFARE_LINK_SPECIALIST);
            if (warfareLinkSpecBonus > 0) {
                out = out * (1f + 0.1f*warfareLinkSpecBonus);
            }
        }
        
        return out;
    }
    
    /**
     * Returns true, if the character should be hidden.
     * @return 
     */
    public boolean isHidden() {
        synchronized(blocker) {
            return hidden;
        }
    }
    
    /**
     * Sets the hidden flag.
     * @param what 
     */
    public void setHidden(boolean what) {
        synchronized(blocker) {
            hidden = what;
        }
    }

    /**
     * Returns the name of last selected booster in asteroid monitor.
     * Can return null!
     * @return 
     */
    public String getMonitorBooster() {
        synchronized(blocker) {
            return monitorBooster;
        }
    }

     /**
     * Returns the name of last selected ship in asteroid monitor.
     * Can return null!
     * @return 
     */
    public String getMonitorShip() {
        synchronized(blocker) {
            return monitorShip;
        }
    }

     /**
     * Sets the name of last selected booster in asteroid monitor. 
     * @param monitorBooster
     */
    public void setMonitorBooster(String monitorBooster) {
        synchronized(blocker) {
            this.monitorBooster = monitorBooster;
        }
    }

    /**
     * Sets the name of last selected ship in asteroid monitor.
     * @param monitorShip 
     */
    public void setMonitorShip(String monitorShip) {
        synchronized(blocker) {
            this.monitorShip = monitorShip;
        }
    }        

    /**
     * Returns the name of last selected booster ship in asteroid monitor.
     * Can return null!
     * @return 
     */
    public String getMonitorBoosterShip() {
        synchronized(blocker) {
            return monitorBoosterShip;
        }
    }

    /**
     * Sets the name of last selected booster ship in asteroid monitor.
     * @param monitorBoosterShip 
     */
    public void setMonitorBoosterShip(String monitorBoosterShip) {
        synchronized(blocker) {
            this.monitorBoosterShip = monitorBoosterShip;
        }
    }        

    /**
     * Returns true, if the character uses boosting ship in asteroid monitor,
     * @return 
     */
    public boolean isMonitorUseBoosterShip() {
        synchronized(blocker) {
            return monitorUseBoosterShip;
        }
    }

    /**
     * Sets if the character uses boosting ship in asteroid monitor,
     * @param monitorUseBoosterShip 
     */
    public void setMonitorUseBoosterShip(boolean monitorUseBoosterShip) {
        synchronized(blocker) {
            this.monitorUseBoosterShip = monitorUseBoosterShip;
        }
    }        
}
