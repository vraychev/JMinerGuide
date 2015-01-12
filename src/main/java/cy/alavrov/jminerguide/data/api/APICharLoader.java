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
package cy.alavrov.jminerguide.data.api;

import cy.alavrov.jminerguide.data.character.APIException;
import cy.alavrov.jminerguide.data.character.EVECharacter;

/**
 * A task to load a pilot data from the server.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class APICharLoader implements Runnable{
    private final EVECharacter target;
    private final ICharLoadingResultReceiver receiver;
    
    public APICharLoader(EVECharacter target, ICharLoadingResultReceiver receiver) {
        this.target = target;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        try {
            target.loadAPIData();
        } catch (APIException e) {
            final String message = e.getMessage();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    receiver.loadingDone(false, message, target);
                }
            });
            return;
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                receiver.loadingDone(true, "OK", target);
            }
        });
    }    
    
}
