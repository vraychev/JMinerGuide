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
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import cy.alavrov.jminerguide.util.api.win32.Kernel32;
import cy.alavrov.jminerguide.util.api.win32.Psapi;
import cy.alavrov.jminerguide.util.api.win32.User32;
import cy.alavrov.jminerguide.util.winmanager.IEVEWindow;
import cy.alavrov.jminerguide.util.winmanager.IWindowManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Windows32 window manager implementation.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class Win32WindowManager implements IWindowManager {


    @Override
    public IEVEWindow getCurrentEVEWindow() {
        HWND handle = User32.GetForegroundWindow();
        if (isDesiredWindow(handle, "EVE", "exefile.exe")) {
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
                if (isDesiredWindow(hWnd, "EVE", "exefile.exe")) {
                    out.add(new Win32Window(hWnd));
                }
                
                return true;
            }
        }, 0);
        
        return out;
    }
    
    /**
     * Returns true, if the window handle belongs to the executable with a given name, and
     * window's title starts with the given string.
     * When checking for EVE Online window, it must be created by exefile.exe and it's title should start 
     * with "EVE"
     * @param handle handle to the window
     * @param titleStartsWith window's title should start with this
     * @param exename executable name, *converted to lowercase*, should be exactly this
     * @return 
     */
    private boolean isDesiredWindow(HWND handle, String titleStartsWith, String exename) {
        if (handle != null) {
            char[] buffer = new char[1024];
            int res = User32.GetWindowTextW(handle, buffer, buffer.length);
            String curTitle = Native.toString(buffer);
            if (res != 0 && !curTitle.isEmpty() && curTitle.startsWith(titleStartsWith)) {                
                PointerByReference pointer = new PointerByReference();
                User32.GetWindowThreadProcessId(handle, pointer);
                Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());
                
                res = Psapi.GetModuleBaseNameW(process, null, buffer, 1024);
                if (res != 0) {
                    String exeName = Native.toString(buffer);           
                    return exeName.toLowerCase().equals(exename);                                              
                }
            }
        }
        
        return false;
    }       
    
    /**
     * Returns true, if the window handle belongs to the executable with a given pid, and
     * window's title starts with the given string.
     * @param handle handle to the window
     * @param titleStartsWith window's title should start with this
     * @param pid process's id
     * @return 
     */
    private boolean isDesiredWindow(HWND handle, String titleStartsWith, Pointer pid) {
        if (handle != null) {
            char[] buffer = new char[1024];
            int res = User32.GetWindowTextW(handle, buffer, buffer.length);
            String curTitle = Native.toString(buffer);
            if (res != 0 && !curTitle.isEmpty() && curTitle.startsWith(titleStartsWith)) {                
                PointerByReference pointer = new PointerByReference();
                User32.GetWindowThreadProcessId(handle, pointer);
                return (pointer.getValue().equals(pid));
            }
        }
        
        return false;
    } 
    
    /**
     * Returns true, if the window handle belongs to the system process.
     * Basically, we're catching task switching and taskbar mouseovers here.     
     * @param handle handle to the window
     * @return 
     */
    private boolean isSystemWindow(HWND handle) {
        if (handle != null) {
            char[] buffer = new char[1024];
            User32.GetWindowTextW(handle, buffer, buffer.length);
            String curTitle = Native.toString(buffer);
            if (curTitle.isEmpty() ||  curTitle.startsWith("Task Switching")) {                
                PointerByReference pointer = new PointerByReference();
                User32.GetWindowThreadProcessId(handle, pointer);
                Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());
                
                int res = Psapi.GetModuleBaseNameW(process, null, buffer, 1024);
                if (res != 0) {
                    String exeName = Native.toString(buffer);           
                    return exeName.toLowerCase().equals("explorer.exe");                                              
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean isMonitorOrSystemWindow() {
        HWND handle = User32.GetForegroundWindow();        
        return isDesiredWindow(handle, "Asteroid Monitor", "java.exe") || isSystemWindow(handle);
    }

    @Override
    public void minimizeMonitorWindow() {
        HWND handle = getMonitorWindow();
        User32.ShowWindow(handle, User32.SW_MINIMIZE);
    }

    @Override
    public void restoreMonitorWindow() {
        HWND handle = getMonitorWindow();
        User32.ShowWindow(handle, User32.SW_RESTORE);
    }
        
    private HWND getMonitorWindow() {
        final Pointer pid = Kernel32.GetCurrentProcessId();
        
        final List<HWND> out = new ArrayList<>();
        
        User32.EnumWindows(new User32.WndEnumProc() {
            @Override
            public boolean callback(HWND hWnd, int lParam) {
                if (isDesiredWindow(hWnd, "Asteroid Monitor", pid)) {
                    out.add(hWnd);
                }
                
                return true;
            }
        }, 0);
        
        if (out.isEmpty()) {
            return null;
        } else {
            return out.get(0);
        }
    }

    @Override
    public void setCurrentWindowForeground() {
        HWND handle = User32.GetForegroundWindow();
        User32.ShowWindow(handle, User32.SW_SHOW);
    }
}
