package org.gatein.wcm.tests.performance;

import org.gatein.wcm.api.services.ContentService;
import org.jboss.logging.Logger;

public class WcmWorker implements Runnable {
    private static final Logger log = Logger.getLogger("org.gatein.wcm.tests.performance");
    private final int nTest;

    ContentService cs;

    WcmWorker(int nTest, ContentService cs) {
        this.nTest = nTest;
        this.cs = cs;
    }

    @Override
    public void run() {

        try {
            log.info( "Test #" + nTest );
            cs.createTextContent("test" + nTest, "es", "/", "<h1>Primer test...</h1><p>Este es un párrafo.</p>", "UTF8");
            // cs.createTextContent("test" + nTest, "en", "/", "<h1>First test...</h1><p>This is a paragraph</p>", "UTF8");
            // cs.createTextContent("test" + nTest, "fr", "/", "<h1>First test...</h1><p>Ceci est un paragraphe</p>", "UTF8");
            // cs.createTextContent("test" + nTest, "de", "/", "<h1>Erster Test...</h1><p>Dies ist ein Absatz</p>", "UTF8");
/*
            List<String> locales = cs.getContentLocales("/test" + nTest);
            if ( (!locales.contains("es")) ||
                 (!locales.contains("en")) ||
                 (!locales.contains("fr")) ||
                 (!locales.contains("de"))
               ) {
                throw new Exception("Not locales in content");
            }
*/
            // Cleaning test
            // cs.deleteContent("/test" + nTest);
            cs.closeSession();
        } catch (Exception e) {
            log.error("Test #" + nTest + " Failed " + e.getMessage());
        }
   }

}
