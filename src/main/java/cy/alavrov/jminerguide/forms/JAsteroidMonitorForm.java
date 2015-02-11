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

import cy.alavrov.jminerguide.data.CalculatedStats;
import cy.alavrov.jminerguide.data.DataContainer;
import cy.alavrov.jminerguide.data.booster.BoosterShip;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.data.ship.Ship;
import cy.alavrov.jminerguide.log.JMGLogger;
import cy.alavrov.jminerguide.monitor.MiningSession;
import cy.alavrov.jminerguide.monitor.MiningSessionMonitor;
import cy.alavrov.jminerguide.monitor.SessionCharacter;
import cy.alavrov.jminerguide.monitor.UpdateWindowTask;
import cy.alavrov.jminerguide.util.IntegerDocumentFilter;
import cy.alavrov.jminerguide.util.winmanager.IWindowManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.text.AbstractDocument;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Asteroid harvesting monitor and EVE instance selector.
 * @author Andrey Lavrov <lavroff@gmail.com>
 */
public class JAsteroidMonitorForm extends javax.swing.JFrame {
    private final static int WINDOW_LOSS_TIMEOUT = 500;
    
    private final static DecimalFormat fmt = new DecimalFormat("0.##");
    private final static PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
     .printZeroAlways()
     .appendMinutes()
     .appendSeparator(":")
     .appendSeconds()
     .toFormatter();
    
    private final MainFrame parent;
    private final DataContainer dCont;
    private final IWindowManager wManager;
    private final MiningSessionMonitor msMonitor;
    
    private final ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(2);
    
    private volatile String currentMiner = null;
    private volatile MiningSession currentSession = null;
    private volatile List<MiningSession> currentSessions;
    
    private volatile long loseOnTopAt = 0;
    private volatile boolean shouldLooseOnTop = false;
    
    private volatile boolean processEvents = false;

    private volatile JLoadScanDialog lsDlog = null;
    
    /**
     * Creates new form JAsteroidMonitorDialog
     */
    public JAsteroidMonitorForm(MainFrame parent, DataContainer dCont, IWindowManager wManager) {        
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("asteroid.png")) {
            Image image = ImageIO.read(resourceStream);
            this.setIconImage(image);
        } catch (Exception e) {
            JMGLogger.logSevere("Unable to set application icon", e);
        }
        this.setTitle("Asteroid Monitor");        
        initComponents();
        
        AbstractDocument idDoc = ((AbstractDocument)jTextFieldHold.getDocument());
        idDoc.setDocumentFilter(new IntegerDocumentFilter());
        
        this.parent = parent;
        this.dCont = dCont;
        this.wManager = wManager;
        this.msMonitor = new MiningSessionMonitor(wManager, dCont);
        
        jComboBoxShip.setModel(dCont.getShipContainer().getShipModel());
        jComboBoxBooster.setModel(dCont.getCharacterContainer().getCharModel());
        jComboBoxBoosterShip.setModel(dCont.getBoosterShipContainer().getBoosterShipModel());
        
        timer.scheduleWithFixedDelay(new UpdateWindowTask(msMonitor, this), 100, 100, TimeUnit.MILLISECONDS);
        disableMonitorPanel();
        processEvents = true;
    }

    public void updateCurrentSession() {
        processEvents = false; 
        
        MiningSession session = msMonitor.getCurrentSession();
        if (session == null) {            
            // wait a few milliseconds to lose always on top to combat flickering.
            if (this.isAlwaysOnTop() && !shouldLooseOnTop) {
                shouldLooseOnTop = true;
                loseOnTopAt = System.currentTimeMillis() + WINDOW_LOSS_TIMEOUT;
            }
            
            if (shouldLooseOnTop && loseOnTopAt < System.currentTimeMillis()) {
                this.setAlwaysOnTop(false);
                shouldLooseOnTop = false;
            }
            
        } else {
            currentSession = session;
            shouldLooseOnTop = false;
            if (!this.isAlwaysOnTop()) {
                this.setAlwaysOnTop(true);
            }
            
            String name = session.getCharacterName();
            if (name == null) {                
                if (lsDlog != null) {
                    lsDlog.setVisible(false);
                    lsDlog.dispose();
                    deleteLoadScanDialog();
                }
                
                if (currentMiner != null) {
                    currentMiner = null;
                    jLabelMinerName.setText("none");
                    disableMonitorPanel();
                }
            } else {
                if (!name.equals(currentMiner)) {                                    
                    if (lsDlog != null) {
                        lsDlog.setVisible(false);
                        lsDlog.dispose();
                        deleteLoadScanDialog();
                    }
                    
                    currentMiner = name;
                    SessionCharacter curchar = session.getSessionCharacter();                    
                    jLabelMinerName.setText(currentMiner + (curchar == null ? "(not found)" : ""));
                    
                    if (curchar == null) {
                        disableMonitorPanel();
                    } else {
                        loadCharacterData(session);
                        updateAsteroids(session);
                    }
                }
            }                        
        }
        
        List<MiningSession> sessions = msMonitor.getSessions();
        if (!sessions.equals(currentSessions)) {
            recreateButtons(sessions);
            currentSessions = sessions;
        }
        
        processEvents = true;
    }
    
    private void disableMonitorPanel() {
        if (jPanelSetup.getComponent(0).isEnabled()) {
            enableSubcomponents(jPanelSetup, false);
        }
    }
    
    private void loadCharacterData(MiningSession session) {
        if (!jPanelSetup.getComponent(0).isEnabled()) {
            enableSubcomponents(jPanelSetup, true);
        }
        
        SessionCharacter character = session.getSessionCharacter();
        
        Ship ship = character.getShip();
        jComboBoxShip.setSelectedItem(ship);
        
        EVECharacter booster = character.getBooster();
        jComboBoxBooster.setSelectedItem(booster);
        
        BoosterShip bShip = character.getBoosterShip();
        jComboBoxBoosterShip.setSelectedItem(bShip);
        
        boolean isUsingBS = character.isUseBoosterShip();
        jCheckBoxUseBoosterShip.setSelected(isUsingBS);
        
        if (isUsingBS) {
            if (!jComboBoxBoosterShip.isEnabled()) jComboBoxBoosterShip.setEnabled(true);
        } else {
            if (jComboBoxBoosterShip.isEnabled()) jComboBoxBoosterShip.setEnabled(false);
        }
        
        CalculatedStats stats = character.getStats();
        updateCharacterStats(session);
    }
    
    private void updateCharacterStats(MiningSession session) {
        SessionCharacter character = session.getSessionCharacter();
        if (character == null) return;
        
        CalculatedStats stats = character.getStats();
        jLabelStats.setText(stats.getTurretYield()+" m3 / "+stats.getTurretCycle()+" sec / turret");
        jLabelHoldStats.setText(fmt.format(session.getUsedCargo())+" / "+stats.getOreHold()+" m3");
    }
    
    /**
     * Recursively enables (or disables) subcomponents of a given container.
     * Container itself doesn't have its enabled flag changed.
     * @param container a given container.
     * @param enable true to enable, false to disable.
     */
    private void enableSubcomponents(Container container, boolean enable) {
        for (Component component : container.getComponents()) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableSubcomponents((Container) component, enable);
            }
        }
    }
    
    /**
     * Cleans up old EVE selector buttons (if any) and recreates them
     * according to the list of EVE sessions.
     * @param sessions 
     */
    private void recreateButtons(List<MiningSession> sessions) {
        jPanelSelector.removeAll();
        int rows = (int) Math.ceil(sessions.size() / 2f);
        GridLayout layout = new GridLayout(rows, 2, 2, 2);
        jPanelSelector.setLayout(layout);
        for (final MiningSession session : sessions) {
            JButton button = new JButton(session.getCharacterName());
            
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {                                    
                    if (lsDlog != null) {
                        lsDlog.setVisible(false);
                        lsDlog.dispose();
                        deleteLoadScanDialog();
                    }
                    
                    JAsteroidMonitorForm.this.setAlwaysOnTop(true);
                    session.switchToWindow();
                }
            });
            
            jPanelSelector.add(button);
        }
        layout.layoutContainer(jPanelSelector);
        jPanelSelector.validate();
    }
    
    public void updateAsteroids(MiningSession session) {
        jTableRoids.setModel(session.getTableModel());
        
        jTableRoids.getColumnModel().getColumn(0).setResizable(false);
        jTableRoids.getColumnModel().getColumn(0).setPreferredWidth(150);
        jTableRoids.getColumnModel().getColumn(1).setResizable(false);
        jTableRoids.getColumnModel().getColumn(2).setResizable(false);
        jTableRoids.getColumnModel().getColumn(3).setResizable(false);
    }
    
    /**
     * Deletes reference to scan loading dialog.
     * Called when the dialog is closed.
     */
    public void deleteLoadScanDialog() {
        lsDlog = null;
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
        jPanelSetup = new javax.swing.JPanel();
        jComboBoxShip = new javax.swing.JComboBox<Ship>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxBooster = new javax.swing.JComboBox<EVECharacter>();
        jLabelStats = new javax.swing.JLabel();
        jLabelHoldStats = new javax.swing.JLabel();
        jButtonResetOreHold = new javax.swing.JButton();
        jTextFieldHold = new javax.swing.JTextField();
        jButtonSetOreHold = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRoids = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxBoosterShip = new javax.swing.JComboBox<BoosterShip>();
        jLabel4 = new javax.swing.JLabel();
        jCheckBoxUseBoosterShip = new javax.swing.JCheckBox();
        jPanelSelector = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jLabelMinerName = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setText("Character:");

        jPanelSetup.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jComboBoxShip.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxShipItemStateChanged(evt);
            }
        });

        jLabel2.setText("Ship");

        jLabel3.setText("Booster");

        jComboBoxBooster.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxBoosterItemStateChanged(evt);
            }
        });

        jLabelStats.setText("0000 m3 / 000 sec / turret");

        jLabelHoldStats.setText("00000 / 00000 m3");

        jButtonResetOreHold.setText("Reset");
        jButtonResetOreHold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetOreHoldActionPerformed(evt);
            }
        });

        jTextFieldHold.setText("0");

        jButtonSetOreHold.setText("Set");
        jButtonSetOreHold.setActionCommand("");
        jButtonSetOreHold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetOreHoldActionPerformed(evt);
            }
        });

        jButton3.setText("Load Scan");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Filters");

        jButton5.setText("Clear");
        jButton5.setActionCommand("");

        jTableRoids.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Distance", "Rem", "Turrets"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableRoids.setColumnSelectionAllowed(true);
        jTableRoids.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTableRoids);
        jTableRoids.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTableRoids.getColumnModel().getColumnCount() > 0) {
            jTableRoids.getColumnModel().getColumn(0).setResizable(false);
            jTableRoids.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTableRoids.getColumnModel().getColumn(1).setResizable(false);
            jTableRoids.getColumnModel().getColumn(2).setResizable(false);
            jTableRoids.getColumnModel().getColumn(3).setResizable(false);
        }

        jButton6.setText("1");

        jButton7.setText("2");

        jButton8.setText("3");

        jButton9.setText("Cleanup");

        jLabel6.setText("Ore Hold");

        jComboBoxBoosterShip.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxBoosterShipItemStateChanged(evt);
            }
        });

        jLabel4.setText("B. Ship");

        jCheckBoxUseBoosterShip.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxUseBoosterShipItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelSetupLayout = new javax.swing.GroupLayout(jPanelSetup);
        jPanelSetup.setLayout(jPanelSetupLayout);
        jPanelSetupLayout.setHorizontalGroup(
            jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanelSetupLayout.createSequentialGroup()
                        .addComponent(jLabelStats)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelHoldStats))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSetupLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSetupLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldHold, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSetOreHold)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonResetOreHold))
                            .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanelSetupLayout.createSequentialGroup()
                        .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSetupLayout.createSequentialGroup()
                                .addComponent(jButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8))
                            .addGroup(jPanelSetupLayout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelSetupLayout.createSequentialGroup()
                        .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxShip, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxBooster, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelSetupLayout.createSequentialGroup()
                                .addComponent(jComboBoxBoosterShip, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxUseBoosterShip)))))
                .addContainerGap())
        );
        jPanelSetupLayout.setVerticalGroup(
            jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxShip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxBooster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxBoosterShip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(jCheckBoxUseBoosterShip))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelStats)
                    .addComponent(jLabelHoldStats))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonResetOreHold)
                    .addComponent(jTextFieldHold, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSetOreHold)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelSelectorLayout = new javax.swing.GroupLayout(jPanelSelector);
        jPanelSelector.setLayout(jPanelSelectorLayout);
        jPanelSelectorLayout.setHorizontalGroup(
            jPanelSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSelectorLayout.setVerticalGroup(
            jPanelSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jLabelMinerName.setText("none");

        jCheckBox1.setText("Ignore");
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        jCheckBox2.setText("Popup On Alerts");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelSelector, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelSetup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelMinerName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBox1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonClose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabelMinerName)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSetup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSelector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClose)
                    .addComponent(jCheckBox2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        parent.setVisible(true);
        this.setVisible(false);
        this.dispose();
        timer.shutdown();
        parent.deleteMonitorForm();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jComboBoxShipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxShipItemStateChanged
        if (!processEvents) return;
        processEvents = false;
        
        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        if (currentSession != null) {
            currentSession.updateCharacherShip(ship);
            updateCharacterStats(currentSession);
        }
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxShipItemStateChanged

    private void jComboBoxBoosterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxBoosterItemStateChanged
        if (!processEvents) return;
        processEvents = false;
        
        EVECharacter booster = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (currentSession != null) {
            currentSession.updateCharacherBooster(booster);
            updateCharacterStats(currentSession);
        }
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxBoosterItemStateChanged

    private void jComboBoxBoosterShipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxBoosterShipItemStateChanged
        if (!processEvents) return;
        processEvents = false;
        
        BoosterShip bShip = (BoosterShip) jComboBoxBoosterShip.getSelectedItem();
        if (currentSession != null) {
            currentSession.updateCharacherBoosterShip(bShip);            
            updateCharacterStats(currentSession);            
        }
        
        processEvents = true;
    }//GEN-LAST:event_jComboBoxBoosterShipItemStateChanged

    private void jCheckBoxUseBoosterShipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxUseBoosterShipItemStateChanged
        if (!processEvents) return;
        processEvents = false;
                
        if (currentSession != null) {
            currentSession.updateCharacherUsingBoosterShip(jCheckBoxUseBoosterShip.isSelected());
            SessionCharacter curchar = currentSession.getSessionCharacter();
            
            if (curchar != null) {
                if (jCheckBoxUseBoosterShip.isSelected()) {
                    if (!jComboBoxBoosterShip.isEnabled()) jComboBoxBoosterShip.setEnabled(true);
                } else {
                    if (jComboBoxBoosterShip.isEnabled()) jComboBoxBoosterShip.setEnabled(false);
                }
                
                updateCharacterStats(currentSession);
            }
        }
        
        processEvents = true;
    }//GEN-LAST:event_jCheckBoxUseBoosterShipItemStateChanged

    private void jButtonSetOreHoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetOreHoldActionPerformed
        if (!processEvents) return;
        processEvents = false;
        
        if (currentSession != null) {
            String hold = jTextFieldHold.getText();
            int newHoldSize;
            try {
                newHoldSize = Integer.decode(hold);
            } catch (NumberFormatException e) {
                newHoldSize = 0;
            }

            SessionCharacter curchar = currentSession.getSessionCharacter();
            if (curchar != null) {
                int maxHoldSize = curchar.getStats().getOreHold();
                if (newHoldSize > maxHoldSize) newHoldSize = maxHoldSize;
            } else {
                newHoldSize = 0;
            }
            
            currentSession.setUsedCargo(newHoldSize);
            updateCharacterStats(currentSession);
        }
        
        processEvents = true;
    }//GEN-LAST:event_jButtonSetOreHoldActionPerformed

    private void jButtonResetOreHoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetOreHoldActionPerformed
        if (!processEvents) return;
        processEvents = false;
        
        if (currentSession != null) {
            currentSession.setUsedCargo(0);
            updateCharacterStats(currentSession);
        }
        
        processEvents = true;
    }//GEN-LAST:event_jButtonResetOreHoldActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (currentSession != null) {
            
            if (lsDlog == null) {            
                lsDlog = new JLoadScanDialog(this, currentSession);
                lsDlog.setLocationRelativeTo(this);
            }
            
            lsDlog.setVisible(true);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonResetOreHold;
    private javax.swing.JButton jButtonSetOreHold;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBoxUseBoosterShip;
    private javax.swing.JComboBox<EVECharacter> jComboBoxBooster;
    private javax.swing.JComboBox<BoosterShip> jComboBoxBoosterShip;
    private javax.swing.JComboBox<Ship> jComboBoxShip;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelHoldStats;
    private javax.swing.JLabel jLabelMinerName;
    private javax.swing.JLabel jLabelStats;
    private javax.swing.JPanel jPanelSelector;
    private javax.swing.JPanel jPanelSetup;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableRoids;
    private javax.swing.JTextField jTextFieldHold;
    // End of variables declaration//GEN-END:variables
}
