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
 * Mining turret.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum Turret {
    CIVILIANMINER("Civilian Miner", 
            3651, TurretType.MININGLASER, false, OreType.ORE, 30, 60, 10000),
    MINERI("Miner I", 
            483, TurretType.MININGLASER, false, OreType.ORE, 40, 60, 10000),
    GAUSSIAN("EP-S Gaussian Scoped Mining Laser", 
            5239, TurretType.MININGLASER, false, OreType.ORE, 50, 60, 16000),
    PARTICLEBORE("Particle Bore Compact Mining Laser", 
            5245, TurretType.MININGLASER, false, OreType.ORE, 50, 60, 11000),
    MINERII("Miner II", 
            482, TurretType.MININGLASER, false, OreType.ORE, 60, 60, 12000),    
    SINGLEDIODE("Single Diode Basic Mining Laser", 
            5233, TurretType.MININGLASER, false, OreType.ORE, 25, 60, 11000),
    GALLENTEMINING("Gallente Mining Laser", 
            21841, TurretType.MININGLASER, false, OreType.ORE, 45, 60, 10000),
    OREMINER("ORE Miner", 
            28750, TurretType.MININGLASER, false, OreType.ORE, 65, 60, 16000),
        
    
    DEEPCORE("Deep Core Mining Laser I", 
            12108, TurretType.MININGLASER, false, OreType.MERCOXIT, 40, 60, 5000),
    DEEPCOREII("Modulated Deep Core Miner II", 
            18068, TurretType.MININGLASER, true, OreType.MERCOXIT, 120, 180, 10000),
    OREDEEPCORE("ORE Deep Core Mining Laser", 
            28748, TurretType.MININGLASER, false, OreType.MERCOXIT, 40, 60, 7000),
    
    
    GASHARVESTERI("Gas Cloud Harvester I", 
            25266, TurretType.GASHARVESTER, false, OreType.GAS, 10, 30, 1500),
    CROPGASHARVESTER("'Crop' Gas Cloud Harvester", 
            25540, TurretType.GASHARVESTER, false, OreType.GAS, 10, 30, 1500),
    PLOWGASHARVESTER("'Plow' Gas Cloud Harvester", 
            25542, TurretType.GASHARVESTER, false, OreType.GAS, 10, 30, 1500),
    GASHARVESTERII("Gas Cloud Harvester II", 
            25812, TurretType.GASHARVESTER, false, OreType.GAS, 20, 40, 1500),
    SYNDICATEGASHARVESTER("Syndicate Gas Cloud Harvester", 
            28788, TurretType.GASHARVESTER, false, OreType.GAS, 10, 30, 1500),
    
    
    STRIPMINERI("Strip Miner I", 
            17482, TurretType.STRIPMINER, false, OreType.ORE, 540, 180, 15000),
    STRIPMINERII("Modulated Strip Miner II", 
            17912, TurretType.STRIPMINER, true, OreType.ORE, 360, 180, 15000),
    DEEPCORESTRIPMINER("Modulated Deep Core Strip Miner II", 
            24305, TurretType.STRIPMINER, true, OreType.MERCOXIT, 250, 180, 15000),
    ORESTRIPMINER("ORE Strip Miner", 
            28754, TurretType.STRIPMINER, false, OreType.ORE, 540, 180, 17),
    
    
    ICEHARVESTERI("Ice Harvester I", 
            16278, TurretType.ICEHARVESTER, false, OreType.ICE, 1000, 300, 10000),
    ICEHARVESTERII("Ice Harvester II", 
            22229, TurretType.ICEHARVESTER, false, OreType.ICE, 1000, 250, 10000),
    OREICEHARVESTER("ORE Ice Harvester", 
            28752, TurretType.ICEHARVESTER, false, OreType.ICE, 1000, 250, 12000);
    
    public final static Turret[] smallTurrets = {
        CIVILIANMINER, MINERI, GAUSSIAN,
        PARTICLEBORE, MINERII, SINGLEDIODE, GALLENTEMINING, OREMINER, 
        DEEPCORE, DEEPCOREII, OREDEEPCORE,
        GASHARVESTERI, CROPGASHARVESTER, PLOWGASHARVESTER, GASHARVESTERII, 
        SYNDICATEGASHARVESTER
    };
    
    public final static Turret[] bigTurrets = {
        STRIPMINERI, STRIPMINERII, DEEPCORESTRIPMINER, ORESTRIPMINER,
        ICEHARVESTERI, ICEHARVESTERII, OREICEHARVESTER
    };
    
    public final static Map<Integer, Turret> turrets;
    
    static {
        Map<Integer, Turret> turrs = new HashMap<>();
        for (Turret turr : Turret.values()) {
            turrs.put(turr.id, turr);
        }
        turrets = Collections.unmodifiableMap(turrs);
    }
    
    private final String name; 
    private final int id;
    private final TurretType type;
    private final boolean usesCrystals;
    private final OreType oreType;
    private final int baseYield;
    private final int cycleDuration;
    private final int optimalRange;
    
    
    Turret(String name, int id, TurretType type, boolean usesCrystals,
            OreType oreType, int baseYield, int cycleDuration, int optimalRange) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.usesCrystals = usesCrystals;
        this.oreType = oreType;
        this.baseYield = baseYield;
        this.cycleDuration = cycleDuration;
        this.optimalRange = optimalRange;
    }
    
    /**
     * Returns turret name - just as you see it ingame.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns internal turret's id.
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * Returns turret's type (mining laser, gas harvester, strip miner, ice harvester).
     * @return 
     */
    public TurretType getTurretType() {
        return type;
    }
    
    /**
     * Returns true, if the turret can use a mining crystal.
     * @return 
     */
    public boolean isUsingCrystals() {
        return usesCrystals;
    }
    
    /**
     * Returns ore type the turret is harvesting (basic ores, ores+mercoxit, ice, gas)
     * @return 
     */
    public OreType getOreType() {
        return oreType;
    }
    
    /**
     * Returns base yield of a turret, in cubic metres.
     * @return 
     */
    public int getBaseYield() {
        return baseYield;
    }
    
    /**
     * Returns base cycle duration of a turret.
     * @return 
     */
    public int getCycleDuration() {
        return cycleDuration;
    }
    
    /**
     * Returns base optimal range of a turret.
     * @return 
     */
    public int getOptimalRange() {
        return optimalRange;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
