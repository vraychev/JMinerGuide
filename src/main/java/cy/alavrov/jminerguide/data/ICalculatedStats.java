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
 * Calculated stats for a ship.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public interface ICalculatedStats {
    /**
     * Yield of a turret, in m3.
     * @return the turretYield
     */
    public float getTurretYield();

    /**
     * Yield of all turrets, combined, in m3.
     * @return the combinedTurretYield
     */
    public float getCombinedTurretYield();

    /**
     * Cycle of a turret, in seconds.
     * @return the turretCycle
     */
    public float getTurretCycle();

    /**
     * Total individual yield per second, in m3/sec.
     * @return the turretM3S
     */
    public float getTurretM3S();  
    
    /**
     * Total turret yield per second, in m3/sec.
     * @return the combinedTurretM3S
     */
    public float getCombinedTurretM3S();

    /**
     * Yield of a drone, in m3.
     * @return the droneYield
     */
    public float getDroneYield();

    /**
     * Yield of all drones, combined, in m3.
     * @return the combinedDroneYield
     */
    public float getCombinedDroneYield();

    /**
     * Cycle of a drone, in seconds.
     * @return the droneCycle
     */
    public float getDroneCycle();

    /**
     * Total drone yield per second, in m3/sec.
     * @return the droneM3S
     */
    public float getDroneM3S();

    /**
     * Total ship yield, in m3/hour.
     * @return the totalM3H
     */
    public float getTotalM3H();

    /**
     * Ship's optimal, in metres.
     * @return the optimal
     */
    public int getOptimal();

    /**
     * Ship's ore (or cargo) hold, in m3.
     * @return the cargo
     */
    public int getOreHold();

    /**
     * How long it takes for ore hold to fill, in seconds.
     * @return the secsForCargo
     */
    public int getSecsForOreHold();

    /**
     * Link bonus to the cycle time, in percents.
     * @return the linkCycleBonus
     */
    public float getLinkCycleBonus();

    /**
     * Link bonus to the mining turret optimal, in percents.
     * @return the linkOptimalBonus
     */
    public float getLinkOptimalBonus();
}
