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
package cy.alavrov.jminerguide.monitor;

import cy.alavrov.jminerguide.data.CalculatedStats;
import cy.alavrov.jminerguide.data.DataContainer;
import cy.alavrov.jminerguide.data.ICalculatedStats;
import cy.alavrov.jminerguide.data.SimpleCalculatedStats;
import cy.alavrov.jminerguide.data.booster.BoosterShip;
import cy.alavrov.jminerguide.data.booster.BoosterShipContainer;
import cy.alavrov.jminerguide.data.character.CharacterContainer;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.data.character.ICoreCharacter;
import cy.alavrov.jminerguide.data.character.SimpleCharacter;
import cy.alavrov.jminerguide.data.ship.Ship;
import cy.alavrov.jminerguide.data.ship.ShipContainer;

/**
 * Session's character on a ship with booster.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class SessionCharacter implements ISessionCharacter{
    private final EVECharacter character;
    private final SimpleCharacter simpleCharacter;
    private Ship ship;
    private EVECharacter booster;
    private BoosterShip boosterShip;
    private BoosterShip noBoosterShip;
    private boolean useBoosterShip;
    private ICalculatedStats stats; 
    private ICalculatedStats statsMercoxit;    
    private ICalculatedStats simpleStats; 
    
    public SessionCharacter(EVECharacter character, SimpleCharacter simpleCharacter, DataContainer dCont) {       
        CharacterContainer cCont = dCont.getCharacterContainer();
        ShipContainer sCont = dCont.getShipContainer();
        BoosterShipContainer bCont = dCont.getBoosterShipContainer();
        
        String boosterName = character.getMonitorBooster();
        EVECharacter newBooster = cCont.getCharacterByName(boosterName);
        if (newBooster == null) newBooster = cCont.getAll0();
        
        String shipName = character.getMonitorShip();
        Ship newShip = sCont.getShip(shipName);
        if (newShip == null) newShip = sCont.getShipModel().getElementAt(0); // always have something, so safe.
        
        String boosterShipName = character.getMonitorBooster();
        BoosterShip newBoosterShip = bCont.getBoosterShip(boosterShipName);
        if (newBoosterShip == null) newBoosterShip = bCont.getBoosterShipModel().getElementAt(0);                
   
        Boolean isUseBoosterShip = character.isMonitorUseBoosterShip();
        
        booster = newBooster;
        ship = newShip;
        boosterShip = newBoosterShip;
        useBoosterShip = isUseBoosterShip;
        noBoosterShip = bCont.getNoBooster();
        this.character = character; 
        this.simpleCharacter = simpleCharacter;
        
        recalculateStats();
        
    }
    
    @Override
    public synchronized void recalculateStats() {
        CalculatedStats newStats = new CalculatedStats(character, booster, ship, 
                useBoosterShip ? boosterShip : noBoosterShip, false);
        
        stats = newStats;
        
        CalculatedStats newMercoStats = new CalculatedStats(character, booster, ship, 
                useBoosterShip ? boosterShip : noBoosterShip, true);
        
        statsMercoxit = newMercoStats;
        
        SimpleCalculatedStats newSimpleStats = new SimpleCalculatedStats(simpleCharacter);
        
        simpleStats = newSimpleStats;
    }

    @Override
    public synchronized ICoreCharacter getCoreCharacter() {        
        return character;
    }

    public synchronized SimpleCharacter getSimpleCharacter() {
        return simpleCharacter;
    }
    
    public synchronized Ship getShip() {
        return ship;
    }

    public synchronized void setShip(Ship ship) {
        this.ship = ship;
        recalculateStats();
    }        

    public synchronized EVECharacter getBooster() {
        return booster;
    }

    public synchronized void setBooster(EVECharacter booster) {
        this.booster = booster;
        recalculateStats();
    }   
    
    public synchronized BoosterShip getBoosterShip() {
        return boosterShip;
    }

    public synchronized void setBoosterShip(BoosterShip boosterShip) {
        this.boosterShip = boosterShip;
        recalculateStats();
    }

    public synchronized boolean isUseBoosterShip() {
        return useBoosterShip;
    }    

    public synchronized void setUseBoosterShip(boolean useBoosterShip) {
        this.useBoosterShip = useBoosterShip;
        recalculateStats();
    }
    
    @Override
    public synchronized ICalculatedStats getStats() {
        if (character.isMonitorSimple()) {
            return simpleStats;
        } else {
            return stats;
        }
    }        

    @Override
    public synchronized ICalculatedStats getStatsMercoxit() {
        if (character.isMonitorSimple()) {
            return simpleStats;
        } else {
            return statsMercoxit;
        }
    }        

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public synchronized int getTurretCount() {
        if (character.isMonitorSimple()) {
            return simpleCharacter.getTurrets();
        } else {
            return ship.getTurretCount();
        }
    }
}
