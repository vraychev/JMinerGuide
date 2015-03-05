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
import cy.alavrov.jminerguide.data.character.ICoreCharacter;
import cy.alavrov.jminerguide.data.character.SimpleCharacter;
import cy.alavrov.jminerguide.data.harvestable.Asteroid;
import cy.alavrov.jminerguide.data.harvestable.BasicHarvestable;
import cy.alavrov.jminerguide.util.winmanager.IEVEWindow;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * EVE session, hopefully for mining.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class MiningSession {
    private final static PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
     .printZeroAlways()
     .appendMinutes()
     .appendSeparator(":")
     .appendSeconds()
     .toFormatter();
    
    private final IEVEWindow window;
    private ISessionCharacter character;    
    private float usedCargo;  
    private CopyOnWriteArrayList<Asteroid> roids;
    private final TurretInstance turret1;
    private final TurretInstance turret2;
    private final TurretInstance turret3;
    private MiningTimer timer;
    private boolean haveAlerts = false;
    
    public MiningSession(IEVEWindow window) {
        this.window = window;
        this.roids = new CopyOnWriteArrayList<>();
        
        turret1 = new TurretInstance(1);
        turret2 = new TurretInstance(2);
        turret3 = new TurretInstance(3);
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
     * Returns session's character. Can return null.
     * @return 
     */
    public synchronized ISessionCharacter getSessionCharacter() {
        return character;
    }        
    
    /**
     * Unbinds all turrets from their respective asteroids.
     */
    public synchronized void unbindAllTurrets() {
        turret1.unbindAsteroid();
        turret2.unbindAsteroid();
        turret3.unbindAsteroid();
    }

    /**
     * Creates session's character from the generic character.
     * Resets turrets.
     * @param character
     * @param dCont 
     */
    public synchronized void createSessionCharacter(EVECharacter character, DataContainer dCont) {                       
        if (character == null) return;   
        
        unbindAllTurrets();
        
        ISessionCharacter schar = new SessionCharacter(character, dCont);
        this.character = schar;
    }     
    
    /**
     * Creates session character from the simple character.
     * @param character 
     */
    public synchronized void createSessionCharacter(SimpleCharacter character) {
        if (character == null) return;   
        
        unbindAllTurrets();
        
        ISessionCharacter schar = new SimpleSessionCharacter(character);
        this.character = schar;
    }
    
    /**
     * Returns used cargo.
     * @return 
     */
    public synchronized float getUsedCargo() {
        return usedCargo;
    }
    
    /**
     * Returns remaining free cargo.
     * @return 
     */
    public synchronized float getRemainingCargo() {
        if (character == null) return 0;
        
        float ret = character.getStats().getOreHold() - usedCargo;
        if (ret < 0) ret = 0;
        return ret;
    }
    
    /**
     * Sets used cargo. Also stops all turrets from working.
     * @param amt used cargo, in m3. Can't be more, than ship's ore hold.
     */
    public synchronized void setUsedCargo(float amt) {      
        unbindAllTurrets();
        
        int maxCargo = 0;
        if (character != null) {
            maxCargo = character.getStats().getOreHold();
        }
        if (amt > maxCargo) amt = maxCargo;
        
        usedCargo = amt;
    }
    
    private synchronized void putToCargo(float amt) {
        if (character != null) {
            int maxCargo = character.getStats().getOreHold();
            float newCargo = usedCargo + amt;
            if (newCargo > maxCargo) newCargo = maxCargo;
            
            usedCargo = newCargo;
        }
    }
    
    /**
     * Cleans old asteroid list and loads in a new one.
     * Unbinds all turrets in the process.
     * @param newRoids 
     */
    public synchronized void clearAndAddRoids(List<Asteroid> newRoids) {
        if (character == null) return;
        unbindAllTurrets();
                
        Set<BasicHarvestable> filter = character.getCoreCharacter().getAsteroidFilter();                
        roids = new CopyOnWriteArrayList<>(filterRoids(newRoids, filter));
    }
    
    /**
     * Filters given list of asteroids against a filter set.
     * Only asteroids with basic harvestables in a filter set will get through.
     * @param unfiltered
     * @param filter
     * @return 
     */
    private List<Asteroid> filterRoids(List<Asteroid> unfiltered, Set<BasicHarvestable> filter) {
        List<Asteroid> filtered = new ArrayList<>();
        for (Asteroid roid : unfiltered) {
            if (filter.contains(roid.getHarvestable().getBasicHarvestable())) {
                filtered.add(roid);
            }
        }
        return filtered;
    }
    
    /**
     * Resets cargohold, stops turrets and loads in an asteroid list.
     * @param newRoids 
     */
    public synchronized void resetAndAddRoids(List<Asteroid> newRoids) {
        if (character == null) return;
        
        setUsedCargo(0);
        Set<BasicHarvestable> filter = character.getCoreCharacter().getAsteroidFilter();                
        roids = new CopyOnWriteArrayList<>(filterRoids(newRoids, filter));
    }
    
    /**
     * Adds list of asteroids into the existing one.
     * @param newRoids 
     */
    public synchronized void addRoids(List<Asteroid> newRoids) {
        if (character == null) return;
        
        Set<BasicHarvestable> filter = character.getCoreCharacter().getAsteroidFilter();                
        roids.addAll(filterRoids(newRoids, filter));
    }
    
    /**
     * Cleans up the asteroid list, removing empty ones.
     */
    public synchronized void cleanupRoids() {
        List<Asteroid> filtered = new ArrayList<>();
        for(Asteroid roid : roids) {
            if(roid.getRemainingUnits() > 0) {
                filtered.add(roid);
            }
        }
        
        roids = new CopyOnWriteArrayList<>(filtered);
    }
    
    /**
     * Clears the asteroid list, leaving it empty.
     * Also, turns of turrets.
     */
    public synchronized void clearRoids() {            
        unbindAllTurrets();
        
        roids = new CopyOnWriteArrayList<>();
    }
    
    public synchronized TableModel getTableModel() {
        return new AsteroidTableModel();
    }

    /**
     * @return the turret1
     */
    public TurretInstance getTurret1() {
        return turret1;
    }

    /**
     * @return the turret2
     */
    public TurretInstance getTurret2() {
        return turret2;
    }

    /**
     * @return the turret3
     */
    public TurretInstance getTurret3() {
        return turret3;
    }
    
    
    public synchronized void doMining() throws AsteroidMinedException, FullOreHoldException {
        if (character == null) return;
        
        boolean isRoidError = false;
        AsteroidMinedException roidEx = null;
        
        boolean isHoldError = false;
        FullOreHoldException holdEx = null;
        
        // we actually have to make a full calculation cycle before throwing out the exception.
        
        try {
            putToCargo(turret1.mineSome(character.getStats(), character.getStatsMercoxit(), getRemainingCargo()));
        } catch (AsteroidMinedException e) {
            isRoidError = true;
            roidEx = e;
            putToCargo(e.getMinedM3());
        } catch (FullOreHoldException e) {
            isHoldError = true;
            holdEx = e;
        }
        
        try {
            putToCargo(turret2.mineSome(character.getStats(), character.getStatsMercoxit(), getRemainingCargo()));
        } catch (AsteroidMinedException e) {
            isRoidError = true;
            roidEx = e;
            putToCargo(e.getMinedM3());
        } catch (FullOreHoldException e) {
            isHoldError = true;
            holdEx = e;
        }
        
        try {
            putToCargo(turret3.mineSome(character.getStats(), character.getStatsMercoxit(), getRemainingCargo()));
        } catch (AsteroidMinedException e) {
            isRoidError = true;
            roidEx = e;
            putToCargo(e.getMinedM3());
        } catch (FullOreHoldException e) {
            isHoldError = true;
            holdEx = e;
        }
        
        if (isHoldError) {
            haveAlerts = true;
            // we should unbind all turrets here, because we could get the exception
            // only on the last turret, for example.
            unbindAllTurrets();
            throw holdEx;            
        }
        
        if (isRoidError) {
            haveAlerts = true;
            throw roidEx;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MiningSession)) return false;
        
        MiningSession other = (MiningSession) obj;
        return window.equals(other.window);
    }

    @Override
    public int hashCode() {
        return window.hashCode();
    }

    public synchronized MiningTimer getTimer() {
        return timer;
    }
    
    /**
     * Starts a new timer.
     * Will not do anything, if there is no character.
     * @param seconds 
     */
    public synchronized void newTimer(int seconds, int secondsToClear) {
        if (character == null) return;
        timer = new MiningTimer(seconds, secondsToClear);
    }
    
    public synchronized void stopTimer() {
        timer = null;
    }
    
    private String getButtonHTML(String name, String secondLine) {
        if (name == null) {
            name = "- no pilot selected -";
        }
        
        if (secondLine == null) {
            secondLine = "";
        }
        
        return "<html><center>"+name+"<br>"+secondLine+"</center></html>";
    }
    
    /**
     * Updates corresponding session button with actual information, based
     * on the session state.
     * @param button 
     */
    public synchronized void updateButton(MiningSessionButton button) {
        if (character == null) {
            button.setText(getButtonHTML(getCharacterName(), "&nbsp;"));
            haveAlerts = false;
        } else {
            ICoreCharacter eveChr = character.getCoreCharacter();
            if (!eveChr.isMonitorIgnore() && getRemainingCargo() < 1) {
                haveAlerts = true;
                button.setForeground(Color.RED);
                button.setText(getButtonHTML(getCharacterName(), "/!\\ CARGO /!\\"));
                return;
            } 
            
            if (timer != null && timer.isFinished()) {
                haveAlerts = true;
                button.setForeground(Color.RED);
                button.setText(getButtonHTML(getCharacterName(), "/!\\ TIMER /!\\"));
                return;
            }
            
            boolean t1isMining = turret1.isMining();
            // if there is no turret2 on a ship, return true to skip
            boolean t2isMining = character.getTurretCount() < 2 || turret2.isMining(); 
            // if there is no turret3 on a ship, return true to skip
            boolean t3isMining = character.getTurretCount() < 3 || turret3.isMining(); 

            if (!eveChr.isMonitorIgnore() && timer == null && (!t1isMining || !t2isMining || !t3isMining)) {  
                haveAlerts = true;              
                button.setForeground(Color.RED);
                button.setText(getButtonHTML(getCharacterName(), "/!\\ TURRET /!\\"));
            } else {
                haveAlerts = false;
                int rem = Integer.MAX_VALUE;

                // we skip remaining time of 0, as unused turrets return exactly that.
                // for mining and used turrets remaining time will be not zero at this point.

                int secs = turret1.getRemainingSeconds(character.getStats());
                if (secs > 0 && rem > secs) {
                    rem = secs;
                }

                secs = turret2.getRemainingSeconds(character.getStats());
                if (secs > 0 && rem > secs) {
                    rem = secs;
                }

                secs = turret3.getRemainingSeconds(character.getStats());
                if (secs > 0 && rem > secs) {
                    rem = secs;
                }
                
                boolean showTimer = false;
                if (timer != null) {
                    secs = timer.getRemainingSeconds();
                    if (rem > secs) {
                        rem = secs;
                        showTimer = true;
                    }
                }

                if (rem == Integer.MAX_VALUE) {
                    button.setForeground(Color.BLACK);
                    button.setText(getButtonHTML(character.getCoreCharacter().getName(), "&nbsp;"));
                } else {
                    int cycle;
                    if (showTimer) {
                        cycle = timer.getSeconds();
                    } else {
                        cycle = (int) character.getStats().getTurretCycle();
                    }
                    float remcycles = rem /(float)cycle;

                    Period remPeriod = Seconds.seconds(rem)
                        .toStandardDuration().toPeriod();

                    button.setText(getButtonHTML(character.getCoreCharacter().getName(), 
                            minutesAndSeconds.print(remPeriod)));
                    if (remcycles > 1) {
                        button.setForeground(Color.BLACK);
                    } else {
                        button.setForeground(new Color(1 - remcycles, 0, 0));
                    }
                }
            }           
        }
    }
    
    /**
     * Returns true, if the session have alerts.     
     * @return 
     */
    public synchronized boolean haveAlerts() {
        return haveAlerts;
    }
    
    public class AsteroidTableModel extends AbstractTableModel {                

        @Override
        public int getRowCount() {
            synchronized (MiningSession.this) {
                return roids.size();
            }
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
            synchronized (MiningSession.this) {
                if (rowIndex >= roids.size() || columnIndex > 3) return null;

                Asteroid roid = roids.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return roid;

                    case 1:
                        return roid.getDistance();

                    case 2:
                        if (character == null) {
                            return roid.getRemainingUnits();
                        } else {
                            return roid.getRemString(character.getStats());
                        }

                    case 3:
                        return roid.getTurrets(); // tba

                    default:
                        return null;

                }                
            }
        }                            
    }
}
