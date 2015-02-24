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

import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Settings for the asteroid monitor window.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class AsteroidMonitorSettings {
    
    private final String path;
    
    /**
     * Should we popup the asteroid monitor on alert?
     */
    private volatile boolean popupOnAlert;
    
    /**
     * Should we play a sound on alert?
     */
    private volatile boolean soundOnAlert;
    
    /**
     * How many seconds we should wait before removing an expired timer alert?
     */
    private volatile int timerAlertRemoveTimeout;
    
    /**
     * X coordinate of the top left corner.
     */
    private volatile int x;
    
    /**
     * Y coordinate of the top left corner.
     */
    private volatile int y;

    public AsteroidMonitorSettings(String path) {
        this.path = path;
        
        File src = new File(path+File.separator+"amsettings.dat");
        if (!src.exists()) {   
            popupOnAlert = true;
            soundOnAlert = true;
            timerAlertRemoveTimeout = 5;
            x = -1;
            y = -1;
            save();
            return;
        }
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            
            popupOnAlert = "true".equals(rootNode.getChildText("popuponalert"));
            soundOnAlert = "true".equals(rootNode.getChildText("soundonalert"));
            
            timerAlertRemoveTimeout = Integer.parseInt(rootNode.getChildText("timeralerttimeout"), 10);            
            x = Integer.parseInt(rootNode.getChildText("x"), 10);               
            y = Integer.parseInt(rootNode.getChildText("y"), 10);            
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load settings for the asteroid monitor", e);
            popupOnAlert = true;
            soundOnAlert = true;
            timerAlertRemoveTimeout = 5;
            x = -1;
            y = -1;
        } 
    }
    
     /**
     * Saves setttings into a file.
     */
    public void save() {
        File src = new File(path+File.separator+"amsettings.dat");
        if (!src.exists()) {
            try {
                if (!src.createNewFile()) {
                    JMGLogger.logSevere("Unable to create a settings file for the asteroid monitor");
                    return;
                }
            } catch (IOException e) {
                JMGLogger.logSevere("Unable to create a settings file for the asteroid monitor", e);
                return;
            }
        }
        
        
        Element root = new Element("settings");
        Document doc = new Document(root);
        
        root.addContent(new Element("popuponalert").setText(String.valueOf(popupOnAlert)));
        root.addContent(new Element("soundonalert").setText(String.valueOf(soundOnAlert)));
        root.addContent(new Element("timeralerttimeout").setText(String.valueOf(timerAlertRemoveTimeout)));

        root.addContent(new Element("x").setText(String.valueOf(x)));
        root.addContent(new Element("y").setText(String.valueOf(y)));
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try (FileWriter fw = new FileWriter(path+File.separator+"amsettings.dat")){
            xmlOutput.output(doc, fw);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to save "+path+File.separator+"amsettings.dat", e);
        }
    }
    
    /**
     * Should we popup the asteroid monitor on alert?
     * @return the popupOnAlert
     */
    public boolean isPopupOnAlert() {
        return popupOnAlert;
    }

    /**
     * Should we popup the asteroid monitor on alert?
     * @param popupOnAlert the popupOnAlert to set
     */
    public void setPopupOnAlert(boolean popupOnAlert) {
        this.popupOnAlert = popupOnAlert;
    }

    /**
     * Should we play a sound on alert?
     * @return the soundOnAlert
     */
    public boolean isSoundOnAlert() {
        return soundOnAlert;
    }

    /**
     * Should we play a sound on alert?
     * @param soundOnAlert the soundOnAlert to set
     */
    public void setSoundOnAlert(boolean soundOnAlert) {
        this.soundOnAlert = soundOnAlert;
    }

    /**
     * How many seconds we should wait before removing an expired timer alert?
     * @return the timerAlertRemoveTimeout
     */
    public int getTimerAlertRemoveTimeout() {
        return timerAlertRemoveTimeout;
    }

    /**
     * How many seconds we should wait before removing an expired timer alert?
     * @param timerAlertRemoveTimeout the timerAlertRemoveTimeout to set
     */
    public void setTimerAlertRemoveTimeout(int timerAlertRemoveTimeout) {
        this.timerAlertRemoveTimeout = timerAlertRemoveTimeout;
    }

    /**
     * X coordinate of the top left corner.
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * X coordinate of the top left corner.
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Y coordinate of the top left corner.
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Y coordinate of the top left corner.
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }
}
