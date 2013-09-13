package org.netbeans.eclipse.formatter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.NotificationDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

@ActionID(
        category = "Build",
        id = "org.netbeans.eclipse.formatter.ReformatWithEclipseBeforeSaveTask")
@ActionRegistration(
        lazy = true,
        displayName = "#CTL_EclipseFormatter")
@ActionReferences({
    @ActionReference(path = "Menu/Source", position = 0),
    @ActionReference(path = "Editors/text/x-java/Actions", position = 0),
    @ActionReference(path = "Shortcuts", name = "OS-F")
})
@NbBundle.Messages("CTL_EclipseFormatter=Format")
public class ReformatWithEclipseAction extends CookieAction implements ActionListener {

    private EditorCookie context = null;
    private EclipseFormatter formatter;

    public ReformatWithEclipseAction() {
        this.formatter = EclipseFormatterUtilities.getEclipseFormatter();
    }
    
    @Override
    protected boolean enable(Node[] nodes) {
        if (nodes.length==1) {
            final EditorCookie editorCookie = nodes[0].getLookup().lookup(EditorCookie.class);
            if (editorCookie != null && null!=editorCookie.getDocument()) {
                boolean isJava = EclipseFormatterUtilities.isJava(editorCookie.getDocument());
                if (isJava) {
                    context=editorCookie;
                    return true;
                }
            }
        }
        context = null;
        return false;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final EditorCookie editorCookie = context;
        if (null == editorCookie || null == editorCookie.getDocument()) {
            return;
        }
        final StyledDocument styledDoc = editorCookie.getDocument();
        GuardedSectionManager guards = GuardedSectionManager.getInstance(styledDoc);
        EclipseFormatterUtilities u = new EclipseFormatterUtilities();
        if (guards == null && this.formatter != null) {
            u.reFormatWithEclipse(styledDoc, formatter, u.isJava(styledDoc));
        } else {
            if (EclipseFormatterUtilities.getGlobalPrefs().getBoolean("globalEclipseFormatterDebug", false) == true) {
                NotificationDisplayer.getDefault().notify("NetBeans formatter", EclipseFormatterUtilities.icon, "(Files with guarded blocks are not supported by Eclipse formatters)", null);
            }
            u.reformatWithNetBeans(styledDoc);
        }
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    //Not used:
    @Override
    public String getName() {return null;}
 
    //Not used:
    @Override protected void performAction(Node[] nodes) {}

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
