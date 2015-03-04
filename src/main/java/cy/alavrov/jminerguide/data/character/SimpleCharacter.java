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

import cy.alavrov.jminerguide.data.harvestable.BasicHarvestable;
import cy.alavrov.jminerguide.data.harvestable.HarvestableType;
import cy.alavrov.jminerguide.log.JMGLogger;
import java.util.HashSet;
import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 * Simple charaters are the characters without bound API. So, only their name
 * and final yield parameters, entered by hand, are known for them.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class SimpleCharacter implements ICoreCharacter{
    private final String name;
    private int turretYield;
    private int turrets;
    private float turretCycle;
    private int oreHold;
    private int optimal;
    
    private final HashSet<BasicHarvestable> roidFilter;
    private int monitorSequence = 0;
    private boolean monitorIgnore;

    public SimpleCharacter(String name) {
        this.name = name;
        this.roidFilter = new HashSet<>();
        monitorIgnore = false;
        allOnAsteroidFilter();
    }
    
    public SimpleCharacter(Element root) throws Exception {
        String tmpName = root.getChildText("name");
        if (tmpName == null) tmpName = "Simple Character "+System.nanoTime();        
        name = tmpName;
        
        try {
            Attribute attr = root.getAttribute("sequence");
            monitorSequence = attr.getIntValue();
        } catch (Exception e) {
            monitorSequence = 0;
        }
        
         try {
            Attribute attr = root.getAttribute("ignore");
            monitorIgnore = attr.getBooleanValue();
        } catch (Exception e) {
            monitorIgnore = false;
        }
        
        Element turretConf = root.getChild("turrets");
        
        try {
            turrets = turretConf.getAttribute("turrets").getIntValue();
            turretYield = turretConf.getAttribute("turretyield").getIntValue();
            turretCycle = turretConf.getAttribute("turretcycle").getFloatValue();
        } catch (Exception e) {
            turrets = 0;
            turretYield = 0;
            turretCycle = 0;
        }
        
        Element miscConf = root.getChild("misc");       
        
        try {
            oreHold = miscConf.getAttribute("orehold").getIntValue();
            optimal = miscConf.getAttribute("optimal").getIntValue();
        } catch (Exception e) {
            oreHold = 0;
            optimal = 0;
        }
        
        this.roidFilter = new HashSet<>();
        
        Element roidFilters = root.getChild("asteroidfilter");
        
        try {
            String filters = roidFilters.getText().toUpperCase();
            String[] filterArr = filters.split("[,]");
            for (String oreName : filterArr) {
                BasicHarvestable hv = BasicHarvestable.nameMap.get(oreName);
                if (hv != null && (
                        hv.getType() == HarvestableType.ORE || 
                        hv.getType() == HarvestableType.MERCOXIT)) {
                    roidFilter.add(hv);
                }
            }
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load roid filters for "+name, e);            
            allOnAsteroidFilter();
        } 
    }
    
    public synchronized Element getXMLElement() {
        Element root = new Element("simplecharacter");
        root.addContent(new Element("name").setText(name));
        root.setAttribute(new Attribute("sequence", String.valueOf(monitorSequence))); 
        root.setAttribute(new Attribute("ignore", String.valueOf(monitorIgnore)));  
        
        Element turretConf = new Element("turrets");
        turretConf.setAttribute(new Attribute("turrets", String.valueOf(turrets)));  
        turretConf.setAttribute(new Attribute("turretyield", String.valueOf(turretYield)));
        turretConf.setAttribute(new Attribute("turretcycle", String.valueOf(turretCycle)));    
        root.addContent(turretConf);
        
        Element miscConf = new Element("misc");
        miscConf.setAttribute(new Attribute("orehold", String.valueOf(oreHold)));  
        miscConf.setAttribute(new Attribute("optimal", String.valueOf(optimal)));
        root.addContent(miscConf);
        
        Element roidFilterElem = new Element("asteroidfilter");
        String filterStr = "";
        for (BasicHarvestable hv : roidFilter) {
            if (filterStr.isEmpty()) {
                filterStr = hv.name();
            } else {
                filterStr = filterStr + ","+ hv.name();
            }
        }
        roidFilterElem.setText(filterStr);
        root.addContent(roidFilterElem);
        
        return root;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the turretYield
     */
    public synchronized int getTurretYield() {
        return turretYield;
    }

    /**
     * @param turretYield the turretYield to set
     */
    public synchronized void setTurretYield(int turretYield) {
        this.turretYield = turretYield;
    }

    /**
     * @return the turrets
     */
    public synchronized int getTurrets() {
        return turrets;
    }

    /**
     * @param turrets the turrets to set
     */
    public synchronized void setTurrets(int turrets) {
        this.turrets = turrets;
    }

    /**
     * @return the turretCycle
     */
    public synchronized float getTurretCycle() {
        return turretCycle;
    }

    /**
     * @param turretCycle the turretCycle to set
     */
    public synchronized void setTurretCycle(float turretCycle) {
        this.turretCycle = turretCycle;
    }

    /**
     * @return the oreHold
     */
    public synchronized int getOreHold() {
        return oreHold;
    }

    /**
     * @param oreHold the oreHold to set
     */
    public synchronized void setOreHold(int oreHold) {
        this.oreHold = oreHold;
    }

    /**
     * @return the optimal
     */
    public synchronized int getOptimal() {
        return optimal;
    }

    /**
     * @param optimal the optimal to set
     */
    public synchronized void setOptimal(int optimal) {
        this.optimal = optimal;
    }
    
    @Override
    public synchronized void setMonitorSequence(int monitorSequence) {
        this.monitorSequence = monitorSequence;
    }

    @Override
    public synchronized int getMonitorSequence() {
            return monitorSequence;
    } 
                
    /**
     * Returns a copy of asteroid filter.
     * If you want to change something, you shouldn't come here, look at 
     * addHarvestableToFilter and removeHarvestableFromFilter methods.
     * @return 
     */
    @Override
    public synchronized HashSet<BasicHarvestable> getAsteroidFilter() {
        return (HashSet<BasicHarvestable>) roidFilter.clone();
    }     
    
    /**
     * Adds BasicHarvestable to asteroid filter.
     * @param type 
     */
    @Override
    public synchronized void addHarvestableToFilter(BasicHarvestable type) {
        roidFilter.add(type);
    }  
    
    /**
     * Removes BasicHarvestable from asteroid filter.
     * @param type 
     */
    @Override
    public synchronized void removeHarvestableFromFilter(BasicHarvestable type) {
        roidFilter.remove(type);
    }
    
    /**
     * Clears asteroid filter.
     * Equivalent to "allow nothing" filter.
     */
    @Override
    public synchronized void clearAsteroidFilter() {
        roidFilter.clear();
    }
    
    /**
     * Fills asteroid filter with every ore harvestable out there.
     * Equivalent to "allow all" filter.
     */
    @Override
    public synchronized void allOnAsteroidFilter() {
        for (BasicHarvestable hv : BasicHarvestable.values()) {
            if (hv.getType() == HarvestableType.ORE || hv.getType() == HarvestableType.MERCOXIT) {
                roidFilter.add(hv);
            }
        }
    }     

    /**
     * Returns true, if character's mining status is ignored in the asteroid monitor.
     * @return 
     */
    @Override
    public synchronized boolean isMonitorIgnore() {
        return monitorIgnore;
    }

    /**
     * Sets if character's mining status should be ignored in the asteroid monitor.
     * @param monitorIgnore 
     */
    @Override
    public synchronized void setMonitorIgnore(boolean monitorIgnore) {
        this.monitorIgnore = monitorIgnore;
    }
}
