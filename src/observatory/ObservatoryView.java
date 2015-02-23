package observatory;

//JAVA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.EditorKit;

//LICENSER, VRODDON
import oeg.ckan.CKANDatasets;
import oeg.ckan.CKANExplorer;
import oeg.ckan.CKANExplorerold;
import oeg.lov.LOVVocabs;
import licenser.LicenseFinder;
import vroddon.commons.StringUtils;
import vroddon.sw.Modelo;
import vroddon.sw.Vocab;
//import net.infonode.gui.laf.InfoNodeLookAndFeel;


//INFONODE LOOK AND FEEL
import net.infonode.gui.laf.InfoNodeLookAndFeel;

import vroddon.sw.Licenser;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;
import vroddon.hilos.EjecutadorOperacionLenta;
import vroddon.hilos.OperacionLenta;
import vroddon.hilos.Reportador;
import vroddon.sw.Dataset;
import vroddon.sw.RDFDump;
import vroddon.sw.SemanticWeb;

/**
 * The application's main frame.
 */
public class ObservatoryView extends FrameView implements Reportador {

    public ObservatoryView(SingleFrameApplication app) {
        super(app);


        //elegimos el color
        try {
            UIManager.setLookAndFeel(new InfoNodeLookAndFeel());
            setUIFont(new javax.swing.plaf.FontUIResource("Arial", 0, 13));//Font.ITALIC
        } catch (Exception e) {
            //no informamos porque no es importante
        }


        initComponents();

        //pintamos 
        setAppIcon();


        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        initPanels();


        edConsola.setContentType("text/html");
        edConsola.setFont(new Font("Courier New", Font.PLAIN, 12));
        edConsola.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        edConsola.setFont(new Font("Courier New", Font.PLAIN, 14));
//        final EditorKit kit = edConsola.getEditorKitForContentType("text/html");
//        edConsola.setEditorKit(kit);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        for (String s : fontNames) {
            //     System.out.println(s);
        }
        edConsola.setText("<html> Welcome to the <font color=\"red\">Linked Data</font> Observatory! </html>");
        edConsola.setContentType("text/plain");

        /* esto funciona. usar si quiere implementar cosas
        AbstractAction find = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
        System.out.println("dio");
        }
        };
        panelMain.getInputMap().put(KeyStroke.getKeyStroke("F3"),"find");
        panelMain.getActionMap().put("find",find);        
         */

        listVocabs.addMouseListener(new ActionJList(listVocabs));
        listVocabs.setCellRenderer(new RendererListVocab());
        listVocabs.addKeyListener(new ActionJList(listVocabs));
        listDatasets.setCellRenderer(new RendererListDataset());
        listDatasets.addMouseListener(new ActionJList(listDatasets));




    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public void initPanels() {
        /*        List<Vocab> vocabs = LOVVocabs.getPreloadedVocabs("lovvocabs.txt");
        listVocabs.removeAll();
        DefaultListModel listModel = new DefaultListModel();
        for (Vocab v : vocabs) {
        listModel.addElement(v);
        }
        listVocabs.setModel(listModel);
        //        Object objetos[] = new Object[vocabs.size()];
        //       listVocabs.setListData(objetos);
        listVocabs.updateUI();*/
    }

    public void refreshVocabs() {
        listVocabs.removeAll();
        DefaultListModel listModel = new DefaultListModel();
        for (Vocab v : Observatory.observation.vocabs) {
            listModel.addElement(v);
        }
        listVocabs.setModel(listModel);
        listVocabs.updateUI();
    }

    public void refreshDatasets() {
        listDatasets.removeAll();
        DefaultListModel listModel = new DefaultListModel();
        for (Dataset d : Observatory.observation.datasets) {
            listModel.addElement(d);
        }
        listDatasets.setModel(listModel);
        listDatasets.updateUI();
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ObservatoryApp.getApplication().getMainFrame();
            aboutBox = new ObservatoryAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ObservatoryApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        tabContainerx = new javax.swing.JTabbedPane();
        tabVocabs = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listVocabs = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listDatasets = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        btnLoadVocabs = new javax.swing.JButton();
        btnViewDetails = new javax.swing.JButton();
        btnSearchLicense = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        edConsola = new javax.swing.JEditorPane();
        btnReporte = new javax.swing.JButton();
        btnTestHealth = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        menuLoadFile = new javax.swing.JMenuItem();
        menuFileOpenDump = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuLoad = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuRDFDumpCountTriples = new javax.swing.JMenuItem();
        menuRDFDumpFilter = new javax.swing.JMenuItem();
        menuRDFDumpExportCSV = new javax.swing.JMenuItem();
        menuID = new javax.swing.JMenu();
        menuJoker = new javax.swing.JMenuItem();
        menuJoker2 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        tabContainerx.setName("tabContainerx"); // NOI18N

        tabVocabs.setName("tabVocabs"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        listVocabs.setName("listVocabs"); // NOI18N
        jScrollPane1.setViewportView(listVocabs);

        javax.swing.GroupLayout tabVocabsLayout = new javax.swing.GroupLayout(tabVocabs);
        tabVocabs.setLayout(tabVocabsLayout);
        tabVocabsLayout.setHorizontalGroup(
            tabVocabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVocabsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabVocabsLayout.setVerticalGroup(
            tabVocabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabVocabsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ObservatoryView.class);
        tabContainerx.addTab(resourceMap.getString("tabVocabs.TabConstraints.tabTitle"), tabVocabs); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        listDatasets.setName("listDatasets"); // NOI18N
        jScrollPane2.setViewportView(listDatasets);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabContainerx.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 221, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 446, Short.MAX_VALUE)
        );

        tabContainerx.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        btnLoadVocabs.setIcon(resourceMap.getIcon("btnLoadVocabs.icon")); // NOI18N
        btnLoadVocabs.setText(resourceMap.getString("btnLoadVocabs.text")); // NOI18N
        btnLoadVocabs.setToolTipText(resourceMap.getString("btnLoadVocabs.toolTipText")); // NOI18N
        btnLoadVocabs.setName("btnLoadVocabs"); // NOI18N
        btnLoadVocabs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadVocabsActionPerformed(evt);
            }
        });

        btnViewDetails.setIcon(resourceMap.getIcon("btnViewDetails.icon")); // NOI18N
        btnViewDetails.setToolTipText(resourceMap.getString("btnViewDetails.toolTipText")); // NOI18N
        btnViewDetails.setName("btnViewDetails"); // NOI18N
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });

        btnSearchLicense.setIcon(resourceMap.getIcon("btnSearchLicense.icon")); // NOI18N
        btnSearchLicense.setText(resourceMap.getString("btnSearchLicense.text")); // NOI18N
        btnSearchLicense.setToolTipText(resourceMap.getString("btnSearchLicense.toolTipText")); // NOI18N
        btnSearchLicense.setName("btnSearchLicense"); // NOI18N
        btnSearchLicense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchLicenseActionPerformed(evt);
            }
        });

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        edConsola.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        edConsola.setName("edConsola"); // NOI18N
        jScrollPane3.setViewportView(edConsola);

        btnReporte.setIcon(resourceMap.getIcon("btnReporte.icon")); // NOI18N
        btnReporte.setText(resourceMap.getString("btnReporte.text")); // NOI18N
        btnReporte.setToolTipText(resourceMap.getString("btnReporte.toolTipText")); // NOI18N
        btnReporte.setName("btnReporte"); // NOI18N
        btnReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteActionPerformed(evt);
            }
        });

        btnTestHealth.setIcon(resourceMap.getIcon("btnTestHealth.icon")); // NOI18N
        btnTestHealth.setToolTipText(resourceMap.getString("btnTestHealth.toolTipText")); // NOI18N
        btnTestHealth.setName("btnTestHealth"); // NOI18N
        btnTestHealth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestHealthActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabContainerx, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(btnLoadVocabs, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTestHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearchLicense, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tabContainerx, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLoadVocabs)
                            .addComponent(btnViewDetails)
                            .addComponent(btnSearchLicense)
                            .addComponent(btnReporte)
                            .addComponent(btnTestHealth))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        menuLoadFile.setText(resourceMap.getString("menuLoadFile.text")); // NOI18N
        menuLoadFile.setName("menuLoadFile"); // NOI18N
        menuLoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLoadFileActionPerformed(evt);
            }
        });
        fileMenu.add(menuLoadFile);

        menuFileOpenDump.setText(resourceMap.getString("menuFileOpenDump.text")); // NOI18N
        menuFileOpenDump.setName("menuFileOpenDump"); // NOI18N
        menuFileOpenDump.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileOpenDumpActionPerformed(evt);
            }
        });
        fileMenu.add(menuFileOpenDump);

        menuSave.setText(resourceMap.getString("menuSave.text")); // NOI18N
        menuSave.setName("menuSave"); // NOI18N
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        fileMenu.add(menuSave);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ObservatoryView.class, this);
        menuLoad.setAction(actionMap.get("quit")); // NOI18N
        menuLoad.setText(resourceMap.getString("menuLoad.text")); // NOI18N
        menuLoad.setName("menuLoad"); // NOI18N
        menuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLoadActionPerformed(evt);
            }
        });
        fileMenu.add(menuLoad);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu1.add(jMenuItem3);

        menuBar.add(jMenu1);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        menuRDFDumpCountTriples.setText(resourceMap.getString("menuRDFDumpCountTriples.text")); // NOI18N
        menuRDFDumpCountTriples.setName("menuRDFDumpCountTriples"); // NOI18N
        menuRDFDumpCountTriples.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRDFDumpCountTriplesActionPerformed(evt);
            }
        });
        jMenu2.add(menuRDFDumpCountTriples);

        menuRDFDumpFilter.setText(resourceMap.getString("menuRDFDumpFilter.text")); // NOI18N
        menuRDFDumpFilter.setName("menuRDFDumpFilter"); // NOI18N
        menuRDFDumpFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRDFDumpFilterActionPerformed(evt);
            }
        });
        jMenu2.add(menuRDFDumpFilter);

        menuRDFDumpExportCSV.setText(resourceMap.getString("menuRDFDumpExportCSV.text")); // NOI18N
        menuRDFDumpExportCSV.setName("menuRDFDumpExportCSV"); // NOI18N
        menuRDFDumpExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRDFDumpExportCSVActionPerformed(evt);
            }
        });
        jMenu2.add(menuRDFDumpExportCSV);

        menuBar.add(jMenu2);

        menuID.setText(resourceMap.getString("menuID.text")); // NOI18N
        menuID.setName("menuID"); // NOI18N
        menuID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuIDActionPerformed(evt);
            }
        });

        menuJoker.setText(resourceMap.getString("menuJoker.text")); // NOI18N
        menuJoker.setName("menuJoker"); // NOI18N
        menuJoker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuJokerActionPerformed(evt);
            }
        });
        menuID.add(menuJoker);

        menuJoker2.setText(resourceMap.getString("menuJoker2.text")); // NOI18N
        menuJoker2.setName("menuJoker2"); // NOI18N
        menuJoker2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuJoker2ActionPerformed(evt);
            }
        });
        menuID.add(menuJoker2);

        menuBar.add(menuID);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

private void btnLoadVocabsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadVocabsActionPerformed
    int pestana = tabContainerx.getSelectedIndex();
    if (pestana == 0) {
        Observation.loadListFromFile("./observations/vocabs.txt");
        this.refreshVocabs();
    }
    if (pestana == 1) {
        List<String> ldatasets = CKANDatasets.getPreloadedDatasets("loddatasets.txt");
        for (String dataset : ldatasets) {
            String extension = "";
            //     List<String> uris=CKANExplorer.getResourceURIsFromCKANDataset(dataset);
            Dataset d = new Dataset();
            d.title = dataset;
            //     d.uris=uris;
            Observatory.observation.datasets.add(d);
            this.refreshDatasets();
        }
    }
}//GEN-LAST:event_btnLoadVocabsActionPerformed

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_aboutMenuItemActionPerformed

    /**
     * Shows the vocabulary in the main window
     */
    public void showVocabulary(Vocab v) {
        edConsola.setText("");
        flashStatus("Downloading " + v.uri);
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String modelo = Modelo.loadXMLRDF("cachevocab", v.uri);
        if (!modelo.isEmpty()) {
            boolean ok = false;
            try {
                ok = Modelo.parseFromString(modelo, false);
            } catch (Exception e) {
                Logger.getLogger("licenser").info("Model could not be parsed");
                ok = false;
            }
            edConsola.setText(modelo);
        }
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

private void btnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailsActionPerformed
    int pestana = tabContainerx.getSelectedIndex();
    if (pestana == 0) {
        Vocab v = (Vocab) listVocabs.getSelectedValue();
        if (v == null) {
            return;
        }
        showVocabulary(v);

    } else if (pestana == 1) {
        showVoid();
    }


}//GEN-LAST:event_btnViewDetailsActionPerformed

    public static void main(String[] args) {
        Vocab v = new Vocab("test", "http://purl.org/vocab/aiiso/schema");
        Modelo.model = ModelFactory.createDefaultModel();
        Modelo.model.read(v.uri);
        LicenseFinder lf = new LicenseFinder();
        String findLicenseInOntology = lf.findLicenseInOntology();


    }

    public String showLicensesInVocab(Vocab v) {
        boolean ok = ObservatoryCommands.loadVocab(v);
        if (ok) {
            flashStatus("Vocabulary has been correctly retrieved");
        }
        LicenseFinder lf = new LicenseFinder();
        List<String> ls = lf.findLicenseStatementsForResource(Modelo.getOntologyUri());
        String superls = "";
        for (String s : ls) {
            superls += s + "\n";
        }

        ls = lf.findLicenseStatementsForResource(v.uri + "/");
        for (String s : ls) {
            superls += s + "\n";
        }

        edConsola.setText(superls);
        return superls;
    }

    private boolean showVoid() {
        boolean ok = false;
        Dataset d = (Dataset) listDatasets.getSelectedValue();
        if (d == null) {
            return false;
        }
        edConsola.setText("");
        flashStatus("Downloading void description of " + d.title);
        List<String> ls = CKANExplorer.getVoidURIsFromCKANDataset(d.title);
        if (ls.isEmpty()) {
            return false;
        }
        String modelo = Modelo.loadXMLRDF("cachevoid", ls.get(0));
        if (!modelo.isEmpty()) {
            try {
                ok = Modelo.parseFromString(modelo, true);
            } catch (Exception e) {
                Logger.getLogger("licenser").info("Model could not be parsed");
                ok = false;
            }
            edConsola.setText(modelo);
        }
        if (ok && (d.uri == null || d.uri.isEmpty())) {
            d.uri = Modelo.getDatasetUri();
        }


        return ok;
    }

private void btnSearchLicenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchLicenseActionPerformed
    if (tabContainerx.getSelectedIndex() == 0) {
        Vocab v = (Vocab) listVocabs.getSelectedValue();
        if (v == null) {
            return;
        }
        showLicensesInVocab(v);
    }
    if (tabContainerx.getSelectedIndex() == 1) {
        Dataset d = (Dataset) listDatasets.getSelectedValue();
        if (d == null) {
            return;
        }
        List<String> ls = CKANExplorer.getVoidURIsFromCKANDataset(d.title);
        if (ls.isEmpty()) {
            d.alive = false;
        } else {
            d.alive = true;
            d.voiduri = ls.get(0);
        }

        boolean ok = ObservatoryCommands.loadVoid(d);
        if (!ok) {
            return;
        }
        d.uri = Modelo.getDatasetUri();
        showLicensesInVoidDataset(d);

    }






}//GEN-LAST:event_btnSearchLicenseActionPerformed

private void menuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLoadActionPerformed

    final JFileChooser fc = new JFileChooser("./observations");
    int returnVal = fc.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = fc.getSelectedFile();
        Observation.loadFromFile(f.getAbsolutePath());
    }
    this.refreshDatasets();
    this.refreshVocabs();
    /*
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    final JFileChooser fc = new JFileChooser("./observations");
    int returnVal = fc.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
    File f = fc.getSelectedFile();
    Observation.loadListFromFile(f.getAbsolutePath());
    Logger.getLogger("licenser").info("Loaded from file " + f.getAbsolutePath());
    }
    this.updateStatus("Data loaded", true);
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     */
}//GEN-LAST:event_menuLoadActionPerformed
    private void makeReportForVocabs(String newcustom) {
        List<Vocab> vocabs = listVocabs.getSelectedValuesList();
        if (vocabs.size() == 0) {
            return;
        }
        edConsola.setText("");
        String s = "";
        String modelo = "";
        for (Vocab v : vocabs) {
            Logger.getLogger("licenser").info("Analyzing " + v.uri);
//        flashStatus("Analyzing " + v.uri);
            flashStatus("Analyzing " + v.uri);
            boolean ok = ObservatoryCommands.isAlive(v);
            v.alive = ok;
//        if (!ok) continue;
            String uri = Modelo.getOntologyUri();
            if (uri == null || uri.isEmpty()) {
                uri = v.uri;
            }
            String objeto = Modelo.findFirstObjectForSubjectAndPredicate(uri, newcustom);

            if (objeto.isEmpty()) {
                objeto = Modelo.findFirstObjectForSubjectAndPredicate(uri + "/", newcustom);
            }

            String newstring = v.vocab + "\t" + v.uri + "\t" + ok + "\t" + objeto + "\n";
            s += newstring;
//--        System.out.print(newstring);
            edConsola.setText(s);
            edConsola.updateUI();
        }
    }

    private void makeReportForDatasets(String newcustom) {
        List<Dataset> vocabs = listDatasets.getSelectedValuesList();
        if (vocabs.size() == 0) {
            return;
        }
        String s = "";
        for (Dataset d : vocabs) {
            String filename = "void/" + d.title + ".rdf";
            boolean ok = Modelo.parserWithoutThread(filename);
            d.alive = ok;
            Logger.getLogger("licenser").info("Vocabulary " + d.title + " has void " + ok);
            String uri = Modelo.getDatasetUri();
            String objeto = Modelo.findFirstObjectForSubjectAndPredicate(uri, newcustom);
            String newstring = d.title + "\t" + uri + "\t" + ok + "\t" + objeto + "\n";
            s += newstring;
//--        System.out.print(newstring);
            edConsola.setText(s);
            edConsola.updateUI();
        }
    }

private void btnReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteActionPerformed
    List<String> ls = Licenser.getRightsPredicates();
    String[] choices = new String[ls.size()];
    for (int i = 0; i < ls.size(); i++) {
        choices[i] = ls.get(i);
    }
    String newcustom = (String) JOptionPane.showInputDialog(null, "Choose a predicate to look for", "Predicate", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]); // Initial choice
    if (newcustom == null) {
        return;
    }
    if (tabContainerx.getSelectedIndex() == 0) {
        makeReportForVocabs(newcustom);
    } else if (tabContainerx.getSelectedIndex() == 1) {
        makeReportForDatasets(newcustom);
    } else {
        flashStatus("Action not defined here");
    }
    // TODO add your handling code here:
}//GEN-LAST:event_btnReporteActionPerformed

private void btnTestHealthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestHealthActionPerformed
    if (tabContainerx.getSelectedIndex() == 0) {
        List<Vocab> vocabs = listVocabs.getSelectedValuesList();
        ObservatoryCommands.testAliveVocabs(vocabs);
        listVocabs.repaint();
        listVocabs.updateUI();
    } else if (tabContainerx.getSelectedIndex() == 1) {
        List<Dataset> ld = listDatasets.getSelectedValuesList();
        int conta = 0;
        for (Dataset d : ld) {
            Dataset ds = CKANExplorer.getDatasetFromCKAN(d.title);
            if (ds == null) {
                continue;
            }
            Observatory.observation.updateDataset(ds);
//            CKANExplorer.updateDataFromCKAN(d);
            progressBar.setValue(++conta * 100 / ld.size());
        }
        progressBar.setValue(0);
        refreshDatasets();
    }
    // TODO add your handling code here:
}//GEN-LAST:event_btnTestHealthActionPerformed

private void menuJokerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuJokerActionPerformed
    List<Dataset> lds = new ArrayList();
    List<String> ls = CKANExplorer.getDatasetNames();
    for (String s : ls) {
        Dataset d = new Dataset();
        d.uri = "http://datahub.io/dataset/" + s;
        d.title = s;
        lds.add(d);
    }
    Observatory.observation.datasets = lds;
    refreshDatasets();



    /*
    if (tabContainerx.getSelectedIndex() == 0) {
    }
    else if (tabContainerx.getSelectedIndex() == 1)
    {
    List<Dataset> ld = listDatasets.getSelectedValuesList();      
    int conta=0;
    for (Dataset d : ld)
    {
    Model m=Dataset.Dataset2DCAT(d);
    String s=SemanticWeb.toString(m);
    System.out.println(s);
    }
    }*/
}//GEN-LAST:event_menuJokerActionPerformed

private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
    final JFileChooser fc = new JFileChooser("./observations");
    File f = null;
    String path = "";
    int returnVal = fc.showSaveDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
            f = fc.getSelectedFile();
            if (f != null) {
                path = f.getAbsolutePath();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } else {
        return;
    }
    if (f == null) {
        return;
    }
    String extension = "";
    int i = path.lastIndexOf('.');
    if (i > 0) {
        extension = path.substring(i + 1);
    }

    if (extension.equals("ttl")) {
        Observation.saveToFileAsRDF(path);
    } else {
        Observation.saveToFile(path);// TODO add your handling code here:
    }
}//GEN-LAST:event_menuSaveActionPerformed

    /**
     * Carga un archivo
     */
private void menuLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLoadFileActionPerformed
    final JFileChooser fc = new JFileChooser("./observations");
    int returnVal = fc.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = fc.getSelectedFile();
        String path = f.getAbsolutePath();
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }

        if (extension.equals("ttl")) {
            Observation.loadFromFileRDF(f.getAbsolutePath());
        } else {
            Observation.loadFromFile(f.getAbsolutePath());
        }
    }
    this.refreshDatasets(); 
    this.refreshVocabs();// TODO add your handling code here:
}//GEN-LAST:event_menuLoadFileActionPerformed

private void menuIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuIDActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_menuIDActionPerformed

private void menuFileOpenDumpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileOpenDumpActionPerformed
    final JFileChooser fc = new JFileChooser("./data");
    int returnVal = fc.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = fc.getSelectedFile();
        String path = f.getAbsolutePath();
        String name = f.getName();
        Dataset ds = new Dataset();
        ds.uri = path;
        ds.title = name;
        ds.rdfdump = path;
        Observatory.observation.datasets.add(ds);
        refreshDatasets();
    }
// TODO add your handling code here:
}//GEN-LAST:event_menuFileOpenDumpActionPerformed

private void menuRDFDumpCountTriplesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRDFDumpCountTriplesActionPerformed
    Dataset ds = (Dataset) listDatasets.getSelectedValue();
    if (ds == null || ds.rdfdump == null || ds.rdfdump.isEmpty()) {
        return;
    }
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    RDFDump dump = new RDFDump(ds.rdfdump);
    dump.setReportador(ObservatoryApp.getReportador());
    int ntriples = dump.countTriples();
    ObservatoryApp.getApplication().getReportador().promptMessage("RDFDump de " + ntriples +" triples", null);
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));


}//GEN-LAST:event_menuRDFDumpCountTriplesActionPerformed

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu2ActionPerformed

    
    
    
    
    private void menuRDFDumpFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRDFDumpFilterActionPerformed
    Dataset ds = (Dataset) listDatasets.getSelectedValue();
    if (ds == null || ds.rdfdump == null || ds.rdfdump.isEmpty()) {
        return;
    }
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    RDFDump dump = new RDFDump(ds.rdfdump);
        dump.setReportador(ObservatoryApp.getReportador());

    List<String> predicates=Licenser.getRightsPredicates();
    
    dump.filterByPredicates(predicates, "./local/output.nt");
    
//    ObservatoryApp.getApplication().getReportador().promptMessage("RDFDump de " + ntriples +" triples", null);
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    }//GEN-LAST:event_menuRDFDumpFilterActionPerformed

    private void menuRDFDumpExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRDFDumpExportCSVActionPerformed
    Dataset ds = (Dataset) listDatasets.getSelectedValue();
    if (ds == null || ds.rdfdump == null || ds.rdfdump.isEmpty()) {
        return;
    }
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    final JFileChooser fc = new JFileChooser("./observations");
    String path = "";
    int returnVal = fc.showSaveDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
            RDFDump dump = new RDFDump(ds.rdfdump);
                dump.setReportador(ObservatoryApp.getReportador());

            File f = fc.getSelectedFile();
            dump.exportToCSV(f.getAbsolutePath());
            ObservatoryApp.getReportador().promptMessage("Archivo guardado correctamentew", "info");
        }catch(Exception e)
        {
            ObservatoryApp.getReportador().promptMessage("No se pudo exportar", "error");
        }
    }
        
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_menuRDFDumpExportCSVActionPerformed

    
private void menuJoker2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuJoker2ActionPerformed
    final Dataset ds = (Dataset) listDatasets.getSelectedValue();
    if (ds == null || ds.rdfdump == null || ds.rdfdump.isEmpty()) {
        return;
    }
    mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        EjecutadorOperacionLenta task = new EjecutadorOperacionLenta(new OperacionLenta() {
            public Object metodolento() {
                updateStatus("Joker2", false);
                try {
                    RDFDump dump = new RDFDump(ds.rdfdump);
                    dump.setReportador(ObservatoryApp.getReportador());
                    List<String> namespaces=GeneralPolicy.getPolicyNamespaces();
                    dump.filterByNamespace(namespaces, "./local/output.nt");
                } catch (Exception ex) {
                }
                return (Object) null;
            }
            public void done() {
                mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));                
                updateStatus("listo", false);
            }
        });
        task.execute();
        
        
    
    


}//GEN-LAST:event_menuJoker2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoadVocabs;
    private javax.swing.JButton btnReporte;
    private javax.swing.JButton btnSearchLicense;
    private javax.swing.JButton btnTestHealth;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JEditorPane edConsola;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList listDatasets;
    private javax.swing.JList listVocabs;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuFileOpenDump;
    private javax.swing.JMenu menuID;
    private javax.swing.JMenuItem menuJoker;
    private javax.swing.JMenuItem menuJoker2;
    private javax.swing.JMenuItem menuLoadFile;
    private javax.swing.JMenuItem menuRDFDumpCountTriples;
    private javax.swing.JMenuItem menuRDFDumpExportCSV;
    private javax.swing.JMenuItem menuRDFDumpFilter;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTabbedPane tabContainerx;
    private javax.swing.JPanel tabVocabs;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    @Override
    public void promptMessage(String text, String tipo) {
        if (tipo == null) {
            tipo = "info";
        }
        if (tipo.equals("warn")) {
            JOptionPane.showMessageDialog(null, text, "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if (tipo.equals("error")) {
            JOptionPane.showMessageDialog(null, text, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, text, "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Cambia el icono de la aplicación mientras este se está ejecutando
     * (Pero no en el explorador de Windows)
     */
    private void setAppIcon() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame mainFrame = ObservatoryApp.getApplication().getMainFrame();
                JLabel label = new JLabel();
                MediaTracker media = new MediaTracker(label);
                Image imagen = Toolkit.getDefaultToolkit().getImage("src/observatory/resources/about32.png");
                media.addImage(imagen, 23);
                try {
                    media.waitForID(23);
                } catch (InterruptedException e) {
                }
                mainFrame.setIconImage(imagen);
            }
        });
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }

    public void flashStatus(final String text) {
        EjecutadorOperacionLenta task = new EjecutadorOperacionLenta(new OperacionLenta() {

            public Object metodolento() {
                updateStatus(text, false);
                try {
                    Logger.getLogger("licenser").info("FLASH: " + text);
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                }
                return (Object) null;
            }

            public void done() {
                updateStatus("listo", false);
            }
        });
        task.execute();
    }

    public void updateStatus(String s, boolean refresh) {
        status(s, 0);
    }

    /**
     * Cambia el mensaje de texto que aparece en la barra de status
     */
    public void status(String text, int progreso) {
        progressBar.setValue(progreso);
        statusMessageLabel.setText(text);
        statusMessageLabel.paintImmediately(statusMessageLabel.getVisibleRect());
    }

    private void showLicensesInVoidDataset(Dataset d) {

        LicenseFinder lf = new LicenseFinder();
        List<String> ls = lf.findLicenseStatementsForResource(d.uri);
        String superls = "";
        for (String s : ls) {
            superls += s + "\n";
        }

        ls = lf.findLicenseStatementsForResource(d.uri + "/");
        for (String s : ls) {
            superls += s + "\n";
        }

        edConsola.setText(superls);
    }
}

/**
 * Clase para tratar los eventos de ratón en la lista. 
 * Solo trata el doble click
 */
class ActionJList extends MouseAdapter implements KeyListener {

    protected JList list;

    public ActionJList(JList l) {
        list = l;
    }

    public void mouseRelased(MouseEvent e) {
        check(e);
    }

    public void mouseClicked(MouseEvent e) {
        check(e);
    }

    /**
     * Muestra un menú de popup
     */
    private void showPopupMenu(MouseEvent e) {
        final ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        list.setSelectedIndex(list.locationToIndex(e.getPoint())); //select the item
        int index = list.locationToIndex(e.getPoint());
        ListModel dlm = list.getModel();
        if (dlm == null || index == -1) {
            return;
        }
        final Object o = dlm.getElementAt(index);

        if (o.getClass() == Vocab.class) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("View vocabulary");
            item.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Vocab v = (Vocab) o;
                    view.showVocabulary(v);
                }
            });
            menu.add(item);
            JMenuItem item2 = new JMenuItem("Check availability");
            item2.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Vocab v = (Vocab) o;
                    v.alive = ObservatoryCommands.isAlive(v);
                    view.flashStatus(v.vocab + (v.alive == true ? " is alive" : " is not alive"));
                    view.refreshVocabs();
                }
            });
            menu.add(item2);

            JMenuItem item3 = new JMenuItem("Scan for rights information");
            item3.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Vocab v = (Vocab) o;
                    String str = view.showLicensesInVocab(v);
                    if (str.isEmpty()) {
                        view.flashStatus("No rights information has been found");
                    }
                }
            });
            menu.add(item3);


            menu.show(list, e.getX(), e.getY()); //and show the menu
        }
    }

    private void check(MouseEvent e) {
        final ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();

        if (e.getButton() == 3) { //if the event shows the menu
            showPopupMenu(e);
        }

        if (e.getClickCount() == 2) {
            int index = list.locationToIndex(e.getPoint());
            ListModel dlm = list.getModel();
            Object o = dlm.getElementAt(index);
            list.ensureIndexIsVisible(index);
            String sclase = o.getClass().toString();

            if (sclase.equals("class vroddon.sw.Vocab")) {
                Vocab vocab = (Vocab) dlm.getElementAt(index);
                DialogVocabulary dialog = new DialogVocabulary(view.getFrame(), true);
                dialog.setLocationRelativeTo(null);
                dialog.setVocab(vocab);
                dialog.setTitle("Linked Data Resource");
                dialog.pack();
                dialog.setVisible(true);
                Logger.getLogger("licenser").info("Mostrando diálogo de vocab " + vocab.uri);
            } else if (sclase.equals("class vroddon.sw.Dataset")) {
                Dataset d = (Dataset) dlm.getElementAt(index);
                DialogDataset dialog = new DialogDataset(view.getFrame(), true);
                dialog.setLocationRelativeTo(null);
                dialog.setDataset(d);
                dialog.setTitle("Linked Data Resource");
                dialog.pack();
                dialog.setVisible(true);
                Logger.getLogger("licenser").info("Mostrando diálogo de vocab " + d.uri);
            }

            JFrame.setDefaultLookAndFeelDecorated(true);

        }
    }

    public void keyTyped(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyPressed(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
            List<Vocab> lv = (List<Vocab>) list.getSelectedValuesList();
            for (Vocab v : lv) {
                Observatory.observation.vocabs.remove(v);
            }
            ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
            view.refreshVocabs();
            ke.consume();
        }
    }
}
