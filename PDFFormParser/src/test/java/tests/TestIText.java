package tests;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import fr.jp.pdf.FormParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * Date: 10/10/16
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestIText {
    public String VERSION = "16.10.13.1";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestIText.class);
    private static final String TEST_PROPERTIES_FILE = "Test.properties";
    private static final String CFG_KEY__TEST_PDF = "pdf.processing.file";
    private static final String CFG_KEY__TEST_FILL_VALUES = "fill.values";

    private Properties props;


    public TestIText() {
        props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(TEST_PROPERTIES_FILE));
            LOGGER.debug("test properties loaded succ");
        } catch (IOException e) {
            LOGGER.error("Problem init Tester instance:", e);
        }
    }

    @Before
    @After
    public void addLogSeparator() {
        LOGGER.info("============= Test v" + VERSION + " ===========================");
    }


    @Test
    public void test_getProp() {
        LOGGER.debug("Start");
        String file_name = props.getProperty(CFG_KEY__TEST_PDF);
        LOGGER.info(CFG_KEY__TEST_PDF + " = [{}]", file_name);
        assertNotNull("Problem create a new RedirectChecker instance", file_name);
        LOGGER.debug("Finish");
    }

    @Test
    public void test_PDDocumentFormFill() {
        LOGGER.debug("Start");
        String file_name = props.getProperty(CFG_KEY__TEST_PDF);

        LOGGER.debug("Start for [{}]", file_name);
        LOGGER.debug("Try open PDF file: [{}]", file_name);

        URL fileURL = super.getClass().getClassLoader().getResource(file_name);
        assertNotNull("NOT FOUND File \"" + file_name + "\"", fileURL);

        PdfReader pdfReader = null;
        PdfStamper pdfStamper = null;
        try {
            Hashtable<String, String> fill_values = FormParser.prepareData( props.getProperty(CFG_KEY__TEST_FILL_VALUES));

            String src = fileURL.getFile();
            LOGGER.debug("Try open PDF file: [{}]", src);
            pdfReader = new PdfReader(src);
            assertNotNull("Problem iText with load PdfReader!", pdfReader);
            LOGGER.debug("PdfReader open succ");

            String dest = "_" + file_name;
            pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(dest));
            assertNotNull("Problem iText with load PdfStamper!", pdfStamper);

            AcroFields acroFields = pdfStamper.getAcroFields();
            LOGGER.debug("AcroFields form getted: [{}]", acroFields);
            assertNotNull("AcroFields not exist!", acroFields);
            acroFields.setGenerateAppearances(true);

            Map<String, AcroFields.Item> fields = acroFields.getFields();
            int i = 0;
            for (String field_key : fill_values.keySet()) {
                AcroFields.Item field = fields.get(field_key);
                if (field != null) {
                    acroFields.setField(field_key, fill_values.get(field_key));
                    LOGGER.info("{}. set [Page:{}, tabOrder:{}, {}={}] ", ++i, field.getPage(0), field.getTabOrder(0), field_key, fill_values.get(field_key) );
                } else {
                    throw new DocumentException("NOT FOUND the Field Key: [" + field_key + "]" );
                }

            }
        } catch (IOException | DocumentException | ParseException e) {
            LOGGER.error("Problem: ", e);
        } finally {
            if (pdfStamper != null)
                try {
                    pdfStamper.close();
                } catch (DocumentException | IOException e) {
                    LOGGER.error("Problem: ", e);
                }
            LOGGER.trace("PdfStamper closed");
            if (pdfReader != null)
                pdfReader.close();
            LOGGER.trace("PdfReader closed");
        }

        LOGGER.debug("Finish processing PDF doc [{}]", file_name);
    }


    @Test
    public void test_PDDocumentFormList() {
        LOGGER.debug("Start");
        String file_name = props.getProperty(CFG_KEY__TEST_PDF);

        LOGGER.debug("Start for [{}]", file_name);
        LOGGER.debug("Try open PDF file: [{}]", file_name);

        URL fileURL = super.getClass().getClassLoader().getResource(file_name);
        assertNotNull("NOT FOUND File \"" + file_name + "\"", fileURL);

        PdfReader pdfReader = null;
        try {
            String src = fileURL.getFile();
            LOGGER.debug("Try open PDF file: [{}]", src);
            pdfReader = new PdfReader(src);
            assertNotNull("Problem iText with load PdfReader!", pdfReader);
            LOGGER.debug("PdfReader open succ");

            AcroFields acroFields = pdfReader.getAcroFields();
            LOGGER.debug("AcroFields form getted: [{}]", acroFields);
            assertNotNull("AcroFields not exist!", acroFields);

            Map<String, AcroFields.Item> fields = acroFields.getFields();
            LOGGER.debug("Field count: [{}]", fields.size());
            int i = 0;
            for (String field_key : fields.keySet()) {
                AcroFields.Item field = fields.get(field_key);
                LOGGER.info("{}. [Page:{}, tabOrder:{}, Field.size:{}, Key:{}] ", ++i, field.getPage(0), field.getTabOrder(0), field.size(), field_key );
            }
        } catch (IOException e) {
            LOGGER.error("Problem: ", e);
        } finally {
            if (pdfReader != null)
                pdfReader.close();
            LOGGER.trace("PdfReader closed");
        }
        LOGGER.debug("Finish processing PDF doc [{}]", file_name);
    }


    @Test
    public void test_PDDocumentFormListDetails() {
        LOGGER.debug("Start");
        String file_name = props.getProperty(CFG_KEY__TEST_PDF);

        LOGGER.debug("Start for [{}]", file_name);
        LOGGER.debug("Try open PDF file: [{}]", file_name);

        URL fileURL = super.getClass().getClassLoader().getResource(file_name);
        assertNotNull("NOT FOUND File \"" + file_name + "\"", fileURL);

        PdfReader pdfReader = null;
        try {
            String src = fileURL.getFile();
            LOGGER.debug("Try open PDF file: [{}]", src);
            pdfReader = new PdfReader(src);
            assertNotNull("Problem iText with load PdfReader!", pdfReader);
            LOGGER.debug("PdfReader open succ");

            AcroFields acroFields = pdfReader.getAcroFields();
            LOGGER.debug("AcroFields form getted: [{}]", acroFields);
            assertNotNull("AcroFields not exist!", acroFields);

            Map<String, AcroFields.Item> fields = acroFields.getFields();
            LOGGER.debug("Field count: [{}]", fields.size());
            int i = 0;
            for (String field_key : fields.keySet()) {
                AcroFields.Item field = fields.get(field_key);
                LOGGER.info("{}. [Page:{}, tabOrder:{}, Field.size:{}, Key:{}] ", ++i, field.getPage(0), field.getTabOrder(0), field.size(), field_key );
                //
                for (int j = 0; j < field.size(); j++) {
                    PdfDictionary value = field.getValue(j);
                    for (PdfName key : value.getKeys()) {
                        LOGGER.debug("   {}:[{}]", key, value.get(key));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Problem: ", e);
        } finally {
            if (pdfReader != null)
                pdfReader.close();
            LOGGER.trace("PdfReader closed");
        }
        LOGGER.debug("Finish processing PDF doc [{}]", file_name);
    }




}
