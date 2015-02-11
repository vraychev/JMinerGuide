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
import cy.alavrov.jminerguide.data.harvestable.Asteroid;

/**
 * Instance of a virtual turret, mining something.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class TurretInstance {
    private final int id;
    
    private Asteroid asteroid = null;    
    private float minedAmount = 0;

    public TurretInstance(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }        
    
    /**
     * Mines something from the bound asteroid, according to stats.
     * If there's no asteroid, or the asteroid is empty, does nothing.
     * @param stats
     * @param remainingOreHold how many free space in the hold do we actually have?
     * @return 
     * @throws FullOreHoldException 
     * @throws AsteroidMinedException 
     */
    public synchronized int mineSome(CalculatedStats stats, float remainingOreHold) throws FullOreHoldException, AsteroidMinedException {
        if (asteroid == null) return 0;
        if (asteroid.getRemainingUnits() <= 0) {
            unbindAsteroid();
            throw new AsteroidMinedException(0, this);
        }
        if (remainingOreHold <= 0) {
            unbindAsteroid();
            throw new FullOreHoldException(0);
        }
        
        int ret = 0;
        
        float m3s = stats.getTurretM3S();
        if (m3s >  remainingOreHold) {
            // we shouldn't mine more, than free space in the hold.
            m3s =  remainingOreHold;
        }
        
        minedAmount = minedAmount + m3s;
        float unitVolume = asteroid.getHarvestable().getBasicHarvestable().getVolume();
        // did we mined enough to have full unit?
        if (m3s > unitVolume) {
            // we'll removed only fully mined units, as you obviously can't mine 1/2 of an unit.
            int toRemove = (int) (m3s / unitVolume);
            // as we can try to mine more, than there are in reality, we do this.
            ret = asteroid.removeSomeUnits(toRemove);
                        
            if (asteroid.getRemainingUnits() > 0) {
                // mined amount, that isn't enough for full unit will be left for the next cycle.
                minedAmount = minedAmount - ret * unitVolume;
            } else {
                // if we mined out the asteroid, we may discard leftovers, because there's none actually.                
                minedAmount = 0;
                // and unbind the asteroid, because, well.
                unbindAsteroid();
                
                throw new AsteroidMinedException(ret, this);
            }
        } else {
            if (remainingOreHold < unitVolume) { // we can't fit one more unit!
                unbindAsteroid();
                throw new FullOreHoldException(0);
            }
        }
        
        return ret;
    }
    
    public synchronized void unbindAsteroid() {
        if (asteroid == null) return;
        asteroid.unbindTurret(this);
        asteroid = null;
    }
    
    public synchronized void bindAsteroid(Asteroid asteroid) {
        if (asteroid == null || this.asteroid != null 
                || asteroid.getRemainingUnits() == 0) return;
        
        asteroid.bindTurret(this);
        this.asteroid = asteroid;
    }
    
    public synchronized boolean isMining() {
        return asteroid != null;
    }
    
    /**
     * How many seconds remains to mine out bound asteroid?
     * Takes into account all bound turrets, by the way.
     * @param stats
     * @return 
     */
    public synchronized int getRemainingSeconds(CalculatedStats stats) {
        if (asteroid == null) return 0;
        return asteroid.getRemainingSeconds(stats);
    }
}
