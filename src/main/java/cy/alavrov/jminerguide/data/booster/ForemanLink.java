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
package cy.alavrov.jminerguide.data.booster;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mining Foreman Link
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public enum ForemanLink {
    NOTHING ("- nothing -", 0, 0, 0),
    CYCLEI ("Laser Optimization I", 22557, 6, 0),
    CYCLEII ("Laser Optimization II", 28890, 7.5f, 0),
    OPTIMALI ("Laser Field Enhancement I", 22555, 0, 13.6f),
    OPTIMALII ("Laser Field Enhancement II", 4278, 0, 17);
    
    private final String name;
    private final int id;
    private final float cycleBonus;
    private final float optimalBonus;
    
    public final static ForemanLink[] cycleLinksArr = {NOTHING, CYCLEI, CYCLEII};
    public final static ForemanLink[] optimalLinksArr = {NOTHING, OPTIMALI, OPTIMALII};

    public final static Map<Integer, ForemanLink> linksMap;
    
    static {
        Map<Integer, ForemanLink> links = new HashMap<>();
        for (ForemanLink link : ForemanLink.values()) {
            links.put(link.id, link);
        }
        linksMap = Collections.unmodifiableMap(links);
    }
    
    private ForemanLink(String name, int id, float cycleBonus, float optimalBonus) {
        this.name = name;
        this.id = id;
        this.cycleBonus = cycleBonus;
        this.optimalBonus = optimalBonus;
    }
    
    
    /**
     * Returns foreman link's name - almost as you see it ingame. Relevant parts of it.
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns internal link's id.
     * @return 
     */
    public int getID() {
        return id;
    }
    
    /**
     * Returns base mining cycle bonus of a link, in percents.     
     * @return 
     */
    public float getCycleBonus() {
        return cycleBonus;
    }
    
    /**
     * Returns base turret optimal bonus of a link, in percents.     
     * @return 
     */
    public float getOptimalBonus() {
        return optimalBonus;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
