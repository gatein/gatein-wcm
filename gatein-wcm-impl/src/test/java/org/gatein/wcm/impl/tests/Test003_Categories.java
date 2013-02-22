package org.gatein.wcm.impl.tests;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.gatein.wcm.api.model.content.Content;
import org.gatein.wcm.api.model.metadata.Category;
import org.gatein.wcm.api.services.ContentService;
import org.gatein.wcm.api.services.RepositoryService;
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
public class Test003_Categories {

    private static final Logger log = Logger.getLogger("org.gatein.wcm.impl.tests.test003");

    @Deployment
    public static Archive<?> createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "gatein-wcm-impl-test003.war")
                .addAsResource(new File("src/test/resources/cmis-spec-v1.0.pdf"))
                .addAsResource(new File("src/test/resources/wcm-whiteboard.jpg"))
                .addAsResource(new File("src/test/resources/jcr-2.0_specification.pdf"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setManifest(new File("src/main/webapp/META-INF/MANIFEST.MF"));

    }

    @Resource(mappedName = "java:jboss/gatein-wcm")
    RepositoryService repos;

    @Test
    public void create_categories() {

        log.info("[[ START TEST  create_categories ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            Category c1 = cs.createCategory("sports1", "en", "Sports", "/");
            Category c2 = cs.createCategory("sports1", "es", "Deportes", "/");
            Category c3 = cs.createCategory("sports1", "fr", "Sportif", "/");
            Category c4 = cs.createCategory("news1", "en", "News", "/");
            Category c5 = cs.createCategory("news1", "es", "Noticias", "/");
            Category c6 = cs.createCategory("news1", "fr", "Nouvelles", "/");
            Category c7 = cs.createCategory("basket", "en", "Basketball", "/sports1");
            Category c8 = cs.createCategory("basket", "es", "Baloncesto", "/sports1");
            Category c9 = cs.createCategory("basket", "fr", "Baloncesto", "/sports1");
            Category c10 = cs.createCategory("football", "en", "Soccer", "/sports1");
            Category c11 = cs.createCategory("football", "es", "Futbol", "/sports1");
            Category c12 = cs.createCategory("football", "fr", "Football", "/sports1");
            Category c13 = cs.createCategory("national", "en", "National", "/news1");
            Category c14 = cs.createCategory("national", "es", "Nacional", "/news1");
            Category c15 = cs.createCategory("national", "fr", "National", "/news1");
            Category c16 = cs.createCategory("international", "en", "International", "/news1");
            Category c17 = cs.createCategory("international", "es", "Internacional", "/news1");
            Category c18 = cs.createCategory("international", "fr", "International", "/news1");

            log.info(c1);
            log.info(c2);
            log.info(c3);
            log.info(c4);
            log.info(c5);
            log.info(c6);
            log.info(c7);
            log.info(c8);
            log.info(c9);
            log.info(c10);
            log.info(c11);
            log.info(c12);
            log.info(c13);
            log.info(c14);
            log.info(c15);
            log.info(c16);
            log.info(c17);
            log.info(c18);

        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  create_categories ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void update_categories() {

        log.info("[[ START TEST  update_categories ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            cs.createCategory("sports2", "en", "Sports", "/");
            cs.createCategory("sports2", "es", "Deportes", "/");
            cs.createCategory("sports2", "fr", "Sportif", "/");
            cs.createCategory("news2", "en", "News", "/");
            cs.createCategory("news2", "es", "Noticias", "/");
            cs.createCategory("news2", "fr", "Nouvelles", "/");
            cs.createCategory("basket2", "en", "Basketball", "/sports2");
            cs.createCategory("basket2", "es", "Baloncesto", "/sports2");
            cs.createCategory("basket2", "fr", "Baloncesto", "/sports2");
            cs.createCategory("football2", "en", "Soccer", "/sports2");
            cs.createCategory("football2", "es", "Futbol", "/sports2");
            cs.createCategory("football2", "fr", "Football", "/sports2");
            cs.createCategory("national2", "en", "National", "/news2");
            cs.createCategory("national2", "es", "Nacional", "/news2");
            cs.createCategory("national2", "fr", "National", "/news2");
            cs.createCategory("international2", "en", "International", "/news2");
            cs.createCategory("international2", "es", "Internacional", "/news2");
            cs.createCategory("international2", "fr", "International", "/news2");

            Category c1 = cs.updateCategoryDescription("/news2/national2", "es", "Noticias en español");
            log.info(c1);

            Category c2 = cs.updateCategoryLocation("/sports2", "es", "/news2");
            log.info(c2);
        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  update_categories ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void get_categories() {

        log.info("[[ START TEST  get_categories ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");

            // Cleaning a old test
            try {
                cs.deleteCategory("/sports3");
                cs.deleteCategory("/news3");
            } catch (Exception ignored) {
            }

            cs.createCategory("sports3", "en", "Sports", "/");
            cs.createCategory("sports3", "es", "Deportes", "/");
            cs.createCategory("sports3", "fr", "Sportif", "/");
            cs.createCategory("news3", "en", "News", "/");
            cs.createCategory("news3", "es", "Noticias", "/");
            cs.createCategory("news3", "fr", "Nouvelles", "/");
            cs.createCategory("basket3", "en", "Basketball", "/sports3");
            cs.createCategory("basket3", "es", "Baloncesto", "/sports3");
            cs.createCategory("basket3", "fr", "Baloncesto", "/sports3");
            cs.createCategory("football3", "en", "Soccer", "/sports3");
            cs.createCategory("football3", "es", "Futbol", "/sports3");
            cs.createCategory("football3", "fr", "Football", "/sports3");
            cs.createCategory("national3", "en", "National", "/news3");
            cs.createCategory("national3", "es", "Nacional", "/news3");
            cs.createCategory("national3", "fr", "National", "/news3");
            cs.createCategory("international3", "en", "International", "/news3");
            cs.createCategory("international3", "es", "Internacional", "/news3");
            cs.createCategory("international3", "fr", "International", "/news3");

            List<Category> categories = cs.getCategories("/", "es");
            for (Category cy : categories) {
                log.info( cy );
            }
            categories = cs.getCategories("/sports3", "en");
            for (Category cy : categories) {
                log.info( cy );
            }
            categories = cs.getCategories("/news3", "en");
            for (Category cy : categories) {
                log.info( cy );
            }

        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  get_categories ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void add_categories() {

        log.info("[[ START TEST  add_categories ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            cs.createCategory("sports4", "en", "Sports", "/");
            cs.createCategory("sports4", "es", "Deportes", "/");
            cs.createCategory("sports4", "fr", "Sportif", "/");
            cs.createCategory("news4", "en", "News", "/");
            cs.createCategory("news4", "es", "Noticias", "/");
            cs.createCategory("news4", "fr", "Nouvelles", "/");
            cs.createCategory("basket4", "en", "Basketball", "/sports4");
            cs.createCategory("basket4", "es", "Baloncesto", "/sports4");
            cs.createCategory("basket4", "fr", "Baloncesto", "/sports4");
            cs.createCategory("football4", "en", "Soccer", "/sports4");
            cs.createCategory("football4", "es", "Futbol", "/sports4");
            cs.createCategory("football4", "fr", "Football", "/sports4");
            cs.createCategory("national4", "en", "National", "/news4");
            cs.createCategory("national4", "es", "Nacional", "/news4");
            cs.createCategory("national4", "fr", "National", "/news4");
            cs.createCategory("international4", "en", "International", "/news4");
            cs.createCategory("international4", "es", "Internacional", "/news4");
            cs.createCategory("international4", "fr", "International", "/news4");

            Category c1 = cs.updateCategoryDescription("/news4/national4", "es", "Noticias en español");
            log.info(c1);
            Category c2 = cs.updateCategoryLocation("/sports4", "es", "/news4");
            log.info(c2);

            cs.createTextContent("my noticia", "es", "/", "Esta es una noticia de ejemplo", "UTF8");

            cs.addContentCategory("/my noticia","/news4");

        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  add_categories ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void delete_categories() {

        log.info("[[ START TEST  delete_categories ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");
            cs.createCategory("sports5", "en", "Sports", "/");
            cs.createCategory("sports5", "es", "Deportes", "/");
            cs.createCategory("sports5", "fr", "Sportif", "/");
            cs.createCategory("news5", "en", "News", "/");
            cs.createCategory("news5", "es", "Noticias", "/");
            cs.createCategory("news5", "fr", "Nouvelles", "/");
            cs.createCategory("basket5", "en", "Basketball", "/sports5");
            cs.createCategory("basket5", "es", "Baloncesto", "/sports5");
            cs.createCategory("basket5", "fr", "Baloncesto", "/sports5");
            cs.createCategory("football5", "en", "Soccer", "/sports5");
            cs.createCategory("football5", "es", "Futbol", "/sports5");
            cs.createCategory("football5", "fr", "Football", "/sports5");
            cs.createCategory("national5", "en", "National", "/news5");
            cs.createCategory("national5", "es", "Nacional", "/news5");
            cs.createCategory("national5", "fr", "National", "/news5");
            cs.createCategory("international5", "en", "International", "/news5");
            cs.createCategory("international5", "es", "Internacional", "/news5");
            cs.createCategory("international5", "fr", "International", "/news5");

            Category c = cs.deleteCategory("/sports5", "fr");
            log.info(c);
            cs.deleteCategory("/sports5/basket5");

        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  delete_categories ]]");
        Assert.assertTrue( true );
    }

    @Test
    public void query_categories() {
        log.info("[[ START TEST  query_categories ]]");
        try {
            ContentService cs = repos.createContentSession("sample", "default", "admin", "admin");

            try {
                cs.deleteContent("/site1");
                cs.deleteContent("/site2");
                cs.deleteContent("/site3");
                cs.deleteCategory("/news");
                cs.deleteCategory("/countries");
            } catch (Exception ignored) {
            }

            /*
             * Sites
             *      In both locales {es, en}
             *
             *  /site1/new1
             *  /site1/new2
             *  /site1/new3
             *  /site1/new4
             *  /site2/new5
             *  /site2/new6
             *  /site2/new7
             *  /site2/new8
             *  /site3/new9
             *  /site3/new10
             *
             *  Categories
             *  /news/national --> new1, new5
             *  /news/sports --> new2, new6
             *  /countries/spain --> new3, new7
             *  /countries/france --> new4, new8
             *
             *  Queries:
             *  /news --> new1, new2, new5, new6 :: filtered by location "/"
             *  /countries -> new3, new4, new7, new8 :: filtered by location "/"
             *
             *  /news --> new1, new2 :: filtered by location "/site1"
             *  /news --> new5, new6 :: filtered by location "/site2"
             *
             *
             */

            // Creating test content
            Content c = null;
            for (int i=1; i<=3; i++) {
                c = cs.createFolder("site" + i, "/");
                log.info(c);
            }
            int news=1;
            for (int site=1; site<=3; site++) {
                for (int i=1; i<=4; i++) {
                    c = cs.createTextContent("new" + news, "es", "/site" + site, "Esta es la noticia " + news, "UTF8");
                    log.info(c);
                    c = cs.createTextContent("new" + news, "en", "/site" + site, "This is the news " + news, "UTF8");
                    log.info(c);
                    news++;
                }
            }

            // Creating categories
            Category cat = null;
            cat = cs.createCategory("news", "es", "Noticias", "/");
            log.info(cat);
            cat = cs.createCategory("news", "en", "News", "/");
            log.info(cat);
            cat = cs.createCategory("countries", "es", "Paises", "/");
            log.info(cat);
            cat = cs.createCategory("countries", "en", "Countries", "/");
            log.info(cat);
            cat = cs.createCategory("national", "es", "Nacional", "/news");
            log.info(cat);
            cat = cs.createCategory("national", "en", "National", "/news");
            log.info(cat);
            cat = cs.createCategory("sports", "es", "Deportes", "/news");
            log.info(cat);
            cat = cs.createCategory("sports", "en", "Sports", "/news");
            log.info(cat);
            cat = cs.createCategory("spain", "es", "España", "/countries");
            log.info(cat);
            cat = cs.createCategory("spain", "en", "Spain", "/countries");
            log.info(cat);
            cat = cs.createCategory("france", "es", "Francia", "/countries");
            log.info(cat);
            cat = cs.createCategory("france", "en", "France", "/countries");
            log.info(cat);

            // Assing categories in content

            cs.addContentCategory("/site1/new1", "/news/national");
            cs.addContentCategory("/site2/new5", "/news/national");
            cs.addContentCategory("/site1/new2", "/news/sports");
            cs.addContentCategory("/site2/new6", "/news/sports");

            cs.addContentCategory("/site1/new3", "/countries/spain");
            cs.addContentCategory("/site2/new7", "/countries/spain");
            cs.addContentCategory("/site1/new4", "/countries/france");
            cs.addContentCategory("/site2/new8", "/countries/france");


           /*  Categories
            *  /news/national --> new1, new5
            *  /news/sports --> new2, new6
            *  /countries/spain --> new3, new7
            *  /countries/france --> new4, new8
            *
            *  Queries:
            *  /news --> new1, new2, new5, new6 :: filtered by location "/"
            *  /countries -> new3, new4, new7, new8 :: filtered by location "/"
            *
            *  /news --> new1, new2 :: filtered by location "/site1"
            *  /news --> new5, new6 :: filtered by location "/site2"
            *
            *
            */
            List<Category> cats;
            List<Content> result;

            cats = cs.getCategories("/news", "es");
            result = cs.getContent(cats, "/site1", "es");
            for (Content r : result)
                log.info(r);

            result = cs.getContent(cats, "/site2", "es");
            for (Content r : result)
                log.info(r);

            result = cs.getContent(cats, "/", "es");
            for (Content r : result)
                log.info(r);

            cats = cs.getCategories("/countries", "es");
            result = cs.getContent(cats, "/", "es");
            for (Content r : result)
                log.info(r);


        }  catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue( false );
        }
        log.info("[[ STOP TEST  query_categories ]]");
        Assert.assertTrue( true );
    }

}