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
package cy.alavrov.jminerguide.data.booster;

import cy.alavrov.jminerguide.data.character.EVECharacter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Hull of a booster ship
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum BoosterHull {
    GENERIC ("Generic ship", 0, new BonusCalculator() {
        @Override
        public float calculateBoostModifier(EVECharacter pilot, boolean deployed) {
            return 1;
        }
    }, false),
    ORCA ("Orca", 28606, new BonusCalculator() {
        @Override
        public float calculateBoostModifier(EVECharacter pilot, boolean deployed) {
            return 1 + 0.03f * pilot.getSkillLevel(EVECharacter.SKILL_INDUSTRIAL_COMMAND_SHIPS);
        }
    }, false),
    RORQUAL("Rorqual", 28352, new BonusCalculator() {
        @Override
        public float calculateBoostModifier(EVECharacter pilot, boolean deployed) {
            if (deployed) {
                return 1 + 0.2f * pilot.getSkillLevel(EVECharacter.SKILL_CAPITAL_INDUSTRIAL_SHIPS);
            } else {
                return 1;
            }
        }
    }, true);
    
    private final String name; 
    private final int id;
    private final BonusCalculator calculator;
    private final boolean haveDeployedMode;

    public final static Map<Integer, BoosterHull> boosterHullsMap;
    
    static {
        Map<Integer, BoosterHull> hulls = new HashMap<>();
        for (BoosterHull hull : BoosterHull.values()) {
            hulls.put(hull.id, hull);
        }
        boosterHullsMap = Collections.unmodifiableMap(hulls);
    }
    
    private BoosterHull(String name, int id, BonusCalculator calculator, boolean haveDeployedMode) {
        this.name = name;
        this.id = id;
        this.calculator = calculator;
        this.haveDeployedMode = haveDeployedMode;
    }
    
    
    /**
     * Returns hull's name - as you see it ingame.
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
     * Is this hull can has deployed mode?
     * @return 
     */
    public boolean haveDeployedMode() {
        return haveDeployedMode;
    }
    
    /**
     * Calculates hull's boost modifier (NOT in percents, a final modifier, 
     * ready for multiplication), based on pilot's skills.
     * @param pilot
     * @param deployed
     * @return 
     */
    public float calculateBoostModifier(EVECharacter pilot, boolean deployed) {
        return calculator.calculateBoostModifier(pilot, deployed);
    }
    
    @Override
    public String toString() {
        return name;
    }        
    
    private interface BonusCalculator {
        float calculateBoostModifier(EVECharacter pilot, boolean deployed);
    }
}
