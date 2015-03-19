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
package cy.alavrov.jminerguide.data.harvestable;

import cy.alavrov.jminerguide.data.ICalculatedStats;
import cy.alavrov.jminerguide.monitor.TurretInstance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Something with harvestable inside. 
 * I.e. asteroid, ice roid or gas cloud.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class Asteroid {
    private final static PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
     .printZeroAlways()
     .appendMinutes()
     .appendSeparator(":")
     .appendSeconds()
     .toFormatter();
    
    private final IHarvestable harvestable;
    private final int distance;
    private int remaining;    
        
    private final HashSet<TurretInstance> turrets;

    public Asteroid(IHarvestable harvestable, int distance, int remaining) {
        this.harvestable = harvestable;
        this.distance = distance;
        this.remaining = remaining;
        this.turrets = new HashSet<>();
    }
 
    public int getDistance() {
        return distance;
    }

    public IHarvestable getHarvestable() {
        return harvestable;
    }        

    /**
     * Returns, how many units of harvestable are still there.
     * @return 
     */
    public synchronized int getRemainingUnits() {        
        return remaining;
    }
    
    /**
     * Removes some units of harvestable from the asteroid.
     * @param amount how many units we want to remove 
     * @return how many was actually removed;
     */
    public synchronized int removeSomeUnits(int amount) {
        if (remaining == 0) return 0;
        if (amount > remaining) amount = remaining;
        
        remaining = remaining - amount;      
        
        return amount;
    }

    @Override
    public String toString() {
        return harvestable.getName();
    } 
    
    public synchronized void bindTurret(TurretInstance turret) {
        turrets.add(turret);
    }
    
    public synchronized void unbindTurret(TurretInstance turret) {
        turrets.remove(turret);
    }
    
    /**
     * Returns remaining seconds to mine out the asteroid.
     * If there's no bound turrets, returns 0.
     * @param stats
     * @return 
     */
    public synchronized int getRemainingSeconds(ICalculatedStats stats) {
        if (turrets.isEmpty()) return 0;
            
        float m3s = stats.getTurretM3S() * turrets.size();
        float volume = harvestable.getBasicHarvestable().getVolume() * remaining;
        return (int) (volume / m3s);
    }
    
    public synchronized String getRemString(ICalculatedStats stats) {
        if (stats == null || turrets.isEmpty()) return String.valueOf(remaining);        
        
        Period rem = Seconds.seconds(getRemainingSeconds(stats))
                        .toStandardDuration().toPeriod();
        
        return remaining + " ("+minutesAndSeconds.print(rem)+")";
    }
    
    public synchronized boolean isMined() {
        return !turrets.isEmpty();
    }
    
    public synchronized String getTurrets() {
        if (turrets.isEmpty()) return "";
        
        ArrayList<Integer> turrIDs = new ArrayList<>();
        
        for (TurretInstance turret : turrets) {
            turrIDs.add(turret.getId());
        }
        
        Collections.sort(turrIDs);
        String out = "";
        for (Integer turrID : turrIDs) {
            if (out.isEmpty()) {
                out = String.valueOf(turrID);
            } else {
                out = out + ", "+turrID;
            }
        }
        return out;
    }
}
