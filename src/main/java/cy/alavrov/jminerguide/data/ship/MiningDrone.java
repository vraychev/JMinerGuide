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
package cy.alavrov.jminerguide.data.ship;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mining dronesMap.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum MiningDrone {
    // NOTHING have bandwidth of 5 no avoid division to null.
    
    NOTHING ("- nothing -", 0, 0, 0, 5),
    CIVILIAN ("Civilian Mining Drone", 
            1202, 13, 60, 5),
    MININGI ("Mining Drone I", 
            10246, 20, 60, 5),
    MININGII ("Mining Drone II", 
            10250, 33, 60, 5),
    HARVESTER ("Harvester Mining Drone", 
            3218, 40, 60, 10);
    
    private final String name; 
    private final int id;
    private final int baseYield;
    private final int cycleDuration;
    private final int bandwidth;
    
    
    public final static Map<Integer, MiningDrone> dronesMap;
    
    static {
        Map<Integer, MiningDrone> drons = new HashMap<>();
        for (MiningDrone drone : MiningDrone.values()) {
            drons.put(drone.id, drone);
        }
        dronesMap = Collections.unmodifiableMap(drons);
    }

    private MiningDrone(String name, int id, int baseYield, int cycleDuration, int bandwidth) {
        this.name = name;
        this.id = id;
        this.baseYield = baseYield;
        this.cycleDuration = cycleDuration;
        this.bandwidth = bandwidth;
    }
    
    /**
     * Returns drone's name - just as you see it ingame.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns internal drone's id.
     * @return 
     */
    public int getID() {
        return id;
    }
    
    
    /**
     * Returns base yield of a drone, in cubic metres.
     * @return 
     */
    public int getBaseYield() {
        return baseYield;
    }
    
    /**
     * Returns base cycle duration of a drone, in seconds.
     * @return 
     */
    public int getCycleDuration() {
        return cycleDuration;
    }
    
    /**
     * Returns bandwidth, needed to use a drone.
     * @return 
     */
    public int getBandwidth() {
        return bandwidth;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
