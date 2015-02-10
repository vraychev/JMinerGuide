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
            72, 121, 144, 0, 72, 3, 0, 0),
    JASPET_PU("Pure Jaspet", 17448, 28408, BasicHarvestable.JASPET, true, 
            76, 127, 151, 0, 76, 3, 0, 0),
    JASPET_PR("Pristine Jaspet", 17449, 28407, BasicHarvestable.JASPET, true, 
            79, 133, 158, 0, 79, 3, 0, 0),
    
    HEMORPHITE("Hemorphite", 1231, 28403, BasicHarvestable.HEMORPHITE, false, 
            180, 72, 17, 59, 118, 8, 0, 0),
    HEMORPHITE_V("Vivid Hemorphite", 17444, 28405, BasicHarvestable.HEMORPHITE, true, 
            189, 76, 18, 62, 123, 9, 0, 0),
    HEMORPHITE_R("Radiant Hemorphite", 17445, 28404, BasicHarvestable.HEMORPHITE, true, 
            198, 79, 19, 65, 129, 9, 0, 0),
    
    HEDBERGITE("Hedbergite", 21, 28401, BasicHarvestable.HEDBERGITE, false, 
            0, 81, 0, 196, 98, 9, 0, 0),
    HEDBERGITE_V("Vitric Hedbergite", 17440, 28402, BasicHarvestable.HEDBERGITE, true, 
            0, 85, 0, 206, 103, 10, 0, 0),
    HEDBERGITE_G("Glazed Hedbergite", 17441, 28400, BasicHarvestable.HEDBERGITE, true, 
            0, 89, 0, 216, 108, 10, 0, 0),
    
    GNEISS("Gneiss", 1229, 28397, BasicHarvestable.GNEISS, false, 
            1278, 0, 1278, 242, 0, 60, 0, 0),
    GNEISS_I("Iridescent Gneiss", 17865, 28398, BasicHarvestable.GNEISS, true, 
            1342, 0, 1342, 254, 0, 63, 0, 0),
    GNEISS_P("Prismatic Gneiss", 17866, 28399, BasicHarvestable.GNEISS, true, 
            1406, 0, 1406, 266, 0, 65, 0, 0),
    
    DARKOCHRE("Dark Ochre", 1232, 28394, BasicHarvestable.DARKOCHRE, false, 
            8804, 0, 0, 0, 173, 87, 0, 0),
    DARKOCHRE_ON("Onyx Ochre", 17436, 28396, BasicHarvestable.DARKOCHRE, true, 
            9245, 0, 0, 0, 182, 91, 0, 0),
    DARKOCHRE_OB("Obsidian Ochre", 17437, 28395, BasicHarvestable.DARKOCHRE, true, 
            9685, 0, 0, 0, 190, 95, 0, 0),
    
    SPODUMAIN("Spodumain", 19, 28420, BasicHarvestable.SPODUMAIN, false, 
            39221, 4972, 0, 0, 0, 0, 78, 0),
    SPODUMAIN_B("Bright Spodumain", 17466, 28418, BasicHarvestable.SPODUMAIN, true, 
            41182, 5221, 0, 0, 0, 0, 82, 0),
    SPODUMAIN_G("Gleaming Spodumain", 17467, 28419, BasicHarvestable.SPODUMAIN, true, 
            43143, 5469, 0, 0, 0, 0, 86, 0),
    
    CROKITE("Crokite", 1225, 28391, BasicHarvestable.CROKITE, false, 
            20992, 0, 0, 0, 275, 367, 0, 0),
    CROKITE_S("Sharp Crokite", 17432, 28393, BasicHarvestable.CROKITE, true, 
            22041, 0, 0, 0, 290, 385, 0, 0),
    CROKITE_C("Crystalline Crokite", 17433, 28392, BasicHarvestable.CROKITE, true, 
            23091, 0, 0, 0, 304, 403, 0, 0),
    
    BISTOT("Bistot", 1223, 28388, BasicHarvestable.BISTOT, false, 
            0, 16572, 0, 0, 0, 236, 118, 0),
    BISTOT_T("Triclinic Bistot", 17428, 28389, BasicHarvestable.BISTOT, true, 
            0, 17402, 0, 0, 0, 248, 124, 0),
    BISTOT_M("Monoclinic Bistot", 17429, 28390, BasicHarvestable.BISTOT, true, 
            0, 18230, 0, 0, 0, 259, 130, 0),
    
    ARKONOR("Arkonor", 22, 28367, BasicHarvestable.ARKONOR, false, 
            6905, 0, 1278, 0, 0, 115, 230, 0),
    ARKONOR_C("Crimson Arkonor", 17425, 28385, BasicHarvestable.ARKONOR, true, 
            7251, 0, 1342, 0, 0, 121, 242, 0),
    ARKONOR_P("Prime Arkonor", 17426, 28387, BasicHarvestable.ARKONOR, true, 
            7596, 0, 1406, 0, 0, 127, 253, 0),
    
    MERCOXIT("Mercoxit", 11396, 28413, BasicHarvestable.MERCOXIT, false, 
            0, 0, 0, 0, 0, 0, 0, 293),
    MERCOXIT_M("Magma Mercoxit", 17869, 28412, BasicHarvestable.MERCOXIT, true, 
            0, 0, 0, 0, 0, 0, 0, 308),
    MERCOXIT_V("Vitreous Mercoxit", 17870, 28414, BasicHarvestable.MERCOXIT, true, 
            0, 0, 0, 0, 0, 0, 0, 323);
    
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

