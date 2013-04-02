package org.gatein.wcm.jcr.tests;

import java.io.File;

import javax.annotation.Resource;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.api.Repositories;
import org.jboss.logging.Logger;

@RunWith(Arquillian.class)
public class BasicModeShapeTest {

    private static final Logger log = Logger.getLogger(BasicModeShapeTest.class);

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-jrc-tests.war").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:/jcr")
    Repositories repositories;

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void accesingRepository() {

        try {
            log.debug("[[ START TEST accesingRepository ]]");
            if (repositories != null) {
                for (String repo : repositories.getRepositoryNames()) {
                    log.debug("[[ REPO: " + repo + " ]]");
                }
            }
            if (repository != null) {

                javax.jcr.Session session = repository.login("default");
                log.debug("[[ Repo /jcr/sample.... RootNode... ");
                log.debug(session.getRootNode().toString());

                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
            log.debug("[[ END TEST accesingRepository ]]");
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void simpleCredentials() {

        try {
            log.debug("[[ START TEST  simpleCredentials ]]");
            if (repositories != null) {
                for (String repo : repositories.getRepositoryNames()) {
                    log.debug("[[ REPO: " + repo + " ]]");
                }
            }
            if (repository != null) {
                SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
                javax.jcr.Session session = repository.login(credentials, "default");
                log.debug("[[ Repo /jcr/sample.... RootNode... ");
                log.debug(session.getRootNode().toString());
                session.logout();
                credentials = new SimpleCredentials("admin", "bad".toCharArray());
                session = repository.login(credentials, "default");
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
            log.debug("[[ END TEST  simpleCredentials ]]");
        } catch (LoginException e) {
            log.debug(" [[ Expected: " + e.toString() + " ]] ");
            log.debug(" [[ END TEST simpleCredentials ]] ");
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

}