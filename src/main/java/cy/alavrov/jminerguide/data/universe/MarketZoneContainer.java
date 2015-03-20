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
package cy.alavrov.jminerguide.data.universe;

import cy.alavrov.jminerguide.log.JMGLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * Container for market zones.
 * Populated at the moment of creation.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class MarketZoneContainer {
    private final static Comparator<MarketZone> marketNameComp = new Comparator<MarketZone>() {

        @Override
        public int compare(MarketZone o1, MarketZone o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    
    private final List<MarketZone> zones;

    public MarketZoneContainer() {
        ArrayList<MarketZone> out = new ArrayList<>();
        
        out.add(new MarketZone("• Jita", 30000142, MarketZone.ZoneType.SYSTEM));
        out.add(new MarketZone("• Amarr", 30002187, MarketZone.ZoneType.SYSTEM));
        out.add(new MarketZone("• Dodixie", 30002659, MarketZone.ZoneType.SYSTEM));
        out.add(new MarketZone("• Rens", 30002510, MarketZone.ZoneType.SYSTEM));
        out.add(new MarketZone("• Hek", 30002053, MarketZone.ZoneType.SYSTEM));
        
        List<MarketZone> regions = new ArrayList<>();
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(getClass().getClassLoader().getResourceAsStream("regions.xml"));
            Element rootNode = doc.getRootElement();
            
            List<Element> regionElemList = rootNode.getChildren("region"); 
            for (Element regionElem : regionElemList) {                
                try {
                    String regionName = regionElem.getChildText("regionName");
                    int regionID = Integer.parseInt(regionElem.getChildText("regionID"), 10);
                    
                    String factionIDStr = regionElem.getChildText("factionID");
                    int factionID = 0;
                    if (factionIDStr != null && !factionIDStr.isEmpty()) {
                        factionID = Integer.parseInt(factionIDStr, 10);
                    }
                    
                    if (regionID < 11000000 && factionID != 500005) { // we should skip wormhole and Jove systems.
                        MarketZone region = new MarketZone(regionName, regionID, MarketZone.ZoneType.REGION);
                        regions.add(region);
                    }
                } catch (Exception e) {
                    JMGLogger.logWarning("Unable to load region element", e);
                }
            }

        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load an item price file", e);
        }
        
        if (!regions.isEmpty()) {
            Collections.sort(regions, marketNameComp);
        }
        
        out.addAll(regions);
        
        this.zones = Collections.unmodifiableList(out);
    }

    public List<MarketZone> getZones() {
        return zones;
    }
    
    public DefaultComboBoxModel<MarketZone> getComboBoxModel(){
        DefaultComboBoxModel<MarketZone> out = new DefaultComboBoxModel<>();
        for (MarketZone zone : zones) {
            out.addElement(zone);
        }
        
        return out;
    }
}
