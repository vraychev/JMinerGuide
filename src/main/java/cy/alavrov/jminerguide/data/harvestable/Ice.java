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

/**
 * Ices.
 * Non-mission ones, with high-yield variants.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum Ice implements IHarvestable{
    CLEARICILE("Clear Icicle", BasicHarvestable.CLEARICICLE, false, 
            50, 25, 1, 300, 0, 0, 0),
    CLEARICILE_E("Enriched Clear Icicle", BasicHarvestable.CLEARICICLE, true, 
            75, 40, 1, 350, 0, 0, 0),
    
    WHITEGLAZE("White Glaze", BasicHarvestable.WHITEGLAZE, false, 
            50, 25, 1, 0, 300, 0, 0),
    WHITEGLAZE_P("Pristine White Glaze", BasicHarvestable.WHITEGLAZE, true, 
            75, 40, 1, 0, 350, 0, 0),
    
    BLUEICE("Blue Ice", BasicHarvestable.BLUEICE, false, 
            50, 25, 1, 0, 0, 300, 0),
    BLUEICE_Е("Thick Blue Ice", BasicHarvestable.BLUEICE, true, 
            75, 40, 1, 0, 0, 350, 0),
    
    GLACIALMASS("Glacial Mass", BasicHarvestable.GLACIALMASS, false, 
            50, 25, 1, 0, 0, 0, 300),
    GLACIALMASS_S("Smooth Glacial Mass", BasicHarvestable.GLACIALMASS, true, 
            75, 40, 1, 0, 0, 0, 350),
    
    
    GLARECRUST("Glare Crust", BasicHarvestable.GLARECRUST, false, 
            1000, 500, 25, 0, 0, 0, 0),
    DARKGLITTER("Dark Glitter", BasicHarvestable.DARKGLITTER, false, 
            500, 1000, 50, 0, 0, 0, 0),
    GELIDUS("Gelidus", BasicHarvestable.GELIDUS, false, 
            250, 500, 75, 0, 0, 0, 0),
    KRYSTALLOS("Krystallos", BasicHarvestable.KRYSTALLOS, false, 
            125, 500, 125, 0, 0, 0, 0);
        
    private final String name;
    private final BasicHarvestable basicType;
    private final boolean highYield;
    
    private final int heavyWater;
    private final int liquidOzone;
    private final int strontiumClathrates;
    private final int heliumIsotopes;
    private final int nitrogenIsotopes;
    private final int oxygenIsotopes;
    private final int hydrogenIsotopes;

    private Ice(String name, BasicHarvestable basicType, boolean highYield, 
            int heavyWater, int liquidOzone, int strontiumClathrates, 
            int heliumIsotopes, int nitrogenIsotopes, int oxygenIsotopes, int hydrogenIsotopes) {
        this.name = name;
        this.basicType = basicType;
        this.highYield = highYield;
        this.heavyWater = heavyWater;
        this.liquidOzone = liquidOzone;
        this.strontiumClathrates = strontiumClathrates;
        this.heliumIsotopes = heliumIsotopes;
        this.nitrogenIsotopes = nitrogenIsotopes;
        this.oxygenIsotopes = oxygenIsotopes;
        this.hydrogenIsotopes = hydrogenIsotopes;
    }
    

    
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public BasicHarvestable getBasicHarvestable() {
        return basicType;
    }

    @Override
    public boolean isHighYield() {
        return highYield;
    }

    @Override
    public boolean isQuest() {
        return false;
    }

    public int getHeavyWater() {
        return heavyWater;
    }

    public int getHeliumIsotopes() {
        return heliumIsotopes;
    }

    public int getHydrogenIsotopes() {
        return hydrogenIsotopes;
    }

    public int getLiquidOzone() {
        return liquidOzone;
    }

    public int getNitrogenIsotopes() {
        return nitrogenIsotopes;
    }

    public int getOxygenIsotopes() {
        return oxygenIsotopes;
    }

    public int getStrontiumClathrates() {
        return strontiumClathrates;
    }

    @Override
    public String toString() {
        return name;
    }        
}
