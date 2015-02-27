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
package cy.alavrov.jminerguide.util.winmanager.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import cy.alavrov.jminerguide.util.api.win32.User32;
import cy.alavrov.jminerguide.util.winmanager.IEVEWindow;

/**
 *
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class Win32Window implements IEVEWindow {
    private volatile boolean exists = true;
    private volatile String name = null;
    private final HWND window;

    public Win32Window(HWND window) {
        this.window = window;
        update();
    }
    
    

    @Override
    public String getCharacterName() {
        return name;
    }

    @Override
    public void makeActive() {        
        if (User32.IsIconic(window)) {
            User32.ShowWindow(window, User32.SW_RESTORE);
        }
        User32.SetForegroundWindow(window);
    }

    @Override
    public final void update() {
        char[] buffer = new char[1024];
        int res = User32.GetWindowTextW(window, buffer, buffer.length);
        if (res == 0) {
            // haven't find a window, or a title is empty
            exists = false;
            name = null;
        } else {
            String curTitle = Native.toString(buffer);
            if (curTitle == null || curTitle.isEmpty() || !curTitle.startsWith("EVE")) {
                // something went wrong, let's flag an error
                exists = false;
                name = null;
            } else {
                // ok, that's EVE Online window.
                exists = true;
                String[] parts = curTitle.split("[-]", 2);
                // if there is no character name on a title, we haven't logged in yet.
                if (parts.length < 2) { 
                    name = null;
                } else {
                    name = parts[1].trim();
                }
            }
        }
    }

    @Override
    public boolean exists() {
        return exists;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Win32Window)) return false;
        Win32Window other = (Win32Window) o;
        return window.equals(other.window);
    }

    @Override
    public int hashCode() {
        return window.hashCode();
    }
}
