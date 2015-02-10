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
 * Gases.
 * Non-mission ones.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum Gas implements IHarvestable{
    AMBERCYTOSEROCIN("Amber Cytoserocin", 25268, BasicHarvestable.CYTOSEROCIN),
    AZURECYTOSEROCIN("Azure Cytoserocin", 25279, BasicHarvestable.CYTOSEROCIN),
    CELADONCYTOSEROCIN("Celadon Cytoserocin", 25275, BasicHarvestable.CYTOSEROCIN),
    GOLDENCYTOSEROCIN("Golden Cytoserocin", 25273, BasicHarvestable.CYTOSEROCIN),
    LIMECYTOSEROCIN("Lime Cytoserocin", 25277, BasicHarvestable.CYTOSEROCIN),
    MALACHITECYTOSEROCIN("Malachite Cytoserocin", 25276, BasicHarvestable.CYTOSEROCIN),
    VERMILLIONCYTOSEROCIN("Vermillion Cytoserocin", 25278, BasicHarvestable.CYTOSEROCIN),
    VIRIDIANCYTOSEROCIN("Viridian Cytoserocin", 25274, BasicHarvestable.CYTOSEROCIN),

    AMBERMYKOSEROCIN("Amber Mykoserocin", 28694, BasicHarvestable.MYKOSEROCIN),
    AZUREMYKOSEROCIN("Azure Mykoserocin", 28695, BasicHarvestable.MYKOSEROCIN),
    CELADONMYKOSEROCIN("Celadon Mykoserocin", 28696, BasicHarvestable.MYKOSEROCIN),
    GOLDENMYKOSEROCIN("Golden Mykoserocin", 28697, BasicHarvestable.MYKOSEROCIN),
    LIMEMYKOSEROCIN("Lime Mykoserocin", 28698, BasicHarvestable.MYKOSEROCIN),
    MALACHITEMYKOSEROCIN("Malachite Mykoserocin", 28699, BasicHarvestable.MYKOSEROCIN),
    VERMILLIONMYKOSEROCIN("Vermillion Mykoserocin", 28700, BasicHarvestable.MYKOSEROCIN),
    VIRIDIANMYKOSEROCIN("Viridian Mykoserocin", 28701, BasicHarvestable.MYKOSEROCIN),

    FULLERITEC28("Fullerite-C28", 30375, BasicHarvestable.FULLERITEC28),
    FULLERITEC32("Fullerite-C32", 30376, BasicHarvestable.FULLERITEC32),
    FULLERITEC50("Fullerite-C50", 30370, BasicHarvestable.FULLERITEC50),
    FULLERITEC60("Fullerite-C60", 30371, BasicHarvestable.FULLERITEC60),
    FULLERITEC70("Fullerite-C70", 30372, BasicHarvestable.FULLERITEC70),
    FULLERITEC72("Fullerite-C72", 30373, BasicHarvestable.FULLERITEC72),
    FULLERITEC84("Fullerite-C84", 30374, BasicHarvestable.FULLERITEC84),
    FULLERITEC320("Fullerite-C320", 30377, BasicHarvestable.FULLERITEC320),
    FULLERITEC540("Fullerite-C540", 30378, BasicHarvestable.FULLERITEC540);
            
    private final String name;
    private final int id;
    private final BasicHarvestable basicType;

    private Gas(String name, int id,  BasicHarvestable basicType) {
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
        return false;
    }

    @Override
    public String toString() {
        return name;
    }            
}
