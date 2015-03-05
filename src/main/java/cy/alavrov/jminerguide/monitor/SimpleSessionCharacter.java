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
package cy.alavrov.jminerguide.monitor;

import cy.alavrov.jminerguide.data.ICalculatedStats;
import cy.alavrov.jminerguide.data.SimpleCalculatedStats;
import cy.alavrov.jminerguide.data.character.ICoreCharacter;
import cy.alavrov.jminerguide.data.character.SimpleCharacter;

/**
 *
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class SimpleSessionCharacter implements ISessionCharacter{
    private final SimpleCharacter character;
    private ICalculatedStats stats; 

    public SimpleSessionCharacter(SimpleCharacter character) {
        this.character = character;
        recalculateStats();
    }
        
    private void recalculateStats() {
        stats = new SimpleCalculatedStats(character);
    }

    @Override
    public ICoreCharacter getCoreCharacter() {
        return character;
    }

    @Override
    public ICalculatedStats getStats() {
        return stats;
    }

    @Override
    public ICalculatedStats getStatsMercoxit() {
        return stats;
    }

    @Override
    public int getTurretCount() {
        return character.getTurrets();
    }

    @Override
    public boolean isSimple() {
        return true;
    }
    
}
