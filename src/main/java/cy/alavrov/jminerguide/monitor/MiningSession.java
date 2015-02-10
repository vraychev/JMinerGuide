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
import cy.alavrov.jminerguide.data.booster.BoosterShip;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.data.harvestable.Asteroid;
import cy.alavrov.jminerguide.data.ship.Ship;
import cy.alavrov.jminerguide.util.winmanager.IEVEWindow;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class MiningSession {
    private final IEVEWindow window;
    private volatile SessionCharacter character;    
    private volatile float usedCargo;  
    private volatile CopyOnWriteArrayList<Asteroid> roids;
    
    public MiningSession(IEVEWindow window) {
        this.window = window;
        this.roids = new CopyOnWriteArrayList<>();
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
    
    /**
     * Switches to the session's EVE window.
     */
    public void switchToWindow() {
        window.makeActive();
    }
    
    /**
     * Returns session's character.
     * @return 
     */
    public SessionCharacter getSessionCharacter() {
        return character;
    }

    /**
     * Creates session's character from the generic character.
     * @param character
     * @param dCont 
     */
    public void createSessionCharacter(EVECharacter character, DataContainer dCont) {                       
        if (character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, dCont);
        this.character = schar;
    }        
    
    /**
     * Updates session character's ship.
     * Does nothing, if there's no session character.
     * @param ship 
     */
    public void updateCharacherShip(Ship ship) {
        if (ship == null || character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, ship);
        this.character = schar;
    }
    
    /**
     * Updates session character's booster char.
     * Does nothing, if there's no session character.
     * @param booster 
     */
    public void updateCharacherBooster(EVECharacter booster) {
        if (booster == null || character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, booster);
        this.character = schar;
    }
    
    /**
     * Updates session character's booster ship.
     * Does nothing, if there's no session character.
     * @param boosterShip
     */
    public void updateCharacherBoosterShip(BoosterShip boosterShip) {
        if (boosterShip == null || character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, boosterShip);
        this.character = schar;
    }
    
    /**
     * Sets, if the session character's booster char actually uses booster ship, or not.
     * Does nothing, if there's no session character.
     * @param what
     */
    public void updateCharacherUsingBoosterShip(boolean what) {
        if (character == null) return;
        
        SessionCharacter schar = new SessionCharacter(character, what);
        this.character = schar;
    }
    
    /**
     * Returns used cargo, rounded down.
     * @return 
     */
    public float getUsedCargo() {
        return usedCargo;
    }
    
    /**
     * Sets used cargo.
     * @param amt used cargo, in m3. Can't be more, than ship's ore hold.
     */
    public void setUsedCargo(float amt) {      
        int maxCargo = 0;
        if (character != null) {
            maxCargo = character.getStats().getOreHold();
        }
        if (amt > maxCargo) amt = maxCargo;
        
        usedCargo = amt;
    }
    
    /**
     * Cleans old asteroid list and loads in a new one.
     * @param newRoids 
     */
    public void clearAndAddRoids(List<Asteroid> newRoids) {
        CopyOnWriteArrayList<Asteroid> newList = new CopyOnWriteArrayList<>(newRoids);
        roids = newList;
    }
    
    /**
     * Adds list of asteroids into the existing one.
     * @param newRoids 
     */
    public void addRoids(List<Asteroid> newRoids) {
        roids.addAll(newRoids);
    }
    
    public TableModel getTableModel() {
        return new AsteroidTableModel();
    }
    
    public class AsteroidTableModel extends AbstractTableModel {                

        @Override
        public int getRowCount() {
            return roids.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Type";
                    
                case 1:
                    return "Distance";
                    
                case 2:
                    return "Rem";
                    
                case 3:
                    return "Target";
                    
                default:
                    return "";
                    
            }
        }
        
        

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= roids.size() || columnIndex > 3) return null;
            
            Asteroid roid = roids.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return roid;
                    
                case 1:
                    return roid.getDistance();
                    
                case 2:
                    return roid.getRemainingUnits();
                    
                case 3:
                    return ""; // tba
                    
                default:
                    return null;
                    
            }
        }                            
    }
}
