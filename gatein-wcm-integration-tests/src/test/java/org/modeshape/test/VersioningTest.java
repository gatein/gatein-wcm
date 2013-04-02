package org.modeshape.test;

import java.io.File;

import javax.annotation.Resource;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class VersioningTest {

    private static final Logger log = Logger.getLogger("org.modeshape.tests.versions");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "modeshape-tests.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void createVersioningNodesAndDelete() throws Exception {
        log.debug("[[ START JCR Versioning Test ]]");

        String nTest = "1";

        SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
        javax.jcr.Session jcrSession = repository.login(credentials, "default");
        String location = "/";
        String id = "test" + nTest;
        String MARK = "__";
        String locale = "es";
        String encoding = "UTF8";

        Value content = jcrSession.getValueFactory().createValue("This is a text content from " + id);

        jcrSession.getNode(location)
                  .addNode(id, "nt:folder");
        jcrSession.getNode(location + "/" + id)
                  .addNode(MARK + locale, "nt:folder")
                  .addNode(MARK + id, "nt:file")
                  .addNode("jcr:content", "nt:resource")
                  .setProperty("jcr:data", content);

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
        VersionHistory history = jcrSession.getWorkspace()
                                    .getVersionManager()
                                    .getVersionHistory(location + "/" + id + "/" + MARK + locale + "/" + MARK + id);

        VersionIterator vi = history.getAllVersions();
        while (vi.hasNext()) {
            Version version = vi.nextVersion();
            log.debug(version.getName());
            if (!"jcr:rootVersion".equals(version.getName()))
                history.removeVersion(version.getName());
        }
        log.debug("VH: " + history.getVersionableIdentifier());

        jcrSession.removeItem(location + id);

        jcrSession.save();

        // This fails...
        // jcrSession.getNodeByIdentifier(history.getVersionableIdentifier()).remove();

        jcrSession.save();

        log.debug("[[ STOP JCR Versioning Test ]]");
    }

}
