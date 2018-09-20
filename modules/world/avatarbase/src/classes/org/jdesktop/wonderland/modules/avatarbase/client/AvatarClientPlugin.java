/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */

/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.avatarbase.client;

import com.jme.math.Vector3f;
import imi.camera.CameraModels;
import imi.camera.ChaseCamModel;
import imi.camera.ChaseCamState;
import imi.character.AvatarSystem;
import imi.character.avatar.Avatar;
import imi.character.avatar.AvatarContext;
import imi.character.behavior.CharacterBehaviorManager;
import imi.character.behavior.Task;
import imi.character.statemachine.GameContext;
import imi.character.statemachine.GameState;
import imi.character.statemachine.corestates.CycleActionState;
import imi.character.statemachine.corestates.SitState;
import imi.repository.Repository;
import imi.scene.PJoint;
import imi.scene.animation.AnimationListener;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.instrument.Instrumentation;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.CellStatusChangeListener;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuInvocationSettings;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuManager;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.CameraController;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.FirstPersonCameraProcessor;
import org.jdesktop.wonderland.client.jme.FrontHackPersonCameraProcessor;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.client.jme.MainFrameImpl;
import org.jdesktop.wonderland.client.jme.ThirdPersonCameraProcessor;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.ViewManager.ViewManagerListener;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionLifecycleListener;
import org.jdesktop.wonderland.client.scenemanager.SceneManager;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarSessionLoader.AvatarLoaderStateListener;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarSessionLoader.State;
import org.jdesktop.wonderland.modules.avatarbase.client.cell.AvatarConfigComponent;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.AvatarInteractionListener;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.AvatarInteractionListenerRegistrar;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.TouchArm;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.TouchShoulder;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.TurnHeadAdvanced;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.FlexibleCameraAdapter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarCollisionChangeRequestEvent;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarControls;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME.AvatarChangedListener;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarTestPanel;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.GestureConfiguration;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.GestureConfigurationPanel;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.GestureHUD;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.ShakeHands;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarContext;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WonderlandAvatarCache;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry.AvatarInUseListener;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;
import org.jdesktop.wonderland.modules.avatarbase.client.ui.AvatarConfigFrame;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.GestureMessage;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A client-side plugin to initialize the avatar system
 *
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@Plugin
public class AvatarClientPlugin extends BaseClientPlugin
        implements AvatarLoaderStateListener, ViewManagerListener, SessionLifecycleListener, SessionStatusListener {

    private static Logger logger = Logger.getLogger(AvatarClientPlugin.class.getName());
    private static final ResourceBundle bundle
            = ResourceBundle.getBundle("org/jdesktop/wonderland/modules/"
                    + "avatarbase/client/resources/Bundle");

    // A map of a session and the loader for that session
    private Map<ServerSessionManager, AvatarSessionLoader> loaderMap = null;

    // Listener for when a new avatar becomes in-use
    private AvatarInUseListener inUseListener = null;

    // Listen for when the avatar character changes to update the state of the
    // chase camera.
    private AvatarChangedListener avatarChangedListener = null;

    // The current avatar cell renderer
    private AvatarImiJME avatarCellRenderer = null;

    // Chase camera state and model and menu item
    private ChaseCamState camState = null;
    private ChaseCamModel camModel = null;
    private static JRadioButtonMenuItem chaseCameraMI = null;
    private static JMenuItem resetCamera = null;

    // Some test control panels for the avatar
    private WeakReference<AvatarTestPanel> testPanelRef = null;
    private JMenuItem avatarControlsMI = null;
    private JMenuItem avatarSettingsMI = null;
    private Instrumentation instrumentation = null;

    // The gesture HUD panel and menu item
    private static WeakReference<GestureHUD> gestureHUDRef = null;
    private JCheckBoxMenuItem gestureMI = null;

    // True if the menus have been added to the main menu, false if not
    private boolean menusAdded = false;

    // Menu items for the collision & gravity check boxes
    private JCheckBoxMenuItem collisionResponseEnabledMI = null;
    private JCheckBoxMenuItem gravityEnabledMI = null;
    private JMenuItem gestureConfigMI = null;

    // The avatar configuration menu item
    private JMenuItem avatarConfigMI = null;

    // Indicates that the avatar has already been set once the primary view
    // has been set, so that is does not happen more than once. We synchronized
    // on the mutex
    private Lock isAvatarSetMutex = new ReentrantLock();
    private Boolean isAvatarSet = false;

    // Context menu listener
    private ContextMenuListener ctxListener;

    private static GestureConnection gestureConn = null;
    private static Map gestureMap = null;
    private static GesturesKeyListener gesturesKeyListener = null;
    private static GesturesMouseListener gesturesMouseListener = null;
    private static MyCellChangeListener cellChangeListener = null;
    private static HashMap<String, Cell> cellMap = null;
    private static MainFrameFocusListener focusListener = null;
    private static LinkedList<LookAtListener> lookAtlisteners;
    private static AvatarConfigFrame avatarConfigFrame = null;

    /**
     * {@inheritDoc]
     */
    @Override
    public void initialize(ServerSessionManager manager) {
        loaderMap = new HashMap();

        // A listener for changes to the primary view cell renderer. (This
        // rarely happens in practice). When the avatar cell renderer changes,
        // reset the chase camera state.
        avatarChangedListener = new AvatarChangedListener() {
            public void avatarChanged(Avatar newAvatar) {
                if (camState != null) {
                    // stop listener for changes from the old avatar cell
                    // renderer.
                    avatarCellRenderer.removeAvatarChangedListener(avatarChangedListener);

                    if (newAvatar.getContext() != null) {
                        camState.setTargetCharacter(newAvatar);
                    }
                    else {
                        camState.setTargetCharacter(null);
                    }

                    // Fetch the initial position of the camera. This is based
                    // upon the current avatar position. We can assume that the
                    // primary View Cell exists at this point, since the menu
                    // item is not added until a primary View Cell exists.
                    ViewManager viewManager = ViewManager.getViewManager();
                    ViewCell viewCell = viewManager.getPrimaryViewCell();
                    CellTransform transform = viewCell.getWorldTransform();
                    Vector3f translation = transform.getTranslation(null);

                    // This is the offset from the avatar view Cell to place the
                    // camera
                    Vector3f offset = new Vector3f(0.0f, 4.0f, -10.0f);

                    // force an update
                    camState.setCameraPosition(translation.add(offset));
                }

                // OWL issue #125: Reinitialize the gesture HUD panel with the
                // current avatar character.
                if (gestureHUDRef != null && gestureHUDRef.get() != null) {
                    if (newAvatar instanceof WlAvatarCharacter) {
                        while (newAvatar.getContext() == null) {
                            newAvatar.setContext(avatarCellRenderer.initializeContext((WlAvatarCharacter) newAvatar));
                        }
                        gestureHUDRef.get().setAvatarCharacter((WlAvatarCharacter) newAvatar,
                                gestureMI.getState(), gestureConn);
                    } else {
                        gestureHUDRef.get().setVisible(false);
                    }
                }
            }
        };

        // A menu item for the chase camera
        chaseCameraMI = new JRadioButtonMenuItem(bundle.getString("Chase_Camera"));
        chaseCameraMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ViewManager viewManager = ViewManager.getViewManager();
                // get the Chase Camera with the model and state and add to
                // the View Manager
                viewManager.setCameraController(getChaseCamera());
            }
        });

        // Adding a reset cameara, to reset the  transforms of the 
        // selected camera to default
        resetCamera = new JMenuItem("Reset Camera");
        resetCamera.setAccelerator(KeyStroke.getKeyStroke('r'));
        resetCamera.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                CameraController cameraController = ClientContextJME.getViewManager().getCameraController();
                if (cameraController.getClass() == FirstPersonCameraProcessor.class) {
                    ClientContextJME.getViewManager().setCameraController(new FirstPersonCameraProcessor());
                    MainFrameImpl.getFirstPersonRB().setSelected(true);
                } else if (cameraController.getClass() == ThirdPersonCameraProcessor.class) {
                    ClientContextJME.getViewManager().setCameraController(new ThirdPersonCameraProcessor());
                    MainFrameImpl.getThirdPersonRB().setSelected(true);
                } else if (cameraController.getClass() == FrontHackPersonCameraProcessor.class) {
                    ClientContextJME.getViewManager().setCameraController(new FrontHackPersonCameraProcessor());
                    MainFrameImpl.getFrontPersonRB().setSelected(true);
                } else if (cameraController.getClass() == FlexibleCameraAdapter.class) {
                    ClientContextJME.getViewManager().setCameraController(getChaseCamera());
                    chaseCameraMI.setSelected(true);
                }
            }
        });

        // A menu item for a test control panel for the avatar.
//        avatarControlsMI = new JMenuItem(bundle.getString("Avatar_Controls"));
//        avatarControlsMI.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (testPanelRef == null || testPanelRef.get() == null) {
//                    AvatarTestPanel test = new AvatarTestPanel();
//                    JFrame f = new JFrame(bundle.getString("Avatar_Controls"));
//                    f.getContentPane().add(test);
//                    f.pack();
//                    f.setVisible(true);
//                    f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//                    test.setAvatarCharacter(avatarCellRenderer.getAvatarCharacter());
//                    testPanelRef = new WeakReference(test);
//                } else {
//                    SwingUtilities.getRoot(testPanelRef.get().getParent()).setVisible(true);
//                }
//            }
//        });
        // Avatar Instrumentation is a dev tool
//        avatarSettingsMI = new JMenuItem(bundle.getString("Avatar_Settings..."));
//        avatarSettingsMI.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                AvatarInstrumentation in = new AvatarInstrumentation(instrumentation);
//                in.setVisible(true);
//            }
//        });
//        instrumentation = new DefaultInstrumentation(ClientContextJME.getWorldManager());
        // The menu item for the Gesture (HUD)
        gestureMI = new JCheckBoxMenuItem(bundle.getString("Gesture_UI"));
        gestureMI.setSelected(false);
        gestureMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean visible = gestureMI.isSelected();
                //issue #174 hud visibility management
                if (visible) {
                    gestureHUDRef.get().setMaximized();
                }
                gestureHUDRef.get().setVisible(visible);
            }
        });

        // The menu item for the avatar configuration
        avatarConfigMI = new JMenuItem(bundle.getString("Avatar_Appearance..."));
        avatarConfigMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AvatarConfigFrame f = getAvatarConfigFrame();
                if (f != null) {
                    f.setVisible(true);
                }
            }
        });

        // Check box to set collision enabled
        collisionResponseEnabledMI = new JCheckBoxMenuItem(bundle.getString("Avatar_Collision_Response_Enabled"));
        collisionResponseEnabledMI.setSelected(true); // TODO should be set by server
        collisionResponseEnabledMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isCollisionResponse = collisionResponseEnabledMI.isSelected();
                boolean isGravity = gravityEnabledMI.isSelected();
                ClientContext.getInputManager().postEvent(
                        new AvatarCollisionChangeRequestEvent(isCollisionResponse, isGravity));
            }
        });

        // Check box to set gravity (floor following) enabled
        gravityEnabledMI = new JCheckBoxMenuItem(bundle.getString("Avatar_Gravity_Enabled"));
        gravityEnabledMI.setSelected(true); // TODO should be set by server
        gravityEnabledMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isCollisionResponse = collisionResponseEnabledMI.isSelected();
                boolean isGravity = gravityEnabledMI.isSelected();
                ClientContext.getInputManager().postEvent(
                        new AvatarCollisionChangeRequestEvent(isCollisionResponse, isGravity));
            }
        });

        // The menu item for the Gesture Configuration Panel
        gestureConfigMI = new JMenuItem(bundle.getString("GestureConfiguration_UI"));
        gestureConfigMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame mainFrame = new JFrame("Gesture Configuration Panel");
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                mainFrame.setLocation(dim.width / 2 - mainFrame.getSize().width / 2, dim.height / 2 - mainFrame.getSize().height / 2);
                GestureConfigurationPanel gestureConfigurationPanel = new GestureConfigurationPanel(gestureMap);
                mainFrame.add(gestureConfigurationPanel);
                mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            }
        });

        ctxListener = new ContextMenuListener() {
            public void contextMenuDisplayed(ContextMenuEvent event) {
                // only deal with invocations on AvatarCell
                if (!(event.getPrimaryCell() instanceof AvatarCell)) {
                    return;
                }

                ContextMenuInvocationSettings settings = event.getSettings();
                settings.setDisplayStandard(false);
                settings.setDisplayCellStandard(false);

                AvatarCell cell = (AvatarCell) event.getPrimaryCell();
                settings.setMenuName(cell.getIdentity().getUsername());

                // if this is our avatar, add the configuration menu
                if (cell == ViewManager.getViewManager().getPrimaryViewCell()) {
                    settings.addTempFactory(new ConfigureContextMenuFactory());
                } else {
                    settings.addTempFactory(new ConfigureContextMenuFactory(cell, "TurnHead"));
                    settings.addTempFactory(new ConfigureContextMenuFactory(cell, "ShakeHands"));
                    settings.addTempFactory(new ConfigureContextMenuFactory(cell, "TouchArm"));
                    settings.addTempFactory(new ConfigureContextMenuFactory(cell, "TouchShoulder"));
                }
            }
        };

        // register the renderer for this session
        ClientContextJME.getAvatarRenderManager().registerRenderer(manager,
                AvatarImiJME.class, AvatarControls.class);

        // XXX TODO: this shouldn't be done here -- it should be done in
        // activate or should be registered per session not globally
        // XXX
        try {
            String serverHostAndPort = manager.getServerNameAndPort();
            String baseURL = "wla://avatarbaseart/";
            URL url = AssetUtils.getAssetURL(baseURL, serverHostAndPort);
            WorldManager worldManager = ClientContextJME.getWorldManager();
            worldManager.addUserData(Repository.class, new Repository(worldManager,
                    new WonderlandAvatarCache(url.toExternalForm(),
                            ClientContext.getUserDirectory("AvatarCache"))));
        } catch (MalformedURLException excp) {
            logger.log(Level.WARNING, "Unable to form avatar base URL", excp);
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Exception, are you using JDK 5 ?", e);
        }

        // Initialize the AvatarSystem after we set up caching
        AvatarSystem.initialize(ClientContextJME.getWorldManager());

        manager.addLifecycleListener(this);
        super.initialize(manager);
    }

    /**
     * set the default transforms for the Chase camera
     *
     * @return the FlexibleCameraAdapter object
     */
    private FlexibleCameraAdapter getChaseCamera() {
        // Fetch the initial position of the camera. This is based upon
        // the current avatar position. We can assume that the primary
        // View Cell exists at this point, since the menu item is not
        // added until a primary View Cell exists.
        ViewManager viewManager = ViewManager.getViewManager();
        ViewCell viewCell = viewManager.getPrimaryViewCell();
        CellTransform transform = viewCell.getWorldTransform();
        Vector3f translation = transform.getTranslation(null);
        // This is the offset from the avatar view Cell to place the
        // camera
        Vector3f offset = new Vector3f(0.0f, 4.0f, -10.0f);
        // Create the camera state if it does not yet exist. Initialize
        // the initial position to that of the view Cell.
        if (camState == null) {
            camModel = (ChaseCamModel) CameraModels.getCameraModel(ChaseCamModel.class);
            camState = new ChaseCamState(offset, new Vector3f(0.0f, 1.8f, 0.0f));
            camState.setDamping(1.7f);
            camState.setLookAtDamping(1.7f);
        }
        camState.setCameraPosition(translation.add(offset));
        camState.setTargetCharacter(avatarCellRenderer.getAvatarCharacter());
        camModel.setZoom(0);
        // Create the Chase Camera with the model and state
        return new FlexibleCameraAdapter(camModel, camState);
    }

    public static GestureHUD getGestureHUD() {
        if (gestureHUDRef == null) {
            return null;
        }
        return gestureHUDRef.get();
    }

    public static Map getGestureMap() {
        return gestureMap;
    }

    public static void updateGestureMap(Map<String, Boolean> gestureMap) {
        AvatarClientPlugin.gestureMap = gestureMap;
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
        } catch (JAXBException ex) {
            Logger.getLogger(AvatarClientPlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(AvatarClientPlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AvatarClientPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }

    /**
     * {@inheritDoc]
     */
    @Override
    public void cleanup() {
        // XXX should be done in deactivate XXX
        WorldManager worldManager = ClientContextJME.getWorldManager();
        worldManager.removeUserData(Repository.class);

        ServerSessionManager manager = getSessionManager();
        ClientContextJME.getAvatarRenderManager().unregisterRenderer(manager);

        getSessionManager().removeLifecycleListener(this);
        super.cleanup();
    }

    /**
     * {@inheritDoc]
     */
    @Override
    protected void activate() {
        // Upon a new session, load the session and put it in the map. Wait
        // for it to finish loading. When done then set up the view Cell or
        // wait for it to finish.
        ServerSessionManager manager = getSessionManager();
        AvatarSessionLoader loader = new AvatarSessionLoader(manager);
        loaderMap.put(manager, loader);
        loader.addAvatarLoaderStateListener(this);
        loader.load();

        // set up our custom context menu listener to disable the standard
        // menus on avatar cells
        ContextMenuManager.getContextMenuManager().addContextMenuListener(ctxListener);

        //for triggering gestures on key+mouseclick combinations
        gesturesKeyListener = new GesturesKeyListener();
        InputManager.inputManager().addGlobalEventListener(gesturesKeyListener);
        gesturesMouseListener = new GesturesMouseListener();
        InputManager.inputManager().addGlobalEventListener(gesturesMouseListener);

        focusListener = new MainFrameFocusListener();
        JmeClientMain.getFrame().getFrame().addFocusListener(focusListener);

        //register cell status change listener
        //this will manage the list of cells in world
        cellMap = new HashMap<String, Cell>();
        cellChangeListener = new MyCellChangeListener();
        ClientContextJME.getCellManager().addCellStatusChangeListener(cellChangeListener);
    }

    /**
     * {@inheritDoc]
     */
    @Override
    protected void deactivate() {
        ContextMenuManager.getContextMenuManager().removeContextMenuListener(ctxListener);

        // First remove the menus. This will prevent users from taking action
        // upon the avatar in the (small) chance they do while the session is
        // being deactivated.
        if (menusAdded == true) {
            MainFrame frame = JmeClientMain.getFrame();
            frame.removeFromWindowMenu(gestureMI);
            frame.removeFromWindowMenu(gestureConfigMI);
            frame.removeFromToolsMenu(collisionResponseEnabledMI);
            frame.removeFromToolsMenu(gravityEnabledMI);
            frame.removeFromEditMenu(avatarConfigMI);
            frame.removeFromViewMenu(resetCamera);

            if (frame instanceof MainFrameImpl) { // Until MainFrame gets this method added
                ((MainFrameImpl) frame).removeFromCameraChoices(chaseCameraMI);
            }
            else {
                frame.removeFromViewMenu(chaseCameraMI);
            }

            // Remove the avatar controls (test) if it exists
            if (avatarControlsMI != null) {
                frame.removeFromWindowMenu(avatarControlsMI);
            }

            // Add the avatar instrumentions settings if it exists
            if (avatarSettingsMI != null) {
                frame.removeFromEditMenu(avatarSettingsMI);
            }
            menusAdded = false;
        }

        // Next, remove the listener for changes in avatar in use. This will
        // prevent the avatar from being updated while the avatars are removed
        // from the system.
        if (inUseListener != null) {
            AvatarRegistry.getAvatarRegistry().removeAvatarInUseListener(inUseListener);
            inUseListener = null;
        }

        // Stop listening for primary view changes.
        ViewManager.getViewManager().removeViewManagerListener(this);

        // Finally, fetch the avatar session loader for the session just ended
        // and unload all of the avatars from the system. This will remove the
        // avatars one-by-one.
        AvatarSessionLoader loader = loaderMap.get(getSessionManager());
        if (loader != null) {
            loader.removeAvatarLoaderStateListener(this);
            loader.unload();
            loaderMap.remove(getSessionManager());
        }
    }

    /**
     * {@inheritDoc]
     */
    public void stateChanged(State state) {
        if (state == State.READY) {
            // If the state is ready, then set-up the primary view Cell if it is
            // ready or wait for it to become ready.
            ViewManager manager = ViewManager.getViewManager();
            manager.addViewManagerListener(this);
            if (manager.getPrimaryViewCell() != null) {
                // fake a view cell changed event
                primaryViewCellChanged(null, manager.getPrimaryViewCell());
            }
        }
    }

    /**
     * {@inheritDoc]
     */
    public void primaryViewCellChanged(ViewCell oldViewCell, final ViewCell newViewCell) {
        // If there is an old avatar, then remove the listener (although in
        // practice primary view cells do not change.
        if (avatarCellRenderer != null) {
            avatarCellRenderer.removeAvatarChangedListener(avatarChangedListener);
        }

        GestureConfiguration gestureConfig = getGestureConfigurationInfo(getGroupUsersRepo());
        if (gestureConfig == null) {
            gestureMap = new HashMap<String, Boolean>();
        } else {
            gestureMap = gestureConfig.getGesturesMap();
        }

        // If the new primary view cell is null, then just return here
        if (newViewCell == null) {
            return;
        }

        logger.info("Primary view Cell Changes from " + oldViewCell +
                " to " + newViewCell + " " + newViewCell.getName());

        // Fetch the cell renderer for the new primary view Cell. It should
        // be of type AvatarImiJME. If not, log a warning and return
        CellRenderer rend = newViewCell.getCellRenderer(RendererType.RENDERER_JME);
        if (!(rend instanceof AvatarImiJME)) {
            logger.warning("Cell renderer for view " + newViewCell.getName() +
                    " is not of type AvatarImiJME.");
            return;
        }

        // We also want to listen (if we aren't doing so already) for when the
        // avatar in-use has changed.
        if (inUseListener == null) {
            inUseListener = new AvatarInUseListener() {
                public void avatarInUse(AvatarSPI avatar, boolean isLocal) {
                    refreshAvatarInUse(newViewCell, isLocal);
                }
            };
            AvatarRegistry.getAvatarRegistry().addAvatarInUseListener(inUseListener);
        }

        // set the current avatar
        avatarCellRenderer = (AvatarImiJME) rend;

        // start listener for new changes. This is used for the chase camera.
        avatarCellRenderer.addAvatarChangedListener(avatarChangedListener);

        // Set the state of the chase camera from the current avatar in the
        // cell renderer.
        if (camState != null) {
            camState.setTargetCharacter(avatarCellRenderer.getAvatarCharacter());
            camModel.reset(camState);
        }

        // Initialize the gesture HUD panel with the current avatar character.
        if (gestureHUDRef != null && gestureHUDRef.get() != null) {
            gestureHUDRef.get().setAvatarCharacter(avatarCellRenderer.getAvatarCharacter(),
                    gestureMI.isSelected(), gestureConn);
        }

        // We also want to listen (if we aren't doing so already) for when the
        // avatar in-use has changed.
        if (inUseListener == null) {
            inUseListener = new AvatarInUseListener() {
                public void avatarInUse(AvatarSPI avatar, boolean isLocal) {
                    logger.info("2) new cell = " + newViewCell);
                    refreshAvatarInUse(newViewCell, isLocal);
                }
            };
            AvatarRegistry.getAvatarRegistry().addAvatarInUseListener(inUseListener);
        }

        // Once the avatar loader is ready and the primary view has been set,
        // we tell the avatar cell component to set it's avatar in use. We can
        // only do this after we know the AvatarConfigComponent is on the View
        // Cell. We therefore add a listener, but also check immediately whether
        // the component exists. The handleSetAvatar() method makes sure that
        // the call to refresh() only happens once.
        isAvatarSet = false;
        newViewCell.addComponentChangeListener(new ComponentChangeListener() {
            public void componentChanged(Cell cell, ComponentChangeListener.ChangeType type,
                    CellComponent component) {
                AvatarConfigComponent c
                        = cell.getComponent(AvatarConfigComponent.class);
                if (type == ComponentChangeListener.ChangeType.ADDED && c != null) {
                    handleSetAvatar((ViewCell) cell);
                }
            }
        });
        if (newViewCell.getComponent(AvatarConfigComponent.class) != null) {
            handleSetAvatar(newViewCell);
        }

        // Finally, enable the menu items to allow avatar configuration. We
        // do this after the view cell is set, so we know we have an avatar
        // in the world.
        if (menusAdded == false) {
            MainFrame frame = JmeClientMain.getFrame();
            frame.addToWindowMenu(gestureMI, 0);
            frame.addToWindowMenu(gestureConfigMI, 1);
            frame.addToToolsMenu(gravityEnabledMI, 3);
            frame.addToToolsMenu(collisionResponseEnabledMI, 2);
            frame.addToEditMenu(avatarConfigMI, 0);
            frame.addToViewMenu(resetCamera, 4);

            if (frame instanceof MainFrameImpl) { // Only until the MainFrame interface gets this method
                ((MainFrameImpl) frame).addToCameraChoices(chaseCameraMI, 3);
            }
            else {
                frame.addToViewMenu(chaseCameraMI, 3);
            }

            // Add the avatar control (test) if it exists
            if (avatarControlsMI != null) {
                frame.addToWindowMenu(avatarControlsMI, 0);
            }

            // Add the avatar instrumentation settings if it exists
            if (avatarSettingsMI != null) {
                frame.addToEditMenu(avatarSettingsMI, 1);
            }
            menusAdded = true;
        }

        if (gestureHUDRef == null || gestureHUDRef.get() == null) {
            GestureHUD hud = new GestureHUD(gestureMI);
            hud.setAvatarCharacter(avatarCellRenderer.getAvatarCharacter(), false, gestureConn);
            gestureHUDRef = new WeakReference(hud);
        }
        JmeClientMain.getFrame().getCanvas().requestFocusInWindow();
    }

    public static JRadioButtonMenuItem getChaseCameraMI() {
        return chaseCameraMI;
    }

    /**
     * Handles when the primary view has been set and the view cell contains an
     * AvatarConfigComponent. This insures that the refresh() avatar is only
     * called once.
     */
    private void handleSetAvatar(ViewCell viewCell) {
        // We synchronize on 'isAvatarSet' and only call refresh() if the value
        // is false.
        isAvatarSetMutex.lock();
        try {
            if (isAvatarSet == false) {
                isAvatarSet = true;
                refreshAvatarInUse(viewCell, false);
            }
        } finally {
            isAvatarSetMutex.unlock();
        }
    }

    /**
     * Refreshes the primary view cell with the current avatar in use given the
     * current primary view cell.
     */
    public synchronized void refreshAvatarInUse(ViewCell viewCell, boolean isLocal) {

        // Once the avatar loader is ready and the primary view has been set,
        // we tell the avatar cell component to set it's avatar in use.
        AvatarConfigComponent configComponent = viewCell.getComponent(AvatarConfigComponent.class);
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        AvatarSPI avatar = registry.getAvatarInUse();
        if (avatar != null) {
            ServerSessionManager session = viewCell.getCellCache().getSession().getSessionManager();
            AvatarConfigInfo configInfo = avatar.getAvatarConfigInfo(session);
            configComponent.requestAvatarConfigInfo(configInfo, isLocal);
        }
    }

    public void sessionCreated(WonderlandSession session) {}

    public void primarySession(WonderlandSession session) {
        // Handle when a new primary session happens. Note that when there is
        // no primary session, the 'session' argument is null. In such a
        // case, we do nothing -- the case where the primary session becomes
        // disconnected is handled by the SessionStatusListener.
        if (session != null) {
            session.addSessionStatusListener(this);
            if (session.getStatus() == WonderlandSession.Status.CONNECTED) {
                connectClient(session);
            }
        }
    }

    public void sessionStatusChanged(WonderlandSession session, WonderlandSession.Status status) {
        switch (status) {
            case CONNECTED:
                connectClient(session);
                return;
            case DISCONNECTED:
                disconnectClient();
                return;
        }
    }

    /**
     * Connect the client.
     */
    private void connectClient(WonderlandSession session) {
        gestureConn = new GestureConnection();
        try {
            gestureConn.connect(session);
            if (gestureHUDRef != null && gestureHUDRef.get() != null
                    && gestureHUDRef.get().isConnectionNull() == null) {
                gestureHUDRef.get().setAvatarCharacter((WlAvatarCharacter) avatarCellRenderer.getAvatarCharacter(),
                        gestureMI.getState(), gestureConn);
            }
        } catch (ConnectionFailureException ex) {
            Logger.getLogger(AvatarClientPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Disconnect the client
     */
    private void disconnectClient() {
        gestureConn.disconnect();
    }

    /**
     * Context menu factory for configuring your avatar
     */
    private static class ConfigureContextMenuFactory
            implements ContextMenuFactorySPI 
    {

        private Cell cell = null;
        private String task = "";

        public ConfigureContextMenuFactory() {
        }

        public ConfigureContextMenuFactory(Cell cell, String task) {
            this.cell = cell;
            this.task = task;
        }

        public ContextMenuItem[] getContextMenuItems(ContextEvent event) {
            if (task.equals("")) {
                return new ContextMenuItem[]{
                    new SimpleContextMenuItem(bundle.getString("Configure..."),
                    new ContextMenuActionListener() {
                        public void actionPerformed(ContextMenuItemEvent event) {
                            AvatarConfigFrame f = getAvatarConfigFrame();
                            if (f != null) {
                                f.setVisible(true);
                            }
                        }
                    })
                };
            } else if (task.equals("TurnHead")) {
                return new ContextMenuItem[]{
                    new SimpleContextMenuItem("Look At (Press '1' + Left Click)",
                    new ContextMenuActionListener() {
                        public void actionPerformed(ContextMenuItemEvent event) {
                            if (AvatarClientPlugin.isShakingHand(cell.getCellID().toString())) {
                                return;
                            }
                            //create turn head task and run it
                            lookAtAdvanced(cell);

                            //notify listeners
                            notifyLookAtListener(cell);
                        }
                    })
                };
            } else if (task.equals("ShakeHands")) {
                SimpleContextMenuItem scmiSH = new SimpleContextMenuItem("Shake Hands (Press '2' + Left Click)",
                        new ContextMenuActionListener() {
                            public void actionPerformed(ContextMenuItemEvent event) {
                                if (AvatarClientPlugin.isShakingHand(cell.getCellID().toString())) {
                                    return;
                                }
                                //create shake hand task and run it
                                shakeHands(cell);
                            }
                        });
                GameState gs = ((AvatarImiJME) cell.getCellRenderer(RendererType.RENDERER_JME))
                        .getAvatarCharacter().getContext().getCurrentState();
                if (gs instanceof SitState) {
                    if (((SitState) gs).isSleeping()) {
                        scmiSH.setEnabled(false);
                    }
                }
                return new ContextMenuItem[]{
                    scmiSH
                };
            } else if (task.equals("TouchArm")) {
                SimpleContextMenuItem scmiTA = new SimpleContextMenuItem("Touch Arm (Press '3' + Left Click)",
                        new ContextMenuActionListener() {
                            public void actionPerformed(ContextMenuItemEvent event) {
                                if (AvatarClientPlugin.isShakingHand(cell.getCellID().toString())) {
                                    return;
                                }
                                //create Touch Arm task and run it
                                touchArm(cell);
                            }
                        });
                GameState gs = ((AvatarImiJME) cell.getCellRenderer(RendererType.RENDERER_JME))
                        .getAvatarCharacter().getContext().getCurrentState();
                if (gs instanceof SitState) {
                    if (((SitState) gs).isSleeping()) {
                        scmiTA.setEnabled(false);
                    }
                }
                return new ContextMenuItem[]{
                    scmiTA
                };
            } else if (task.equals("TouchShoulder")) {
                SimpleContextMenuItem scmiTS = new SimpleContextMenuItem("Touch Shoulder (Press '4' + Left Click)",
                        new ContextMenuActionListener() {
                            public void actionPerformed(ContextMenuItemEvent event) {
                                if (AvatarClientPlugin.isShakingHand(cell.getCellID().toString())) {
                                    return;
                                }
                                //create Touch Shoulder task and run it
                                touchShoulder(cell);
                            }
                        });
                //Check if avatar is at proper height
                GameState gs = ((AvatarImiJME) cell.getCellRenderer(RendererType.RENDERER_JME))
                        .getAvatarCharacter().getContext().getCurrentState();
                if (gs instanceof SitState) {
                    if (((SitState) gs).isSleeping()) {

                        //Check if avatar is at rpoper height??
                        CellRenderer rendT = cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                        WlAvatarCharacter avatarT = ((AvatarImiJME) rendT).getAvatarCharacter();
                        PJoint headJ = avatarT.getSkeleton().getJoint("Head");

                        float headY = headJ.getTransform().getWorldMatrix(false).getTranslation().y;

                        if (headY <= 1) {
                            scmiTS.setEnabled(false);
                        }
                    }
                }
                return new ContextMenuItem[]{
                    scmiTS
                };
            } else {
                return null;
            }
        }
    }

    private static AvatarConfigFrame getAvatarConfigFrame() {
        try {
            if (AvatarImiJME.getPrimaryAvatarRenderer() != null
                    && (AvatarImiJME.getPrimaryAvatarRenderer().getAvatarCharacter().getContext().getCurrentState() instanceof SitState)) {
                final String message = "Avatar should be in standing position to configure.";
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        JOptionPane.showMessageDialog(null, message, "Warning for Configuring Avatar", TrayIcon.MessageType.INFO.ordinal());
                    }
                });
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (avatarConfigFrame == null) {
            avatarConfigFrame = new AvatarConfigFrame();
        }
        return avatarConfigFrame;
    }

    public static void lookAtAdvanced(final Cell targetCell) {
        //send message
        ViewCell thisCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        //check if we are looking at our avatar or not
        if (targetCell.getCellID().toString().equals(thisCell.getCellID().toString())) {
            return;
        }
        GestureMessage msg = new GestureMessage();
        msg.setUserId(thisCell.getCellID().toString());
        msg.setForGesture(true);
        msg.setMessageType("StartTurnHead");
        msg.setMessageString(targetCell.getCellID().toString());
        gestureConn.send(msg);

        //local look at
        lookAtAdvancedLocal(thisCell, targetCell);
    }

    /**
     * look at another avatar or any other object
     *
     * @param thisCell
     * @param targetCell
     */
    public static void lookAtAdvancedLocal(Cell thisCell, Cell targetCell) {
        try {
            //notify
            String targetName = "";
            if (targetCell instanceof AvatarCell) {
                CellRenderer rend = targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
                targetName = charr.getName();
            } else {
                targetName = targetCell.getName();
            }
            for (AvatarInteractionListener lis : AvatarInteractionListenerRegistrar.getRegisteredListeners()) {
                lis.avatarsInteract(thisCell, targetCell, "Look At " + targetName, true);
            }

            CellRenderer rend = thisCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
            final WlAvatarContext context = (WlAvatarContext) myAvatar.getContext();
            Task task = myAvatar.getContext().getBehaviorManager().getCurrentTask();
            if (task != null && (task instanceof TurnHeadAdvanced)) {
                ((TurnHeadAdvanced) task).restart(targetCell);
            } else {
                CharacterBehaviorManager helm = context.getBehaviorManager();
                TurnHeadAdvanced th = new TurnHeadAdvanced(thisCell, targetCell, gestureConn);
                helm.clearTasks();
                helm.setEnable(true);
                helm.addTaskToTop(th);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * shake hand with target avatar
     *
     * @param targetCell
     */
    public static void shakeHands(final Cell targetCell) {
        try {
            Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
            if (avatarCell.getCellID().toString().equals(targetCell.getCellID().toString())) {
                return;
            }
            CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
            GameContext context = myAvatar.getContext();
            CharacterBehaviorManager helm = context.getBehaviorManager();

            //remove look at if already in
            removeLookAt(avatarCell);

            //removing the currently playing gestures before playing the interactions
            if (context.getCurrentState() instanceof CycleActionState) {
                GameState gs = context.getCurrentState();
                stopGesture(avatarCell, gs);
                // help transfer the third person camera to shake hand camera
                if (((CycleActionState) gs).getAnimationName().contains("PublicSpeaking")) {
                    logger.fine("Playing idle");
                    context.triggerPressed(AvatarContext.TriggerNames.Idle.ordinal());
                    context.triggerReleased(AvatarContext.TriggerNames.Idle.ordinal());
                }
            }

            ShakeHands sh = new ShakeHands(targetCell, gestureConn, context);
            helm.clearTasks();
            helm.setEnable(true);
            helm.addTaskToTop(sh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Touch Arm of target avatar
     *
     * @param targetCell
     */
    public static void touchArm(final Cell targetCell) {
        try {
            Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
            if (avatarCell.getCellID().toString().equals(targetCell.getCellID().toString())) {
                return;
            }
            CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
            GameContext context = myAvatar.getContext();
            CharacterBehaviorManager helm = context.getBehaviorManager();

            //remove look at if already in
            removeLookAt(avatarCell);

            //removing the currently playing gestures before playing the interactions
            if (context.getCurrentState() instanceof CycleActionState) {
                GameState gs = context.getCurrentState();
                stopGesture(avatarCell, gs);
            }

            TouchArm sh = new TouchArm(targetCell, gestureConn, context);
            helm.clearTasks();
            helm.setEnable(true);
            helm.addTaskToTop(sh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Touch Shoulder of target avatar
     *
     * @param targetCell
     */
    public static void touchShoulder(final Cell targetCell) {
        try {
            Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
            if (avatarCell.getCellID().toString().equals(targetCell.getCellID().toString())) {
                return;
            }
            CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            WlAvatarCharacter myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
            GameContext context = myAvatar.getContext();
            CharacterBehaviorManager helm = context.getBehaviorManager();

            //remove look at if already in
            removeLookAt(avatarCell);
            //removing the currently playing gestures before playing the interactions
            if (context.getCurrentState() instanceof CycleActionState) {
                GameState gs = context.getCurrentState();
                stopGesture(avatarCell, gs);
            }
            TouchShoulder sh = new TouchShoulder(targetCell, gestureConn, context);

            helm.clearTasks();
            helm.setEnable(true);
            helm.addTaskToTop(sh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void stopGesture(Cell avatarCell, GameState gs) {
        GestureMessage msg = new GestureMessage();
        gs.notifyAnimationMessage(AnimationListener.AnimationMessageType.EndOfCycleWithoutExitAnim, gs.getAnimationName());
        msg.setMessageType("EndOfCycleWithoutExitAnim");
        msg.setMessageString(gs.getAnimationName());
        msg.setUserId(avatarCell.getCellID().toString());
        gestureConn.send(msg);
        logger.info("Exiting");
    }

    /**
     * force the look at task to complete
     *
     * @param avatarCell
     * @throws Exception
     */
    private static void removeLookAt(final Cell avatarCell) throws Exception {
        CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        WlAvatarCharacter myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
        GameContext context = myAvatar.getContext();
        final CharacterBehaviorManager helm = context.getBehaviorManager();

        if (helm.getCurrentTask() instanceof TurnHeadAdvanced) {
            final TurnHeadAdvanced tha = (TurnHeadAdvanced) helm.getCurrentTask();
            if (!tha.getStatus().equals("finished")) {
                Thread waitLookAt = new Thread(new Runnable() {

                    public void run() {
                        try {
                            tha.forceCompleteTask();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                waitLookAt.start();
                waitLookAt.join(10000);
            }
        }
    }

    /**
     * Check if mouse left click if pressed or not, If yes then trigger
     * appropriate gesture depending upon the key variable.
     */
    private class GesturesMouseListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{MouseEvent3D.class};
        }

        @Override
        public void commitEvent(Event event) {
            if (event instanceof MouseButtonEvent3D) {
                MouseButtonEvent3D me = ((MouseButtonEvent3D) event);
                if (me.isClicked() && me.getButton() == MouseEvent3D.ButtonId.BUTTON1) {
                    Cell cell = SceneManager.getCellForEntity(me.getEntity());
                    if (isShakingHand(cell.getCellID().toString())) {
                        return;
                    }
                    if (key == 1) {
                        lookAtAdvanced(cell);
                    } else if (key == 2) {
                        boolean doShakeHand = true;
                        GameState gs = ((AvatarImiJME) cell.getCellRenderer(RendererType.RENDERER_JME))
                                .getAvatarCharacter().getContext().getCurrentState();
                        if (gs instanceof SitState) {
                            if (((SitState) gs).isSleeping()) {
                                doShakeHand = false;
                            }
                        }
                        if (doShakeHand) {
                            shakeHands(cell);
                        }
                    } else if (key == 3) {
                        touchArm(cell);
                    } else if (key == 4) {
                        //Check if avatar is at proper height
                        GameState gs = ((AvatarImiJME) cell.getCellRenderer(RendererType.RENDERER_JME))
                                .getAvatarCharacter().getContext().getCurrentState();
                        if (gs instanceof SitState && ((SitState) gs).isSleeping()) {
                            //Check if avatar is at rpoper height??
                            CellRenderer rendT = cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                            WlAvatarCharacter avatarT = ((AvatarImiJME) rendT).getAvatarCharacter();
                            PJoint headJ = avatarT.getSkeleton().getJoint("Head");

                            float headY = headJ.getTransform().getWorldMatrix(false).getTranslation().y;

                            if (headY > 1) {
                                touchShoulder(cell);
                            }
                        } else {
                            touchShoulder(cell);
                        }

                    }
                }
            }
        }
    }

    /**
     * update key variable if we have pressed any desired key to perform
     * gestures.
     */
    private static int key = -1;

    private class GesturesKeyListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{KeyEvent3D.class};
        }

        @Override
        public void commitEvent(Event event) {
            if (event instanceof KeyEvent3D) {
                KeyEvent3D e = (KeyEvent3D) event;
                if (e.getKeyCode() == KeyEvent.VK_1 && e.isPressed()) {
                    key = 1;
                } else if (e.getKeyCode() == KeyEvent.VK_1 && e.isReleased()) {
                    key = -1;
                } else if (e.getKeyCode() == KeyEvent.VK_2 && e.isPressed()) {
                    key = 2;
                } else if (e.getKeyCode() == KeyEvent.VK_2 && e.isReleased()) {
                    key = -1;
                } else if (e.getKeyCode() == KeyEvent.VK_3 && e.isPressed()) {
                    key = 3;
                } else if (e.getKeyCode() == KeyEvent.VK_3 && e.isReleased()) {
                    key = -1;
                } else if (e.getKeyCode() == KeyEvent.VK_4 && e.isPressed()) {
                    key = 4;
                } else if (e.getKeyCode() == KeyEvent.VK_4 && e.isReleased()) {
                    key = -1;
                }
            }
        }
    }

    /**
     * If the main frame lost focus, Remove if any key is pressed.
     */
    private class MainFrameFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {

        }

        public void focusLost(FocusEvent e) {
            key = -1;
        }

    }

    public static int getPressedKey() {
        return key;
    }

    /**
     * Stop cells in map so that we can search using cell id.
     */
    private class MyCellChangeListener implements CellStatusChangeListener {

        public void cellStatusChanged(Cell cell, CellStatus status) {
            if (status == CellStatus.ACTIVE) {
                cellMap.put(cell.getCellID().toString(), cell);
            } else if (status == CellStatus.INACTIVE) {
                cellMap.remove(cell.getCellID().toString());
            }
        }
    }

    public static Cell getCellByCellId(String cellId) {
        Cell cell = cellMap.get(cellId);
        return cell;
    }

    public static Cell getAvatarCellByCellId(String cellId) {
        Cell cell = cellMap.get(cellId);
        if (cell == null) {
            cell = getAvatarCellByCellId1(cellId);
        }
        return cell;
    }

    public static Cell getAvatarCellByCellId1(String cellId) {
        Cell avatarCell = null;
        Cell cell = ClientContextJME.getViewManager().getPrimaryViewCell();
        try {
            Collection<Cell> cellList = cell.getCellCache().getRootCells();
            for (Cell cell1 : cellList) {
                if (cell1 instanceof AvatarCell
                        && cell1.getCellID().toString().equals(cellId)) {
                    avatarCell = cell1;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avatarCell;
    }

    /**
     * register look at listeners
     *
     * @param toAdd listener to add
     */
    public static void registerLookAtListener(LookAtListener toAdd) {
        if (lookAtlisteners == null) {
            lookAtlisteners = new LinkedList<LookAtListener>();
        }
        lookAtlisteners.add(toAdd);
    }

    /**
     * deregister look at listeners
     *
     * @param toAdd listener to remove
     */
    public static void deRegisterLookAtListener(LookAtListener toAdd) {
        lookAtlisteners.remove(toAdd);
    }

    /**
     * to notify that look at has just performed
     */
    public interface LookAtListener {

        public void lookAtPerformed();

    }

    public static void notifyLookAtListener(Cell cell) {
        try {
            CellComponent comp = cell.getComponent((Class<CellComponent>) Class
                    .forName("org.jdesktop.wonderland.modules.bestview.client.BestViewComponent"));

            if (comp != null && (comp instanceof LookAtListener)) {
                ((LookAtListener) comp).lookAtPerformed();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AvatarClientPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isShakingHand(String targetCellId2) {
        if (gestureConn.isShakingHand(ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString(), targetCellId2)) {
            return true;
        }
        return false;
    }
}
