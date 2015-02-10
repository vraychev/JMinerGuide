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
 * Mission ores, ices and gases.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum MissionHarvestables implements IHarvestable{
    BANIDINE("Banidine", 28617, BasicHarvestable.VELDSPAR),
    AUGUMENE("Augumene", 28618, BasicHarvestable.PYROXERES),
    MERCIUM("Mercium", 28619, BasicHarvestable.OMBER),
    LYAVITE("Lyavite", 28620, BasicHarvestable.KERNITE),
    PITHIX("Pithix", 28621, BasicHarvestable.JASPET),
    GREENARISITE("Green Arisite", 28622, BasicHarvestable.GNEISS),
    OERYL("Oeryl", 28623, BasicHarvestable.DARKOCHRE),
    GEODITE("Geodite", 28624, BasicHarvestable.CROKITE),
    POLYGYPSUM("Polygypsum", 28625, BasicHarvestable.ARKONOR),
    ZUTHRINE("Zuthrine", 28626, BasicHarvestable.MERCOXIT), // because we can!
        
    AZUREICE("Azure Ice", 28627, BasicHarvestable.BLUEICE),
    CRYSTALLINEICICLE("Crystalline Icicle", 28628, BasicHarvestable.CLEARICICLE),
        
    GAMBOGECYTOSEROCIN("Gamboge Cytoserocin", 28629, BasicHarvestable.CYTOSEROCIN),
    CHARTREUSECYTOSEROCIN("Chartreuse Cytoserocin", 28630, BasicHarvestable.CYTOSEROCIN);
    
    private final String name;
    private final int id;
    private final BasicHarvestable basicType;

    private MissionHarvestables(String name, int id, BasicHarvestable basicType) {
        this.name = name;
        this.basicType = basicType;
        this.id = id;
    }        
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getItemID() {
        return id;
    }

    @Override
    public int getCompressedItemID() {
        return 0;
    }

    @Override
    public BasicHarvestable getBasicHarvestable() {
        return basicType;
    }

    @Override
    public boolean isHighYield() {
        return false;
    }

    @Override
    public boolean isQuest() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}
