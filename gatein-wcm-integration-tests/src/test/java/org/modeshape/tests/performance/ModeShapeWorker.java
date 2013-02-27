package org.modeshape.tests.performance;


import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.version.VersionManager;

import org.jboss.logging.Logger;

public class ModeShapeWorker implements Runnable {
    private static final Logger log = Logger.getLogger("org.modeshape.tests.performance");
    private final int nTest;

    Session jcrSession;

    ModeShapeWorker(int nTest, Session jcrSession) {
        this.nTest = nTest;
        this.jcrSession = jcrSession;
    }

    @Override
    public void run() {

        try {
            log.info( "Test #" + nTest );

            String location = "/";
            String id = "test" + nTest;
            String MARK = "__";
            String locale = "es";
            String encoding = "UTF8";

            Value content = jcrSession.getValueFactory().createValue("This is a text content from " + id);
            VersionManager vm = jcrSession.getWorkspace().getVersionManager();

            jcrSession.getNode(location).addNode(id, "nt:folder");
            jcrSession.getNode(location + "/" + id).addNode(MARK + locale, "nt:folder").addNode(MARK + id, "nt:file")
            .addNode("jcr:content", "nt:resource").setProperty("jcr:data", content);

            Node n = jcrSession.getNode(location + "/" + id);
            n.addMixin("mix:title");
            n.addMixin("mix:lastModified");
            n.addMixin("mix:shareable");
            n.setProperty("jcr:description", "textcontent:" + n.getPath());

            n = jcrSession.getNode(location + "/" + id + "/" + MARK + locale + "/" + MARK + id);
            n.addMixin("mix:title");
            n.addMixin("mix:versionable");
            n.addMixin("mix:lastModified");
            n.addMixin("mix:mimeType");

            // Adding properties
            n.getNode("jcr:content");
            n.setProperty("jcr:description", content.getString());
            n.setProperty("jcr:encoding", encoding);

            // Saving changes into JCR
            jcrSession.save();

            // Getting VM properties
            // vm.getVersionHistory(location + "/" + id + "/" + MARK + locale + "/" + MARK + id).getAllLinearFrozenNodes();
            jcrSession.getWorkspace().getVersionManager()
            .getVersionHistory(location + "/" + id + "/" + MARK + locale + "/" + MARK + id).getAllLinearFrozenNodes();

            jcrSession.logout();

        } catch (Exception e) {
            log.error("Test #" + nTest + " Failed " + e.getMessage(), e);
        }
   }

}
