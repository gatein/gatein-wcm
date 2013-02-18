package org.gatein.wcm.tests;

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
public class Test003_BasicJCROperationsTest {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.tests");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-tests-003.war")
                .addAsResource(new File("src/main/resources/cmis-spec-v1.0.pdf"))
                .addAsResource(new File("src/main/resources/wcm-whiteboard.jpg"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @SuppressWarnings("deprecation")
    @Test
    public void query_root() {

        try {

            log.info( "[[ START TEST query_root ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String sQuery = "/jcr:root";

            Query query = queryManager.createQuery( sQuery, Query.XPATH );
            QueryResult result = query.execute();

            if (result.getNodes().getSize() == 1) {
                Node n = result.getNodes().nextNode();
                log.info("Path: " + n.getPath() + " Node: " + n.getIdentifier());
            } else {
                log.info( "result.getNodes().getSize(): " + result.getNodes().getSize() );
            }

            Assert.assertTrue( true );

            log.info("[[ END TEST query_root ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }
    }

    public void clean_tests_folder() {

        try {

            log.info( "[[ START CLEANING ]]" );

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

            log.info("[[ STOP CLEANING ]]");

        } catch (Exception e) {

            log.error("Cleaning: " + e.getMessage());

        }

    }

    @SuppressWarnings("deprecation")
    @Test
    public void add_folder() {

        clean_tests_folder();

        try {

            log.info( "[[ START TEST add_folder ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String sQuery = "/jcr:root";

            Query query = queryManager.createQuery( sQuery, Query.XPATH );
            QueryResult result = query.execute();

            if (result.getNodes().getSize() == 1) {
                Node n = result.getNodes().nextNode();
                n.addNode("tests", JcrConstants.NT_FOLDER);
                log.info("Path: " + n.getPath() + " Node: " + n.getIdentifier());
                log.info("Check: " + session.nodeExists("/tests") );
                session.save();

            } else {
                log.info( "result.getNodes().getSize(): " + result.getNodes().getSize() );
            }

            Assert.assertTrue( true );

            log.info("[[ END TEST add_folder ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }

    @Test
    public void complex_folders() {

        clean_tests_folder();

        try {

            log.info( "[[ START TEST complex_folders ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.info("Test / " + session.nodeExists("/"));

            Value content = session.getValueFactory().createValue("<html>This is a test content</html>");

            Node root = session.getNode("/");

            root.addNode("content1", JcrConstants.NT_FOLDER).getName();
            session.getNode("/content1").addNode("__es", JcrConstants.NT_FOLDER);
            session.getNode("/content1/__es").addNode("__content1", JcrConstants.NT_FILE);
            session.getNode("/content1/__es/__content1").addNode("jcr:content", "nt:resource").setProperty("jcr:data", content);

            session.save();

            Assert.assertTrue( true );

            log.info("[[ END TEST complex_folders ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }

    @Test
    public void basic_versioning() {

        clean_tests_folder();

        try {

            log.info( "[[ START TEST basic_versioning ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.info("Test / " + session.nodeExists("/"));

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

            log.info("Number of versions: " + h.getAllLinearVersions().getSize() );

            vm.checkout( "/content1/__es/__content1" );
            vm.checkin( "/content1/__es/__content1" );

            log.info("Number of versions: " + h.getAllLinearVersions().getSize() );

            Assert.assertTrue( true );

            log.info("[[ END TEST basic_versioning ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }

    @Test
    public void basic_properties() {

        clean_tests_folder();

        try {

            log.info( "[[ START TEST basic_properties ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.info("Test / " + session.nodeExists("/"));

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

            log.info("Number of versions: " + h.getAllLinearVersions().getSize() );

            vm.checkout( "/content1/__es/__content1" );
            vm.checkin( "/content1/__es/__content1" );

            log.info("Number of versions: " + h.getAllLinearVersions().getSize() );

            Assert.assertTrue( true );

            Node n = session.getNode("/content1");
            n.addMixin("mix:created");
            n.addMixin("mix:lastModified");

            session.save();

            n = session.getNode("/content1");
            log.info("Created: " + n.getProperty("jcr:created").getString() );
            log.info("CreatedBy: " + n.getProperty("jcr:createdBy").getString() );
            log.info("LastModified: " + n.getProperty("jcr:lastModified").getString() );
            log.info("LastModifiedBy: " + n.getProperty("jcr:lastModifiedBy").getString() );

            VersionIterator vi = h.getAllLinearVersions();
            while (vi.hasNext()) {
                Version v = vi.nextVersion();
                log.info("V: " + v.getName());
            }

            log.info("[[ END TEST basic_properties ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }

    @Test
    public void check_acl() {

        clean_tests_folder();

        try {

            log.info( "[[ START TEST check_acl ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            log.info("Test / " + session.nodeExists("/"));

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
                log.info("ACE: " + ace);
            }

            Assert.assertTrue( true );

            log.info("[[ END TEST check_acl ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }

    @Test
    public void full_text_data() {

        clean_tests_folder();

        try {

            log.info( "[[ START TEST full_text_data ]]" );

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
                log.info("Result: " + n.getName() );
            }

            Assert.assertTrue( true );

            log.info("[[ END TEST full_text_data ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }

    @Test
    public void load_data() {

        clean_tests_folder();

        long global_start = System.currentTimeMillis();

        // Change this to increase the loop
        int MAX = 1;

        try {

            log.info( "[[ START TEST full_text_data ]]" );

            SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

            javax.jcr.Session session = repository.login(credentials, "default");

            Node root = session.getNode("/");
            root.addNode("loadtest", "nt:folder");
            session.save();


            for (int i=0; i<MAX; i++) {

                long doc_start = System.currentTimeMillis();

                InputStream pdf = getClass().getClassLoader().getResourceAsStream("/cmis-spec-v1.0.pdf");
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

                log.info( "WRITTING: content" + i + " Time: " + (doc_stop - doc_start) );

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

                log.info( "QUERYING: " + i + ". Results: " + ni.getSize() + " Time: " + (doc_stop - doc_start));
            }

            long global_stop = System.currentTimeMillis();

            log.info( "Global time: " + ((global_stop - global_start)/1000) + " s" );

            log.info( "[[ END TEST full_text_data ]]" );

            Assert.assertTrue( true );



        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }

    @Test
    public void read_resources() {
        try {
            log.info( "File: " + getClass().getClassLoader().getResourceAsStream("/cmis-spec-v1.0.pdf") );

            Assert.assertTrue( true );
        } catch (Exception e) {
            Assert.fail(e.toString());

        }
    }

    @Test
    public void url_syntax() {

        clean_tests_folder();

        try {

            log.info( "[[ START TEST url_syntax ]]" );

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
            log.info( "/ . Url test: " + session.nodeExists("/") );
            log.info( "/content1 . Url test: " + session.nodeExists("/content1") );
            log.info( "/content1/ . Url test: " + session.nodeExists("/content1/") );
            log.info( "/content1//__es . Url test: " + session.nodeExists("/content1//__es") );

            Assert.assertTrue( true );

            log.info("[[ END TEST url_syntax ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }

        clean_tests_folder();
    }



}