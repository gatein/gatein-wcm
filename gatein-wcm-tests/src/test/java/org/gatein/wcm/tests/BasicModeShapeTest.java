package org.gatein.wcm.tests;

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

    private static final Logger log = Logger.getLogger("org.gatein.wcm.tests");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-test.war").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:/jcr")
    Repositories repositories;

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void accesing_repository() {

        try {

            log.info("[[ START TEST accesing_repository ]]");

            if (repositories != null) {
                for (String repo : repositories.getRepositoryNames()) {
                    log.info("[[ REPO: " + repo + " ]]");
                }
            }

            if (repository != null) {

                javax.jcr.Session session = repository.login("default");
                log.info("[[ Repo /jcr/sample.... RootNode... ");
                log.info(session.getRootNode().toString());

                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
            log.info("[[ END TEST accesing_repository ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }
    }

    @Test
    public void simple_credentials() {

        try {

            log.info("[[ START TEST  simple_credentials ]]");

            if (repositories != null) {
                for (String repo : repositories.getRepositoryNames()) {
                    System.out.println("[[ REPO: " + repo + " ]]");
                }
            }

            if (repository != null) {

                SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());

                javax.jcr.Session session = repository.login(credentials, "default");
                System.out.println("[[ Repo /jcr/sample.... RootNode... ");
                System.out.println(session.getRootNode().toString());

                session.logout();

                credentials = new SimpleCredentials("admin", "bad".toCharArray());

                session = repository.login(credentials, "default");

                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
            log.info("[[ END TEST  simple_credentials ]]");

        } catch (LoginException e) {

            log.info(" [[ Expected: " + e.toString() + " ]] ");
            log.info(" [[ END TEST simple_credentials ]] ");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }
    }

}