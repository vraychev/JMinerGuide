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

import cy.alavrov.jminerguide.log.JMGLogger;
import org.jdom2.Element;

/**
 * Prices for an item of a given ID.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class ItemPrice {
    private final int itemID;
    private int buyPrice;
    private int sellPrice;

    public ItemPrice(int itemID) {
        this.itemID = itemID;
        this.buyPrice = 0;
        this.sellPrice = 0;
    }

    public int getItemID() {
        return itemID;
    }

    public synchronized int getBuyPrice() {
        return buyPrice;
    }

    public synchronized int getSellPrice() {
        return sellPrice;
    }

    public synchronized void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public synchronized void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    public synchronized Element getXMLElement() {
        Element root = new Element("itemprice");
        
        root.setAttribute("itemid", String.valueOf(itemID));
        root.setAttribute("buy", String.valueOf(buyPrice));
        root.setAttribute("sell", String.valueOf(sellPrice));
        
        return root;
    }
    
    public synchronized void updatePrice(Element elem) {
        try {
            if (elem.getAttribute("itemid").getIntValue() != itemID) return;
            
            int newBuy = elem.getAttribute("buy").getIntValue();
            int newSell = elem.getAttribute("sell").getIntValue();
            
            buyPrice = newBuy;
            sellPrice = newSell;
        } catch (Exception e) {
            JMGLogger.logWarning("Unable to update price for item #"+itemID, e);
        }        
    }
}
