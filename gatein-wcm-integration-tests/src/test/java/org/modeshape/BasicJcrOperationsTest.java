package org.modeshape;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.api.JcrConstants;


@RunWith(Arquillian.class)
public class BasicJcrOperationsTest {

    private static final Logger log = Logger.getLogger(BasicJcrOperationsTest.class);

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-jrc-tests.war")
                .addAsResource(new File("src/test/resources/jbossportletbridge.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @SuppressWarnings("deprecation")
    @Test
    public void queryRoot() {

        try {

            log.debug( "[[ START TEST queryRoot ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String sQuery = "/jcr:root";

            Query query = queryManager.createQuery( sQuery, Query.XPATH );
            QueryResult result = query.execute();

            if (result.getNodes().getSize() == 1) {
                Node n = result.getNodes().nextNode();
                log.debug("Path: " + n.getPath() + " Node: " + n.getIdentifier());
            } else {
                log.debug( "result.getNodes().getSize(): " + result.getNodes().getSize() );
            }

            Assert.assertTrue( true );
            log.debug("[[ END TEST queryRoot ]]");

        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    public void cleanTestsFolder() {

        try {

            log.debug( "[[ START CLEANING ]]" );
            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
            javax.jcr.Session session = repository.login(credentials, "default");

            // Nodes to clean...
            if (session.nodeExists("/tests")) {
                session.removeItem("/tests");
            }
            if (session.nodeExists("/content1")) {
                session.removeItem("/content1");
            }
            if (session.nodeExists("/content2")) {
                session.removeItem("/content2");
            }
            if (session.nodeExists("/loadtest")) {
                session.removeItem("/loadtest");
            }
            if (session.nodeExists("/myfolder")) {
                session.removeItem("/myfolder");
            }
            session.save();

            log.debug("[[ STOP CLEANING ]]");
        } catch (Exception e) {
            log.error("Cleaning: " + e.getMessage());
        }

    }

    @SuppressWarnings("deprecation")
    @Test
    public void addFolder() {

        cleanTestsFolder();

        try {

            log.debug( "[[ START TEST addFolder ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String sQuery = "/jcr:root";

            Query query = queryManager.createQuery( sQuery, Query.XPATH );
            QueryResult result = query.execute();

            if (result.getNodes().getSize() == 1) {
                Node n = result.getNodes().nextNode();
                n.addNode("tests", JcrConstants.NT_FOLDER);
                log.debug("Path: " + n.getPath() + " Node: " + n.getIdentifier());
                log.debug("Check: " + session.nodeExists("/tests") );
                session.save();

            } else {
                log.debug( "result.getNodes().getSize(): " + result.getNodes().getSize() );
            }

            Assert.assertTrue( true );

            log.debug("[[ END TEST addFolder ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }

    @Test
    public void complexFolders() {

        cleanTestsFolder();

        try {

            log.debug( "[[ START TEST complexFolders ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.debug("Test / " + session.nodeExists("/"));

            Value content = session.getValueFactory().createValue("<html>This is a test content</html>");

            Node root = session.getNode("/");

            root.addNode("content1", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content1").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content1/__es").addNode("__content1", JcrConstants.NT_FILE);
            session.getNode("/content1/__es/__content1").addNode("jcr:content", "nt:resource").setProperty("jcr:data", content);

            session.save();

            Assert.assertTrue( true );

            log.debug("[[ END TEST complexFolders ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }

    @Test
    public void basicVersioning() {

        cleanTestsFolder();

        try {

            log.debug( "[[ START TEST basicVersioning ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.debug("Test / " + session.nodeExists("/"));

            Value content = session.getValueFactory().createValue("<html>This is a test content</html>");

            Node root = session.getNode("/");

            root.addNode("content1", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content1").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content1/__es").addNode("__content1", JcrConstants.NT_FILE);
            session.getNode("/content1/__es/__content1").addMixin("mix:versionable");
            session.getNode("/content1/__es/__content1").addNode("jcr:content", "nt:resource").setProperty("jcr:data", content);

            session.save();

            VersionManager vm = session.getWorkspace().getVersionManager();

            VersionHistory h = vm.getVersionHistory("/content1/__es/__content1");

            log.debug("Number of versions: " + h.getAllLinearVersions().getSize() );

            vm.checkout( "/content1/__es/__content1" );
            vm.checkin( "/content1/__es/__content1" );

            log.debug("Number of versions: " + h.getAllLinearVersions().getSize() );

            Assert.assertTrue( true );

            log.debug("[[ END TEST basicVersioning ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }

    @Test
    public void basicProperties() {

        cleanTestsFolder();

        try {

            log.debug( "[[ START TEST basicProperties ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.debug("Test / " + session.nodeExists("/"));

            Value content = session.getValueFactory().createValue("<html>This is a test content</html>");

            Node root = session.getNode("/");

            root.addNode("content1", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content1").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content1/__es").addNode("__content1", JcrConstants.NT_FILE);
            session.getNode("/content1/__es/__content1").addMixin("mix:versionable");
            session.getNode("/content1/__es/__content1").addNode("jcr:content", "nt:resource").setProperty("jcr:data", content);

            session.save();

            VersionManager vm = session.getWorkspace().getVersionManager();

            VersionHistory h = vm.getVersionHistory("/content1/__es/__content1");

            log.debug("Number of versions: " + h.getAllLinearVersions().getSize() );

            vm.checkout( "/content1/__es/__content1" );
            vm.checkin( "/content1/__es/__content1" );

            log.debug("Number of versions: " + h.getAllLinearVersions().getSize() );

            Assert.assertTrue( true );

            Node n = session.getNode("/content1");
            n.addMixin("mix:created");
            n.addMixin("mix:lastModified");

            session.save();

            n = session.getNode("/content1");
            log.debug("Created: " + n.getProperty("jcr:created").getString() );
            log.debug("CreatedBy: " + n.getProperty("jcr:createdBy").getString() );
            log.debug("LastModified: " + n.getProperty("jcr:lastModified").getString() );
            log.debug("LastModifiedBy: " + n.getProperty("jcr:lastModifiedBy").getString() );

            VersionIterator vi = h.getAllLinearVersions();
            while (vi.hasNext()) {
                Version v = vi.nextVersion();
                log.debug("V: " + v.getName());
            }

            log.debug("[[ END TEST basicProperties ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }

    @Test
    public void checkAcl() {

        cleanTestsFolder();

        try {

            log.debug( "[[ START TEST checkAcl ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.debug("Test / " + session.nodeExists("/"));

            Value content = session.getValueFactory().createValue("<html>This is a test content</html>");

            Node root = session.getNode("/");

            root.addNode("content1", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content1").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content1/__es").addNode("__content1", JcrConstants.NT_FILE);
            session.getNode("/content1/__es/__content1").addMixin("mix:versionable");
            session.getNode("/content1/__es/__content1").addNode("jcr:content", "nt:resource").setProperty("jcr:data", content);

            // Adding ACL
            // Value acl = session.getValueFactory().createValue("admin:USER:ALL,guess:USER:READ");
            Value acl = session.getValueFactory().createValue("admin:USER:ALL");
            session.getNode("/content1").addNode("__acl", JcrConstants.NT_FILE);
            session.getNode("/content1/__acl").addNode("jcr:content", "nt:resource").setProperty("jcr:data", acl);
            session.save();

            String aces = session.getNode("/content1/__acl/jcr:content").getProperty("jcr:data").getString();
            for (String ace : aces.split(",")) {
                log.debug("ACE: " + ace);
            }

            Assert.assertTrue( true );

            log.debug("[[ END TEST checkAcl ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }

    @Test
    public void fullTextData() {

        cleanTestsFolder();

        try {

            log.debug( "[[ START TEST fullTextData ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            Value content1 = session.getValueFactory().createValue("<html>This is a test word1</html>");
            Value content2 = session.getValueFactory().createValue("<html>This is a test word2</html>");

            Node root = session.getNode("/");

            root.addNode("content1", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content1").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content1/__es").addNode("__content1", JcrConstants.NT_FILE);
            session.getNode("/content1/__es/__content1").addNode("jcr:content", "nt:resource").addMixin("mix:title");
            session.getNode("/content1/__es/__content1/jcr:content").setProperty("jcr:data", content1);
            session.getNode("/content1/__es/__content1/jcr:content").setProperty("jcr:description", "<html>This is a test word1</html>");

            root.addNode("content2", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content2").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content2/__es").addNode("__content2", JcrConstants.NT_FILE);
            session.getNode("/content2/__es/__content2").addNode("jcr:content", "nt:resource").addMixin("mix:title");
            session.getNode("/content2/__es/__content2/jcr:content").setProperty("jcr:data", content2);
            session.getNode("/content2/__es/__content2/jcr:content").setProperty("jcr:description", "<html>This is a test word2</html>");

            session.save();

            // Query parts...
            String language = "JCR-SQL2";
            String expression = "select * from [nt:base] where [jcr:description] LIKE '%test%'";
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(expression, language);
            QueryResult result = query.execute();

            NodeIterator ni = result.getNodes();
            while (ni.hasNext()) {
                Node n = ni.nextNode();
                log.debug("Result: " + n.getName() );
            }

            Assert.assertTrue( true );

            log.debug("[[ END TEST fullTextData ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }

    @Test
    public void loadData() {

        cleanTestsFolder();

        long global_start = System.currentTimeMillis();

        // Change this to increase the loop
        int MAX = 1;

        try {

            log.debug( "[[ START TEST loadData ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            Node root = session.getNode("/");
            root.addNode("loadtest", "nt:folder");
            session.save();


            for (int i=0; i<MAX; i++) {

                long doc_start = System.currentTimeMillis();

                InputStream pdf = getClass().getClassLoader().getResourceAsStream("/jbossportletbridge.pdf");
                InputStream jpg = getClass().getClassLoader().getResourceAsStream("/wcm-whiteboard.jpg");
                Binary _pdf = session.getValueFactory().createBinary(pdf);
                Binary _jpg = session.getValueFactory().createBinary(jpg);

                String str = "<html>This is a test word" + i + "</html>";
                Value content = session.getValueFactory().createValue( str );

                session.getNode("/loadtest").addNode("content" + i, JcrConstants.NT_FOLDER);
                session.getNode("/loadtest/content" + i).addNode("__es", JcrConstants.NT_FOLDER);
                session.getNode("/loadtest/content" + i + "/__es").addNode("__content" + i, JcrConstants.NT_FILE);
                session.getNode("/loadtest/content" + i + "/__es/__content" + i).addNode("jcr:content", "nt:resource").addMixin("mix:title");
                session.getNode("/loadtest/content" + i + "/__es/__content" + i + "/jcr:content").setProperty("jcr:data", content);
                session.getNode("/loadtest/content" + i + "/__es/__content" + i + "/jcr:content").setProperty("jcr:description", str);

                session.getNode("/loadtest").addNode("pdf" + i, JcrConstants.NT_FOLDER);
                session.getNode("/loadtest/pdf" + i).addNode("__en", JcrConstants.NT_FOLDER);
                session.getNode("/loadtest/pdf" + i + "/__en").addNode("__pdf" + i, JcrConstants.NT_FILE);
                session.getNode("/loadtest/pdf" + i + "/__en/__pdf" + i).addNode("jcr:content", "nt:resource").addMixin("mix:title");
                session.getNode("/loadtest/pdf" + i + "/__en/__pdf" + i + "/jcr:content").setProperty("jcr:data", _pdf);
                session.getNode("/loadtest/pdf" + i + "/__en/__pdf" + i + "/jcr:content").setProperty("jcr:description", "JCR Specification PDF");

                session.getNode("/loadtest").addNode("jpg" + i, JcrConstants.NT_FOLDER);
                session.getNode("/loadtest/jpg" + i).addNode("__en", JcrConstants.NT_FOLDER);
                session.getNode("/loadtest/jpg" + i + "/__en").addNode("__jpg" + i, JcrConstants.NT_FILE);
                session.getNode("/loadtest/jpg" + i + "/__en/__jpg" + i).addNode("jcr:content", "nt:resource").addMixin("mix:title");
                session.getNode("/loadtest/jpg" + i + "/__en/__jpg" + i + "/jcr:content").setProperty("jcr:data", _jpg);
                session.getNode("/loadtest/jpg" + i + "/__en/__jpg" + i + "/jcr:content").setProperty("jcr:description", "jpg sample image");

                session.save();

                long doc_stop = System.currentTimeMillis();

                log.debug( "WRITTING: content" + i + " Time: " + (doc_stop - doc_start) );

            }

            for (int i=0; i<MAX; i++) {

                long doc_start = System.currentTimeMillis();

                // Query parts...
                String language = "JCR-SQL2";
                String expression = "select * from [nt:base] where [jcr:description] LIKE '%test%'";
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(expression, language);
                QueryResult result = query.execute();

                NodeIterator ni = result.getNodes();
                while (ni.hasNext()) {
                    ni.nextNode();
                }

                long doc_stop = System.currentTimeMillis();

                log.debug( "QUERYING: " + i + ". Results: " + ni.getSize() + " Time: " + (doc_stop - doc_start));
            }

            long global_stop = System.currentTimeMillis();

            log.debug( "Global time: " + ((global_stop - global_start)/1000) + " s" );

            log.debug( "[[ END TEST loadData ]]" );

            Assert.assertTrue( true );



        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }

    @Test
    public void readResources() {
        try {
            log.debug( "File: " + getClass().getClassLoader().getResourceAsStream("/jbossportletbridge.pdf") );

            Assert.assertTrue( true );
        } catch (Exception e) {
            Assert.fail(e.toString());

        }
    }

    @Test
    public void urlSyntax() {

        cleanTestsFolder();

        try {

            log.debug( "[[ START TEST urlSyntax ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            Value content1 = session.getValueFactory().createValue("<html>This is a test word1</html>");
            Value content2 = session.getValueFactory().createValue("<html>This is a test word2</html>");

            Node root = session.getNode("/");

            root.addNode("content1", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content1").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content1/__es").addNode("__content1", JcrConstants.NT_FILE);
            session.getNode("/content1/__es/__content1").addNode("jcr:content", "nt:resource").addMixin("mix:title");
            session.getNode("/content1/__es/__content1/jcr:content").setProperty("jcr:data", content1);
            session.getNode("/content1/__es/__content1/jcr:content").setProperty("jcr:description", "<html>This is a test word1</html>");

            root.addNode("content2", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content2").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content2/__es").addNode("__content2", JcrConstants.NT_FILE);
            session.getNode("/content2/__es/__content2").addNode("jcr:content", "nt:resource").addMixin("mix:title");
            session.getNode("/content2/__es/__content2/jcr:content").setProperty("jcr:data", content2);
            session.getNode("/content2/__es/__content2/jcr:content").setProperty("jcr:description", "<html>This is a test word2</html>");

            session.save();

            // Query parts...
            log.debug( "/ . Url test: " + session.nodeExists("/") );
            log.debug( "/content1 . Url test: " + session.nodeExists("/content1") );
            log.debug( "/content1/ . Url test: " + session.nodeExists("/content1/") );
            log.debug( "/content1/__es . Url test: " + session.nodeExists("/content1/__es") );

            Assert.assertTrue( true );

            log.debug("[[ END TEST urlSyntax ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        cleanTestsFolder();
    }



}