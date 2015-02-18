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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic harvestable types.
 * Basically, volume and type.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum BasicHarvestable {
    VELDSPAR    (0.1f,  HarvestableType.ORE),
    SCORDITE    (0.15f,  HarvestableType.ORE),
    PYROXERES   (0.3f,  HarvestableType.ORE),
    PLAGIOCLASE (0.35f,  HarvestableType.ORE),
    OMBER       (0.6f,  HarvestableType.ORE),
    KERNITE     (1.2f,  HarvestableType.ORE),
    JASPET      (2,  HarvestableType.ORE),
    HEMORPHITE  (3,  HarvestableType.ORE),
    HEDBERGITE  (3,  HarvestableType.ORE),
    GNEISS      (5,  HarvestableType.ORE),
    DARKOCHRE   (8,  HarvestableType.ORE),
    CROKITE     (16,  HarvestableType.ORE),
    SPODUMAIN   (16,  HarvestableType.ORE),
    BISTOT      (16,  HarvestableType.ORE),
    ARKONOR     (16,  HarvestableType.ORE),
    
    MERCOXIT    (40,  HarvestableType.MERCOXIT),
    
    BLUEICE    (1000,  HarvestableType.ICE),
    CLEARICICLE (1000,  HarvestableType.ICE),
    GLACIALMASS  (1000,  HarvestableType.ICE),
    WHITEGLAZE  (1000,  HarvestableType.ICE),
    DARKGLITTER (1000,  HarvestableType.ICE),
    GELIDUS (1000,  HarvestableType.ICE),
    GLARECRUST (1000,  HarvestableType.ICE),
    KRYSTALLOS (1000,  HarvestableType.ICE),
    
    FULLERITEC28    (2,  HarvestableType.GAS),
    FULLERITEC32    (5,  HarvestableType.GAS),
    FULLERITEC50    (1,  HarvestableType.GAS),
    FULLERITEC60    (1,  HarvestableType.GAS),
    FULLERITEC70    (1,  HarvestableType.GAS),
    FULLERITEC72    (2,  HarvestableType.GAS),
    FULLERITEC84    (2,  HarvestableType.GAS),
    FULLERITEC320   (5,  HarvestableType.GAS),
    FULLERITEC540   (10,  HarvestableType.GAS),
    CYTOSEROCIN     (10,  HarvestableType.GAS),
    MYKOSEROCIN     (10,  HarvestableType.GAS);
    
    private final float volume;
    private final HarvestableType type;
    
    public final static Map<String, BasicHarvestable> nameMap;
    
    static {
        HashMap<String, BasicHarvestable> out = new HashMap<>();
        for (BasicHarvestable hv : values()) {
            out.put(hv.name(), hv);
        }
        
        nameMap = Collections.unmodifiableMap(out);
    }

    private BasicHarvestable(float volume, HarvestableType type) {
        this.volume = volume;
        this.type = type;
    }

    /**
     * Volume of one unit of harvestable, in m3.
     * @return 
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Type of a harvestable (ore, mercoxit, gas, ice)
     * @return 
     */
    public HarvestableType getType() {
        return type;
    }        
}
