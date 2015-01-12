/*
 * Copyright (c) 2014, Andrey Lavrov <lavroff@gmail.com>
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

import cy.alavrov.jminerguide.App;
import cy.alavrov.jminerguide.data.DataContainer;
import cy.alavrov.jminerguide.data.api.APICharLoader;
import cy.alavrov.jminerguide.data.character.CharacterContainer;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.log.JMGLogger;
import java.awt.Image;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author alavrov
 */
public final class MainFrame extends javax.swing.JFrame {
    
    private Integer[] skillLvls = {0, 1, 2, 3, 4, 5};
    
    private DataContainer dCont;
    
    /**
     * Creates new form MainFrame
     * @param container data container with data (captain Obvious to the rescue!)
     */
    public MainFrame(DataContainer container) {        
        this.dCont = container;
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("app.png")) {  
            Image image = ImageIO.read(resourceStream);
            this.setIconImage(image);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to set application icon", e);
        }
        
        this.setTitle("JMinerGuide "+App.getVersion());                
        initComponents();                    
        this.setLocationRelativeTo(null);
        
        Integer[] skillv = {0, 1, 2, 3, 4, 5};
        
        loadMinerList(true);
    }
    
    public void loadMinerList(boolean loadSelection) {
        
        CharacterContainer cCont = dCont.getCharacterContainer();    
        
        EVECharacter sel = (EVECharacter) jComboBoxMiner.getSelectedItem();
        
        DefaultComboBoxModel<EVECharacter> model = cCont.getCharModel();
        jComboBoxMiner.setModel(model);
        
        // we're assuming here that there is always something in the combobox
        
        if (loadSelection) {
            if (sel == null) {
                jComboBoxMiner.setSelectedIndex(0);
            } else {
                jComboBoxMiner.setSelectedItem(sel);
            }
            
            loadSelectedMiner();
        }
        
    }

    public void loadSelectedMiner() {
        // if we got there, selection is not null.
        EVECharacter sel = (EVECharacter) jComboBoxMiner.getSelectedItem();
        
        if (sel.isPreset()) {
            jButtonCharReload.setEnabled(false);
            
            jComboBoxAstrogeo.setEnabled(false);
            jComboBoxDroneInt.setEnabled(false);
            jComboBoxDrones.setEnabled(false);
            jComboBoxExhumers.setEnabled(false);
            jComboBoxExpeFrig.setEnabled(false);
            jComboBoxGasHar.setEnabled(false);
            jComboBoxIceHar.setEnabled(false);
            jComboBoxMining.setEnabled(false);
            jComboBoxMiningBarge.setEnabled(false);
            jComboBoxMiningDrones.setEnabled(false);
            jComboBoxMiningFrig.setEnabled(false);
        } else {
            jButtonCharReload.setEnabled(true);
            
            jComboBoxAstrogeo.setEnabled(true);
            jComboBoxDroneInt.setEnabled(true);
            jComboBoxDrones.setEnabled(true);
            jComboBoxExhumers.setEnabled(true);
            jComboBoxExpeFrig.setEnabled(true);
            jComboBoxGasHar.setEnabled(true);
            jComboBoxIceHar.setEnabled(true);
            jComboBoxMining.setEnabled(true);
            jComboBoxMiningBarge.setEnabled(true);
            jComboBoxMiningDrones.setEnabled(true);
            jComboBoxMiningFrig.setEnabled(true);
        }
        
        jComboBoxAstrogeo.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_ASTROGEOLOGY));
        jComboBoxDroneInt.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_DRONE_INTERFACING));
        jComboBoxDrones.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_DRONES));
        jComboBoxExhumers.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_EXHUMERS));
        jComboBoxExpeFrig.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_EXPEDITION_FRIGATES));
        jComboBoxGasHar.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_GAS_CLOUD_HARVESTING));
        jComboBoxIceHar.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_ICE_HARVESTING));
        jComboBoxMining.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_MINING));
        jComboBoxMiningBarge.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_MINING_BARGE));
        jComboBoxMiningDrones.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_MINING_DRONE_OPERATION));
        jComboBoxMiningFrig.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_MINING_FRIGATE));
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        jTextField1 = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        JButtonManageAPI = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        JButtonQuit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jComboBoxMiner = new javax.swing.JComboBox<EVECharacter>();
        jButtonCharReload = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxMining = new javax.swing.JComboBox<Integer>(skillLvls);
        jComboBoxAstrogeo = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel1 = new javax.swing.JLabel();
        jComboBoxIceHar = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel2 = new javax.swing.JLabel();
        jComboBoxMiningFrig = new javax.swing.JComboBox<Integer>(skillLvls);
        jComboBoxExpeFrig = new javax.swing.JComboBox<Integer>(skillLvls);
        jComboBoxMiningBarge = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxExhumers = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel7 = new javax.swing.JLabel();
        jComboBoxDroneInt = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel8 = new javax.swing.JLabel();
        jComboBoxMiningDrones = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel9 = new javax.swing.JLabel();
        jComboBoxDrones = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jComboBoxImplant10 = new javax.swing.JComboBox();
        jComboBoxImplant8 = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jComboBoxGasHar = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel13 = new javax.swing.JLabel();
        jCheckBoxMichi = new javax.swing.JCheckBox();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setFloatable(false);

        JButtonManageAPI.setText("Manage API");
        JButtonManageAPI.setFocusable(false);
        JButtonManageAPI.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        JButtonManageAPI.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        JButtonManageAPI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButtonManageAPIActionPerformed(evt);
            }
        });
        jToolBar1.add(JButtonManageAPI);

        jButton4.setText("Prices");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 879, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        jToolBar1.add(jPanel1);

        JButtonQuit.setText("Quit");
        JButtonQuit.setFocusable(false);
        JButtonQuit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        JButtonQuit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        JButtonQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButtonQuitActionPerformed(evt);
            }
        });
        jToolBar1.add(JButtonQuit);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Ore"));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 572, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 569, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Mining Ship"));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Miner"));

        jComboBoxMiner.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMinerItemStateChanged(evt);
            }
        });

        jButtonCharReload.setText("Reload");
        jButtonCharReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCharReloadActionPerformed(evt);
            }
        });

        jLabel3.setText("Mining");

        jComboBoxMining.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMiningItemStateChanged(evt);
            }
        });

        jComboBoxAstrogeo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxAstrogeoItemStateChanged(evt);
            }
        });

        jLabel1.setText("Astrogeology");

        jComboBoxIceHar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxIceHarItemStateChanged(evt);
            }
        });

        jLabel2.setText("Ice Harvesting");

        jLabel4.setText("Mining Frigate");

        jLabel5.setText("Mining Barge");

        jLabel6.setText("Expedition Frigates");

        jLabel7.setText("Exhumers");

        jLabel8.setText("Drone Interfacing");

        jComboBoxMiningDrones.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMiningDronesItemStateChanged(evt);
            }
        });

        jLabel9.setText("Mining Drone Operation");

        jComboBoxDrones.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxDronesItemStateChanged(evt);
            }
        });

        jLabel10.setText("Drones");

        jLabel11.setText("Slot 10");

        jLabel12.setText("Slot 8");

        jComboBoxGasHar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxGasHarItemStateChanged(evt);
            }
        });

        jLabel13.setText("Gas Cloud Harvesting");

        jCheckBoxMichi.setText("Michi's Excavation Augmentor");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 1, Short.MAX_VALUE)
                        .addComponent(jComboBoxMiner, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonCharReload))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBoxGasHar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxAstrogeo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxMining, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxIceHar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBoxMiningDrones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBoxDrones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(41, 41, 41)
                                .addComponent(jComboBoxDroneInt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(79, 79, 79)
                                .addComponent(jComboBoxExhumers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(34, 34, 34)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxMiningFrig, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxExpeFrig, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxMiningBarge, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxImplant8, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxImplant10, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBoxMichi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxMiner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCharReload, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxMining, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMiningFrig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxAstrogeo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxExpeFrig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxIceHar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxMiningBarge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxExhumers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxDrones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxDroneInt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBoxMiningDrones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxGasHar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jComboBoxImplant8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxImplant10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxMichi)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void JButtonQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButtonQuitActionPerformed
        dCont.save();
        JQuitDialog dlg = new JQuitDialog(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_JButtonQuitActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        dCont.save();
        JQuitDialog dlg = new JQuitDialog(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void JButtonManageAPIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButtonManageAPIActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JAPIDialog dlg = new JAPIDialog(MainFrame.this, true, dCont);
                dlg.setLocationRelativeTo(MainFrame.this);
                dlg.setVisible(true);
            }
        });
    }//GEN-LAST:event_JButtonManageAPIActionPerformed

    private void jComboBoxMinerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMinerItemStateChanged
        loadSelectedMiner();
    }//GEN-LAST:event_jComboBoxMinerItemStateChanged

    private void jButtonCharReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCharReloadActionPerformed
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
                
        final JWaitDialog dlg = new JWaitDialog(MainFrame.this, true, "Character Data");
        
        // As setVisible blocks until the dialog is closed,
        // we'll have to run it in a different thread.        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {  
                synchronized(dlg) {
                    // hypotetically, we can find ourself in a situation
                    // where loader stopped worked already, and dialog
                    // still haven't showed up. practically, we shouldn't,
                    // but because of THREADS, we'd better be overparanoid here.
                    if (!dlg.isFinished()) {
                        dlg.setLocationRelativeTo(MainFrame.this);
                        dlg.setVisible(true);
                    }
                }
            }
        });
        
        APICharLoader loader = new APICharLoader(curChar, dlg);
        dCont.startAPILoader(loader);
    }//GEN-LAST:event_jButtonCharReloadActionPerformed

    private void jComboBoxMiningItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMiningItemStateChanged
        Integer level = (Integer) jComboBoxMining.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_MINING, level);
        
        if (level < 4) {
            curChar.setSkillLevel(EVECharacter.SKILL_ASTROGEOLOGY, 0);
            jComboBoxAstrogeo.setSelectedItem(0);
            curChar.setSkillLevel(EVECharacter.SKILL_GAS_CLOUD_HARVESTING, 0);
            jComboBoxGasHar.setSelectedItem(0);
            curChar.setSkillLevel(EVECharacter.SKILL_ICE_HARVESTING, 0);
            jComboBoxIceHar.setSelectedItem(0);
            curChar.setSkillLevel(EVECharacter.SKILL_MINING_BARGE, 0);
            jComboBoxMiningBarge.setSelectedItem(0);
            curChar.setSkillLevel(EVECharacter.SKILL_EXHUMERS, 0);
            jComboBoxExhumers.setSelectedItem(0);
        }
        
        if (level < 2) {            
            curChar.setSkillLevel(EVECharacter.SKILL_MINING_DRONE_OPERATION, 0);
            jComboBoxMiningDrones.setSelectedItem(0);
        }
    }//GEN-LAST:event_jComboBoxMiningItemStateChanged

    private void jComboBoxAstrogeoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxAstrogeoItemStateChanged
        Integer level = (Integer) jComboBoxAstrogeo.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_ASTROGEOLOGY, level);
        
        if (level < 5) {
            curChar.setSkillLevel(EVECharacter.SKILL_EXHUMERS, 0);
            jComboBoxExhumers.setSelectedItem(0);            
        }
        
        if (level < 3) {
            curChar.setSkillLevel(EVECharacter.SKILL_MINING_BARGE, 0);
            jComboBoxMiningBarge.setSelectedItem(0);           
        }
        
        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING) < 4) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING, 4);
                jComboBoxMining.setSelectedItem(4);
            }
        }
    }//GEN-LAST:event_jComboBoxAstrogeoItemStateChanged

    private void jComboBoxIceHarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxIceHarItemStateChanged
        Integer level = (Integer) jComboBoxIceHar.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_ICE_HARVESTING, level);
        
        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING) < 4) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING, 4);
                jComboBoxMining.setSelectedItem(4);
            }
        }
    }//GEN-LAST:event_jComboBoxIceHarItemStateChanged

    private void jComboBoxDronesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxDronesItemStateChanged
        Integer level = (Integer) jComboBoxDrones.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_DRONES, level);
        
        if (level < 5) {
            curChar.setSkillLevel(EVECharacter.SKILL_DRONE_INTERFACING, 0);
            jComboBoxDroneInt.setSelectedItem(0);            
        }
        
        if (level < 1) {
            curChar.setSkillLevel(EVECharacter.SKILL_MINING_DRONE_OPERATION, 0);
            jComboBoxMiningDrones.setSelectedItem(0);            
        }
    }//GEN-LAST:event_jComboBoxDronesItemStateChanged

    private void jComboBoxMiningDronesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMiningDronesItemStateChanged
        Integer level = (Integer) jComboBoxMiningDrones.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_MINING_DRONE_OPERATION, level);
        
        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING) < 2) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING, 2);
                jComboBoxMining.setSelectedItem(2);
            }
            
            if (curChar.getSkillLevel(EVECharacter.SKILL_DRONES) < 1) {
                curChar.setSkillLevel(EVECharacter.SKILL_DRONES, 1);
                jComboBoxDrones.setSelectedItem(1);
            }
        }
    }//GEN-LAST:event_jComboBoxMiningDronesItemStateChanged

    private void jComboBoxGasHarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxGasHarItemStateChanged
        Integer level = (Integer) jComboBoxGasHar.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_GAS_CLOUD_HARVESTING, level);
        
        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING) < 4) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING, 4);
                jComboBoxMining.setSelectedItem(4);
            }
        }
    }//GEN-LAST:event_jComboBoxGasHarItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JButtonManageAPI;
    private javax.swing.JButton JButtonQuit;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonCharReload;
    private javax.swing.JCheckBox jCheckBoxMichi;
    private javax.swing.JComboBox<Integer> jComboBoxAstrogeo;
    private javax.swing.JComboBox<Integer> jComboBoxDroneInt;
    private javax.swing.JComboBox<Integer> jComboBoxDrones;
    private javax.swing.JComboBox<Integer> jComboBoxExhumers;
    private javax.swing.JComboBox<Integer> jComboBoxExpeFrig;
    private javax.swing.JComboBox<Integer> jComboBoxGasHar;
    private javax.swing.JComboBox<Integer> jComboBoxIceHar;
    private javax.swing.JComboBox jComboBoxImplant10;
    private javax.swing.JComboBox jComboBoxImplant8;
    private javax.swing.JComboBox<EVECharacter> jComboBoxMiner;
    private javax.swing.JComboBox<Integer> jComboBoxMining;
    private javax.swing.JComboBox<Integer> jComboBoxMiningBarge;
    private javax.swing.JComboBox<Integer> jComboBoxMiningDrones;
    private javax.swing.JComboBox<Integer> jComboBoxMiningFrig;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
