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
package cy.alavrov.jminerguide.data.price;

import cy.alavrov.jminerguide.data.harvestable.Gas;
import cy.alavrov.jminerguide.data.harvestable.Ice;
import cy.alavrov.jminerguide.data.harvestable.Ore;
import cy.alavrov.jminerguide.data.harvestable.BaseElement;
import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Container for item prices.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class ItemPriceContainer {
    private final LinkedHashMap<Integer, ItemPrice> prices;
    
    private final String path; 

    public ItemPriceContainer(String path) {
        this.prices = new LinkedHashMap<>();
        
        for (Ore ore : Ore.values()) {
            prices.put(ore.getItemID(), new ItemPrice(ore.getItemID(), ore.getName(), 
                    ItemPrice.ItemType.ORE, ItemPrice.CompressionType.UNCOMPRESSED));
            prices.put(ore.getCompressedItemID(), new ItemPrice(ore.getCompressedItemID(), "Compressed "+ore.getName(), 
                    ItemPrice.ItemType.ORE, ItemPrice.CompressionType.COMPRESSED));
        }
        
        for (Ice ice : Ice.values()) {
            prices.put(ice.getItemID(), new ItemPrice(ice.getItemID(), ice.getName(), 
                    ItemPrice.ItemType.ICE, ItemPrice.CompressionType.UNCOMPRESSED));
            prices.put(ice.getCompressedItemID(), new ItemPrice(ice.getCompressedItemID(), "Compressed "+ice.getName(),
                    ItemPrice.ItemType.ICE, ItemPrice.CompressionType.COMPRESSED));
        }
        
        for (Gas gas : Gas.values()) {
            prices.put(gas.getItemID(), new ItemPrice(gas.getItemID(), gas.getName(),
                    ItemPrice.ItemType.GAS, ItemPrice.CompressionType.UNCOMPRESSED));
        }
        
        for (BaseElement res : BaseElement.values()) {
            prices.put(res.getItemID(), new ItemPrice(res.getItemID(), res.getName(),
                    ItemPrice.ItemType.BASIC, ItemPrice.CompressionType.UNCOMPRESSED));
        }
        
        this.path = path;
    }
    
    public synchronized void load() {        
        JMGLogger.logWarning("Loading item prices...");
        File src = new File(path+File.separator+"prices.dat");
        if (!src.exists()) {            
            JMGLogger.logWarning("No item price file found, creating new.");
            save();
            return;
        }
        
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(src);
            Element rootNode = doc.getRootElement();
            
            List<Element> itemPrices = rootNode.getChildren("itemprice"); 
            for (Element priceElem : itemPrices) {                
                try {
                    int itemID = priceElem.getAttribute("itemid").getIntValue();
                    ItemPrice priceObj = prices.get(itemID);
                    if (priceObj != null) {
                        priceObj.updatePrice(priceElem);
                    }
                } catch (Exception e) {
                    JMGLogger.logWarning("Unable to load price element", e);
                }

            }

        } catch (Exception e) {
            JMGLogger.logSevere("Unable to load an item price file", e);
        }
    }
    
    public synchronized void save() {
        File src = new File(path+File.separator+"prices.dat");
        if (!src.exists()) {
            try {
                if (!src.createNewFile()) {
                    JMGLogger.logSevere("Unable to create an item price file");
                    return;
                }
            } catch (IOException e) {
                JMGLogger.logSevere("Unable to create an item price file", e);
                return;
            }
        }
        
        Element root = new Element("prices");
        Document doc = new Document(root);
        
        for (ItemPrice price : prices.values()) {
            root.addContent(price.getXMLElement());
        }
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try (FileOutputStream fos = new FileOutputStream(path+File.separator+"prices.dat")){
            xmlOutput.output(doc, fos);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to save "+path+File.separator+"prices.dat", e);
        }
    }
    
    public synchronized ItemPrice getItemPrice(int itemID) {
        return prices.get(itemID);
    }
    
    public synchronized ItemPriceTableModel getTableModel(ItemPrice.ItemType itemTypeFilter, ItemPrice.CompressionType comprTypeFilter) {
        List<ItemPrice> outList = new ArrayList<>();
        
        for (ItemPrice price : prices.values()) {
            boolean passed;
            
            passed = itemTypeFilter == ItemPrice.ItemType.ALL 
                    || itemTypeFilter == price.getType();
            passed = passed && (comprTypeFilter == ItemPrice.CompressionType.ALL 
                    || comprTypeFilter == price.getCompressionType());
            
            if (passed) {
                outList.add(price);
            }
        }
        
        return new ItemPriceTableModel(outList);
    }
    
    public class ItemPriceTableModel extends AbstractTableModel {
        private final List<ItemPrice> localPrices;

        public ItemPriceTableModel(List<ItemPrice> prices) {
            this.localPrices = prices;
        }
        
        
        
        @Override
        public int getRowCount() {
            return localPrices.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Item";
                    
                case 1:
                    return "Buy";
                    
                case 2:
                    return "Sell";
                    
                default:
                    return "";
                    
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= localPrices.size() || columnIndex > 2) return null;

            ItemPrice price = localPrices.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return price;

                case 1:
                    return price.getBuyPrice();

                case 2:
                    return price.getSellPrice();

                default:
                    return null;

            }
        }        
    }
}
