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
package cy.alavrov.jminerguide.data.api.ship;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mining rigs.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum Rig {
    NOTHING ("- nothing -", 0, 0, 0, 0, 0, false),
    DRONEAUGMENTORI ("Drone Mining Augmentor I", 
            32043, 10, 0, 0, 100, false),
    DRONEAUGMENTORII ("Drone Mining Augmentor II", 
            32047, 15, 0, 0, 150, false),
    ICEACCELERATOR ("Medium Ice Harvester Accelerator I", 
            32819, 0, 0, 12, 250, true),
    MERCOXITOPTIMIZATOR ("Medium Mercoxit Mining Crystal Optimization I", 
            32817, 0, 16, 0, 250, true);
    
    private final String name; 
    private final int id;   
    private final int droneYieldBonus;
    private final int mercoxitYieldBonus;
    private final int iceCycleBonus;
    private final int calibrationCost;
    private final boolean strictlyMedium;
    
    public final static Rig[] nonMediumRigArr = {
        NOTHING, DRONEAUGMENTORI, DRONEAUGMENTORII
    };
    
    public final static Map<Integer, Rig> rigsMap;
    
    static {
        Map<Integer, Rig> rigs = new HashMap<>();
        for (Rig rig : Rig.values()) {
            rigs.put(rig.id, rig);
        }
        rigsMap = Collections.unmodifiableMap(rigs);
    }

    private Rig(String name, int id, int droneYieldBonus, int mercoxitYieldBonus, 
            int iceCycleBonus, int calibrationCost, boolean strictlyMedium) {
        this.name = name;
        this.id = id;
        this.droneYieldBonus = droneYieldBonus;
        this.mercoxitYieldBonus = mercoxitYieldBonus;
        this.iceCycleBonus = iceCycleBonus;
        this.calibrationCost = calibrationCost;
        this.strictlyMedium = strictlyMedium;
    }
    
     /**
     * Returns rig name - just as you see it ingame.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns internal rig's id.
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * Returns drone yield bonus, in percents.
     * @return 
     */
    public int getDroneYieldBonus() {
        return droneYieldBonus;
    }
    
    /**
     * Returns mercoxit yield bonus, in percents.
     * @return 
     */
    public int getMercoxitYieldBonus() {
        return mercoxitYieldBonus;
    }
    
    /**
     * Returns ice cycle bonus, in percents.
     * @return 
     */
    public int getIceCycleBonus() {
        return iceCycleBonus;
    }
    
    /**
     * Returns rig calibration cost.
     * @return 
     */
    public int getCalibrationCost() {
        return calibrationCost;
    }
    
    /**
     * True, if the rig have only medium-sized version.
     * @return 
     */
    public boolean isStrictlyMedium() {
        return strictlyMedium;
    }
    
    @Override
    public String toString() {
        return name + ((calibrationCost > 0) ? " ("+calibrationCost+")" : "");
    }
}

