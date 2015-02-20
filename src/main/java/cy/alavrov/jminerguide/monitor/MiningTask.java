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

import cy.alavrov.jminerguide.forms.JAsteroidMonitorForm;
import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.InputStream;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * A task to do mining every second.
 * Also checks timers.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class MiningTask implements Runnable{
    private final MiningSessionMonitor msMonitor;
    private final JAsteroidMonitorForm form;
    private volatile MiningSession lastCurrentSession;

    public MiningTask(MiningSessionMonitor msMonitor, JAsteroidMonitorForm form) {
        this.msMonitor = msMonitor;
        this.form = form;
    }

    @Override
    public void run() {
        try {
            MiningSession curSession = msMonitor.getCurrentSession();
            if (curSession != null) lastCurrentSession = curSession;
            
            List<MiningSession> sessions = msMonitor.getSessions();
            for (final MiningSession session : sessions) {
                try {
                    session.doMining();
                } catch (AsteroidMinedException | FullOreHoldException e) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run(){
                            SessionCharacter chr = session.getSessionCharacter();
                            if (chr != null && !chr.getCharacter().isMonitorIgnore()) {
                                form.setAlwaysOnTop(true);
                                playSound();
                            }
                        }
                    });
                }
                
                MiningTimer timer = session.getTimer();
                if (timer != null && timer.isFinished()) {
                    if (!timer.wasAlarm()) {
                        timer.markAlarm();
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run(){
                                msMonitor.restoreMonitorWindow();
                                form.setAlwaysOnTop(true);
                                playSound();
                            }
                        });
                    }
                    
                    if ( // either we switched off from right window to the monitor
                            ((session.equals(lastCurrentSession) && msMonitor.isMonitorWindow()) || 
                            // or we in the right window.
                            (session.equals(curSession))) 
                            && timer.isOkToClear()) {
                        session.stopTimer();
                    }
                }
            }

            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run(){
                    form.notifyTableUpdate();
                    form.updateCurrentCharacterStats();
                    form.updateSessionButtons();
                    form.updateTimerLabel();
                    
                    form.pack();
                }
            });
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to mine", e);
        }        
    }
    
    private void playSound() {
        try {
            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("ting.wav");
            AudioInputStream aStream = AudioSystem.getAudioInputStream(resourceStream);
            AudioFormat audioFormat = aStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, audioFormat);
            Clip clip = (Clip) AudioSystem.getLine(dataLineInfo);
            clip.open(aStream);
            clip.start();
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to play sound", e);
        }
    }
    
}
