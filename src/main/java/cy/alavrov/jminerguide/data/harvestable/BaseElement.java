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
 * Base elements of ores and ices.
 * Are there for price queries.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum BaseElement {
    TRITANIUM ("Tritanium", 34),
    PYERITE ("Pyerite", 35),
    MEXALLON ("Mexallon", 36),
    ISOGEN ("Isogen", 37),
    NOCXIUM ("Nocxium", 38),
    ZYDRINE ("Zydrine", 39),
    MEGACYTE ("Megacyte", 40),
    MORPHITE ("Morphite", 11399),
    
    HEAVYWATER ("Heavy Water", 16272),
    LIQUIDOZONE ("Liquid Ozone", 16273),
    HELIUMISOTOPES ("Helium Isotopes", 16274),
    STRONTIUM ("Strontium Clathrates", 16275),
    HYDROGENISOTOPES ("Hydrogen Isotopes", 17889),
    OXYGENISOTOPES ("Oxygen Isotopes", 17887),
    NITROGENISOTOPES ("Nitrogen Isotopes", 17888);
    
    
    
    private final String name;
    private final int id;

    private BaseElement(String name, int id) {
        this.name = name;
        this.id = id;
    }        

    public int getItemID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}
