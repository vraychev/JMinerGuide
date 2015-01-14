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

/**
 * Different levels of a mining crystal.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum MiningCrystalLevel {
    NOTHING("- nothing -", 1, 1),
    T1("T1", 1.625f, 1.25f),
    T2("T2", 1.75f, 1.375f);
    
    private final String name;
    private final float oreMod;
    private final float mercMod;
    
    MiningCrystalLevel(String name, float oreMod, float mercMod) {
        this.name = name;
        this.oreMod = oreMod;
        this.mercMod = mercMod;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Yield modificator for common ores
     * @return 
     */
    public float getOreMod() {
        return oreMod;
    }
    
    /**
     * Yield modificator for mercoxit
     * @return 
     */
    public float getMercMod() {
        return mercMod;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
