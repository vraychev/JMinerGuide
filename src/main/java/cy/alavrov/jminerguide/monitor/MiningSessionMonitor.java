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

import cy.alavrov.jminerguide.util.winmanager.IEVEWindow;
import cy.alavrov.jminerguide.util.winmanager.IWindowManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Monitor for the EVE (mining and whatnot) sessions.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class MiningSessionMonitor {
    private final IWindowManager wManager;
    private volatile IEVEWindow currentWindow = null;
    private final ConcurrentHashMap<IEVEWindow, MiningSession> sessions;

    public MiningSessionMonitor(IWindowManager wManager) {
        this.wManager = wManager;
        this.sessions = new ConcurrentHashMap<>();
    }
    
    /**
     * Updates all available EVE window lists and creates/cleans up sessions
     * accordingly.
     */
    public void update() {
        currentWindow = wManager.getCurrentEVEWindow();
        
        Collection<MiningSession> sessionEntries = sessions.values();
        Iterator<MiningSession> iter = sessionEntries.iterator();
        while (iter.hasNext()) {
            MiningSession session = iter.next();
            
            session.updateWindow();
            
            if (!session.exists()) iter.remove();
        }
        
        List<IEVEWindow> windows = wManager.getEVEWindowList();
        for (IEVEWindow window : windows) {
            if (!sessions.containsKey(window)) {
                MiningSession newSession = new MiningSession(window);
                sessions.putIfAbsent(window, newSession);
            }
        }
    }

    /**
     * Returns the current EVE window, or null, if the current window is EVE's.
     * @return 
     */
    public MiningSession getCurrentSession() {
        IEVEWindow window = currentWindow;
        if (window == null) return null;
        
        MiningSession out = sessions.get(window);
        
        if (out == null) {            
            MiningSession newOut = new MiningSession(window);
            out = sessions.putIfAbsent(window, newOut);
            // will return null, if there's nothing here and new value was 
            // inserted successfully, otherwise will return stored value. 
            // We will probably not run into this, but it's better to be safe.
            if (out == null) out = newOut;
        }
        
        return out;
    }
        
    /**
     * Returns all of the sessions available at this moment.
     * @return 
     */
    public List<MiningSession> getSessions() {
        ArrayList<MiningSession> out = new ArrayList<>();
        for (MiningSession session : sessions.values()) {
            out.add(session);
        }
        return out;
    }
}
