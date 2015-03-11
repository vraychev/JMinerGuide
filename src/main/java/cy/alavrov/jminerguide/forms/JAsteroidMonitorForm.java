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
import cy.alavrov.jminerguide.data.ICalculatedStats;
import cy.alavrov.jminerguide.data.harvestable.Asteroid;
import cy.alavrov.jminerguide.log.JMGLogger;
import cy.alavrov.jminerguide.monitor.AsteroidMonitorSettings;
import cy.alavrov.jminerguide.monitor.ISessionCharacter;
import cy.alavrov.jminerguide.monitor.MiningSession;
import cy.alavrov.jminerguide.monitor.MiningSessionButton;
import cy.alavrov.jminerguide.monitor.MiningSessionMonitor;
import cy.alavrov.jminerguide.monitor.MiningTask;
import cy.alavrov.jminerguide.monitor.MiningTimer;
import cy.alavrov.jminerguide.monitor.TurretInstance;
import cy.alavrov.jminerguide.monitor.UpdateWindowTask;
import cy.alavrov.jminerguide.util.IntegerDocumentFilter;
import cy.alavrov.jminerguide.util.winmanager.IWindowManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AbstractDocument;
import org.joda.time.Period;
import org.joda.time.Seconds;
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
    
    private final static Color darkGreen = new Color(0f, 0.5f, 0f);
    
    private final MainFrame parent;
    private final DataContainer dCont;
    private final IWindowManager wManager;
    private final MiningSessionMonitor msMonitor;
    private final AsteroidMonitorSettings settings;
    
    private final ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(2);
    
    private volatile String currentMiner = null;
    private volatile MiningSession currentSession = null;
    private volatile List<MiningSession> currentSessions;
    
    private volatile long loseOnTopAt = 0;
    private volatile boolean shouldLooseOnTop = false;
    
    private volatile boolean processEvents = false;

    private volatile JLoadScanDialog lsDlog = null;
    private volatile JDialog scsDlog = null;
    
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
        
        settings = new AsteroidMonitorSettings(dCont.getPath());
        this.setLocationRelativeTo(null);
        if (settings.getX() != -1 && settings.getX() != -1) {
            this.setLocation(settings.getX(), settings.getY());
        }
        
        AbstractDocument idDoc = ((AbstractDocument)jTextFieldHold.getDocument());
        idDoc.setDocumentFilter(new IntegerDocumentFilter());
        
        idDoc = ((AbstractDocument)jTextFieldCustomTimer.getDocument());
        idDoc.setDocumentFilter(new IntegerDocumentFilter());
        
        this.parent = parent;
        this.dCont = dCont;
        this.wManager = wManager;
        this.msMonitor = new MiningSessionMonitor(wManager, dCont);
        
        timer.scheduleWithFixedDelay(new UpdateWindowTask(msMonitor, this), 100, 100, TimeUnit.MILLISECONDS);
        timer.scheduleAtFixedRate(new MiningTask(msMonitor, this), 1, 1, TimeUnit.SECONDS);
        disableMonitorPanel();
        
        setTurretKeyBindings(jPanelSetup, JComponent.WHEN_IN_FOCUSED_WINDOW);
        setTurretKeyBindings(jPanelSelector, JComponent.WHEN_IN_FOCUSED_WINDOW);
        setTurretKeyBindings(jTableRoids, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
               
        msMonitor.update();
        updateCurrentSession();
        pack();
        
        processEvents = true;
    }

    private void setTurretKeyBindings(JComponent component, int condition) {
        InputMap inputMap = component.getInputMap(condition);
        ActionMap actMap = component.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "turret1");
        actMap.put("turret1", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jToggleButtonTurret1ActionPerformed(e);
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "turret2");
        actMap.put("turret2", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jToggleButtonTurret2ActionPerformed(e);
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "turret3");
        actMap.put("turret3", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jToggleButtonTurret3ActionPerformed(e);
            }
        });
    }
    
    private void hideModalDialogs() {
        if (lsDlog != null) {
            lsDlog.dispose();
            deleteLoadScanDialog();
        }             
        if (scsDlog != null) {
            scsDlog.dispose();
            deleteSessionCharacterSettingsDialog();
        }
    }
    
    public void updateCurrentSession() {
        processEvents = false; 
        
        MiningSession session = msMonitor.getCurrentSession();
        if (session == null) {            
            if (lsDlog == null && !msMonitor.haveAlerts(settings)) { 
                // only hide if we doesn't have load scan dialog open and have no alerts.
                // wait a few milliseconds to lose always on top to combat flickering.
                if (this.isAlwaysOnTop() && !shouldLooseOnTop) {
                    shouldLooseOnTop = true;
                    loseOnTopAt = System.currentTimeMillis() + WINDOW_LOSS_TIMEOUT;
                    updateSessionButtons();
                    updateTimerLabel();
                }

                if (shouldLooseOnTop && loseOnTopAt < System.currentTimeMillis()) {
                    this.setAlwaysOnTop(false);
                    if (!msMonitor.isMonitorOrSystemWindow()) {
                        msMonitor.minimizeMonitorWindow();
                    }
                    shouldLooseOnTop = false;
                }
            }
            
        } else {
            currentSession = session;
            shouldLooseOnTop = false;
            if (!this.isAlwaysOnTop()) {
                this.setAlwaysOnTop(true);
                msMonitor.restoreMonitorWindow();
                session.switchToWindow();
            }
            
            String name = session.getCharacterName();
            if (name == null) {                
                hideModalDialogs();
                
                if (currentMiner != null) {
                    currentMiner = null;
                    jLabelMinerName.setText("none");
                    disableMonitorPanel();
                    updateSessionButtons();
                    updateTimerLabel();
                }
            } else {
                if (!name.equals(currentMiner)) {        
                    hideModalDialogs();
                    
                    currentMiner = name;
                    ISessionCharacter curchar = session.getSessionCharacter();                    
                    jLabelMinerName.setText(currentMiner + (curchar.isSimple() ? "(no API)" : ""));
                    
                    if (curchar == null) {
                        disableMonitorPanel();
                    } else {
                        loadCharacterData(session);
                        updateAsteroids(session);
                    }
                    
                    updateSessionButtons();
                    updateTimerLabel();
                }
            }                        
        }
        
        List<MiningSession> sessions = msMonitor.getSessions();
        if (!sessions.equals(currentSessions)) {
            recreateButtons(sessions);
            currentSessions = sessions;
            updateSessionButtons();
        }
        
        processEvents = true;
    }
    
    public void updateTimerLabel() {
        MiningSession sess = currentSession;
        if (sess == null || sess.getSessionCharacter() == null) {
            jLabelTimer.setText("0:00");
            return;
        }
        
        MiningTimer mTimer = sess.getTimer();
        if (mTimer == null || mTimer.isFinished()) {
            jLabelTimer.setText("0:00");
            return;
        }
        
        Period remPeriod = Seconds.seconds(mTimer.getRemainingSeconds())
                    .toStandardDuration().toPeriod();

        jLabelTimer.setText(minutesAndSeconds.print(remPeriod));        
    }
    
    /**
     * Updates session buttons, setting their selected status
     * and text as needed, according to sessions.
     */
    public void updateSessionButtons() {
        MiningSession sess = currentSession;
        for (Component component : jPanelSelector.getComponents()) {
            if (component instanceof MiningSessionButton) {
                MiningSessionButton button = (MiningSessionButton) component;
                
                MiningSession session = button.getMiningSession();
                boolean current = session.equals(sess);
                if (button.isSelected() != current) {
                    button.setSelected(current);
                }
                
                session.updateButton(button);
            }
        }
    }
    
    private void disableMonitorPanel() {
        if (jPanelSetup.getComponent(0).isEnabled()) {
            enableSubcomponents(jPanelSetup, false);
        }
        
        if (jCheckBoxCharacterIgnore.isEnabled()) jCheckBoxCharacterIgnore.setEnabled(false);
        if (jSpinnerSequence.isEnabled()) jSpinnerSequence.setEnabled(false);
    }
    
    private void loadCharacterData(MiningSession session) {
        if (!jPanelSetup.getComponent(0).isEnabled()) {
            enableSubcomponents(jPanelSetup, true);
        }
        
        if (!jCheckBoxCharacterIgnore.isEnabled()) jCheckBoxCharacterIgnore.setEnabled(true);
        if (!jSpinnerSequence.isEnabled()) jSpinnerSequence.setEnabled(true);
        
        ISessionCharacter character = session.getSessionCharacter();        
        
        jCheckBoxCharacterIgnore.setSelected(character.getCoreCharacter().isMonitorIgnore());
        jSpinnerSequence.setValue(character.getCoreCharacter().getMonitorSequence());
        
        updateCharacterStats(session);
    }
    
    /**
     * Updates stats for session only if it's a current session.
     * @param session 
     */
    protected void updateCharacterStatsIfCurrent(MiningSession session) {
        if (session != null && session.equals(currentSession)) {
            updateCharacterStats(session);
        }
    }
    
    private void updateCharacterStats(MiningSession session) {
        ISessionCharacter character = session.getSessionCharacter();
        if (character == null) return;                
        
        ICalculatedStats stats = character.getStats();
        jLabelStats.setText(fmt.format(stats.getTurretYield())+" m3 / "+stats.getTurretCycle()+" sec / turret");
        jLabelHoldStats.setText(fmt.format(session.getUsedCargo())+" / "+stats.getOreHold()+" m3");
        
        checkTurretButtons();
    }
    
    public void updateCurrentCharacterStats() {
        MiningSession sess = currentSession;
        if (sess != null) updateCharacterStats(sess);
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
            final MiningSessionButton button = new MiningSessionButton(session, session.getCharacterName());
            
            button.setMargin(new Insets(5, 1, 5, 1));
            button.setFont(button.getFont().deriveFont(Font.BOLD));
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {                                    
                    hideModalDialogs();
                    
                    JAsteroidMonitorForm.this.setAlwaysOnTop(true);
                    session.switchToWindow();
                    
                    boolean current = button.getMiningSession().equals(currentSession);
                    if (button.isSelected() != current) {
                        button.setSelected(current);
                    }
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
        jTableRoids.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTableRoids.getColumnModel().getColumn(3).setResizable(false);
        jTableRoids.getColumnModel().getColumn(3).setPreferredWidth(50);
    }
    
    /**
     * Deletes reference to scan loading dialog.
     * Called when the dialog is closed.
     */
    public void deleteLoadScanDialog() {
        lsDlog = null;
    }
    
    /**
     * Deletes reference to character settings dialog.
     * Called when the dialog is closed.
     */
    public void deleteSessionCharacterSettingsDialog() {
        scsDlog = null;
    }
    
    /**
     * Disables all the turret buttons.
     */
    public void disableTurretButtons() {
        disableToggleButton(jToggleButtonTurret1);
        disableToggleButton(jToggleButtonTurret2);
        disableToggleButton(jToggleButtonTurret3);
    }
        
    /**
     * Disables toggle button, if it's enabled, setting it's state to not selected.
     * @param button 
     */
    private void disableToggleButton(JToggleButton button) {
        if (button.isSelected()) button.setSelected(false);
        if (button.isEnabled()) button.setEnabled(false);
        button.setForeground(Color.BLACK);
    }
    
    /**
     * Enables toggle button, if it's not enabled and sets it's selected state to desired value.
     * @param button
     * @param selected 
     */
    private void enableToggleButton(JToggleButton button, boolean selected) {
        if (!button.isEnabled()) button.setEnabled(true);
        if (button.isSelected() != selected) button.setSelected(selected);
        if (selected) {
            button.setForeground(darkGreen);
        } else {
            button.setForeground(Color.RED);
        }
    }
    
    /**
     * Checks turret buttons, enabling or disabling them as needed.
     */
    public void checkTurretButtons() {
        MiningSession sess = currentSession;
        if (sess == null) {
            disableTurretButtons();
            return;
        }
                
        synchronized(sess) {
            ISessionCharacter chr = sess.getSessionCharacter();
            if (chr == null) {
                disableTurretButtons();
                return;
            }

            float holdRem = sess.getRemainingCargo();
            if (holdRem == 0) {
                disableTurretButtons();
                return;
            }

            if (chr.getTurretCount() > 3) {
                disableTurretButtons();
                return;
            }

            if (chr.getTurretCount() > 2) {
                enableToggleButton(jToggleButtonTurret3, sess.getTurret3().isMining());
            } else {
                disableToggleButton(jToggleButtonTurret3);
            }

            if (chr.getTurretCount() > 1) {
                enableToggleButton(jToggleButtonTurret2, sess.getTurret2().isMining());
            } else {
                disableToggleButton(jToggleButtonTurret2);
            }

            if (chr.getTurretCount() > 0) {
                enableToggleButton(jToggleButtonTurret1, sess.getTurret1().isMining());
            } else {
                disableToggleButton(jToggleButtonTurret1);
            }
            
        }
    }
    
    /**
     * Returns a currently selected asteroid, or null, if there's none.
     * @return 
     */
    private Asteroid getSelectedAsteroid() {
        int row = jTableRoids.getSelectedRow();
        if (row == -1) return null;
        
        try {
            return (Asteroid) jTableRoids.getModel().getValueAt(row, 0);
        } catch (ClassCastException e) {
            JMGLogger.logSevere("Unable to get asteroid", e);
            return null;
        }
    }
    
    public void notifyTableUpdate() {
        int row = jTableRoids.getSelectedRow();
        ((AbstractTableModel)jTableRoids.getModel()).fireTableDataChanged();
        
        if (row >= jTableRoids.getRowCount()) row = jTableRoids.getRowCount() - 1;
        
        if (row >= 0) {
            jTableRoids.setRowSelectionInterval(row, row);
            jTableRoids.setColumnSelectionInterval(0, 0);
        }
    }

    public AsteroidMonitorSettings getSettings() {
        return settings;
    }        
    
    private void setCustomHold() {
        MiningSession sess = currentSession;
        if (sess != null) {
            String hold = jTextFieldHold.getText();
            int newHoldSize;
            try {
                newHoldSize = Integer.parseInt(hold, 10);
            } catch (NumberFormatException e) {
                newHoldSize = 0;
            }

            ISessionCharacter curchar = sess.getSessionCharacter();
            if (curchar != null) {
                int maxHoldSize = curchar.getStats().getOreHold();
                if (newHoldSize > maxHoldSize) newHoldSize = maxHoldSize;
            } else {
                newHoldSize = 0;
            }

            sess.setUsedCargo(newHoldSize);
            updateCharacterStats(sess);
        }
    }
    
    private void setCustomTimer() {
        MiningSession sess = currentSession;
        if (sess != null) {            
            try {
                int secs = Integer.parseInt(jTextFieldCustomTimer.getText(), 10);
                sess.newTimer(secs, settings.getTimerAlertRemoveTimeout());
                updateTimerLabel();
            } catch (NumberFormatException | NullPointerException e) {
                // do nothing
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
        jPanelSetup = new javax.swing.JPanel();
        jLabelStats = new javax.swing.JLabel();
        jLabelHoldStats = new javax.swing.JLabel();
        jButtonResetOreHold = new javax.swing.JButton();
        jTextFieldHold = new javax.swing.JTextField();
        jButtonSetOreHold = new javax.swing.JButton();
        jButtonLoadScan = new javax.swing.JButton();
        jButtonFilters = new javax.swing.JButton();
        jButtonClearAsteroids = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRoids = new javax.swing.JTable();
        jButtonCleanupAsteroids = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jToggleButtonTurret1 = new javax.swing.JToggleButton();
        jToggleButtonTurret2 = new javax.swing.JToggleButton();
        jToggleButtonTurret3 = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        jButton15sec = new javax.swing.JButton();
        jButton30sec = new javax.swing.JButton();
        jButton1min = new javax.swing.JButton();
        jButton2min = new javax.swing.JButton();
        jButtonStopTimer = new javax.swing.JButton();
        jTextFieldCustomTimer = new javax.swing.JTextField();
        jButtonCustomTimer = new javax.swing.JButton();
        jLabelTimer = new javax.swing.JLabel();
        jButtonConfigureShip = new javax.swing.JButton();
        jPanelSelector = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jLabelMinerName = new javax.swing.JLabel();
        jCheckBoxCharacterIgnore = new javax.swing.JCheckBox();
        jButtonSettings = new javax.swing.JButton();
        jSpinnerSequence = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
        });

        jLabel1.setText("Character:");

        jPanelSetup.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelStats.setText("0000 m3 / 000 sec / turret");

        jLabelHoldStats.setText("00000 / 00000 m3");

        jButtonResetOreHold.setText("Reset");
        jButtonResetOreHold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetOreHoldActionPerformed(evt);
            }
        });

        jTextFieldHold.setText("0");
        jTextFieldHold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldHoldActionPerformed(evt);
            }
        });

        jButtonSetOreHold.setText("Set");
        jButtonSetOreHold.setActionCommand("");
        jButtonSetOreHold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetOreHoldActionPerformed(evt);
            }
        });

        jButtonLoadScan.setText("Load Scan");
        jButtonLoadScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadScanActionPerformed(evt);
            }
        });

        jButtonFilters.setText("Filters");
        jButtonFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFiltersActionPerformed(evt);
            }
        });

        jButtonClearAsteroids.setText("Clear");
        jButtonClearAsteroids.setActionCommand("");
        jButtonClearAsteroids.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearAsteroidsActionPerformed(evt);
            }
        });

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

        jButtonCleanupAsteroids.setText("Cleanup");
        jButtonCleanupAsteroids.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCleanupAsteroidsActionPerformed(evt);
            }
        });

        jLabel6.setText("Ore Hold");

        jToggleButtonTurret1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jToggleButtonTurret1.setText("1");
        jToggleButtonTurret1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonTurret1ActionPerformed(evt);
            }
        });

        jToggleButtonTurret2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jToggleButtonTurret2.setText("2");
        jToggleButtonTurret2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonTurret2ActionPerformed(evt);
            }
        });

        jToggleButtonTurret3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jToggleButtonTurret3.setText("3");
        jToggleButtonTurret3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonTurret3ActionPerformed(evt);
            }
        });

        jLabel5.setText("Timers");

        jButton15sec.setText("15s");
        jButton15sec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15secActionPerformed(evt);
            }
        });

        jButton30sec.setText("30s");
        jButton30sec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30secActionPerformed(evt);
            }
        });

        jButton1min.setText("1m");
        jButton1min.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1minActionPerformed(evt);
            }
        });

        jButton2min.setText("2m");
        jButton2min.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2minActionPerformed(evt);
            }
        });

        jButtonStopTimer.setText("Stop");
        jButtonStopTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopTimerActionPerformed(evt);
            }
        });

        jTextFieldCustomTimer.setText("0");
        jTextFieldCustomTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCustomTimerActionPerformed(evt);
            }
        });

        jButtonCustomTimer.setText("Custom");
        jButtonCustomTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCustomTimerActionPerformed(evt);
            }
        });

        jLabelTimer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTimer.setText("0:0");

        jButtonConfigureShip.setText("Configure Ship");
        jButtonConfigureShip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfigureShipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSetupLayout = new javax.swing.GroupLayout(jPanelSetup);
        jPanelSetup.setLayout(jPanelSetupLayout);
        jPanelSetupLayout.setHorizontalGroup(
            jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonConfigureShip, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanelSetupLayout.createSequentialGroup()
                        .addComponent(jLabelStats)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelHoldStats))
                    .addGroup(jPanelSetupLayout.createSequentialGroup()
                        .addComponent(jToggleButtonTurret1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonTurret2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonTurret3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonCleanupAsteroids))
                    .addGroup(jPanelSetupLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldHold, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSetOreHold)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonResetOreHold))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSetupLayout.createSequentialGroup()
                        .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSetupLayout.createSequentialGroup()
                                .addComponent(jButton15sec)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton30sec)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1min)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2min)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelSetupLayout.createSequentialGroup()
                                .addComponent(jTextFieldCustomTimer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonCustomTimer)
                                .addGap(13, 13, 13)))
                        .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonStopTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelSetupLayout.createSequentialGroup()
                        .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSetupLayout.createSequentialGroup()
                                .addComponent(jButtonLoadScan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonFilters)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonClearAsteroids))
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelSetupLayout.setVerticalGroup(
            jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonConfigureShip)
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
                    .addComponent(jButtonLoadScan)
                    .addComponent(jButtonFilters)
                    .addComponent(jButtonClearAsteroids))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButtonTurret1)
                    .addComponent(jToggleButtonTurret2)
                    .addComponent(jToggleButtonTurret3)
                    .addComponent(jButtonCleanupAsteroids))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15sec)
                    .addComponent(jButton30sec)
                    .addComponent(jButton1min)
                    .addComponent(jButton2min)
                    .addComponent(jButtonStopTimer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCustomTimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCustomTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelTimer))
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
            .addGap(0, 57, Short.MAX_VALUE)
        );

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jLabelMinerName.setText("none");

        jCheckBoxCharacterIgnore.setText("Ignore");
        jCheckBoxCharacterIgnore.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBoxCharacterIgnore.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxCharacterIgnoreItemStateChanged(evt);
            }
        });

        jButtonSettings.setText("Settings");
        jButtonSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSettingsActionPerformed(evt);
            }
        });

        jSpinnerSequence.setModel(new javax.swing.SpinnerNumberModel(0, 0, 99, 1));
        jSpinnerSequence.setToolTipText("Sequence of the character in session list. Characters with bigger sequence number go first in list");
        jSpinnerSequence.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerSequenceStateChanged(evt);
            }
        });

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
                        .addComponent(jSpinnerSequence, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxCharacterIgnore))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonSettings)
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
                    .addComponent(jCheckBoxCharacterIgnore)
                    .addComponent(jSpinnerSequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSetup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSelector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSettings)
                    .addComponent(jButtonClose))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        settings.save();
        parent.setVisible(true);
        this.dispose();
        timer.shutdown();
        parent.deleteMonitorForm();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jToggleButtonTurret3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonTurret3ActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            synchronized(sess) {
                ISessionCharacter chr = sess.getSessionCharacter();
                if (chr != null && chr.getTurretCount() > 2) {
                    TurretInstance turret = sess.getTurret3();
                    if (turret.isMining()) {
                        turret.unbindAsteroid();
                    } else {
                        Asteroid roid = getSelectedAsteroid();
                        if (roid != null) {
                            turret.bindAsteroid(roid);
                        }
                    }

                    notifyTableUpdate();

                    if (jToggleButtonTurret3.isSelected() != turret.isMining()) {
                        jToggleButtonTurret3.setSelected(turret.isMining());
                    }
                }
            }
        }
    }//GEN-LAST:event_jToggleButtonTurret3ActionPerformed

    private void jToggleButtonTurret2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonTurret2ActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            synchronized(sess) {
                ISessionCharacter chr = sess.getSessionCharacter();
                if (chr != null && chr.getTurretCount() > 1) {
                    TurretInstance turret = sess.getTurret2();
                    if (turret.isMining()) {
                        turret.unbindAsteroid();
                    } else {
                        Asteroid roid = getSelectedAsteroid();
                        if (roid != null) {
                            turret.bindAsteroid(roid);
                        }
                    }

                    notifyTableUpdate();

                    if (jToggleButtonTurret2.isSelected() != turret.isMining()) {
                        jToggleButtonTurret2.setSelected(turret.isMining());
                    }
                }
            }
        }
    }//GEN-LAST:event_jToggleButtonTurret2ActionPerformed

    private void jToggleButtonTurret1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonTurret1ActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            synchronized(sess) {
                ISessionCharacter chr = sess.getSessionCharacter();
                if (chr != null && chr.getTurretCount() > 0) {
                    TurretInstance turret = sess.getTurret1();
                    if (turret.isMining()) {
                        turret.unbindAsteroid();
                    } else {
                        Asteroid roid = getSelectedAsteroid();
                        if (roid != null) {
                            turret.bindAsteroid(roid);
                        }
                    }

                    notifyTableUpdate();

                    if (jToggleButtonTurret1.isSelected() != turret.isMining()) {
                        jToggleButtonTurret1.setSelected(turret.isMining());
                    }
                }
            }
        }
    }//GEN-LAST:event_jToggleButtonTurret1ActionPerformed

    private void jButtonCleanupAsteroidsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCleanupAsteroidsActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            sess.cleanupRoids();
            updateAsteroids(sess);
        }
    }//GEN-LAST:event_jButtonCleanupAsteroidsActionPerformed

    private void jButtonClearAsteroidsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearAsteroidsActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            sess.clearRoids();
            updateAsteroids(sess);
        }
    }//GEN-LAST:event_jButtonClearAsteroidsActionPerformed

    private void jButtonFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFiltersActionPerformed
        MiningSession sess = currentSession;
        if (sess != null && sess.getSessionCharacter() != null) {
            JAsteroidFilterDialog dlog = new JAsteroidFilterDialog(this, sess.getSessionCharacter().getCoreCharacter());
            dlog.setLocationRelativeTo(this);

            dlog.setVisible(true);
        }
    }//GEN-LAST:event_jButtonFiltersActionPerformed

    private void jButtonLoadScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadScanActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {

            if (lsDlog == null) {
                lsDlog = new JLoadScanDialog(this, sess);
                lsDlog.setLocationRelativeTo(this);
            }

            lsDlog.setVisible(true);
        }
    }//GEN-LAST:event_jButtonLoadScanActionPerformed

    private void jButtonSetOreHoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetOreHoldActionPerformed
        if (!processEvents) return;
        processEvents = false;

        setCustomHold();

        processEvents = true;
    }//GEN-LAST:event_jButtonSetOreHoldActionPerformed

    private void jButtonResetOreHoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetOreHoldActionPerformed
        if (!processEvents) return;
        processEvents = false;

        MiningSession sess = currentSession;
        if (sess != null) {
            sess.setUsedCargo(0);
            updateCharacterStats(sess);
        }

        processEvents = true;
    }//GEN-LAST:event_jButtonResetOreHoldActionPerformed

    private void jButton15secActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15secActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            sess.newTimer(15, settings.getTimerAlertRemoveTimeout());
            updateTimerLabel();
        }
    }//GEN-LAST:event_jButton15secActionPerformed

    private void jButton30secActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30secActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            sess.newTimer(30, settings.getTimerAlertRemoveTimeout());
            updateTimerLabel();
        }
    }//GEN-LAST:event_jButton30secActionPerformed

    private void jButton1minActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1minActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            sess.newTimer(60, settings.getTimerAlertRemoveTimeout());
            updateTimerLabel();
        }
    }//GEN-LAST:event_jButton1minActionPerformed

    private void jButton2minActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2minActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            sess.newTimer(120, settings.getTimerAlertRemoveTimeout());
            updateTimerLabel();
        }
    }//GEN-LAST:event_jButton2minActionPerformed

    private void jButtonCustomTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCustomTimerActionPerformed
        setCustomTimer();
    }//GEN-LAST:event_jButtonCustomTimerActionPerformed

    private void jButtonStopTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopTimerActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) { 
            sess.stopTimer();
            updateTimerLabel();
        }
    }//GEN-LAST:event_jButtonStopTimerActionPerformed

    private void jCheckBoxCharacterIgnoreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxCharacterIgnoreItemStateChanged
        MiningSession sess = currentSession;
        if (sess != null) { 
            ISessionCharacter character = sess.getSessionCharacter();
            if (character != null) {
                character.getCoreCharacter().setMonitorIgnore(jCheckBoxCharacterIgnore.isSelected());
            }
        }
    }//GEN-LAST:event_jCheckBoxCharacterIgnoreItemStateChanged

    private void jButtonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSettingsActionPerformed
        JAsteroidMonitorSettingsDialog dlog = new JAsteroidMonitorSettingsDialog(this, settings);
        dlog.setLocationRelativeTo(this);

        dlog.setVisible(true);
    }//GEN-LAST:event_jButtonSettingsActionPerformed

    private void jSpinnerSequenceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerSequenceStateChanged
        if (!processEvents) return;
        processEvents = false;
        
        MiningSession sess = currentSession;
        if (sess != null) { 
            ISessionCharacter character = sess.getSessionCharacter();
            if (character != null) {
                character.getCoreCharacter().setMonitorSequence((int) jSpinnerSequence.getValue());
            }
        }
        
        processEvents = true;
    }//GEN-LAST:event_jSpinnerSequenceStateChanged

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        try {
            Point topleft = getLocationOnScreen();
            settings.setX(topleft.x);
            settings.setY(topleft.y);
        } catch (Exception e) {
            // form isn't visible, do nothing
        }
    }//GEN-LAST:event_formComponentMoved

    private void jTextFieldHoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldHoldActionPerformed
        if (!processEvents) return;
        processEvents = false;

        setCustomHold();

        processEvents = true;
    }//GEN-LAST:event_jTextFieldHoldActionPerformed

    private void jTextFieldCustomTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCustomTimerActionPerformed
        if (!processEvents) return;
        processEvents = false;
        
        setCustomTimer();

        processEvents = true;
    }//GEN-LAST:event_jTextFieldCustomTimerActionPerformed

    private void jButtonConfigureShipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfigureShipActionPerformed
        MiningSession sess = currentSession;
        if (sess != null) {
            ISessionCharacter schar = sess.getSessionCharacter();
            if (schar != null) {
                if (schar.isSimple()) {
                    if (scsDlog == null) {
                        scsDlog = new JSimpleCharacterSettingsDialog(this, sess);
                        scsDlog.setLocationRelativeTo(this);
                    } 
                } else {
                    if (scsDlog == null) {
                        scsDlog = new JSessionCharacterSettingsDialog(this, sess, dCont);
                        scsDlog.setLocationRelativeTo(this);
                    }                    
                }
                scsDlog.setVisible(true);
            }            
        }
    }//GEN-LAST:event_jButtonConfigureShipActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton15sec;
    private javax.swing.JButton jButton1min;
    private javax.swing.JButton jButton2min;
    private javax.swing.JButton jButton30sec;
    private javax.swing.JButton jButtonCleanupAsteroids;
    private javax.swing.JButton jButtonClearAsteroids;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonConfigureShip;
    private javax.swing.JButton jButtonCustomTimer;
    private javax.swing.JButton jButtonFilters;
    private javax.swing.JButton jButtonLoadScan;
    private javax.swing.JButton jButtonResetOreHold;
    private javax.swing.JButton jButtonSetOreHold;
    private javax.swing.JButton jButtonSettings;
    private javax.swing.JButton jButtonStopTimer;
    private javax.swing.JCheckBox jCheckBoxCharacterIgnore;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelHoldStats;
    private javax.swing.JLabel jLabelMinerName;
    private javax.swing.JLabel jLabelStats;
    private javax.swing.JLabel jLabelTimer;
    private javax.swing.JPanel jPanelSelector;
    private javax.swing.JPanel jPanelSetup;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerSequence;
    private javax.swing.JTable jTableRoids;
    private javax.swing.JTextField jTextFieldCustomTimer;
    private javax.swing.JTextField jTextFieldHold;
    private javax.swing.JToggleButton jToggleButtonTurret1;
    private javax.swing.JToggleButton jToggleButtonTurret2;
    private javax.swing.JToggleButton jToggleButtonTurret3;
    // End of variables declaration//GEN-END:variables
}


