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
 * Ores.
 * Non-mission ones, with high-yield variants.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum Ore implements IHarvestable{
    VELDSPAR("Veldspar", 1230, 28432, BasicHarvestable.VELDSPAR ,false, 
            415, 0, 0, 0, 0, 0, 0, 0),
    VELDSPAR_C("Concentrated Veldspar", 17470, 28430, BasicHarvestable.VELDSPAR, true, 
            436, 0, 0, 0, 0, 0, 0, 0),
    VELDSPAR_D("Dense Veldspar", 17471, 28431, BasicHarvestable.VELDSPAR, true, 
            457, 0, 0, 0, 0, 0, 0, 0),
    
    
    SCORDITE("Scordite", 1228, 28429, BasicHarvestable.SCORDITE, false, 
            346, 173, 0, 0, 0, 0, 0, 0),
    SCORDITE_C("Condensed Scordite", 17463, 28427, BasicHarvestable.SCORDITE, true, 
            363, 182, 0, 0, 0, 0, 0, 0),
    SCORDITE_M("Massive Scordite", 17464, 28428, BasicHarvestable.SCORDITE, true, 
            380, 190, 0, 0, 0, 0, 0, 0),
    
    PYROXERES("Pyroxeres", 1224, 28424, BasicHarvestable.PYROXERES, false, 
            351, 25, 50, 0, 5, 0, 0, 0),
    PYROXERES_S("Solid Pyroxeres", 17459, 28425, BasicHarvestable.PYROXERES, true, 
            368, 26, 53, 0, 5, 0, 0, 0),
    PYROXERES_V("Viscous Pyroxeres", 17460, 28426, BasicHarvestable.PYROXERES, true, 
            385, 27, 55, 0, 5, 0, 0, 0),
    
    PLAGIOCLASE("Plagioclase", 18, 28422, BasicHarvestable.PLAGIOCLASE, false, 
            107, 213, 107, 0, 0, 0, 0, 0),
    PLAGIOCLASE_A("Azure Plagioclase", 17455, 28421, BasicHarvestable.PLAGIOCLASE, true, 
            112, 224, 112, 0, 0, 0, 0, 0),
    PLAGIOCLASE_R("Rich Plagioclase", 17456, 28423, BasicHarvestable.PLAGIOCLASE, true, 
            117, 234, 117, 0, 0, 0, 0, 0),
    
    OMBER("Omber", 1227, 28416, BasicHarvestable.OMBER, false, 
            85, 34, 0, 85, 0, 0, 0, 0),
    OMBER_S("Silvery Omber", 17867, 28417, BasicHarvestable.OMBER, true, 
            89, 36, 0, 89, 0, 0, 0, 0),
    OMBER_G("Golden Omber", 17868, 28415, BasicHarvestable.OMBER, true, 
            94, 38, 0, 94, 0, 0, 0, 0),
    
    KERNITE("Kernite", 20, 28410, BasicHarvestable.KERNITE, false, 
            134, 0, 267, 134, 0, 0, 0, 0),
    KERNITE_L("Luminous Kernite", 17452, 28411, BasicHarvestable.KERNITE, true, 
            140, 0, 281, 140, 0, 0, 0, 0),
    KERNITE_F("Fiery Kernite", 17453, 28409, BasicHarvestable.KERNITE, true, 
            147, 0, 294, 147, 0, 0, 0, 0),
    
    JASPET("Jaspet", 1226, 28406, BasicHarvestable.JASPET, false, 
            0, 0, 350, 0, 75, 8, 0, 0),
    JASPET_PU("Pure Jaspet", 17448, 28408, BasicHarvestable.JASPET, true, 
            0, 0, 368, 0, 79, 8, 0, 0),
    JASPET_PR("Pristine Jaspet", 17449, 28407, BasicHarvestable.JASPET, true, 
            0, 0, 385, 0, 83, 9, 0, 0),
    
    HEMORPHITE("Hemorphite", 1231, 28403, BasicHarvestable.HEMORPHITE, false, 
            2200, 0, 0, 100, 120, 15, 0, 0),
    HEMORPHITE_V("Vivid Hemorphite", 17444, 28405, BasicHarvestable.HEMORPHITE, true, 
            2310, 0, 0, 105, 126, 16, 0, 0),
    HEMORPHITE_R("Radiant Hemorphite", 17445, 28404, BasicHarvestable.HEMORPHITE, true, 
            2420, 0, 0, 110, 132, 17, 0, 0),
    
    HEDBERGITE("Hedbergite", 21, 28401, BasicHarvestable.HEDBERGITE, false, 
            0, 1000, 0, 200, 100, 19, 0, 0),
    HEDBERGITE_V("Vitric Hedbergite", 17440, 28402, BasicHarvestable.HEDBERGITE, true, 
            0, 1050, 0, 210, 105, 20, 0, 0),
    HEDBERGITE_G("Glazed Hedbergite", 17441, 28400, BasicHarvestable.HEDBERGITE, true, 
            0, 1100, 0, 220, 110, 21, 0, 0),
    
    GNEISS("Gneiss", 1229, 28397, BasicHarvestable.GNEISS, false, 
            0, 2200, 2400, 300, 0, 0, 0, 0),
    GNEISS_I("Iridescent Gneiss", 17865, 28398, BasicHarvestable.GNEISS, true, 
            0, 2310, 2520, 315, 0, 0, 0, 0),
    GNEISS_P("Prismatic Gneiss", 17866, 28399, BasicHarvestable.GNEISS, true, 
            0, 2420, 2640, 330, 0, 0, 0, 0),
    
    DARKOCHRE("Dark Ochre", 1232, 28394, BasicHarvestable.DARKOCHRE, false, 
            10000, 0, 0, 1600, 120, 0, 0, 0),
    DARKOCHRE_ON("Onyx Ochre", 17436, 28396, BasicHarvestable.DARKOCHRE, true, 
            10500, 0, 0, 1680, 126, 0, 0, 0),
    DARKOCHRE_OB("Obsidian Ochre", 17437, 28395, BasicHarvestable.DARKOCHRE, true, 
            11000, 0, 0, 1760, 132, 0, 0, 0),
    
    SPODUMAIN("Spodumain", 19, 28420, BasicHarvestable.SPODUMAIN, false, 
            56000, 12050, 2100, 450, 0, 0, 0, 0),
    SPODUMAIN_B("Bright Spodumain", 17466, 28418, BasicHarvestable.SPODUMAIN, true, 
            58800, 12653, 2205, 473, 0, 0, 0, 0),
    SPODUMAIN_G("Gleaming Spodumain", 17467, 28419, BasicHarvestable.SPODUMAIN, true, 
            61600, 13255, 2310, 495, 0, 0, 0, 0),
    
    CROKITE("Crokite", 1225, 28391, BasicHarvestable.CROKITE, false, 
            21000, 0, 0, 0, 760, 135, 0, 0),
    CROKITE_S("Sharp Crokite", 17432, 28393, BasicHarvestable.CROKITE, true, 
            22050, 0, 0, 0, 798, 142, 0, 0),
    CROKITE_C("Crystalline Crokite", 17433, 28392, BasicHarvestable.CROKITE, true, 
            23100, 0, 0, 0, 836, 149, 0, 0),
    
    BISTOT("Bistot", 1223, 28388, BasicHarvestable.BISTOT, false, 
            0, 12000, 0, 0, 0, 450, 100, 0),
    BISTOT_T("Triclinic Bistot", 17428, 28389, BasicHarvestable.BISTOT, true, 
            0, 12600, 0, 0, 0, 473, 105, 0),
    BISTOT_M("Monoclinic Bistot", 17429, 28390, BasicHarvestable.BISTOT, true, 
            0, 13200, 0, 0, 0, 495, 110, 0),
    
    ARKONOR("Arkonor", 22, 28367, BasicHarvestable.ARKONOR, false, 
            22000, 0, 2500, 0, 0, 0, 320, 0),
    ARKONOR_C("Crimson Arkonor", 17425, 28385, BasicHarvestable.ARKONOR, true, 
            23100, 0, 2625, 0, 0, 0, 336, 0),
    ARKONOR_P("Prime Arkonor", 17426, 28387, BasicHarvestable.ARKONOR, true, 
            24200, 0, 2750, 0, 0, 0, 352, 0),
    
    MERCOXIT("Mercoxit", 11396, 28413, BasicHarvestable.MERCOXIT, false, 
            0, 0, 0, 0, 0, 0, 0, 300),
    MERCOXIT_M("Magma Mercoxit", 17869, 28412, BasicHarvestable.MERCOXIT, true, 
            0, 0, 0, 0, 0, 0, 0, 315),
    MERCOXIT_V("Vitreous Mercoxit", 17870, 28414, BasicHarvestable.MERCOXIT, true, 
            0, 0, 0, 0, 0, 0, 0, 330);
    
    private final String name;
    private final int id;
    private final int compressedId;
    private final BasicHarvestable basicType;
    private final boolean highYield;
    
    private final int tritanium;
    private final int pyerite;
    private final int mexallon;
    private final int isogen;
    private final int nocxium;
    private final int zydrine;
    private final int megacyte;
    private final int morphite;

    private Ore(String name, int id, int compressedId, BasicHarvestable basicType, boolean highYield, 
            int tritanium, int pyerite, int mexallon, int isogen, int nocxium, 
            int zydrine, int megacyte, int morphite) {
        this.name = name;
        this.id = id;
        this.compressedId = compressedId;
        this.basicType = basicType;
        this.highYield = highYield;
        this.tritanium = tritanium;
        this.pyerite = pyerite;
        this.mexallon = mexallon;
        this.isogen = isogen;
        this.nocxium = nocxium;
        this.zydrine = zydrine;
        this.megacyte = megacyte;
        this.morphite = morphite;
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
        return compressedId;
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

    public int getIsogen() {
        return isogen;
    }

    public int getMegacyte() {
        return megacyte;
    }

    public int getMexallon() {
        return mexallon;
    }

    public int getMorphite() {
        return morphite;
    }

    public int getNocxium() {
        return nocxium;
    }

    public int getPyerite() {
        return pyerite;
    }

    public int getTritanium() {
        return tritanium;
    }

    public int getZydrine() {
        return zydrine;
    }

    @Override
    public String toString() {
        return name; 
    }   
}

