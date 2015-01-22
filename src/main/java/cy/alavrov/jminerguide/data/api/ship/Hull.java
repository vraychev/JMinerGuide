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

import cy.alavrov.jminerguide.data.character.EVECharacter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Ship hulls.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum Hull {
    VENTURE("Venture", 
            32880, 2, false, 1, 3, false, 5000, 100, 100, 0, 10, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float yieldmod = 1f + 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_FRIGATE);
            float cyclemod = 1f - 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_FRIGATE);
            return new BonusCalculationResult(yieldmod, cyclemod, 1, 1, 1);
        }
    }),
    PROSPECT("Prospect", 
            33697, 2, false, 4, 2, false, 10000, 100, 100, 0, 0, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float yieldmod = (1f + 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_FRIGATE))
                    *(1f + 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_EXPEDITION_FRIGATES));
            float cyclemod = 1f - 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_FRIGATE);
            return new BonusCalculationResult(yieldmod, cyclemod, 1, 1, 1);
        }
    }),
    PROCURER("Procurer", 
            17480, 1, true, 2, 3, true, 12000, 150, 0, 60, 25, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float durmod = 1f - 0.02f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE);
            return new BonusCalculationResult(1, 1, durmod, 1, 1);
        }
    }),
    RETRIEVER("Retriever", 
            17478, 2, true, 3, 3, true, 22000, 25, 0, 20, 25, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float durmod = 1f - 0.02f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE);
            float cargomod = 1f + 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE);
            return new BonusCalculationResult(1, 1, durmod, cargomod, 1);
        }
    }),
    COVETOR("Covetor", 
            17476, 3, true, 2, 3, true, 7000, 0, 0, 0, 50, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float durmod = 1f - 0.04f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE);
            float distmod = 1f + 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE);
            return new BonusCalculationResult(1, 1, durmod, 1, distmod);
        }
    }),
    SKIFF("Skiff", 
            22546, 1, true, 3, 2, true, 15000, 150, 0, 60, 50, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float durmod = (1f - 0.02f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE))*
                    (1f - 0.02f * pilot.getSkillLevel(EVECharacter.SKILL_EXHUMERS));
            return new BonusCalculationResult(1, 1, durmod, 1, 1);
        }
    }),
    MACKINAW("Mackinaw", 
            22548, 2, true, 3, 2, true, 28000, 25, 0, 20, 50, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float durmod = (1f - 0.02f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE))*
                    (1f - 0.02f * pilot.getSkillLevel(EVECharacter.SKILL_EXHUMERS));
            float cargomod = 1f + 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE);
            return new BonusCalculationResult(1, 1, durmod, cargomod, 1);
        }
    }),
    HULK("Hulk", 
            22544, 3, true, 2, 2, true, 8500, 0, 0, 0, 50, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            float durmod = (1f - 0.04f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE))*
                    (1f - 0.03f * pilot.getSkillLevel(EVECharacter.SKILL_EXHUMERS));
            float distmod = 1f + 0.05f * pilot.getSkillLevel(EVECharacter.SKILL_MINING_BARGE);
            return new BonusCalculationResult(1, 1, durmod, 1, distmod);
        }
    }),
    GENERIC("Generic hull", 
            1, 8, false, 8, 3, false, 500, 0, 0, 0, 50, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            return new BonusCalculationResult(1, 1, 1, 1, 1);
        }
    }),
    GENERICMEDIUM("Generic medium hull", 
            2, 8, false, 8, 3, true, 500, 0, 0, 0, 50, new BonusCalculator() {
        @Override
        public BonusCalculationResult calculate(EVECharacter pilot) {
            return new BonusCalculationResult(1, 1, 1, 1, 1);
        }
    });        
    
    public final static Map<Integer, Hull> hullsMap;
    
    static {
        Map<Integer, Hull> hulls = new HashMap<>();
        for (Hull hull : Hull.values()) {
            hulls.put(hull.id, hull);
        }
        hullsMap = Collections.unmodifiableMap(hulls);
    }
    
    private final String name;
    private final int id;
    private final int maxTurrets;
    private final boolean stripMiners;
    private final int maxUpgrades;
    private final int rigSlots;
    private final boolean mediumHull;
    private final int oreHold;
    private final int roleMiningYieldBonus;
    private final int roleGasYieldBonus;
    private final int roleIceCycleBonus;
    private final int droneBandwidth;
    private final BonusCalculator calculator;

    private Hull(String name, int id, int maxTurrets, boolean stripMiners, 
            int maxUpgrades, int rigSlots, boolean mediumHull, int oreHold, 
            int roleMiningYieldBonus, int roleGasYieldBonus, int roleIceCycleBonus, 
            int droneBandwidth, BonusCalculator calculator) {
        this.name = name;
        this.id = id;
        this.maxTurrets = maxTurrets;
        this.stripMiners = stripMiners;
        this.maxUpgrades = maxUpgrades;
        this.rigSlots = rigSlots;
        this.mediumHull = mediumHull;
        this.oreHold = oreHold;
        this.roleMiningYieldBonus = roleMiningYieldBonus;
        this.roleGasYieldBonus = roleGasYieldBonus;
        this.roleIceCycleBonus = roleIceCycleBonus;
        this.droneBandwidth = droneBandwidth;
        this.calculator = calculator;
    }
    
    /**
     * Returns hull name - just as you see it ingame.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns internal hull's id.
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * How many mining turrets can be fitted on?
     * @return 
     */
    public int getMaxTurrets() {
        return maxTurrets;
    }    
    
    /**
     * Returns true, if strip miners and ice harvesters can be fitted on.
     * @return 
     */
    public boolean isUsingStripMiners() {
        return stripMiners;
    }    
    
    /**
     * How many harvesting upgrades can be fitted on?     
     * @return 
     */
    public int getMaxUpgrades() {
        return maxUpgrades;
    }   
    
    /**
     * How many rig slots are there?     
     * @return 
     */
    public int getRigSlots() {
        return rigSlots;
    }       
    
    /**
     * Returns true, if hull size is medium.
     * Availability of some rigs depend on this.
     * @return 
     */
    public boolean isMediumHull() {
        return mediumHull;
    }    
    
    /**
     * Returns ore hold volume, in cubic metres.
     * @return 
     */
    public int getOreHold() {
        return oreHold;
    }    
    
    /**
     * Returns mining yield bonus, granted by hull's role, in percents.
     * @return 
     */
    public int getRoleMiningYieldBonus() {
        return roleMiningYieldBonus;
    } 
    
    /**
     * Returns gas yield bonus, granted by hull's role, in percents.
     * @return 
     */
    public int getRoleGasYieldBonus() {
        return roleGasYieldBonus;
    }
    
    /**
     * Returns various bonus modificators, granted by relevant ship skills on a pilot.
     * @param pilot
     * @return 
     */
    public BonusCalculationResult calculateSkillBonusModificators(EVECharacter pilot) {
        return calculator.calculate(pilot);
    }
    
    /**
     * Returns ice harvester cycle bonus, granted by hull's role, in percents.
     * @return 
     */
    public int getRoleIceCycleBonus() {
        return roleIceCycleBonus;
    }
    
    /**
     * Returns ice harvester cycle bonus, granted by hull's role, in percents.
     * @return 
     */
    public int getDroneBandwidth() {
        return droneBandwidth;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    private interface BonusCalculator {
        BonusCalculationResult calculate(EVECharacter pilot);
    }
    
    public static class BonusCalculationResult {
        public final float miningYieldMod;
        public final float gasCycleMod;
        public final float stripCycleMod;
        public final float oreHoldMod;
        public final float stripOptimalMod;

        protected BonusCalculationResult(float miningYieldMod, float gasCycleMod,
                float stripCycleMod, float oreHoldMod, float stripOptimalMod) {
            this.miningYieldMod = miningYieldMod;
            this.gasCycleMod = gasCycleMod;
            this.stripCycleMod = stripCycleMod;
            this.oreHoldMod = oreHoldMod;
            this.stripOptimalMod = stripOptimalMod;
        }
        
        
    }
}
