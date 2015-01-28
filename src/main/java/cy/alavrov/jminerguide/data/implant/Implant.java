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
package cy.alavrov.jminerguide.data.implant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum Implant {
    NOTHING("- nothing -", 0, 0, 0, 0, 0),
    MICHI("Michi's Excavation Augmentor", 20700, 7, 5, 0, 0),
    GH801("Eifyr and Co. 'Alchemist' Gas Harvesting GH-801", 27240, 8, 0, 1, 0),
    GH803("Eifyr and Co. 'Alchemist' Gas Harvesting GH-803", 27238, 8, 0, 3, 0),
    GH805("Eifyr and Co. 'Alchemist' Gas Harvesting GH-805", 27239, 8, 0, 5, 0),
    MX1001("Inherent Implants 'Highwall' Mining MX-1001", 27102, 10, 1, 0, 0),
    MX1003("Inherent Implants 'Highwall' Mining MX-1003", 22534, 10, 3, 0, 0),
    MX1005("Inherent Implants 'Highwall' Mining MX-1005", 22535, 10, 5, 0, 0),
    IH1001("Inherent Implants 'Yeti' Ice Harvesting IH-1001", 27103, 10, 0, 0, 1),
    IH1003("Inherent Implants 'Yeti' Ice Harvesting IH-1003", 22570, 10, 0, 0, 3),
    IH1005("Inherent Implants 'Yeti' Ice Harvesting IH-1005", 22571, 10, 0, 0, 5),
    MFMINDLINK("Mining Foreman Mindlink", 20700, 10, 0, 0, 0),;
    
    public final static Implant[] slot7Imps = {NOTHING, MICHI};
    public final static Implant[] slot8Imps = {NOTHING, GH801, GH803, GH805};
    public final static Implant[] slot10Imps = {NOTHING, MX1001, MX1003, MX1005, IH1001, IH1003, IH1005, MFMINDLINK};
    
    public final static Map<Integer, Implant> implants;
    
    static {
        Map<Integer, Implant> imps = new HashMap<>();
        for (Implant imp : Implant.values()) {
            imps.put(imp.id, imp);
        }
        implants = Collections.unmodifiableMap(imps);
    }
    
    private final String name;
    private final int id;
    private final int slot;
    private final int miningYieldBonus;
    private final int gasCycleBonus;
    private final int iceCycleBonus;
    
    Implant(String name, int id, int slot, int miningYieldBonus,
            int gasCycleBonus, int iceCycleBonus) {
        this.name = name;
        this.id = id;
        this.slot = slot;
        this.miningYieldBonus = miningYieldBonus;
        this.gasCycleBonus = gasCycleBonus;
        this.iceCycleBonus = iceCycleBonus;
    }
    
    /**
     * Returns implant name - just as you see it ingame.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns internal implant id, used, for example, in XML from API calls.
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * Returns a slot the implant is using.
     * @return 
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * Returns a bonus to mining yield, in percents.
     * @return 
     */
    public int getMiningYieldBonus() {
        return miningYieldBonus;
    }
    
    /**
     * Returns a bonus to gas harvester cycle time, in percents.
     * @return 
     */
    public int getGasCycleBonus() {
        return gasCycleBonus;
    }
    
    /**
     * Returns a bonus to ice harvester cycle time, in percents.
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
