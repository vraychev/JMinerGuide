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
package cy.alavrov.jminerguide.data;

/**
 * Stats, calculated for a ship, based on it's hull, modules, pilot and 
 * whatever else.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class SimpleCalculatedStats implements ICalculatedStats{
    /**
     * Yield of a turret, in m3.
     */
    private final float turretYield;
    
    /**
     * Yield of all turrets, combined, in m3.
     */
    private final float combinedTurretYield;
    
    /**
     * Cycle of a turret, in seconds.
     */
    private final float turretCycle;
    
    /**
     * Individual turret yield per second, in m3/sec.
     */
    private final float turretM3S;
    
    /**
     * Total turret yield per second, in m3/sec.
     */
    private final float combinedTurretM3S;
    
    /**
     * Yield of a drone, in m3.
     */
    private final float droneYield;
    
    /**
     * Yield of all drones, combined, in m3.
     */
    private final float combinedDroneYield;
    
    /**
     * Cycle of a drone, in seconds.
     */
    private final float droneCycle;
    
    /**
     * Total drone yield per second, in m3/sec.
     */
    private final float droneM3S;
    
    /**
     * Total ship yield, in m3/hour.
     */
    private final float totalM3H;
    
    /**
     * Ship's optimal, in metres.
     */
    private final int optimal;
    
    /**
     * Ship's ore (or cargo) hold, in m3.
     */
    private final int oreHold;
    
    /**
     * How long it takes for ore hold to fill, in seconds.
     */
    private final int secsForOreHold;
    
    /**
     * Link bonus to the cycle time, in percents.
     */
    private final float linkCycleBonus;
    
    /**
     * Link bonus to the mining turret optimal, in percents.
     */
    private final float linkOptimalBonus;

    public SimpleCalculatedStats(float turretYield, int turrets, float turretCycle, int orehold, int optimal) {
        
        this.turretYield = turretYield;
        this.combinedTurretYield = turretYield * turrets;
        this.turretCycle = turretCycle;
        this.turretM3S = turretYield/turretCycle;
        this.combinedTurretM3S = combinedTurretYield/turretCycle;
        
        // tba as needed
        this.droneYield = 0;
        this.combinedDroneYield = 0;
        this.droneCycle = 0;
        this.droneM3S = 0;
        
        float totalM3S = combinedTurretM3S + droneM3S;
        this.totalM3H = totalM3S * 60 * 60;
        this.optimal = optimal;
        this.oreHold = orehold;
        this.secsForOreHold = (int) (oreHold / totalM3S);        
        
        this.linkCycleBonus = 0;
        this.linkOptimalBonus = 0;
    }
    
    
    
    /**
     * Yield of a turret, in m3.
     * @return the turretYield
     */
    @Override
    public float getTurretYield() {
        return turretYield;
    }

    /**
     * Yield of all turrets, combined, in m3.
     * @return the combinedTurretYield
     */
    @Override
    public float getCombinedTurretYield() {
        return combinedTurretYield;
    }

    /**
     * Cycle of a turret, in seconds.
     * @return the turretCycle
     */
    @Override
    public float getTurretCycle() {
        return turretCycle;
    }

    /**
     * Total individual yield per second, in m3/sec.
     * @return the turretM3S
     */
    @Override
    public float getTurretM3S() {
        return turretM3S;
    }   
    
    /**
     * Total turret yield per second, in m3/sec.
     * @return the combinedTurretM3S
     */
    @Override
    public float getCombinedTurretM3S() {
        return combinedTurretM3S;
    }

    /**
     * Yield of a drone, in m3.
     * @return the droneYield
     */
    @Override
    public float getDroneYield() {
        return droneYield;
    }

    /**
     * Yield of all drones, combined, in m3.
     * @return the combinedDroneYield
     */
    @Override
    public float getCombinedDroneYield() {
        return combinedDroneYield;
    }

    /**
     * Cycle of a drone, in seconds.
     * @return the droneCycle
     */
    @Override
    public float getDroneCycle() {
        return droneCycle;
    }

    /**
     * Total drone yield per second, in m3/sec.
     * @return the droneM3S
     */
    @Override
    public float getDroneM3S() {
        return droneM3S;
    }

    /**
     * Total ship yield, in m3/hour.
     * @return the totalM3H
     */
    @Override
    public float getTotalM3H() {
        return totalM3H;
    }

    /**
     * Ship's optimal, in metres.
     * @return the optimal
     */
    @Override
    public int getOptimal() {
        return optimal;
    }

    /**
     * Ship's ore (or cargo) hold, in m3.
     * @return the cargo
     */
    @Override
    public int getOreHold() {
        return oreHold;
    }

    /**
     * How long it takes for ore hold to fill, in seconds.
     * @return the secsForCargo
     */
    @Override
    public int getSecsForOreHold() {
        return secsForOreHold;
    }

    /**
     * Link bonus to the cycle time, in percents.
     * @return the linkCycleBonus
     */
    @Override
    public float getLinkCycleBonus() {
        return linkCycleBonus;
    }

    /**
     * Link bonus to the mining turret optimal, in percents.
     * @return the linkOptimalBonus
     */
    @Override
    public float getLinkOptimalBonus() {
        return linkOptimalBonus;
    }
}
