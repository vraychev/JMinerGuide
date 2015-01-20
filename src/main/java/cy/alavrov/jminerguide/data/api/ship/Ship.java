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
package cy.alavrov.jminerguide.data.api.ship;

import cy.alavrov.jminerguide.log.JMGLogger;
import org.jdom2.Element;

/**
 * Assembled ship with a pilot.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class Ship {
    private final Object blocker = new Object();
    
    private Hull hull;
    private Turret turret;
    private int turretCount;
    private MiningCrystalLevel miningCrystal;
    private HarvestUpgrade harvestUpgrade;
    private int harvestUpgradeCount;
    
    /**
     * Last selected mining turret. 
     * Used to preserve turret selection between turret types.
     */
    private Turret lastMiningTurret = Turret.MINERI;
    
    /**
     * Last selected mining turret. 
     * Used to preserve turret selection between turret types.
     */
    private Turret lastStripTurret = Turret.STRIPMINERI;
    
    public Ship() {
        hull = Hull.VENTURE;
        turret = Turret.MINERI;
        turretCount = hull.getMaxTurrets();
        miningCrystal = MiningCrystalLevel.NOTHING;
        harvestUpgrade = HarvestUpgrade.MININGI;
        harvestUpgradeCount = hull.getMaxUpgrades();        
    }
    
    public Ship(Element root) throws Exception {
        try {
            int hullid = root.getChild("hull").getAttribute("id").getIntValue();
            Hull newHull = Hull.hullsMap.get(hullid);
            hull = (newHull == null) ? Hull.VENTURE : newHull;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load hull", e);
            hull = Hull.VENTURE;
        }
        
        Element turretElem = root.getChild("turret");
        // instead of multiple checks for null, we'll make few NullPointerException
        // catch-alls. While it's not that beautiful, we'll have less code that way.
        
        try {            
            int turrid = turretElem.getAttribute("id").getIntValue();
            Turret newTurret = Turret.turretsMap.get(turrid);
            if (hull.isUsingStripMiners()) {
                if (newTurret == null || 
                        newTurret.getTurretType() == TurretType.GASHARVESTER || 
                        newTurret.getTurretType() == TurretType.MININGLASER) {
                    newTurret = Turret.STRIPMINERI;
                }                
            } else {
                if (newTurret == null || 
                        newTurret.getTurretType() == TurretType.STRIPMINER || 
                        newTurret.getTurretType() == TurretType.ICEHARVESTER) {
                    newTurret = Turret.MINERI;
                }
            }
            
            turret = newTurret;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load turret", e);
            if (hull.isUsingStripMiners()) {
                turret = Turret.STRIPMINERI;
            } else {
                turret = Turret.MINERI;
            }
        }

        try {
            int newTurrCount = turretElem.getAttribute("count").getIntValue();
            if (newTurrCount < 0 ) newTurrCount = 0;
            if (newTurrCount > hull.getMaxTurrets()) newTurrCount = hull.getMaxTurrets();
            turretCount = newTurrCount;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load turret count", e);
            turretCount = hull.getMaxTurrets();
        }
        
        // as logic will ignore crystals for wrong type of turret, we may just load
        // it regardless. As a side effect, that will allow us not reset crystal
        // selection.
        
        try {
            int crysid = turretElem.getAttribute("crystal").getIntValue();    
            MiningCrystalLevel newCrystal = MiningCrystalLevel.crystalLevelsMap.get(crysid);
            miningCrystal = (newCrystal == null) ? MiningCrystalLevel.NOTHING : newCrystal;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load turret crystal", e);
            miningCrystal = MiningCrystalLevel.NOTHING;
        }
        
        Element upgradeElem = root.getChild("upgrade");
        // same catch-all logic here.
        
        try {
            int upgid = upgradeElem.getAttribute("id").getIntValue();    
            HarvestUpgrade newUpg = HarvestUpgrade.upgradesMap.get(upgid);
            harvestUpgrade = (newUpg == null) ? HarvestUpgrade.NOTHING : newUpg;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load harvest upgrade", e);
            harvestUpgrade = HarvestUpgrade.NOTHING;
        }
        
        try {
            int newUpgCount = upgradeElem.getAttribute("count").getIntValue();
            if (newUpgCount < 0 ) newUpgCount = 0;
            if (newUpgCount > hull.getMaxUpgrades()) newUpgCount = hull.getMaxUpgrades();
            harvestUpgradeCount = newUpgCount;
        } catch (NullPointerException e) {
            JMGLogger.logWarning("Unable to load upgrade count", e);
            harvestUpgradeCount = hull.getMaxUpgrades();
        }
    }
    
    public Element getXMLElement() {     
        synchronized(blocker) {
            Element root = new Element("ship");        
            root.addContent(new Element("hull").setAttribute("id", String.valueOf(hull.getID())));

            root.addContent(new Element("turret")
                    .setAttribute("id", String.valueOf(turret.getID()))
                    .setAttribute("count", String.valueOf(turretCount))
                    .setAttribute("crystal", String.valueOf(miningCrystal.getID()))
            );

            root.addContent(new Element("upgrade")
                    .setAttribute("id", String.valueOf(harvestUpgrade.getID()))
                    .setAttribute("count", String.valueOf(harvestUpgradeCount))
            );
            return root;
        }        
    }
    
    public void setHull(Hull newHull) {
        if (newHull == null) return;
        
        synchronized(blocker) {
            hull = newHull;
            if (turretCount > hull.getMaxTurrets()) {
                turretCount = hull.getMaxTurrets();
            }
            
            // let's preserve turret selection, if we have to change turret type.
            if (hull.isUsingStripMiners()) {
                if (turret.getTurretType() == TurretType.GASHARVESTER || 
                    turret.getTurretType() == TurretType.MININGLASER) {
                    lastMiningTurret = turret;
                    turret = lastStripTurret;
                }
            } else {
                if (turret.getTurretType() == TurretType.STRIPMINER || 
                    turret.getTurretType() == TurretType.ICEHARVESTER) {
                    lastStripTurret = turret;
                    turret = lastMiningTurret;
                }
            }
            
            if (this.harvestUpgradeCount > hull.getMaxUpgrades()) {
                this.harvestUpgradeCount = hull.getMaxUpgrades();
            }
            
            // upgrade type won't change.
        }
    }
    
    public Hull getHull() {
        synchronized(blocker) {
            return hull;
        }
    }
    
    public void setTurret(Turret newTurret) {
        if (turret == null) return;
        
        synchronized(blocker) {
            if (hull.isUsingStripMiners()) {
                if (turret.getTurretType() == TurretType.GASHARVESTER || 
                    turret.getTurretType() == TurretType.MININGLASER) {
                    return;
                }
            } else {
                if (turret.getTurretType() == TurretType.STRIPMINER || 
                    turret.getTurretType() == TurretType.ICEHARVESTER) {
                    return;
                }
            }
            
            turret = newTurret;
        }
    }
    
    public Turret getTurret() {
        synchronized(blocker) {
            return turret;
        }
    }
    
    public void setTurrentCount(int count) {
        synchronized(blocker) {
            if (count < 0) count = 0;
            if (count > hull.getMaxTurrets()) count = hull.getMaxTurrets();
            turretCount = count;
        }
    }
    
    public int getTurretCount() {
        synchronized(blocker) { 
            return turretCount;
        }
    }
    
    public void setTurretCrystal(MiningCrystalLevel lvl) {
        if (lvl == null) return;
        synchronized(blocker) {
            miningCrystal = lvl;
        }
    }
    
    public MiningCrystalLevel getTurretCrystal() {
        synchronized(blocker) {
            return miningCrystal;
        }
    }
    
    public void setHarvestUpgrade(HarvestUpgrade newUpg) {
        if (newUpg == null) return;
        synchronized(blocker) {
            harvestUpgrade = newUpg;
        }
    }
    
    public HarvestUpgrade getHarvestUpgrade() {
        synchronized(blocker) {
            return harvestUpgrade;
        }
    }
    
    public void setHarvestUpgradeCount(int count) {
        synchronized(blocker) {
            if (count < 0) count = 0;
            if (count > hull.getMaxUpgrades()) count = hull.getMaxUpgrades();
            harvestUpgradeCount = count;
        }
    }
    
    public int getHarvestUpgradeCount() {
        synchronized(blocker) { 
            return harvestUpgradeCount;
        }
    }
}
