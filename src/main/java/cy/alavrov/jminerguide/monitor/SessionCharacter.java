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
import cy.alavrov.jminerguide.data.booster.BoosterShip;
import cy.alavrov.jminerguide.data.booster.BoosterShipContainer;
import cy.alavrov.jminerguide.data.character.CharacterContainer;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.data.ship.Ship;
import cy.alavrov.jminerguide.data.ship.ShipContainer;

/**
 * Session's character on a ship with booster.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class SessionCharacter {
    private final EVECharacter character;
    private final Ship ship;
    private final EVECharacter booster;
    private final BoosterShip boosterShip;
    private final CalculatedStats stats;    
    
    public SessionCharacter(EVECharacter character, DataContainer dCont) {

        
        CharacterContainer cCont = dCont.getCharacterContainer();
        ShipContainer sCont = dCont.getShipContainer();
        BoosterShipContainer bCont = dCont.getBoosterContainer();
        
        String boosterName = character.getMonitorBooster();
        EVECharacter newBooster = cCont.getCharacterByName(boosterName);
        if (newBooster == null) newBooster = cCont.getAll0();
        
        String shipName = character.getMonitorShip();
        Ship newShip = sCont.getShip(shipName);
        if (newShip == null) newShip = sCont.getShipModel().getElementAt(0); // always have something, so safe.
        
        BoosterShip bship = bCont.isUsingBoosterShip() ? bCont.getBooster() : bCont.getNoBooster();
        
        booster = newBooster;
        ship = newShip;
        boosterShip = bship;
        
        CalculatedStats newStats = new CalculatedStats(character, booster, ship, boosterShip, false);
        
        stats = newStats;
        
        this.character = character; 
    }
    
    public SessionCharacter(SessionCharacter oldChar, Ship newShip) {
        character = oldChar.character;
        booster = oldChar.booster;
        boosterShip = oldChar.boosterShip;
        ship = newShip;
        
        CalculatedStats newStats = new CalculatedStats(character, booster, ship, boosterShip, false);
        
        stats = newStats;
        character.setMonitorShip(newShip.getName());
    }
    
    public SessionCharacter(SessionCharacter oldChar, EVECharacter newBooster) {
        character = oldChar.character;
        booster = newBooster;
        boosterShip = oldChar.boosterShip;
        ship = oldChar.ship;
        
        CalculatedStats newStats = new CalculatedStats(character, booster, ship, boosterShip, false);
        
        stats = newStats;
        character.setMonitorBooster(newBooster.getName());
    }

    public EVECharacter getCharacter() {
        return character;
    }

    public Ship getShip() {
        return ship;
    }

    public EVECharacter getBooster() {
        return booster;
    }

    public BoosterShip getBoosterShip() {
        return boosterShip;
    }

    public CalculatedStats getStats() {
        return stats;
    }        
}
