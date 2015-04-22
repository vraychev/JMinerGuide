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
package cy.alavrov.jminerguide.forms;

import cy.alavrov.jminerguide.data.DataContainer;
import cy.alavrov.jminerguide.data.price.ItemPrice;
import cy.alavrov.jminerguide.data.price.ItemPriceContainer;
import cy.alavrov.jminerguide.data.price.ItemPriceContainer.ItemPriceTableModel;
import cy.alavrov.jminerguide.data.universe.MarketZone;
import cy.alavrov.jminerguide.data.universe.MarketZoneContainer;
import cy.alavrov.jminerguide.log.JMGLogger;
import cy.alavrov.jminerguide.util.IntegerDocumentFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AbstractDocument;

/**
 * A dialog to setup prices.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class JPriceSetupDialog extends javax.swing.JDialog {
    private final DataContainer dCont;
    private final MainFrame parent;
    
    private volatile boolean processEvents = false;
    
    /**
     * Creates new form JPriceSetupDialog
     */
    public JPriceSetupDialog(MainFrame parent, DataContainer dCont) {
        super(parent, true);
        initComponents();
        
        this.parent = parent;
        this.dCont = dCont;
        
        AbstractDocument idDoc = ((AbstractDocument)jTextFieldBuy.getDocument());
        idDoc.setDocumentFilter(new IntegerDocumentFilter());
        
        idDoc = ((AbstractDocument)jTextFieldSell.getDocument());
        idDoc.setDocumentFilter(new IntegerDocumentFilter());
        
        ItemPriceContainer iCont = dCont.getItemPriceContainer();
        MarketZoneContainer mCont = dCont.getMarketZoneContainer();
        
        jComboBoxItemTypeFilter.setModel(ItemPrice.ItemType.getModel());
        jComboBoxCompressedFilter.setModel(ItemPrice.CompressionType.getModel());        
        
        ItemPrice.ItemType itype = (ItemPrice.ItemType) jComboBoxItemTypeFilter.getSelectedItem();
        ItemPrice.CompressionType ctype = (ItemPrice.CompressionType) jComboBoxCompressedFilter.getSelectedItem();
        
        setPrices(iCont.getTableModel(itype, ctype));
        jComboBoxMarketZone.setModel(mCont.getComboBoxModel());                
        
        jTablePrices.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                // we should trigger event processor only on a final mouse release.
                // that's when getValueIsAdjusting will return false, on a first
                // click or dragging it will be true.
                if(!e.getValueIsAdjusting()) {
                    jTablePricesRowSelected();
                }
            }
        });
        
        checkTableSelection();
        
        processEvents = true;
    }

    private void setPrices(ItemPriceTableModel model) {
        jTablePrices.setModel(model);
        
        jTablePrices.getColumnModel().getColumn(0).setResizable(false);
        jTablePrices.getColumnModel().getColumn(0).setPreferredWidth(200);
        jTablePrices.getColumnModel().getColumn(1).setResizable(false);
        jTablePrices.getColumnModel().getColumn(1).setPreferredWidth(50);
        jTablePrices.getColumnModel().getColumn(2).setResizable(false);
        jTablePrices.getColumnModel().getColumn(2).setPreferredWidth(50);
    }
    
    private void filterPriceList() {
        ItemPrice.ItemType itype = (ItemPrice.ItemType) jComboBoxItemTypeFilter.getSelectedItem();
        ItemPrice.CompressionType ctype = (ItemPrice.CompressionType) jComboBoxCompressedFilter.getSelectedItem();
        
        ItemPriceContainer iCont = dCont.getItemPriceContainer();
        setPrices(iCont.getTableModel(itype, ctype));
    }
    
    private ItemPrice getSelectedItemPrice() {        
        int row = jTablePrices.getSelectedRow();
        if (row == -1) return null;
        
        try {
            return (ItemPrice) jTablePrices.getModel().getValueAt(row, 0);
        } catch (ClassCastException e) {
            JMGLogger.logSevere("Unable to get item price", e);
            return null;
        }
    }
    
    private void jTablePricesRowSelected() {
        if (!processEvents) return;
        processEvents = false;
        
        checkTableSelection();
        processEvents = true;
    }
    
    private void checkTableSelection() {
        ItemPrice price = getSelectedItemPrice();
        if (price == null) {
            jTextFieldBuy.setEnabled(false);
            jTextFieldBuy.setText("0");
            jTextFieldSell.setEnabled(false);
            jTextFieldSell.setText("0");
            
            jButtonPriceUpdate.setEnabled(false);
        } else {
            jTextFieldBuy.setEnabled(true);
            jTextFieldBuy.setText(String.valueOf(price.getBuyPrice()));
            jTextFieldSell.setEnabled(true);
            jTextFieldSell.setText(String.valueOf(price.getSellPrice()));
            
            jButtonPriceUpdate.setEnabled(true);
        }
    }
    
    private void updateItemPrices() {
        ItemPrice price = getSelectedItemPrice();
        if (price != null) {
            try {
                String buyStr = jTextFieldBuy.getText();
                if (buyStr == null || buyStr.isEmpty()) buyStr = "0";
                String sellStr = jTextFieldSell.getText();
                if (sellStr == null || sellStr.isEmpty()) sellStr = "0";
                
                Integer newBuyPrice = Integer.parseInt(buyStr, 10);
                if (newBuyPrice < 0) newBuyPrice = 0;                                
                Integer newSellPrice = Integer.parseInt(sellStr, 10);
                if (newSellPrice < 0) newBuyPrice = 0;                
                
                price.setBuyPrice(newBuyPrice);
                price.setSellPrice(newSellPrice);
                                
                int row = jTablePrices.getSelectedRow();
                ((AbstractTableModel)jTablePrices.getModel()).fireTableRowsUpdated(row, row);
            } catch (Exception e) {
                JMGLogger.logSevere("Unable to update item prices", e);            
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBoxMarketZone = new javax.swing.JComboBox<MarketZone>();
        jButtonLoad = new javax.swing.JButton();
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablePrices = new javax.swing.JTable();
        jTextFieldBuy = new javax.swing.JTextField();
        jTextFieldSell = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxItemTypeFilter = new javax.swing.JComboBox<ItemPrice.ItemType>();
        jComboBoxCompressedFilter = new javax.swing.JComboBox<ItemPrice.CompressionType>();
        jButtonPriceUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Setup Prices");
        setModal(true);
        setResizable(false);

        jLabel1.setText("Region:");

        jButtonLoad.setText("Load");

        jButtonOK.setText("OK");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jTablePrices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Item", "Buy", "Sell"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablePrices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTablePrices);
        if (jTablePrices.getColumnModel().getColumnCount() > 0) {
            jTablePrices.getColumnModel().getColumn(0).setResizable(false);
            jTablePrices.getColumnModel().getColumn(1).setResizable(false);
            jTablePrices.getColumnModel().getColumn(2).setResizable(false);
        }

        jTextFieldBuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBuyActionPerformed(evt);
            }
        });

        jTextFieldSell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSellActionPerformed(evt);
            }
        });

        jLabel2.setText("Buy");

        jLabel3.setText("Sell");

        jLabel4.setText("Type");

        jLabel5.setText("Compressed");

        jComboBoxItemTypeFilter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxItemTypeFilterItemStateChanged(evt);
            }
        });

        jComboBoxCompressedFilter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCompressedFilterItemStateChanged(evt);
            }
        });

        jButtonPriceUpdate.setText("Update");
        jButtonPriceUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPriceUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(24, 24, 24)
                        .addComponent(jTextFieldSell, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonPriceUpdate))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxMarketZone, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonLoad))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBoxItemTypeFilter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxCompressedFilter, 0, 352, Short.MAX_VALUE))))
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxItemTypeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBoxCompressedFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldBuy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldSell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jButtonPriceUpdate))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonCancel)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxMarketZone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLoad))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonPriceUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPriceUpdateActionPerformed
        if (!processEvents) return;
        processEvents = false;
        
        updateItemPrices();
        
        processEvents = true;
    }//GEN-LAST:event_jButtonPriceUpdateActionPerformed

    private void jTextFieldBuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBuyActionPerformed
        if (!processEvents) return;
        processEvents = false;
        
        updateItemPrices();
        
        processEvents = true;
    }//GEN-LAST:event_jTextFieldBuyActionPerformed

    private void jTextFieldSellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSellActionPerformed
        if (!processEvents) return;
        processEvents = false;
        
        updateItemPrices();
        
        processEvents = true;
    }//GEN-LAST:event_jTextFieldSellActionPerformed

    private void jComboBoxItemTypeFilterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxItemTypeFilterItemStateChanged
        if (!processEvents) return;
        processEvents = false;
        
        filterPriceList();
        checkTableSelection();
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxItemTypeFilterItemStateChanged

    private void jComboBoxCompressedFilterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCompressedFilterItemStateChanged
        if (!processEvents) return;
        processEvents = false;
        
        filterPriceList();
        checkTableSelection();
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxCompressedFilterItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonLoad;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonPriceUpdate;
    private javax.swing.JComboBox<ItemPrice.CompressionType> jComboBoxCompressedFilter;
    private javax.swing.JComboBox<ItemPrice.ItemType> jComboBoxItemTypeFilter;
    private javax.swing.JComboBox<MarketZone> jComboBoxMarketZone;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTablePrices;
    private javax.swing.JTextField jTextFieldBuy;
    private javax.swing.JTextField jTextFieldSell;
    // End of variables declaration//GEN-END:variables
}
