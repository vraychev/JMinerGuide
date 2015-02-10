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

/**
 * Something with harvestable inside. 
 * I.e. asteroid, ice roid or gas cloud.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class Asteroid {
    private final IHarvestable harvestable;
    private final int distance;
    private int remaining;    

    public Asteroid(IHarvestable harvestable, int distance, int remaining) {
        this.harvestable = harvestable;
        this.distance = distance;
        this.remaining = remaining;
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
     * @param amount 
     */
    public synchronized void removeSomeUnits(int amount) {
        remaining = remaining - amount;
        if (remaining < 0) remaining = 0;
    }

    @Override
    public String toString() {
        return harvestable.getName();
    }        
}
