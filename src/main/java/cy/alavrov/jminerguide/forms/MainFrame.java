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
import cy.alavrov.jminerguide.data.api.ship.Hull;
import cy.alavrov.jminerguide.data.api.ship.MiningCrystalLevel;
import cy.alavrov.jminerguide.data.api.ship.Ship;
import cy.alavrov.jminerguide.data.api.ship.Turret;
import cy.alavrov.jminerguide.data.character.CharacterContainer;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.data.implant.Implant;
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
     * JComboBox and JCheckBox fire off event on setSelectedItem, and we don't need
     * to react to it when we are the source of the change.
     */
    private volatile boolean processEvents = false;
    
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
        
        loadMinerList(true);
        loadShip();
        
        processEvents = true;
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
        
        jComboBoxImplant8.setSelectedItem(sel.getSlot8Implant());
        jComboBoxImplant10.setSelectedItem(sel.getSlot10Implant());
        
        jCheckBoxMichi.setSelected(sel.getSlot7Implant() == Implant.MICHI);
    }
    
    public void loadShip() {
        Ship ship = dCont.getShip();
        Hull hull = ship.getHull();
        
        jComboBoxHull.setSelectedItem(hull);
        
        jComboBoxTurrets.setModel(getIntegerModel(hull.getMaxTurrets()));
        jComboBoxTurrets.setSelectedItem(ship.getTurretCount());
        
        updateTurretComboBox(hull);
        jComboBoxTurretType.setSelectedItem(ship.getTurret());
        updateCrystalComboBox(ship.getTurret());
        jComboBoxCrystal.setSelectedItem(ship.getTurretCrystal());
    }
    
    public DefaultComboBoxModel<Integer> getIntegerModel(int upto) {
        DefaultComboBoxModel<Integer> out = new DefaultComboBoxModel<>();
        
        if (upto > 0) {
            for (int i = upto; i > 0; i-- ) {
                out.addElement(i);
            }
        }
        out.addElement(0);
        
        return out;
    }
    
    /**
     * Updates turret combo box with appropriate turrets based on a hull's 
     * support for strip miners.
     * @param hull 
     */
    public void updateTurretComboBox(Hull hull) {
        Turret[] turrets;
        if (hull.isUsingStripMiners()) {
            turrets = Turret.bigTurrets;
        } else {
            turrets = Turret.smallTurrets;
        }
        
        jComboBoxTurretType.setModel(new DefaultComboBoxModel<>(turrets));
    }
    
    /**
     * Updates turret crystal combo box enabled state according to turret's
     * ability to use crystals.
     * @param turret 
     */
    public void updateCrystalComboBox(Turret turret) {
        if (turret.isUsingCrystals()) {
            if (!jComboBoxCrystal.isEnabled()) jComboBoxCrystal.setEnabled(true);
        } else {
            if (jComboBoxCrystal.isEnabled()) jComboBoxCrystal.setEnabled(false);
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

        jSeparator2 = new javax.swing.JSeparator();
        jTextField1 = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        JButtonManageAPI = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        JButtonQuit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        JLabel14 = new javax.swing.JLabel();
        jComboBoxHull = new javax.swing.JComboBox<Hull>(Hull.values());
        jComboBoxTurrets = new javax.swing.JComboBox<Integer>();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jComboBoxTurretType = new javax.swing.JComboBox<Turret>();
        jComboBoxCrystal = new javax.swing.JComboBox<MiningCrystalLevel>(MiningCrystalLevel.values());
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jComboBoxHUpgradeType = new javax.swing.JComboBox();
        jComboBoxHUpgrades = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel31 = new javax.swing.JLabel();
        jComboBoxRig1 = new javax.swing.JComboBox();
        jLabel33 = new javax.swing.JLabel();
        jTextFieldTrip = new javax.swing.JTextField();
        jCheckBoxHauler = new javax.swing.JCheckBox();
        jComboBoxRig2 = new javax.swing.JComboBox();
        jComboBoxRig3 = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
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
        jComboBoxImplant10 = new javax.swing.JComboBox<Implant>(Implant.slot10Imps);
        jComboBoxImplant8 = new javax.swing.JComboBox<Implant>(Implant.slot8Imps);
        jLabel12 = new javax.swing.JLabel();
        jComboBoxGasHar = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel13 = new javax.swing.JLabel();
        jCheckBoxMichi = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabelYield = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabelDroneYield = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabelM3S = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabelM3H = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabelCargo = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabelCycle = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabelDroneCycle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabelDroneM3S = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabelOptimal = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabelCargoFill = new javax.swing.JLabel();

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
            .addGap(0, 997, Short.MAX_VALUE)
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 691, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Mining Ship"));

        JLabel14.setText("Hull");

        jComboBoxHull.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxHullItemStateChanged(evt);
            }
        });

        jComboBoxTurrets.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTurretsItemStateChanged(evt);
            }
        });

        jLabel14.setText("Turrets");

        jLabel15.setText("Turret Type");

        jComboBoxTurretType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTurretTypeItemStateChanged(evt);
            }
        });

        jComboBoxCrystal.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCrystalItemStateChanged(evt);
            }
        });

        jLabel16.setText("Crystal");

        jLabel17.setText("Harvesting Upgrade Type");

        jLabel18.setText("Upgrades");

        jLabel24.setText("Drone Type");

        jLabel26.setText("Drones");

        jLabel31.setText("Rig 1");

        jLabel33.setText("Station Trip, sec:");

        jCheckBoxHauler.setText("Dedicated Hauler");

        jLabel34.setText("Rig 2");

        jLabel35.setText("Rig 3");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(JLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jComboBoxHull, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(26, 26, 26)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jComboBoxTurrets, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15)
                            .addComponent(jLabel17)
                            .addComponent(jLabel24)
                            .addComponent(jComboBox1, 0, 264, Short.MAX_VALUE)
                            .addComponent(jComboBoxHUpgradeType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxTurretType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxHUpgrades, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel26)
                                            .addComponent(jLabel18))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jComboBoxCrystal, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTrip, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxHauler)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxRig1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addGap(0, 93, Short.MAX_VALUE))
                            .addComponent(jComboBoxRig2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(jComboBoxRig3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabel14)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxHull, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxTurrets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxTurretType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCrystal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxHUpgradeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxHUpgrades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxRig1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxRig2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxRig3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jTextFieldTrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxHauler))
                .addContainerGap(11, Short.MAX_VALUE))
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

        jComboBoxMiningFrig.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMiningFrigItemStateChanged(evt);
            }
        });

        jComboBoxExpeFrig.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxExpeFrigItemStateChanged(evt);
            }
        });

        jComboBoxMiningBarge.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMiningBargeItemStateChanged(evt);
            }
        });

        jLabel4.setText("Mining Frigate");

        jLabel5.setText("Mining Barge");

        jLabel6.setText("Expedition Frigates");

        jComboBoxExhumers.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxExhumersItemStateChanged(evt);
            }
        });

        jLabel7.setText("Exhumers");

        jComboBoxDroneInt.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxDroneIntItemStateChanged(evt);
            }
        });

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

        jComboBoxImplant10.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxImplant10ItemStateChanged(evt);
            }
        });

        jComboBoxImplant8.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxImplant8ItemStateChanged(evt);
            }
        });

        jLabel12.setText("Slot 8");

        jComboBoxGasHar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxGasHarItemStateChanged(evt);
            }
        });

        jLabel13.setText("Gas Cloud Harvesting");

        jCheckBoxMichi.setText("Michi's Excavation Augmentor");
        jCheckBoxMichi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMichiItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Booster"));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 473, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Stats"));

        jLabel19.setText("Yield, m3:");

        jLabelYield.setText("0");

        jLabel28.setText("Drone Yield, m3:");

        jLabelDroneYield.setText("0");

        jLabel20.setText("m3/s");

        jLabelM3S.setText("0");

        jLabel23.setText("m3/h:");

        jLabelM3H.setText("0");

        jLabel25.setText("Cargo, m3:");

        jLabelCargo.setText("0");

        jLabel21.setText("Cycle, sec:");

        jLabelCycle.setText("0");

        jLabel29.setText("Drone Cycle, sec:");

        jLabelDroneCycle.setText("0");

        jLabel30.setText("Drone m3/s");

        jLabelDroneM3S.setText("0");

        jLabel22.setText("Optimal, km:");

        jLabelOptimal.setText("0");

        jLabel27.setText("Cargo Fills In, min:");

        jLabelCargoFill.setText("0");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(jLabelCargoFill, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelM3S, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDroneYield, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDroneCycle, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDroneM3S, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelM3H, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelOptimal, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelYield, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                            .addComponent(jLabelCycle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabelYield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabelCycle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabelM3S))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabelDroneYield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jLabelDroneCycle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jLabelDroneM3S))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelM3H)
                    .addComponent(jLabel23))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabelOptimal))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabelCargo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabelCargoFill))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        if (!processEvents) return;
        
        processEvents = false;
        
        loadSelectedMiner();
        
        processEvents = true;
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
        if (!processEvents) return;
        
        processEvents = false;
        
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
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxMiningItemStateChanged

    private void jComboBoxAstrogeoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxAstrogeoItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
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
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxAstrogeoItemStateChanged

    private void jComboBoxIceHarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxIceHarItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
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
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxIceHarItemStateChanged

    private void jComboBoxDronesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxDronesItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
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
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxDronesItemStateChanged

    private void jComboBoxMiningDronesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMiningDronesItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
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
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxMiningDronesItemStateChanged

    private void jComboBoxGasHarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxGasHarItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
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
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxGasHarItemStateChanged

    private void jComboBoxMiningFrigItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMiningFrigItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Integer level = (Integer) jComboBoxMiningFrig.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_MINING_FRIGATE, level);
        
        if (level < 3) {
            curChar.setSkillLevel(EVECharacter.SKILL_MINING_BARGE, 0);
            jComboBoxMiningBarge.setSelectedItem(0);           
        }
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxMiningFrigItemStateChanged

    private void jComboBoxExpeFrigItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxExpeFrigItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Integer level = (Integer) jComboBoxExpeFrig.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_EXPEDITION_FRIGATES, level);
        // The most independent skill of them all.
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxExpeFrigItemStateChanged

    private void jComboBoxMiningBargeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMiningBargeItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Integer level = (Integer) jComboBoxMiningBarge.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_MINING_BARGE, level);
        
        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING) < 4) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING, 4);
                jComboBoxMining.setSelectedItem(4);
            }
            
            if (curChar.getSkillLevel(EVECharacter.SKILL_ASTROGEOLOGY) < 3) {
                curChar.setSkillLevel(EVECharacter.SKILL_ASTROGEOLOGY, 3);
                jComboBoxAstrogeo.setSelectedItem(3);
            }
            
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING_FRIGATE) < 3) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING_FRIGATE, 3);
                jComboBoxMiningFrig.setSelectedItem(3);
            }
        }
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxMiningBargeItemStateChanged

    private void jComboBoxExhumersItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxExhumersItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Integer level = (Integer) jComboBoxExhumers.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_EXHUMERS, level);
        
        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING) < 4) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING, 4);
                jComboBoxMining.setSelectedItem(4);
            }
            
            if (curChar.getSkillLevel(EVECharacter.SKILL_ASTROGEOLOGY) < 5) {
                curChar.setSkillLevel(EVECharacter.SKILL_ASTROGEOLOGY, 5);
                jComboBoxAstrogeo.setSelectedItem(5);
            }
        }
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxExhumersItemStateChanged

    private void jComboBoxDroneIntItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxDroneIntItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Integer level = (Integer) jComboBoxDroneInt.getSelectedItem();
                                                       
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;
        
        curChar.setSkillLevel(EVECharacter.SKILL_DRONE_INTERFACING, level);
        
        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_DRONES) < 5) {
                curChar.setSkillLevel(EVECharacter.SKILL_DRONES, 5);
                jComboBoxDrones.setSelectedItem(5);
            }
        }
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxDroneIntItemStateChanged

    private void jComboBoxImplant8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxImplant8ItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Implant imp = (Implant) jComboBoxImplant8.getSelectedItem();
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null) return;
        
        curChar.setSlot8Implant(imp);
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxImplant8ItemStateChanged

    private void jComboBoxImplant10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxImplant10ItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Implant imp = (Implant) jComboBoxImplant10.getSelectedItem();
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null) return;
        
        curChar.setSlot10Implant(imp);
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxImplant10ItemStateChanged

    private void jCheckBoxMichiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMichiItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;     
        
        boolean checked = jCheckBoxMichi.isSelected();
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null) return;
        
        curChar.setSlot7Implant(checked? Implant.MICHI : Implant.NOTHING);
        
        processEvents = true;
    }//GEN-LAST:event_jCheckBoxMichiItemStateChanged

    private void jComboBoxHullItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxHullItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Ship ship = dCont.getShip();
        Hull newHull = (Hull) jComboBoxHull.getSelectedItem();
        ship.setHull(newHull);
        
        newHull = ship.getHull();
        
        jComboBoxTurrets.setModel(getIntegerModel(newHull.getMaxTurrets()));
        jComboBoxTurrets.setSelectedItem(ship.getTurretCount());
        
        Turret curTurret = jComboBoxTurretType.getItemAt(0);
        if (curTurret.getTurretType().isStripMiner() 
                != ship.getTurret().getTurretType().isStripMiner()) {
            updateTurretComboBox(newHull);                        
        }
        
        jComboBoxTurretType.setSelectedItem(ship.getTurret());
        updateCrystalComboBox(ship.getTurret());
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxHullItemStateChanged

    private void jComboBoxTurretsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTurretsItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Ship ship = dCont.getShip();
        Integer turretcnt = (Integer) jComboBoxTurrets.getSelectedItem();
        ship.setTurrentCount(turretcnt);   
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxTurretsItemStateChanged

    private void jComboBoxTurretTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTurretTypeItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Ship ship = dCont.getShip();
        Turret newTurret = (Turret) (jComboBoxTurretType.getSelectedItem());        
        ship.setTurret(newTurret);  
        newTurret = ship.getTurret();
        
        updateCrystalComboBox(newTurret);
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxTurretTypeItemStateChanged

    private void jComboBoxCrystalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCrystalItemStateChanged
        if (!processEvents) return;
        
        processEvents = false;
        
        Ship ship = dCont.getShip();
        MiningCrystalLevel crystal = (MiningCrystalLevel) jComboBoxCrystal.getSelectedItem();
        ship.setTurretCrystal(crystal);
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxCrystalItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JButtonManageAPI;
    private javax.swing.JButton JButtonQuit;
    private javax.swing.JLabel JLabel14;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonCharReload;
    private javax.swing.JCheckBox jCheckBoxHauler;
    private javax.swing.JCheckBox jCheckBoxMichi;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox<Integer> jComboBoxAstrogeo;
    private javax.swing.JComboBox<MiningCrystalLevel> jComboBoxCrystal;
    private javax.swing.JComboBox<Integer> jComboBoxDroneInt;
    private javax.swing.JComboBox<Integer> jComboBoxDrones;
    private javax.swing.JComboBox<Integer> jComboBoxExhumers;
    private javax.swing.JComboBox<Integer> jComboBoxExpeFrig;
    private javax.swing.JComboBox<Integer> jComboBoxGasHar;
    private javax.swing.JComboBox jComboBoxHUpgradeType;
    private javax.swing.JComboBox jComboBoxHUpgrades;
    private javax.swing.JComboBox<Hull> jComboBoxHull;
    private javax.swing.JComboBox<Integer> jComboBoxIceHar;
    private javax.swing.JComboBox<Implant> jComboBoxImplant10;
    private javax.swing.JComboBox<Implant> jComboBoxImplant8;
    private javax.swing.JComboBox<EVECharacter> jComboBoxMiner;
    private javax.swing.JComboBox<Integer> jComboBoxMining;
    private javax.swing.JComboBox<Integer> jComboBoxMiningBarge;
    private javax.swing.JComboBox<Integer> jComboBoxMiningDrones;
    private javax.swing.JComboBox<Integer> jComboBoxMiningFrig;
    private javax.swing.JComboBox jComboBoxRig1;
    private javax.swing.JComboBox jComboBoxRig2;
    private javax.swing.JComboBox jComboBoxRig3;
    private javax.swing.JComboBox<Turret> jComboBoxTurretType;
    private javax.swing.JComboBox<Integer> jComboBoxTurrets;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCargo;
    private javax.swing.JLabel jLabelCargoFill;
    private javax.swing.JLabel jLabelCycle;
    private javax.swing.JLabel jLabelDroneCycle;
    private javax.swing.JLabel jLabelDroneM3S;
    private javax.swing.JLabel jLabelDroneYield;
    private javax.swing.JLabel jLabelM3H;
    private javax.swing.JLabel jLabelM3S;
    private javax.swing.JLabel jLabelOptimal;
    private javax.swing.JLabel jLabelYield;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldTrip;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
