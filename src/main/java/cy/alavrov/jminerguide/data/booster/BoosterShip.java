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
import org.jdom2.Element;

/**
 * Booster ship.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class BoosterShip {
    private String name;
    
    private BoosterHull hull;
    private ForemanLink cycleLink;
    private ForemanLink optimalLink;
    private boolean deployed;

    public BoosterShip(String name) {
        this.name = name;
        hull = BoosterHull.GENERIC;
        cycleLink = ForemanLink.NOTHING;
        optimalLink = ForemanLink.NOTHING;
        deployed = false;
    }
    
    public BoosterShip(Element root) throws Exception {
        String newName = root.getChildText("name");
        if (newName == null) newName = "Unnamed Booster Ship "+System.nanoTime();
        name = newName;
        
        // same catch-all logic, as in Ship
        try {
            Element hullElem = root.getChild("hull");
            int hullID = hullElem.getAttribute("id").getIntValue();
            BoosterHull newHull = BoosterHull.boosterHullsMap.get(hullID);
            hull = (newHull == null) ? BoosterHull.GENERIC : newHull;
            if (hull.haveDeployedMode()) {
                deployed = hullElem.getAttribute("deployed").getBooleanValue();
            }
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load booster hull", e);
            hull = BoosterHull.GENERIC;
            deployed = false;
        }
                        

        try {        
            Element cycleLinkElem = root.getChild("cyclelink");
            int cycleID = cycleLinkElem.getAttribute("id").getIntValue();
            ForemanLink newLink = ForemanLink.linksMap.get(cycleID);                        
            cycleLink = newLink;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load cycle link", e);
            cycleLink = ForemanLink.NOTHING;
        }
        
        try {        
            Element optimalLinkElem = root.getChild("optimallink");
            int cycleID = optimalLinkElem.getAttribute("id").getIntValue();
            ForemanLink newLink = ForemanLink.linksMap.get(cycleID);                        
            optimalLink = newLink;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load cycle link", e);
            optimalLink = ForemanLink.NOTHING;
        }
    }
    
    public synchronized Element getXMLElement() {    
        Element root = new Element("booster");        

        root.addContent(new Element("name").setText(name));

        root.addContent(new Element("hull")
                .setAttribute("id", String.valueOf(hull.getID()))
                .setAttribute("deployed", String.valueOf(deployed))
        );

        root.addContent(new Element("cyclelink")
                .setAttribute("id", String.valueOf(cycleLink.getID()))
        );

        root.addContent(new Element("optimallink")
                .setAttribute("id", String.valueOf(optimalLink.getID()))
        );            

        return root;        
    }
    
    /**
     * Sets a booster ship's hull.
     * @param hull 
     */
    public synchronized void setHull(BoosterHull hull) {
        if (hull == null) return;
        this.hull = hull;
    }
    
    /**
     * Returns a booster ship's hull.
     * @return 
     */
    public synchronized BoosterHull getHull() {
        return hull;
    }
    
    /**
     * Turns deployed mode on or off. Have no effect on hulls without such mode.
     * @param isDeployed 
     */
    public synchronized void setDeployedMode(boolean isDeployed) {
        deployed = isDeployed;
    }
    
    /**
     * Returns true, if a ship is in the deployed mode.
     * Always returns false, if booster's hull have no deployed mode support.
     * @return 
     */
    public synchronized boolean isDeployedMode() {
        if (!hull.haveDeployedMode()) return false;

        return deployed;
    }
    
    /**
     * Sets foreman link for optimal distance.
     * @param link 
     */
    public synchronized void setOptimalLink(ForemanLink link) {
        if (link == null) return;
        optimalLink = link;        
    }
    
    /**
     * Returns foreman link for optimal distance.
     * @return 
     */
    public synchronized ForemanLink getOptimalLink() {        
        return optimalLink;
    }
    
    /**
     * Sets foreman link for mining cycle.
     * @param link 
     */
    public synchronized void setCycleLink(ForemanLink link) {
        if (link == null) return;
        cycleLink = link;
    }
    
    /**
     * Returns foreman link for mining cycle.
     * @return 
     */
    public synchronized ForemanLink getCycleLink() {
        return cycleLink;
    }
    
    public synchronized String getName() {
        return name;
    }
    
    public synchronized void setName(String name) {
        if (name == null) return;
        this.name = name;
    }
    
    @Override
    public synchronized String toString() {
        return name;
    }
}

