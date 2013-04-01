package org.gatein.wcm.tests.performance.aux;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.gatein.wcm.tests.performance.WcmResources;
import org.junit.Test;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;

public class CreateResourcesTest {

    public static final int NDOCUMENTS = 100;
    public static final int NPARAGRAPHS = 1000;
    public static final String RESOURCE = "src/test/resources/wcm-whiteboard.jpg";

    @Test
    public void createPdf() throws DocumentException, IOException {

        // Delete folder
        File testFolder = new File("target/generatedPdf");
        if (testFolder.exists()) testFolder.delete();
        testFolder.mkdir();

        DecimalFormat df = new DecimalFormat("0");

        Image img = Image.getInstance(RESOURCE);

        for (int i=0; i<NDOCUMENTS; i++) {
            String name = "target/generatedPdf/testpdf" + df.format(i) + ".pdf";
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(name));
            doc.open();
            for (int j=0; j<NPARAGRAPHS; j++) {
                doc.add(new Paragraph(WcmResources.HTML_ES));
                doc.add(img);
                doc.add(new Paragraph(WcmResources.HTML_EN));
                doc.add(img);
                doc.add(new Paragraph(WcmResources.HTML_FR));
                doc.add(img);
                doc.add(new Paragraph(WcmResources.HTML_DE));
                doc.add(new Paragraph("\n"));
            }
            doc.close();
            System.out.println("Generated " + i + " / " + NDOCUMENTS + ": " + name);
        }

    }

}
