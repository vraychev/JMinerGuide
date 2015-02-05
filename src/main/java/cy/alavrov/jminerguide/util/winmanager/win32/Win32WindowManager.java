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
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import cy.alavrov.jminerguide.util.api.win32.Kernel32;
import cy.alavrov.jminerguide.util.api.win32.Psapi;
import cy.alavrov.jminerguide.util.api.win32.User32;
import cy.alavrov.jminerguide.util.winmanager.IEVEWindow;
import cy.alavrov.jminerguide.util.winmanager.IWindowManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class Win32WindowManager implements IWindowManager {


    @Override
    public IEVEWindow getCurrentEVEWindow() {
        HWND handle = User32.GetForegroundWindow();
        if (isEVEWindow(handle)) {
            return new Win32Window(handle);
        }
        
        return null;
    }

    @Override
    public List<IEVEWindow> getEVEWindowList() {
        final List<IEVEWindow> out = new ArrayList<>();
        
        User32.EnumWindows(new User32.WndEnumProc() {
            @Override
            public boolean callback(HWND hWnd, int lParam) {
                if (isEVEWindow(hWnd)) {
                    out.add(new Win32Window(hWnd));
                }
                
                return true;
            }
        }, 0);
        
        return out;
    }
    
    /**
     * Returns true, if the handle belongs to the EVE Online window.
     * EVE Online window must be created by exefile.exe and it's title start 
     * with "EVE"
     * @param handle
     * @return 
     */
    private boolean isEVEWindow(HWND handle) {
        if (handle != null) {
            char[] buffer = new char[1024];
            int res = User32.GetWindowTextW(handle, buffer, buffer.length);
            String curTitle = Native.toString(buffer);
            if (res != 0 && !curTitle.isEmpty() && curTitle.startsWith("EVE")) {                
                PointerByReference pointer = new PointerByReference();
                User32.GetWindowThreadProcessId(handle, pointer);
                Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());
                
                res = Psapi.GetModuleBaseNameW(process, null, buffer, 1024);
                if (res != 0) {
                    String exeName = Native.toString(buffer);           
                    return exeName.toLowerCase().equals("exefile.exe");                                              
                }
            }
        }
        
        return false;
    }       
        
    
}
