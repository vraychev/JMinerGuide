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
import cy.alavrov.jminerguide.data.CalculatedStats;
import cy.alavrov.jminerguide.data.DataContainer;
import cy.alavrov.jminerguide.data.api.APICharLoader;
import cy.alavrov.jminerguide.data.booster.BoosterHull;
import cy.alavrov.jminerguide.data.booster.BoosterShip;
import cy.alavrov.jminerguide.data.booster.BoosterShipContainer;
import cy.alavrov.jminerguide.data.booster.ForemanLink;
import cy.alavrov.jminerguide.data.ship.HarvestUpgrade;
import cy.alavrov.jminerguide.data.ship.Hull;
import cy.alavrov.jminerguide.data.ship.MiningCrystalLevel;
import cy.alavrov.jminerguide.data.ship.MiningDrone;
import cy.alavrov.jminerguide.data.ship.OreType;
import cy.alavrov.jminerguide.data.ship.Rig;
import cy.alavrov.jminerguide.data.ship.Ship;
import cy.alavrov.jminerguide.data.ship.ShipContainer;
import cy.alavrov.jminerguide.data.ship.Turret;
import cy.alavrov.jminerguide.data.character.CharacterContainer;
import cy.alavrov.jminerguide.data.character.EVECharacter;
import cy.alavrov.jminerguide.data.implant.Implant;
import cy.alavrov.jminerguide.log.JMGLogger;
import cy.alavrov.jminerguide.util.winmanager.IWindowManager;
import cy.alavrov.jminerguide.util.winmanager.win32.Win32WindowManager;
import java.awt.Image;
import java.io.InputStream;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 *
 * @author alavrov
 */
public final class MainFrame extends javax.swing.JFrame {

    private final static DecimalFormat fmt = new DecimalFormat("0.##");
    private final static PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
     .printZeroAlways()
     .appendMinutes()
     .appendSeparator(":")
     .appendSeconds()
     .toFormatter();

    private Integer[] skillLvls = {0, 1, 2, 3, 4, 5};

    private DataContainer dCont;

    /**
     * JComboBox and JCheckBox fire off event on setSelectedItem, and we don't need
     * to react to it when we are the source of the change.
     */
    private volatile boolean processEvents = false;
    
    private final IWindowManager wManager;
    
    private volatile JAsteroidMonitorForm monitorForm = null;

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

        loadCharacterList(false);
        jComboBoxMiner.setSelectedItem(dCont.getCharacterContainer().getLastSelectedMiner());
        loadSelectedMiner();
        jComboBoxBooster.setSelectedItem(dCont.getCharacterContainer().getLastSelectedBooster());
        loadSelectedBooster();

        loadShipList(false);
        jComboBoxShip.setSelectedItem(dCont.getShipContainer().getLastSelectedShip());
        loadSelectedShip();

        loadSelectedBoosterShip();
        updateBoosterShipInterface();

        recalculateStats();
        
        if (com.sun.jna.Platform.isWindows()) {
            wManager = new Win32WindowManager();
            jButtonAsteroidMonitor.setEnabled(true);
        } else {
            wManager = null;
            jButtonAsteroidMonitor.setEnabled(false);
        }
        processEvents = true;
    }

    public void recalculateStats() {
        EVECharacter miner = (EVECharacter) jComboBoxMiner.getSelectedItem();
        EVECharacter booster = (EVECharacter) jComboBoxBooster.getSelectedItem();
        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        BoosterShipContainer bCont = dCont.getBoosterContainer();
        BoosterShip bShip = bCont.isUsingBoosterShip() ? bCont.getBooster() : bCont.getNoBooster();
        
        Turret turret = ship.getTurret();
        boolean isMerco = false;
        if (turret.getOreType() == OreType.MERCOXIT) {
            if (!jCheckBoxStatsMerco.isEnabled()) jCheckBoxStatsMerco.setEnabled(true);
            isMerco = jCheckBoxStatsMerco.isSelected();
        } else {
            if (jCheckBoxStatsMerco.isEnabled()) jCheckBoxStatsMerco.setEnabled(false);
        }

        CalculatedStats newStats = new CalculatedStats(miner, booster, ship, bShip, isMerco);

        jLabelYield.setText(String.valueOf(fmt.format(newStats.getCombinedTurretYield())));
        jLabelYield.setToolTipText(fmt.format(newStats.getTurretYield())+" per turret");

        jLabelCycle.setText(fmt.format(newStats.getTurretCycle()));
        jLabelM3S.setText(fmt.format(newStats.getTurretM3S()));

        jLabelDroneYield.setText(String.valueOf(fmt.format(newStats.getCombinedDroneYield())));
        jLabelDroneYield.setToolTipText(fmt.format(newStats.getDroneYield())+" per drone");

        jLabelDroneCycle.setText(fmt.format(newStats.getDroneCycle()));
        jLabelDroneM3S.setText(fmt.format(newStats.getDroneM3S()));

        jLabelM3H.setText(fmt.format(newStats.getTotalM3H()));
        jLabelOptimal.setText(fmt.format(newStats.getOptimal()));

        jLabelOreHold.setText(fmt.format(newStats.getOreHold()));
        jLabelOreHoldFill.setText(minutesAndSeconds.print(
                Seconds.seconds(newStats.getSecsForOreHold())
                        .toStandardDuration().toPeriod()
        ));
        
        jLabelLinkCycleBonus.setText(fmt.format(newStats.getLinkCycleBonus())+"%");
        jLabelLinkOptimalBonus.setText(fmt.format(newStats.getLinkOptimalBonus())+"%");
    }

    public void loadCharacterList(boolean loadSelection) {

        CharacterContainer cCont = dCont.getCharacterContainer();

        EVECharacter miner = (EVECharacter) jComboBoxMiner.getSelectedItem();

        DefaultComboBoxModel<EVECharacter> model = cCont.getCharModel();
        jComboBoxMiner.setModel(model);

        EVECharacter booster = (EVECharacter) jComboBoxBooster.getSelectedItem();
        model = cCont.getCharModel(); // models have to be different objects.
        jComboBoxBooster.setModel(model);

        // we're assuming here that there is always something in the combobox

        if (loadSelection) {
            if (miner == null) {
                jComboBoxMiner.setSelectedIndex(0);
            } else {
                jComboBoxMiner.setSelectedItem(miner);
            }

            loadSelectedMiner();

            if (booster == null) {
                jComboBoxBooster.setSelectedIndex(0);
            } else {
                jComboBoxBooster.setSelectedItem(booster);
            }

            loadSelectedBooster();
        }

    }

    public void loadSelectedMiner() {
        // if we got there, selection is not null.
        EVECharacter sel = (EVECharacter) jComboBoxMiner.getSelectedItem();
        dCont.getCharacterContainer().setSelectedMiner(sel.getName());

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

    public void loadSelectedBooster() {
        // if we got there, selection is not null.
        EVECharacter sel = (EVECharacter) jComboBoxBooster.getSelectedItem();
        dCont.getCharacterContainer().setSelectedBooster(sel.getName());

        if (sel.isPreset()) {
            jButtonBoosterReload.setEnabled(false);

            jComboBoxMForeman.setEnabled(false);
            jComboBoxMDirector.setEnabled(false);
            jComboBoxLinkSpec.setEnabled(false);
            jComboBoxIReconf.setEnabled(false);
            jComboBoxIComShips.setEnabled(false);
            jComboBoxCapIShips.setEnabled(false);
        } else {
            jButtonBoosterReload.setEnabled(true);

            jComboBoxMForeman.setEnabled(true);
            jComboBoxMDirector.setEnabled(true);
            jComboBoxLinkSpec.setEnabled(true);
            jComboBoxIReconf.setEnabled(true);
            jComboBoxIComShips.setEnabled(true);
            jComboBoxCapIShips.setEnabled(true);
        }

        jComboBoxMForeman.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_MINING_FOREMAN));
        jComboBoxMDirector.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_MINING_DIRECTOR));
        jComboBoxLinkSpec.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_WARFARE_LINK_SPECIALIST));
        jComboBoxIReconf.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_INDUSTRIAL_RECONFIGURATION));
        jComboBoxIComShips.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_INDUSTRIAL_COMMAND_SHIPS));
        jComboBoxCapIShips.setSelectedItem(sel
                .getSkillLevel(EVECharacter.SKILL_CAPITAL_INDUSTRIAL_SHIPS));

        jCheckBoxMindlink.setSelected(sel.getSlot10Implant() == Implant.MFMINDLINK);
    }

    public void loadShipList(boolean loadSelection) {

        ShipContainer sCont = dCont.getShipContainer();

        Ship sel = (Ship) jComboBoxShip.getSelectedItem();

        DefaultComboBoxModel<Ship> model = sCont.getShipModel();
        jComboBoxShip.setModel(model);

        if (sCont.getShipCount() < 2) {
            jButtonShipRemove.setEnabled(false);
        } else {
            jButtonShipRemove.setEnabled(true);
        }

        // we're assuming here that there is always something in the combobox

        if (loadSelection) {
            if (sel == null) {
                jComboBoxShip.setSelectedIndex(0);
            } else {
                jComboBoxShip.setSelectedItem(sel);
            }

            loadSelectedShip();
        }

    }

    public void setSelectedShip(Ship ship) {
        jComboBoxShip.setSelectedItem(ship);
    }

    public void loadSelectedShip() {
        Ship ship = (Ship) jComboBoxShip.getSelectedItem();

        dCont.getShipContainer().setSelectedShip(ship.getName());
        Hull hull = ship.getHull();

        jComboBoxHull.setSelectedItem(hull);

        jComboBoxTurrets.setModel(getIntegerModel(hull.getMaxTurrets()));
        jComboBoxTurrets.setSelectedItem(ship.getTurretCount());

        updateTurretComboBox(hull);
        jComboBoxTurretType.setSelectedItem(ship.getTurret());
        updateCrystalComboBox(ship.getTurret());
        jComboBoxCrystal.setSelectedItem(ship.getTurretCrystal());

        jComboBoxHUpgradeType.setSelectedItem(ship.getHarvestUpgrade());
        jComboBoxHUpgrades.setModel(getIntegerModel(hull.getMaxUpgrades()));
        jComboBoxHUpgrades.setSelectedItem(ship.getHarvestUpgradeCount());
        if (ship.getHarvestUpgrade() == HarvestUpgrade.NOTHING) {
            if (jComboBoxHUpgrades.isEnabled()) jComboBoxHUpgrades.setEnabled(false);
        } else {
            if (!jComboBoxHUpgrades.isEnabled()) jComboBoxHUpgrades.setEnabled(true);
        }

        jComboBoxDroneType.setSelectedItem(ship.getDrone());
        jComboBoxDroneCount.setModel(getIntegerModel(ship.getMaxDrones()));
        jComboBoxDroneCount.setSelectedItem(ship.getDroneCount());

        if (ship.getDrone() == MiningDrone.NOTHING) {
            if (jComboBoxDroneCount.isEnabled()) jComboBoxDroneCount.setEnabled(false);
        } else {
            if (!jComboBoxDroneCount.isEnabled()) jComboBoxDroneCount.setEnabled(true);
        }

        updateRigComboBoxes(hull);
        jComboBoxRig1.setSelectedItem(ship.getRig1());
        jComboBoxRig2.setSelectedItem(ship.getRig2());
        jComboBoxRig3.setSelectedItem(ship.getRig3());
        updateCalibrationLabel(ship);
    }

    public void loadSelectedBoosterShip() {
        BoosterShip ship = dCont.getBoosterContainer().getBooster();

        BoosterHull hull = ship.getHull();
        jComboBoxBoosterHull.setSelectedItem(hull);
        
        if (dCont.getBoosterContainer().isUsingBoosterShip()) {
            if (hull.haveDeployedMode()) {
                if (!jCheckBoxDeployedMode.isEnabled()) jCheckBoxDeployedMode.setEnabled(true);
                jCheckBoxDeployedMode.setSelected(ship.isDeployedMode());
            } else {
                if (jCheckBoxDeployedMode.isEnabled()) jCheckBoxDeployedMode.setEnabled(false);
            }
        } else {
            if (jCheckBoxDeployedMode.isEnabled()) jCheckBoxDeployedMode.setEnabled(false);
        }

        ForemanLink cLink = ship.getCycleLink();
        jComboBoxLinkCycle.setSelectedItem(cLink);

        ForemanLink oLink = ship.getOptimalLink();
        jComboBoxLinkOptimal.setSelectedItem(oLink);
    }
    
    public void updateBoosterShipInterface() {
        boolean usingBooster = dCont.getBoosterContainer().isUsingBoosterShip();
        if (jCheckBoxUseBoosterShip.isSelected() != usingBooster) {
            jCheckBoxUseBoosterShip.setSelected(usingBooster);
        }
        
        BoosterShip ship = dCont.getBoosterContainer().getBooster();
        BoosterHull hull = ship.getHull();
        
        if (usingBooster) {
            jComboBoxBoosterHull.setEnabled(true);
            jComboBoxLinkCycle.setEnabled(true);
            jComboBoxLinkOptimal.setEnabled(true);
            if (hull.haveDeployedMode()) {
                if (!jCheckBoxDeployedMode.isEnabled()) jCheckBoxDeployedMode.setEnabled(true);
                jCheckBoxDeployedMode.setSelected(ship.isDeployedMode());
            } else {
                if (jCheckBoxDeployedMode.isEnabled()) jCheckBoxDeployedMode.setEnabled(false);
            }
        } else {
            jComboBoxBoosterHull.setEnabled(false);
            jComboBoxLinkCycle.setEnabled(false);
            jComboBoxLinkOptimal.setEnabled(false);
            jCheckBoxDeployedMode.setEnabled(false);
        }
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
     * Updates all rig combo boxes with appropriate rigs based on a hull's
     * size.
     * @param hull
     */
    public void updateRigComboBoxes(Hull hull) {
        if (hull.isMediumHull()) {
            jComboBoxRig1.setModel(new DefaultComboBoxModel<>(Rig.values()));
            jComboBoxRig2.setModel(new DefaultComboBoxModel<>(Rig.values()));
            jComboBoxRig3.setModel(new DefaultComboBoxModel<>(Rig.values()));
        } else {
            jComboBoxRig1.setModel(new DefaultComboBoxModel<>(Rig.nonMediumRigArr));
            jComboBoxRig2.setModel(new DefaultComboBoxModel<>(Rig.nonMediumRigArr));
            jComboBoxRig3.setModel(new DefaultComboBoxModel<>(Rig.nonMediumRigArr));
        }

        if (hull.getRigSlots() > 2) {
            if (!jComboBoxRig3.isEnabled()) jComboBoxRig3.setEnabled(true);
        } else {
            if (jComboBoxRig3.isEnabled()) jComboBoxRig3.setEnabled(false);
        }
    }

    public void updateCalibrationLabel(Ship ship) {
        int calibration = 0;
        calibration += ship.getRig1().getCalibrationCost();
        calibration += ship.getRig2().getCalibrationCost();
        calibration += ship.getRig3().getCalibrationCost();

        jLabelCalibration.setText(calibration+"/400");
    }

    /**
     * Deletes reference to asteroid monitor form.
     * Called when the form is closed.
     */
    public void deleteMonitorForm() {
        monitorForm = null;
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
        jPanel7 = new javax.swing.JPanel();
        jButtonAsteroidMonitor = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
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
        jComboBoxHUpgradeType = new javax.swing.JComboBox<HarvestUpgrade>(HarvestUpgrade.values());
        jComboBoxHUpgrades = new javax.swing.JComboBox<Integer>();
        jLabel18 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBoxDroneType = new javax.swing.JComboBox<MiningDrone>(MiningDrone.values());
        jLabel26 = new javax.swing.JLabel();
        jComboBoxDroneCount = new javax.swing.JComboBox<Integer>();
        jLabel31 = new javax.swing.JLabel();
        jComboBoxRig1 = new javax.swing.JComboBox<Rig>();
        jLabel33 = new javax.swing.JLabel();
        jTextFieldTrip = new javax.swing.JTextField();
        jCheckBoxHauler = new javax.swing.JCheckBox();
        jComboBoxRig2 = new javax.swing.JComboBox<Rig>();
        jComboBoxRig3 = new javax.swing.JComboBox<Rig>();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabelCalibration = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jComboBoxShip = new javax.swing.JComboBox<Ship>();
        jButtonShipAdd = new javax.swing.JButton();
        jButtonShipRemove = new javax.swing.JButton();
        jButtonShipRename = new javax.swing.JButton();
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
        jComboBoxBooster = new javax.swing.JComboBox<EVECharacter>();
        jButtonBoosterReload = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        jComboBoxMForeman = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel37 = new javax.swing.JLabel();
        jComboBoxLinkSpec = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel38 = new javax.swing.JLabel();
        jComboBoxMDirector = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel39 = new javax.swing.JLabel();
        jComboBoxIReconf = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel40 = new javax.swing.JLabel();
        jComboBoxIComShips = new javax.swing.JComboBox<Integer>(skillLvls);
        jLabel41 = new javax.swing.JLabel();
        jComboBoxCapIShips = new javax.swing.JComboBox<Integer>(skillLvls);
        jCheckBoxMindlink = new javax.swing.JCheckBox();
        jComboBoxBoosterHull = new javax.swing.JComboBox<BoosterHull>(BoosterHull.values());
        jComboBoxLinkCycle = new javax.swing.JComboBox<ForemanLink>(ForemanLink.cycleLinksArr);
        jComboBoxLinkOptimal = new javax.swing.JComboBox<ForemanLink>(ForemanLink.optimalLinksArr);
        jCheckBoxDeployedMode = new javax.swing.JCheckBox();
        jLabel42 = new javax.swing.JLabel();
        jCheckBoxUseBoosterShip = new javax.swing.JCheckBox();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabelLinkCycleBonus = new javax.swing.JLabel();
        jLabelLinkOptimalBonus = new javax.swing.JLabel();
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
        jLabelOreHold = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabelCycle = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabelDroneCycle = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabelDroneM3S = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabelOptimal = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabelOreHoldFill = new javax.swing.JLabel();
        jCheckBoxStatsMerco = new javax.swing.JCheckBox();

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

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 82, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        jToolBar1.add(jPanel7);

        jButtonAsteroidMonitor.setText("Asteroid Monitor");
        jButtonAsteroidMonitor.setFocusable(false);
        jButtonAsteroidMonitor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAsteroidMonitor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAsteroidMonitor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAsteroidMonitorActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonAsteroidMonitor);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 798, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        jToolBar1.add(jPanel1);

        jButtonSave.setText("Save");
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSave);

        JButtonQuit.setText("Quit");
        JButtonQuit.setToolTipText("");
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

        jComboBoxHUpgradeType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxHUpgradeTypeItemStateChanged(evt);
            }
        });

        jComboBoxHUpgrades.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxHUpgradesItemStateChanged(evt);
            }
        });

        jLabel18.setText("Upgrades");

        jLabel24.setText("Drone Type");

        jComboBoxDroneType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxDroneTypeItemStateChanged(evt);
            }
        });

        jLabel26.setText("Drones");

        jComboBoxDroneCount.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxDroneCountItemStateChanged(evt);
            }
        });

        jLabel31.setText("Rig 1");

        jComboBoxRig1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxRig1ItemStateChanged(evt);
            }
        });

        jLabel33.setText("Station Trip, sec:");

        jCheckBoxHauler.setText("Dedicated Hauler");

        jComboBoxRig2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxRig2ItemStateChanged(evt);
            }
        });

        jComboBoxRig3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxRig3ItemStateChanged(evt);
            }
        });

        jLabel34.setText("Rig 2");

        jLabel35.setText("Rig 3");

        jLabelCalibration.setText("0/400");

        jLabel32.setText("Calibration:");

        jComboBoxShip.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxShipItemStateChanged(evt);
            }
        });

        jButtonShipAdd.setText("Add");
        jButtonShipAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShipAddActionPerformed(evt);
            }
        });

        jButtonShipRemove.setText("Remove");
        jButtonShipRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShipRemoveActionPerformed(evt);
            }
        });

        jButtonShipRename.setText("Rename");
        jButtonShipRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShipRenameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldTrip, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxHauler)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonShipAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonShipRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonShipRename)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jComboBoxShip, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel34)
                                    .addComponent(jLabel35))
                                .addGap(50, 50, 50))
                            .addComponent(jComboBoxRig3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(JLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jComboBoxHull, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(10, 10, 10)))
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jComboBoxTurrets, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jComboBoxRig1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxRig2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel31)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel32))
                                    .addComponent(jComboBoxHUpgradeType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBoxDroneType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBoxTurretType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15)
                                            .addComponent(jLabel17)
                                            .addComponent(jLabel24))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel16)
                                        .addComponent(jComboBoxCrystal, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jComboBoxDroneCount, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel26)
                                        .addComponent(jComboBoxHUpgrades, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabelCalibration, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxShip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonShipAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonShipRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonShipRename, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(jComboBoxDroneType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxDroneCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jLabelCalibration)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxRig1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxRig2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxRig3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jTextFieldTrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxHauler)))
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
                        .addComponent(jComboBoxMiner, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCharReload)
                        .addGap(0, 0, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(jCheckBoxMichi))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Booster"));

        jComboBoxBooster.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxBoosterItemStateChanged(evt);
            }
        });

        jButtonBoosterReload.setText("Reload");
        jButtonBoosterReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBoosterReloadActionPerformed(evt);
            }
        });

        jLabel36.setText("Mining Foreman");

        jComboBoxMForeman.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMForemanItemStateChanged(evt);
            }
        });

        jLabel37.setText("Warfare Link Specialist");

        jComboBoxLinkSpec.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxLinkSpecItemStateChanged(evt);
            }
        });

        jLabel38.setText("Mining Director");

        jComboBoxMDirector.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMDirectorItemStateChanged(evt);
            }
        });

        jLabel39.setText("Industrial Reconfiguration");

        jComboBoxIReconf.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxIReconfItemStateChanged(evt);
            }
        });

        jLabel40.setText("Industrial Command Ships");

        jComboBoxIComShips.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxIComShipsItemStateChanged(evt);
            }
        });

        jLabel41.setText("Capital Industrial Ships");

        jComboBoxCapIShips.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCapIShipsItemStateChanged(evt);
            }
        });

        jCheckBoxMindlink.setText("Mining Foreman Mindlink");
        jCheckBoxMindlink.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMindlinkItemStateChanged(evt);
            }
        });

        jComboBoxBoosterHull.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxBoosterHullItemStateChanged(evt);
            }
        });

        jComboBoxLinkCycle.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxLinkCycleItemStateChanged(evt);
            }
        });

        jComboBoxLinkOptimal.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxLinkOptimalItemStateChanged(evt);
            }
        });

        jCheckBoxDeployedMode.setText("Deployed Mode");
        jCheckBoxDeployedMode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxDeployedModeItemStateChanged(evt);
            }
        });

        jLabel42.setText("Booster Ship");

        jCheckBoxUseBoosterShip.setText("Dedicated Booster Ship");
        jCheckBoxUseBoosterShip.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBoxUseBoosterShip.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxUseBoosterShipItemStateChanged(evt);
            }
        });

        jLabel43.setText("Bonus:");

        jLabel44.setText("Bonus:");

        jLabelLinkCycleBonus.setText("0%");

        jLabelLinkOptimalBonus.setText("0%");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxIComShips, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxLinkSpec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxMForeman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(174, 174, 174)
                                .addComponent(jComboBoxIReconf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBoxCapIShips, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jComboBoxMDirector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jComboBoxBooster, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBoosterReload, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBoxMindlink)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jCheckBoxUseBoosterShip))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jComboBoxBoosterHull, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxDeployedMode))
                    .addComponent(jLabel42)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxLinkCycle, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelLinkCycleBonus)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelLinkOptimalBonus)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jComboBoxLinkOptimal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxBooster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBoosterReload, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(jLabel38)
                    .addComponent(jComboBoxMDirector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMForeman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jComboBoxLinkSpec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(jComboBoxIReconf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jComboBoxIComShips, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(jComboBoxCapIShips, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxMindlink)
                    .addComponent(jCheckBoxUseBoosterShip))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxBoosterHull, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxDeployedMode))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxLinkCycle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxLinkOptimal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(jLabel44)
                    .addComponent(jLabelLinkCycleBonus)
                    .addComponent(jLabelLinkOptimalBonus)))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Stats"));

        jLabel19.setText("Yield, m3:");

        jLabelYield.setText("0");
        jLabelYield.setToolTipText("0 per turret");

        jLabel28.setText("Drone Yield, m3:");

        jLabelDroneYield.setText("0");
        jLabelDroneYield.setToolTipText("0 per drone");

        jLabel20.setText("m3/s");

        jLabelM3S.setText("0");

        jLabel23.setText("m3/h:");

        jLabelM3H.setText("0");

        jLabel25.setText("Ore Hold, m3:");

        jLabelOreHold.setText("0");

        jLabel21.setText("Cycle, sec:");

        jLabelCycle.setText("0");

        jLabel29.setText("Drone Cycle, sec:");

        jLabelDroneCycle.setText("0");

        jLabel30.setText("Drone m3/s");

        jLabelDroneM3S.setText("0");

        jLabel22.setText("Optimal, km:");

        jLabelOptimal.setText("0");

        jLabel27.setText("Hold Fills In, min:");

        jLabelOreHoldFill.setText("0");

        jCheckBoxStatsMerco.setText("Mercoxit");
        jCheckBoxStatsMerco.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jCheckBoxStatsMerco.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxStatsMercoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelOreHoldFill, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelM3S, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDroneYield, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDroneCycle, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDroneM3S, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelM3H, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelOptimal, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelOreHold, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelYield, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                            .addComponent(jLabelCycle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBoxStatsMerco)
                .addGap(61, 61, 61))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jCheckBoxStatsMerco)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(jLabelOreHold))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabelOreHoldFill))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 273, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void JButtonQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButtonQuitActionPerformed
        JQuitDialog dlg = new JQuitDialog(this, dCont);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_JButtonQuitActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        JQuitDialog dlg = new JQuitDialog(this, dCont);
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
        recalculateStats();

        processEvents = true;
    }//GEN-LAST:event_jComboBoxMinerItemStateChanged

    private void jButtonCharReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCharReloadActionPerformed
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        final JWaitDialog dlg = new JWaitDialog(this, "Character Data", dCont);

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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
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

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxDroneIntItemStateChanged

    private void jComboBoxImplant8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxImplant8ItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Implant imp = (Implant) jComboBoxImplant8.getSelectedItem();
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null) return;

        curChar.setSlot8Implant(imp);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxImplant8ItemStateChanged

    private void jComboBoxImplant10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxImplant10ItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Implant imp = (Implant) jComboBoxImplant10.getSelectedItem();
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null) return;

        curChar.setSlot10Implant(imp);

        EVECharacter booster = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar.equals(booster)) {
            jCheckBoxMindlink.setSelected(imp == Implant.MFMINDLINK);
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxImplant10ItemStateChanged

    private void jCheckBoxMichiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMichiItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        boolean checked = jCheckBoxMichi.isSelected();
        EVECharacter curChar = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar == null) return;

        curChar.setSlot7Implant(checked? Implant.MICHI : Implant.NOTHING);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jCheckBoxMichiItemStateChanged

    private void jComboBoxHullItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxHullItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
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

        jComboBoxHUpgradeType.setSelectedItem(ship.getHarvestUpgrade());

        jComboBoxHUpgrades.setModel(getIntegerModel(newHull.getMaxUpgrades()));
        jComboBoxHUpgrades.setSelectedItem(ship.getHarvestUpgradeCount());

        jComboBoxDroneCount.setModel(getIntegerModel(ship.getMaxDrones()));
        jComboBoxDroneCount.setSelectedItem(ship.getDroneCount());
        if (ship.getDrone() == MiningDrone.NOTHING) {
            if (jComboBoxDroneCount.isEnabled()) jComboBoxDroneCount.setEnabled(false);
        } else {
            if (!jComboBoxDroneCount.isEnabled()) jComboBoxDroneCount.setEnabled(true);
        }

        updateRigComboBoxes(newHull);
        jComboBoxRig1.setSelectedItem(ship.getRig1());
        jComboBoxRig2.setSelectedItem(ship.getRig2());
        jComboBoxRig3.setSelectedItem(ship.getRig3());
        updateCalibrationLabel(ship);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxHullItemStateChanged

    private void jComboBoxTurretsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTurretsItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        Integer turretcnt = (Integer) jComboBoxTurrets.getSelectedItem();
        ship.setTurrentCount(turretcnt);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxTurretsItemStateChanged

    private void jComboBoxTurretTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTurretTypeItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        Turret newTurret = (Turret) (jComboBoxTurretType.getSelectedItem());
        ship.setTurret(newTurret);
        newTurret = ship.getTurret();

        updateCrystalComboBox(newTurret);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxTurretTypeItemStateChanged

    private void jComboBoxCrystalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCrystalItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        MiningCrystalLevel crystal = (MiningCrystalLevel) jComboBoxCrystal.getSelectedItem();
        ship.setTurretCrystal(crystal);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxCrystalItemStateChanged

    private void jComboBoxHUpgradeTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxHUpgradeTypeItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        HarvestUpgrade upgrade = (HarvestUpgrade) jComboBoxHUpgradeType.getSelectedItem();
        ship.setHarvestUpgrade(upgrade);

        if (ship.getHarvestUpgrade() == HarvestUpgrade.NOTHING) {
            if (jComboBoxHUpgrades.isEnabled()) jComboBoxHUpgrades.setEnabled(false);
        } else {
            if (!jComboBoxHUpgrades.isEnabled()) jComboBoxHUpgrades.setEnabled(true);
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxHUpgradeTypeItemStateChanged

    private void jComboBoxHUpgradesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxHUpgradesItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        Integer upcnt = (Integer) jComboBoxHUpgrades.getSelectedItem();
        ship.setHarvestUpgradeCount(upcnt);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxHUpgradesItemStateChanged

    private void jCheckBoxStatsMercoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxStatsMercoItemStateChanged
        if (!processEvents) return;

        processEvents = false;
        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jCheckBoxStatsMercoItemStateChanged

    private void jComboBoxDroneTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxDroneTypeItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        MiningDrone drone =  (MiningDrone) jComboBoxDroneType.getSelectedItem();

        ship.setDrone(drone);

        jComboBoxDroneCount.setModel(getIntegerModel(ship.getMaxDrones()));
        jComboBoxDroneCount.setSelectedItem(ship.getDroneCount());
        if (ship.getDrone() == MiningDrone.NOTHING) {
            if (jComboBoxDroneCount.isEnabled()) jComboBoxDroneCount.setEnabled(false);
        } else {
            if (!jComboBoxDroneCount.isEnabled()) jComboBoxDroneCount.setEnabled(true);
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxDroneTypeItemStateChanged

    private void jComboBoxDroneCountItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxDroneCountItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        Integer droneCount = (Integer) jComboBoxDroneCount.getSelectedItem();
        ship.setDroneCount(droneCount);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxDroneCountItemStateChanged

    private void jComboBoxRig1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxRig1ItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        Rig rig = (Rig) jComboBoxRig1.getSelectedItem();
        if (!ship.setRig1(rig)) {
            jComboBoxRig1.setSelectedItem(ship.getRig1());
        }
        updateCalibrationLabel(ship);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxRig1ItemStateChanged

    private void jComboBoxRig2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxRig2ItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        Rig rig = (Rig) jComboBoxRig2.getSelectedItem();
        if (!ship.setRig2(rig)) {
            jComboBoxRig2.setSelectedItem(ship.getRig2());
        }
        updateCalibrationLabel(ship);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxRig2ItemStateChanged

    private void jComboBoxRig3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxRig3ItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        Rig rig = (Rig) jComboBoxRig3.getSelectedItem();
        if (!ship.setRig3(rig)) {
            jComboBoxRig3.setSelectedItem(ship.getRig3());
        }
        updateCalibrationLabel(ship);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxRig3ItemStateChanged

    private void jComboBoxShipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxShipItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        loadSelectedShip();

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxShipItemStateChanged

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        if (!processEvents) return;

        processEvents = false;

        dCont.save();
        processEvents = true;
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonShipAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShipAddActionPerformed
        JNewShipDialog dlg = new JNewShipDialog(this, dCont.getShipContainer());

        dlg.setLocationRelativeTo(MainFrame.this);
        dlg.setVisible(true);
    }//GEN-LAST:event_jButtonShipAddActionPerformed

    private void jButtonShipRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShipRemoveActionPerformed
        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        if (ship == null || dCont.getShipContainer().getShipCount() < 2) return;
        JRemoveShipDialog dlg = new JRemoveShipDialog(this, ship, dCont.getShipContainer());

        dlg.setLocationRelativeTo(MainFrame.this);
        dlg.setVisible(true);
    }//GEN-LAST:event_jButtonShipRemoveActionPerformed

    private void jButtonShipRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShipRenameActionPerformed
        Ship ship = (Ship) jComboBoxShip.getSelectedItem();
        if (ship == null) return;
        JChangeShipNameDialog dlg = new JChangeShipNameDialog(this, ship, dCont.getShipContainer());

        dlg.setLocationRelativeTo(MainFrame.this);
        dlg.setVisible(true);
    }//GEN-LAST:event_jButtonShipRenameActionPerformed

    private void jComboBoxBoosterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxBoosterItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        loadSelectedBooster();

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxBoosterItemStateChanged

    private void jComboBoxMForemanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMForemanItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Integer level = (Integer) jComboBoxMForeman.getSelectedItem();

        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        curChar.setSkillLevel(EVECharacter.SKILL_MINING_FOREMAN, level);

        if (level < 5) {
            curChar.setSkillLevel(EVECharacter.SKILL_MINING_DIRECTOR, 0);
            jComboBoxMDirector.setSelectedItem(0);
            curChar.setSkillLevel(EVECharacter.SKILL_INDUSTRIAL_COMMAND_SHIPS, 0);
            jComboBoxIComShips.setSelectedItem(0);
            curChar.setSkillLevel(EVECharacter.SKILL_CAPITAL_INDUSTRIAL_SHIPS, 0);
            jComboBoxCapIShips.setSelectedItem(0);
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxMForemanItemStateChanged

    private void jComboBoxMDirectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMDirectorItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Integer level = (Integer) jComboBoxMDirector.getSelectedItem();

        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        curChar.setSkillLevel(EVECharacter.SKILL_MINING_DIRECTOR, level);

        if (level < 1) {
            curChar.setSkillLevel(EVECharacter.SKILL_INDUSTRIAL_COMMAND_SHIPS, 0);
            jComboBoxIComShips.setSelectedItem(0);
            curChar.setSkillLevel(EVECharacter.SKILL_CAPITAL_INDUSTRIAL_SHIPS, 0);
            jComboBoxCapIShips.setSelectedItem(0);
        }

        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING_FOREMAN) < 5) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING_FOREMAN, 5);
                jComboBoxMining.setSelectedItem(5);
            }
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxMDirectorItemStateChanged

    private void jComboBoxIReconfItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxIReconfItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Integer level = (Integer) jComboBoxIReconf.getSelectedItem();

        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        curChar.setSkillLevel(EVECharacter.SKILL_INDUSTRIAL_RECONFIGURATION, level);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxIReconfItemStateChanged

    private void jComboBoxLinkSpecItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLinkSpecItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Integer level = (Integer) jComboBoxLinkSpec.getSelectedItem();

        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        curChar.setSkillLevel(EVECharacter.SKILL_WARFARE_LINK_SPECIALIST, level);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxLinkSpecItemStateChanged

    private void jComboBoxIComShipsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxIComShipsItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Integer level = (Integer) jComboBoxIComShips.getSelectedItem();

        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        curChar.setSkillLevel(EVECharacter.SKILL_INDUSTRIAL_COMMAND_SHIPS, level);

        if (level < 3) {
            curChar.setSkillLevel(EVECharacter.SKILL_CAPITAL_INDUSTRIAL_SHIPS, 0);
            jComboBoxCapIShips.setSelectedItem(0);
        }

        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING_FOREMAN) < 5) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING_FOREMAN, 5);
                jComboBoxMining.setSelectedItem(5);
            }

            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING_DIRECTOR) < 1) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING_DIRECTOR, 1);
                jComboBoxMining.setSelectedItem(1);
            }
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxIComShipsItemStateChanged

    private void jComboBoxCapIShipsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCapIShipsItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        Integer level = (Integer) jComboBoxCapIShips.getSelectedItem();

        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        curChar.setSkillLevel(EVECharacter.SKILL_CAPITAL_INDUSTRIAL_SHIPS, level);

        if (level > 0) {
            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING_FOREMAN) < 5) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING_FOREMAN, 5);
                jComboBoxMining.setSelectedItem(5);
            }

            if (curChar.getSkillLevel(EVECharacter.SKILL_MINING_DIRECTOR) < 1) {
                curChar.setSkillLevel(EVECharacter.SKILL_MINING_DIRECTOR, 1);
                jComboBoxMining.setSelectedItem(1);
            }

            if (curChar.getSkillLevel(EVECharacter.SKILL_INDUSTRIAL_COMMAND_SHIPS) < 3) {
                curChar.setSkillLevel(EVECharacter.SKILL_INDUSTRIAL_COMMAND_SHIPS, 3);
                jComboBoxMining.setSelectedItem(3);
            }
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxCapIShipsItemStateChanged

    private void jCheckBoxMindlinkItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMindlinkItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        boolean checked = jCheckBoxMindlink.isSelected();
        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null) return;

        Implant implant = checked? Implant.MFMINDLINK : Implant.NOTHING;
        curChar.setSlot10Implant(implant);

        EVECharacter miner = (EVECharacter) jComboBoxMiner.getSelectedItem();
        if (curChar.equals(miner)) {
            jComboBoxImplant10.setSelectedItem(implant);
        }

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jCheckBoxMindlinkItemStateChanged

    private void jCheckBoxDeployedModeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxDeployedModeItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        boolean checked = jCheckBoxDeployedMode.isSelected();
        BoosterShip ship = dCont.getBoosterContainer().getBooster();
        ship.setDeployedMode(checked);

        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jCheckBoxDeployedModeItemStateChanged

    private void jComboBoxBoosterHullItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxBoosterHullItemStateChanged
        if (!processEvents) return;

        processEvents = false;

        BoosterShip ship = dCont.getBoosterContainer().getBooster();
        ship.setHull((BoosterHull) jComboBoxBoosterHull.getSelectedItem());
        if (ship.getHull().haveDeployedMode()) {
            if (!jCheckBoxDeployedMode.isEnabled()) jCheckBoxDeployedMode.setEnabled(true);
        } else {
            if (jCheckBoxDeployedMode.isEnabled()) jCheckBoxDeployedMode.setEnabled(false);
        }
        
        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxBoosterHullItemStateChanged

    private void jComboBoxLinkCycleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLinkCycleItemStateChanged
        if (!processEvents) return;

        processEvents = false;
        
        BoosterShip ship = dCont.getBoosterContainer().getBooster();
        ship.setCycleLink((ForemanLink) jComboBoxLinkCycle.getSelectedItem());
        
        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxLinkCycleItemStateChanged

    private void jComboBoxLinkOptimalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLinkOptimalItemStateChanged
        if (!processEvents) return;

        processEvents = false;
        
        BoosterShip ship = dCont.getBoosterContainer().getBooster();
        ship.setOptimalLink((ForemanLink) jComboBoxLinkOptimal.getSelectedItem());
        
        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jComboBoxLinkOptimalItemStateChanged

    private void jCheckBoxUseBoosterShipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxUseBoosterShipItemStateChanged
        if (!processEvents) return;

        processEvents = false;
        
        dCont.getBoosterContainer().setUsingBoosterShip(jCheckBoxUseBoosterShip.isSelected());
        updateBoosterShipInterface();
        
        recalculateStats();
        processEvents = true;
    }//GEN-LAST:event_jCheckBoxUseBoosterShipItemStateChanged

    private void jButtonBoosterReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBoosterReloadActionPerformed
        EVECharacter curChar = (EVECharacter) jComboBoxBooster.getSelectedItem();
        if (curChar == null || curChar.isPreset()) return;

        final JWaitDialog dlg = new JWaitDialog(this, "Character Data", dCont);

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
    }//GEN-LAST:event_jButtonBoosterReloadActionPerformed

    private void jButtonAsteroidMonitorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAsteroidMonitorActionPerformed
        if (wManager == null) return;
        
        if (monitorForm == null) {
            monitorForm = new JAsteroidMonitorForm(this, dCont, wManager);
            monitorForm.setLocationRelativeTo(this);
            monitorForm.setVisible(true);
        }
        
        monitorForm.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButtonAsteroidMonitorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JButtonManageAPI;
    private javax.swing.JButton JButtonQuit;
    private javax.swing.JLabel JLabel14;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonAsteroidMonitor;
    private javax.swing.JButton jButtonBoosterReload;
    private javax.swing.JButton jButtonCharReload;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonShipAdd;
    private javax.swing.JButton jButtonShipRemove;
    private javax.swing.JButton jButtonShipRename;
    private javax.swing.JCheckBox jCheckBoxDeployedMode;
    private javax.swing.JCheckBox jCheckBoxHauler;
    private javax.swing.JCheckBox jCheckBoxMichi;
    private javax.swing.JCheckBox jCheckBoxMindlink;
    private javax.swing.JCheckBox jCheckBoxStatsMerco;
    private javax.swing.JCheckBox jCheckBoxUseBoosterShip;
    private javax.swing.JComboBox<Integer> jComboBoxAstrogeo;
    private javax.swing.JComboBox<EVECharacter> jComboBoxBooster;
    private javax.swing.JComboBox<BoosterHull> jComboBoxBoosterHull;
    private javax.swing.JComboBox<Integer> jComboBoxCapIShips;
    private javax.swing.JComboBox<MiningCrystalLevel> jComboBoxCrystal;
    private javax.swing.JComboBox<Integer> jComboBoxDroneCount;
    private javax.swing.JComboBox<Integer> jComboBoxDroneInt;
    private javax.swing.JComboBox<MiningDrone> jComboBoxDroneType;
    private javax.swing.JComboBox<Integer> jComboBoxDrones;
    private javax.swing.JComboBox<Integer> jComboBoxExhumers;
    private javax.swing.JComboBox<Integer> jComboBoxExpeFrig;
    private javax.swing.JComboBox<Integer> jComboBoxGasHar;
    private javax.swing.JComboBox<HarvestUpgrade> jComboBoxHUpgradeType;
    private javax.swing.JComboBox<Integer> jComboBoxHUpgrades;
    private javax.swing.JComboBox<Hull> jComboBoxHull;
    private javax.swing.JComboBox<Integer> jComboBoxIComShips;
    private javax.swing.JComboBox<Integer> jComboBoxIReconf;
    private javax.swing.JComboBox<Integer> jComboBoxIceHar;
    private javax.swing.JComboBox<Implant> jComboBoxImplant10;
    private javax.swing.JComboBox<Implant> jComboBoxImplant8;
    private javax.swing.JComboBox<ForemanLink> jComboBoxLinkCycle;
    private javax.swing.JComboBox<ForemanLink> jComboBoxLinkOptimal;
    private javax.swing.JComboBox<Integer> jComboBoxLinkSpec;
    private javax.swing.JComboBox<Integer> jComboBoxMDirector;
    private javax.swing.JComboBox<Integer> jComboBoxMForeman;
    private javax.swing.JComboBox<EVECharacter> jComboBoxMiner;
    private javax.swing.JComboBox<Integer> jComboBoxMining;
    private javax.swing.JComboBox<Integer> jComboBoxMiningBarge;
    private javax.swing.JComboBox<Integer> jComboBoxMiningDrones;
    private javax.swing.JComboBox<Integer> jComboBoxMiningFrig;
    private javax.swing.JComboBox<Rig> jComboBoxRig1;
    private javax.swing.JComboBox<Rig> jComboBoxRig2;
    private javax.swing.JComboBox<Rig> jComboBoxRig3;
    private javax.swing.JComboBox<Ship> jComboBoxShip;
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
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCalibration;
    private javax.swing.JLabel jLabelCycle;
    private javax.swing.JLabel jLabelDroneCycle;
    private javax.swing.JLabel jLabelDroneM3S;
    private javax.swing.JLabel jLabelDroneYield;
    private javax.swing.JLabel jLabelLinkCycleBonus;
    private javax.swing.JLabel jLabelLinkOptimalBonus;
    private javax.swing.JLabel jLabelM3H;
    private javax.swing.JLabel jLabelM3S;
    private javax.swing.JLabel jLabelOptimal;
    private javax.swing.JLabel jLabelOreHold;
    private javax.swing.JLabel jLabelOreHoldFill;
    private javax.swing.JLabel jLabelYield;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldTrip;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}

