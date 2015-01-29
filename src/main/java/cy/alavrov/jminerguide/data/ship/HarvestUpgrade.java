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
 * Harvest upgrade.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum HarvestUpgrade {
    NOTHING("- nothing -", 0, 0, 0),
    
    MININGI("Mining Laser Upgrade I", 
            22542, 5, 0),
    ELARA("Elara Restrained Mining Laser Upgrade", 
            22611, 8, 0),
    MININGII("Mining Laser Upgrade II", 
            28576, 9, 0),
    AOEDE("'Aoede' Mining Laser Upgrade", 
            22615, 10, 0),
    CARPO("'Carpo' Mining Laser Upgrade", 
            22613, 9, 0),
    
    ICEI("Ice Harvester Upgrade I", 
            22576, 0, 5),
    FRIGORIS("Frigoris Restrained Ice Harvester Upgrade", 
            22619, 0, 8),
    ICEII("Ice Harvester Upgrade II", 
            28578, 0, 9),
    ANGUIS("'Anguis' Ice Harvester Upgrade", 
            22621, 0, 9),
    INGENII("'Ingenii' Ice Harvester Upgrade", 
            22623, 0, 10);
    
    private final String name;
    private final int id;
    private final int oreYieldBonus;
    private final int iceCycleBonus;

    public final static Map<Integer, HarvestUpgrade> upgradesMap;
    
    static {
        Map<Integer, HarvestUpgrade> upgrds = new HashMap<>();
        for (HarvestUpgrade upgrade : HarvestUpgrade.values()) {
            upgrds.put(upgrade.id, upgrade);
        }
        upgradesMap = Collections.unmodifiableMap(upgrds);
    }
    
    private HarvestUpgrade(String name, int id, int oreYieldBonus, int iceCycleBonus) {
        this.name = name;
        this.id = id;
        this.oreYieldBonus = oreYieldBonus;
        this.iceCycleBonus = iceCycleBonus;
    }
    
    
    /**
     * Returns upgrade's name - just as you see it ingame.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns internal upgrade's id.
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * Returns ore yield bonus, in percents.
     * @return 
     */
    public int getOreYieldBonus() {
        return oreYieldBonus;
    }
    
    /**
     * Returns ice cycle bonus, in percents.
     * @return 
     */
    public int getIceCycleBonus() {
        return iceCycleBonus;
    }
    
    @Override
    public String toString() {
        return name;
    }    
}
