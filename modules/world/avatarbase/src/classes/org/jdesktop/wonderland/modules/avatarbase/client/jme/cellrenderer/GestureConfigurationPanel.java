/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarClientPlugin;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.Gesture;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 *
 * gesture config panel
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class GestureConfigurationPanel extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(GestureConfigurationPanel.class.getName());

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle");
    //private GestureHUD connectedGestureHUD;
    List<Gesture> gestureList = new ArrayList<Gesture>();
    private boolean dirty = false;
    private Map<String, Boolean> currGestureMap = null;
    private Map<String, Boolean> origGestureMap = null;

    public GestureConfigurationPanel(Map<String, Boolean> gestureMap) {
        ViewCell viewCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        AvatarImiJME avatar = (AvatarImiJME) viewCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        if (!avatar.getAvatarCharacter().getCharacterParams().isAnimateBody()) {
            //show message
            setLayout(new BorderLayout());
            JTextArea jta = new JTextArea(3, 30);
            jta.setEditable(false);
            jta.setLineWrap(true);
            jta.setWrapStyleWord(true);
            jta.setFont(new Font(null, Font.PLAIN, 16));
            jta.setText(BUNDLE.getString("Gestures_Not_Supported"));
            JScrollPane jsp = new JScrollPane(jta);
            add(jsp);
        } else {
            initComponents();
            this.origGestureMap = new HashMap<String, Boolean>(gestureMap);
            this.currGestureMap = new HashMap<String, Boolean>(gestureMap);
            initials();
            populatePanel();
        }

    }

    private void initials() {
        //head
        gestureList.add(new Gesture("Yes", BUNDLE.getString("Yes"), Gesture.GestureType.HEAD, true, Gesture.GesturePosition.ANY, false, false, true, false));
        gestureList.add(new Gesture("No", BUNDLE.getString("No"), Gesture.GestureType.HEAD, true, Gesture.GesturePosition.ANY, false, false, true, false));
        gestureList.add(new Gesture("Wink", BUNDLE.getString("Wink"), Gesture.GestureType.HEAD, true, Gesture.GesturePosition.ANY, false, false, true, true));
        //torso
        gestureList.add(new Gesture("AnswerCell", BUNDLE.getString("AnswerCell"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, false, true, true));
        gestureList.add(new Gesture("Bow", BUNDLE.getString("Bow"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.STANDING, false, false, true, false));
        gestureList.add(new Gesture("Breath", BUNDLE.getString("Breath"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, true, true, false));
        gestureList.add(new Gesture("Cheer", BUNDLE.getString("Cheer"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, false, true, true));
        gestureList.add(new Gesture("Clap", BUNDLE.getString("Clap"), Gesture.GestureType.TORSO, true, Gesture.GesturePosition.ANY, true, false, true, false));
        gestureList.add(new Gesture("CrossHands", BUNDLE.getString("CrossHands"), Gesture.GestureType.TORSO, true, Gesture.GesturePosition.SITTING, false, false, false, true));
        gestureList.add(new Gesture("Crying", BUNDLE.getString("Crying"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, true, true, true));
        gestureList.add(new Gesture("FoldArms", BUNDLE.getString("FoldArms"), Gesture.GestureType.TORSO, true, Gesture.GesturePosition.ANY, true, false, false, true));
        gestureList.add(new Gesture("Follow", BUNDLE.getString("Follow"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.STANDING, false, false, true, false));
        gestureList.add(new Gesture("Gesticulate", BUNDLE.getString("Gesticulate"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, true, true, false));
        gestureList.add(new Gesture("Hunch", BUNDLE.getString("Hunch"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, false, true, true));
        gestureList.add(new Gesture("Laugh", BUNDLE.getString("Laugh"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, false, true, true));
        gestureList.add(new Gesture("PublicSpeaking", BUNDLE.getString("PublicSpeaking"), Gesture.GestureType.HEADTORSO, true, Gesture.GesturePosition.ANY, true, false, true, false));
        gestureList.add(new Gesture("RaiseHand", BUNDLE.getString("RaiseHand"), Gesture.GestureType.TORSO, true, Gesture.GesturePosition.ANY, true, false, false, true));
        gestureList.add(new Gesture("Wave", BUNDLE.getString("Wave"), Gesture.GestureType.TORSO, true, Gesture.GesturePosition.ANY, true, false, true, true));
        //leg
        gestureList.add(new Gesture("CrossAnkles", BUNDLE.getString("CrossAnkles"), Gesture.GestureType.LEG, true, Gesture.GesturePosition.SITTING, false, false, false, true));
        gestureList.add(new Gesture("CrossLegs", BUNDLE.getString("CrossLegs"), Gesture.GestureType.LEG, true, Gesture.GesturePosition.SITTING, false, false, false, true));
    }

    private void populatePanel() {
        gesturesScrollPanel.setHorizontalScrollBar(null);

        ((GridLayout) gestureLabelPanel.getLayout()).setRows(gestureList.size());
        ((GridLayout) gestureCheckboxPanel.getLayout()).setRows(gestureList.size());

        for (Gesture gesture : gestureList) {
            JLabel gestureLabel = new JLabel();
            gestureLabel.setText(gesture.getLabelName());

            JCheckBox gesturecheckBox = new JCheckBox();
            gesturecheckBox.addItemListener(new GestureCheckBoxItemChangeListener());
            gesturecheckBox.setName(gesture.getAnimName());
            gesturecheckBox.setHorizontalAlignment(SwingConstants.CENTER);

            if (currGestureMap != null && currGestureMap.get(gesture.getAnimName()) != null) {
                gesturecheckBox.setSelected(!currGestureMap.get(gesture.getAnimName()));
            }
            gestureLabelPanel.add(gestureLabel);
            gestureCheckboxPanel.add(gesturecheckBox);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            JDialog dialog = new JDialog(JmeClientMain.getFrame().getFrame());
            dialog.add(this);
            logger.log(Level.INFO, "dialog.getInsets(); : {0}", dialog.getInsets());//dialog.getInsets();
            dialog.setSize(getSize());
            dialog.pack();
            dialog.setTitle("Configure Gestures");
            dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);

            dialog.setResizable(false);
            dialog.setVisible(visible);

        } else {
            Window ancestor = SwingUtilities.getWindowAncestor(this);
            if (ancestor != null) {
                ancestor.dispose();
            }
        }
    }

    private class GestureCheckBoxItemChangeListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (currGestureMap == null) {
                currGestureMap = new HashMap<String, Boolean>();
            }
            if (cb.isSelected()) {
                currGestureMap.put(cb.getName(), !cb.isSelected());
            } else {
                currGestureMap.remove(cb.getName());
            }
            checkDirty();
        }
    }

    public void checkDirty() {
        if (origGestureMap == null && currGestureMap != null) {
            if (!currGestureMap.isEmpty()) {
                dirty = true;
            }
        } else if (origGestureMap == null && currGestureMap == null) {
            dirty = false;
        } else if (origGestureMap.size() != currGestureMap.size()) {
            dirty = true;
        } else {
            for (Map.Entry<String, Boolean> entry : currGestureMap.entrySet()) {
                Boolean origValue = origGestureMap.get(entry.getKey());
                if (origValue == null || !origValue.equals(entry.getValue())) {
                    dirty = true;
                    break;
                }
            }
        }

        if (dirty) {
            dirty = false;
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }

    private static ContentCollection getSystemContentRepository()
            throws ContentRepositoryException {
        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        ContentRepository cr = registry.getRepository(LoginManager.getPrimary());
        return cr.getSystemRoot();
    }

    public static ContentCollection getGroupUsersRepo() {
        try {
            //if(grpusrRepo == null) {
            ContentCollection collection = getSystemContentRepository();
            ContentCollection grps = (ContentCollection) collection.getParent().getChild("users");
            ContentCollection grpusrs = (ContentCollection) grps.getChild(LoginManager.getPrimary().getUsername());
            return grpusrs;
        } catch (ContentRepositoryException ex) {
            logger.log(Level.INFO, "ex : {0}", ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static GestureConfiguration getGestureConfigurationInfo(ContentCollection grpusrs) {
        GestureConfiguration out = null;
        try {
            // Find the gestureConfiguration.xml file, creating it if necessary.
            ContentCollection csColl = (ContentCollection) grpusrs.getChild("Gesture_Configuration");
            if (csColl == null) {
                return null;
            }
            ArrayList<ContentNode> resources = (ArrayList<ContentNode>) csColl.getChildren();
            ContentResource resource = null;
            if (resources != null && !resources.isEmpty()) {
                resource = (ContentResource) csColl.getChildren().get(0);
            }
            if (resource == null) {
                return null;
            }

            // Write the new list to the resource
            Reader r = new InputStreamReader(resource.getURL().openStream());
            out = GestureConfiguration.decode(r);
        } catch (ContentRepositoryException ex) {
            logger.log(Level.INFO, "ex : {0}", ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            logger.log(Level.INFO, "ex : {0}", ex);
            ex.printStackTrace();
        } catch (JAXBException ex) {
            logger.log(Level.INFO, "ex : {0}", ex);
            ex.printStackTrace();
        }
        return out;
    }

    public static void saveGestureConfiguration(GestureConfiguration gestureConfiguration) {
        try {
            // Find the ConfigQuestionDialog.xml file, creating it if necessary.
            ContentCollection collection = getSystemContentRepository();
            ContentCollection grps = (ContentCollection) collection.getParent().getChild("users");
            if (grps == null) {
                grps = (ContentCollection) collection.getParent().createChild("users", org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type.COLLECTION);
            }
            String user = LoginManager.getPrimary().getUsername();
            ContentCollection grpusrs = (ContentCollection) grps.getChild(user);
            if (grpusrs == null) {
                grpusrs = (ContentCollection) grps.createChild(user, org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type.COLLECTION);
            }
            ContentCollection csColl = (ContentCollection) grpusrs.getChild("Gesture_Configuration");
            if (csColl == null) {
                csColl = (ContentCollection) grpusrs.createChild("Gesture_Configuration", org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type.COLLECTION);
            }

            ArrayList<ContentNode> resources = (ArrayList<ContentNode>) csColl.getChildren();
            if (resources != null && !resources.isEmpty()) {
                csColl.removeChild(resources.get(0).getName());
            }
            ContentResource resource = (ContentResource) csColl.createChild("gestureConfiguration.xml", org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type.RESOURCE);

            // Write the new list to the resource
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(os);
            gestureConfiguration.encode(w);
            resource.put(os.toByteArray());
        } catch (ContentRepositoryException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
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

        gesturesScrollPanel = new javax.swing.JScrollPane();
        gesturesPanel = new javax.swing.JPanel();
        gestureLabelPanel = new javax.swing.JPanel();
        gestureCheckboxPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        gesturesScrollPanel.setBorder(null);

        gestureLabelPanel.setLayout(new java.awt.GridLayout(1, 1, 0, 3));

        gestureCheckboxPanel.setLayout(new java.awt.GridLayout(1, 1, 0, 3));

        javax.swing.GroupLayout gesturesPanelLayout = new javax.swing.GroupLayout(gesturesPanel);
        gesturesPanel.setLayout(gesturesPanelLayout);
        gesturesPanelLayout.setHorizontalGroup(
            gesturesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gesturesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gestureLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(gestureCheckboxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        gesturesPanelLayout.setVerticalGroup(
            gesturesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gesturesPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(gesturesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gestureLabelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gestureCheckboxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
                .addContainerGap())
        );

        gesturesScrollPanel.setViewportView(gesturesPanel);

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Hide");

        jLabel9.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Animations");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        okButton.setText(" Save ");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Hidden gestures will not appear");
        jLabel5.setPreferredSize(null);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("on gesture \"Short List\"");
        jLabel8.setPreferredSize(null);

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Configure Gestures");
        jLabel2.setPreferredSize(null);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(gesturesScrollPanel)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gesturesScrollPanel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        GestureConfiguration configuration = new GestureConfiguration();
        configuration.setGesturesMap(currGestureMap);
        saveGestureConfiguration(configuration);

        AvatarClientPlugin.updateGestureMap(currGestureMap);
        origGestureMap = currGestureMap;
        checkDirty();
        AvatarClientPlugin.getGestureHUD().refresh();
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        int response = JOptionPane.YES_OPTION;
        if (okButton.isEnabled()) {
            response = JOptionPane.showConfirmDialog(this, "Close without saving?", "Are you sure?", JOptionPane.YES_NO_OPTION);
        }
        if (response == JOptionPane.YES_OPTION) {
            this.setVisible(false);
        }
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel gestureCheckboxPanel;
    private javax.swing.JPanel gestureLabelPanel;
    private javax.swing.JPanel gesturesPanel;
    private javax.swing.JScrollPane gesturesScrollPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
