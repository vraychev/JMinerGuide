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

import cy.alavrov.jminerguide.data.DataContainer;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.data.ship.Ship;
import cy.alavrov.jminerguide.util.winmanager.IEVEWindow;

/**
 *
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class MiningSession {
    private final IEVEWindow window;
    private volatile SessionCharacter character;    

    public MiningSession(IEVEWindow window) {
        this.window = window;
    }
    
    /**
     * Retursn session's character name or null, if no character selected yet.
     * You should run window update before querying this!
     * @return 
     */
    public String getCharacterName() {
        return window.getCharacterName();
    }
    
    /**
     * Returns false, if the session's window is no more.
     * You should run window update before querying this!
     * @return 
     */
    public boolean exists() {
        return window.exists();
    }
    
    /**
     * Updates current window parametes - name and aliveness.
     */
    public void updateWindow() {
        window.update();
    }
    
    public void switchToWindow() {
        window.makeActive();
    }
    
    public SessionCharacter getSessionCharacter() {
        return character;
    }

    public void createSessionCharacter(EVECharacter character, DataContainer dCont) {                       
        if (character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, dCont);
        this.character = schar;
    }        
    
    public void updateCharacherShip(Ship ship) {
        if (ship == null || character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, ship);
        this.character = schar;
    }
    
    public void updateCharacherBooster(EVECharacter booster) {
        if (booster == null || character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, booster);
        this.character = schar;
    }
}
