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

@RunWith(Arquillian.class)
public class BasicModeShapeTest {

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl.war").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:/jcr")
    Repositories repositories;

    @Resource(mappedName = "/jcr/sample")
    Repository repository;

    @Test
    public void accesing_repository() {

        try {

            System.out.println("[[ START TEST accesing_repository ]]");

            if (repositories != null) {
                for (String repo : repositories.getRepositoryNames()) {
                    System.out.println("[[ REPO: " + repo + " ]]");
                }
            }

            if (repository != null) {

                javax.jcr.Session session = repository.login("default");
                System.out.println("[[ Repo /jcr/sample.... RootNode... ");
                System.out.println(session.getRootNode().toString());

                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
            System.out.println("[[ END TEST accesing_repository ]]");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }
    }

    @Test
    public void simple_credentials() {

        try {

            System.out.println("[[ START TEST  simple_credentials ]]");

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
            System.out.println("[[ END TEST  simple_credentials ]]");

        } catch (LoginException e) {

            System.out.println(" [[ Expected: " + e.toString() + " ]] ");
            System.out.println(" [[ END TEST simple_credentials ]] ");

        } catch (Exception e) {

            Assert.fail(e.toString());

        }
    }

}