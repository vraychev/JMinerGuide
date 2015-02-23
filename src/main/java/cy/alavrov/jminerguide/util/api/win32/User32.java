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
package cy.alavrov.jminerguide.util.api.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * User32 JNA mapping.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class User32 {
    static { 
        Native.register("user32"); 
    }
    
    public static int SW_HIDE = 0;
    public static int SW_SHOW = 5;
    public static int SW_MINIMIZE = 6;
    public static int SW_RESTORE = 9;
    
    public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
    public static native HWND GetForegroundWindow();
    public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
    public static native boolean EnumWindows (WndEnumProc wndenumproc, int lParam);
    public static native boolean SetForegroundWindow(HWND hWnd);
    public static native boolean ShowWindow(HWND hWnd, int nCmdShow);
    
    public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
        boolean callback (HWND hWnd, int lParam);
    }
}
