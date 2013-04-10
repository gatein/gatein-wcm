package org.modeshape.test;

import java.io.File;

import javax.annotation.Resource;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class VersioningAPITest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "modeshape-tests.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void simpleRemove() throws Exception {
        // Accessing
        SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
        javax.jcr.Session jcrSession = repository.login(credentials, "default");

        // Creates node
        Node n = jcrSession.getRootNode();
        n = n.addNode("simpleRemoveTest", "nt:folder");
        n.addMixin("mix:title");
        n.addMixin("mix:versionable");
        jcrSession.save();

        // Creates version 1
        jcrSession.getWorkspace().getVersionManager().checkout("/simpleRemoveTest");
        n.setProperty("jcr:description", "This is a version test 9999");
        jcrSession.save();
        Version v1 = jcrSession.getWorkspace().getVersionManager().checkin("/simpleRemoveTest");

        // Creates version 2
        jcrSession.getWorkspace().getVersionManager().checkout("/simpleRemoveTest");
        n.setProperty("jcr:description", "This is a version test 9999.9");
        jcrSession.save();
        Version v2 = jcrSession.getWorkspace().getVersionManager().checkin("/simpleRemoveTest");

        // Deletes version 1
        VersionHistory vh = jcrSession.getWorkspace().getVersionManager().getVersionHistory("/simpleRemoveTest");
        vh.removeVersion(v1.getName());

        // Deletes node
        jcrSession.removeItem("/simpleRemoveTest");
        jcrSession.save();

        try {
            vh.refresh(false);
            vh.removeVersion(v2.getName());
        } catch (Exception expected) {
            // similar to MODE-1883
        }
    }

    @Test
    public void createVersioningNodesAndDelete() throws Exception {

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

        jcrSession.getWorkspace().getVersionManager().checkout(location + "/" + id + "/" + MARK + locale + "/" + MARK + id);

        // Adding properties
        n.getNode("jcr:content");
        n.setProperty("jcr:description", content.getString());
        n.setProperty("jcr:encoding", encoding);

        // Saving changes into JCR
        jcrSession.save();

        jcrSession.getWorkspace().getVersionManager().checkin(location + "/" + id + "/" + MARK + locale + "/" + MARK + id);


        // Getting VM properties
        // vm.getVersionHistory(location + "/" + id + "/" + MARK + locale + "/" + MARK + id).getAllLinearFrozenNodes();
        VersionHistory history = jcrSession.getWorkspace()
                                    .getVersionManager()
                                    .getVersionHistory(location + "/" + id + "/" + MARK + locale + "/" + MARK + id);

        try {
        VersionIterator vi = history.getAllVersions();
        while (vi.hasNext()) {
            Version version = vi.nextVersion();
            if (!"jcr:rootVersion".equals(version.getName()))
                history.removeVersion(version.getName());
        }
        } catch (Exception expected) {
            // This is a suspected bad behaviour
            // Modeshape cannot delete a version with only 1 element
        }
        jcrSession.removeItem(location + id);

        jcrSession.save();

        // This fails...
        // jcrSession.getNodeByIdentifier(history.getVersionableIdentifier()).remove();

        jcrSession.save();

    }

    @Test
    public void checkVersionSubgraph() throws Exception {

        // Connecting
        SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
        javax.jcr.Session jcrSession = repository.login(credentials, "default");

        String location = "/";
        String id = "test-version-folder";

        // Base version
        Node n = jcrSession.getNode(location).addNode(id, "nt:folder");
        n.addMixin("mix:title");
        n.addMixin("mix:versionable");
        n.addMixin("mix:lastModified");
        n.addMixin("mix:shareable");

        // Invoking Version Manager before to save
        VersionManager vm = jcrSession.getWorkspace().getVersionManager();

        // Root Version
        // Version 1.0
        vm.checkout(location + id);
        n.setProperty("jcr:description", "folder:" + n.getPath());
        n.addNode("my-folder-version-1.0", "nt:folder").addNode("other-1.0", "nt:folder");
        jcrSession.save();
        Version v = vm.checkin(location + id);
        Assert.assertEquals("1.0", v.getName());

        // Version 1.1
        vm.checkout(location + id);
        n.setProperty("jcr:description", "Version 1");
        n.addNode("my-folder-version-1.1", "nt:folder").addNode("other-1.1", "nt:folder");
        jcrSession.save();
        v = vm.checkin(location + id);
        Assert.assertEquals("1.1", v.getName());

        // Version 1.2
        vm.checkout(location + id);
        n.setProperty("jcr:description", "Version 2");
        n.addNode("my-folder-version-1.2", "nt:folder").addNode("other-1.2", "nt:folder");
        jcrSession.save();
        v = vm.checkin(location + id);
        Assert.assertEquals("1.2", v.getName());

        // Version 1.3
        vm.checkout(location + id);
        n.setProperty("jcr:description", "Version 3");
        n.addNode("my-folder-version-1.3", "nt:folder").addNode("other-1.3", "nt:folder");
        jcrSession.save();
        v = vm.checkin(location + id);
        Assert.assertEquals("1.3", v.getName());

        // Version 1.1.0
        vm.restore(location + id, "1.0", false);
        vm.checkout(location + id);
        n.setProperty("jcr:description", "Version 4");
        n.addNode("my-folder-version-1.1.0", "nt:folder").addNode("other-1.1.0", "nt:folder");

        // These subgraphs belongs to version 1.0
        try {
            n.getNode("my-folder-version-1.2");
        } catch (PathNotFoundException expected) { }

        try {
            n.getNode("my-folder-version-1.2");
        } catch (PathNotFoundException expected) { }

        jcrSession.save();
        v = vm.checkin(location + id);
        Assert.assertEquals("1.1.0", v.getName());


        // Version 1.1.1
        vm.checkout(location + id);
        n.setProperty("jcr:description", "Version 5");
        n.addNode("my-folder-version-1.1.1", "nt:folder").addNode("other-1.1.1", "nt:folder");
        jcrSession.save();
        v = vm.checkin(location + id);
        Assert.assertEquals("1.1.1", v.getName());

        n.remove();
        jcrSession.save();

        // This fails, MODE-1883 open

        // Restoring
////        Version root = vm.getVersionHistory(location+id).getRootVersion();
//        vm.restore(v, false);
//        jcrSession.save();
//
//        // Version 2.0
//        vm.checkout(location + id);
//        n.setProperty("jcr:description", "Version 6");
//        jcrSession.save();
//        v = vm.checkin(location + id);
//        Assert.assertEquals("2.0", v.getName());
//
//        // Root version is always null (we have to add 1 when we count it)
//        VersionHistory vh = vm.getVersionHistory(location + id);
//        // Assert.assertEquals(7, vh.getAllLinearVersions().getSize());

        jcrSession.save();
        jcrSession.logout();

    }

}
