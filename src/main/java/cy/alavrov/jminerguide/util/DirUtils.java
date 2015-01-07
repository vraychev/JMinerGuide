/*
 * Copyright (c) 2014, alavrov
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

package cy.alavrov.jminerguide.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.PointerType;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alavrov
 */
public class DirUtils {
    
    /**
     * Returns the path to the ~/Documents/ (note the finishing slash), OS-dependent.
     * @return 
     */
    public static String getDocumentsDir(){ 
        String dir;
        
        if (com.sun.jna.Platform.isWindows()) {
            HWND hwndOwner = null;
            int nFolder = Shell32.CSIDL_PERSONAL;
            HANDLE hToken = null;
            int dwFlags = Shell32.SHGFP_TYPE_CURRENT;
            char[] pszPath = new char[Shell32.MAX_PATH];
            int hResult = Shell32.INSTANCE.SHGetFolderPath(hwndOwner, nFolder,
                    hToken, dwFlags, pszPath);
            if (Shell32.S_OK == hResult) {
                String path = new String(pszPath);
                int len = path.indexOf('\0');
                dir = path.substring(0, len);
            } else {
                System.err.println("Cannot detect home path: "+hResult);
                dir = ""; // Error? Let's write to the game directory!11
            }
        } else {
            dir = System.getProperty("user.home")+File.separator+"Documents";
        }
        return dir+File.separator;
        
    }
    

    private final static Map<String, Object> OPTIONS = new HashMap<>();
    static {
        OPTIONS.put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
        OPTIONS.put(Library.OPTION_FUNCTION_MAPPER,
                W32APIFunctionMapper.UNICODE);
    }

    static class HANDLE extends PointerType implements NativeMapped {
    }

    static class HWND extends HANDLE {
    }

    static interface Shell32 extends Library {

        public static final int MAX_PATH = 260;
        public static final int CSIDL_PERSONAL = 0x05;
        public static final int SHGFP_TYPE_CURRENT = 0;
        public static final int SHGFP_TYPE_DEFAULT = 1;
        public static final int S_OK = 0;

        static Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32",
                Shell32.class, OPTIONS);

        /**
         * see http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx
         * 
         * HRESULT SHGetFolderPath( HWND hwndOwner, int nFolder, HANDLE hToken,
         * DWORD dwFlags, LPTSTR pszPath);
         */
        public int SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken,
                int dwFlags, char[] pszPath);

    }
    
}
