package tests;

import com.cedarsoftware.util.io.JsonWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import fr.jp.data.adapters.GravityFormDataSource;
import fr.jp.pdf.FormParser;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * Date: 10/10/16
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestIText {
    public String VERSION = "16.10.13.1";
    // To avoid conflicting multiple loggers, it is necessary to specify directly
    //private static final Logger LOGGER = LoggerFactory.getLogger(TestIText.class);
    private static final Logger LOGGER = new Log4jLoggerFactory().getLogger(TestIText.class.getName());

    private static final String TEST_PROPERTIES_FILE = "Test.properties";
    private static final String CFG_KEY__TEST_PDF = "pdf.processing.file";
    private static final String CFG_KEY__TEST_FILL_VALUES = "fill.values";

    private static final String CFG_KEY__TEST_URL = "datasource.url";
    private static final String CFG_KEY__TEST_GRAVITY_API_KEY = "gravity.api_key";
    private static final String CFG_KEY__TEST_GRAVITY_ROUTE = "gravity.route";
    private static final String CFG_KEY__TEST_GRAVITY_PRIVATE_KEY = "gravity.private_key";

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
                List<String> appearanceStates = Arrays.asList( acroFields.getAppearanceStates(field_key));
                LOGGER.info("   appearanceStates:{}", appearanceStates);
                //
                if (field_key.equals("Check Box15")) {
                    LOGGER.trace("Check Box15 found");
                }
                for (int j = 0; j < field.size(); j++) {
                    PdfDictionary value = field.getValue(j);
                    for (PdfName key : value.getKeys()) {
                        LOGGER.info("   {}:[{}]", key, value.get(key));
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

    /**
     * Simple URL.openStream()
     */
    @Test
    public void test_URL_getPage_simple() {
        LOGGER.debug("Start");
        // Test opened URL
        String test_url = props.getProperty(CFG_KEY__TEST_URL);
        LOGGER.debug("Try read Page from URL: [{}]" + test_url);
        URL testURL = null;
        try {
            testURL = new URL(test_url);
            // read page without Gravity-authentication
            BufferedReader br = new BufferedReader(new InputStreamReader(testURL.openStream()));
            String page = "";
            String strTemp = "";
            while(null != (strTemp = br.readLine())){
                page += strTemp + "\n";
            }
            assertTrue("!!!Not secure resource: [" + test_url + "]", page.contains("\"status\":401,\"response\":\"Permission denied\""));
            LOGGER.info("Result of the attempt get the Page without Gravity-authentication:\n" + page);
        } catch (MalformedURLException e) {
            LOGGER.error("Incorrect the URL String: [{}]",test_url, e);
        } catch (IOException e) {
            LOGGER.error("Problem read from URL: \"" + test_url + "\"", e);
        }
        assertNotNull("Problem open URL", testURL);
        LOGGER.debug("Finish");
    }



    /**
     * https://www.gravityhelp.com/documentation/article/web-api/#authentication-for-external-applications
     */
    @Test
    public void test_URL_getPage_withGravityAuthentication() {
        LOGGER.debug("Start");
        // Test opened URL
        String test_url = props.getProperty(CFG_KEY__TEST_URL);
        LOGGER.debug("Try read Page from URL: [{}]" + test_url);
        URL testURL = null;
        try {
            // build the authentication string
            // see https://www.gravityhelp.com/documentation/article/web-api/#authentication-for-external-applications
            String string_to_sign = "{api_key}:{http method}:{route}:{expires}"; //Authentication for External applications
            LOGGER.debug("Using format string for sign: [{}]", string_to_sign);
            string_to_sign = string_to_sign.replace("{http method}", "GET");

            String api_key = props.getProperty(CFG_KEY__TEST_GRAVITY_API_KEY);
            LOGGER.trace("api_key: [{}]", api_key);
            string_to_sign = string_to_sign.replace("{api_key}", api_key);

            String route = props.getProperty(CFG_KEY__TEST_GRAVITY_ROUTE);
            LOGGER.trace("route: [{}]", route);
            string_to_sign = string_to_sign.replace("{route}", route);

            long expires = new Date().getTime()/1000 + 10; //10 second
            LOGGER.trace("expires: [{}]", expires);
            string_to_sign = string_to_sign.replace("{expires}", ""+expires);
            LOGGER.debug("Gravity's Authentication string, unsigned: [{}]", string_to_sign);

            // sign the authentication string
            String signature = new GravityFormDataSource().signHmacSHA1hash(string_to_sign);
            LOGGER.debug("Signature created: [{}]", signature);

            // add into URL request params
            test_url +=("?api_key=" + api_key + "&signature=" + signature + "&expires=" + expires);
            LOGGER.debug("Try request URL: [{}]", test_url);
            // read page with Gravity-authentication
            testURL = new URL(test_url);
            BufferedReader br = new BufferedReader(new InputStreamReader(testURL.openStream()));
            String page = "";
            String strTemp = "";
            while(null != (strTemp = br.readLine())){
                page += strTemp + "\n";
            }
            LOGGER.debug("Result of the attempt get the Page with Gravity-authentication:\n" + page);
            LOGGER.info("Result of the attempt get the Page with Gravity-authentication:\n" + JsonWriter.formatJson(page));
            assertFalse("!!!Not secure resource: [" + test_url + "]", page.contains("\"status\":401,\"response\":\"Permission denied\""));

        } catch (MalformedURLException e) {
            LOGGER.error("Incorrect the URL String: [{}]",test_url, e);
        } catch (IOException e) {
            LOGGER.error("Problem read from URL: \"" + test_url + "\"", e);
        }
        assertNotNull("Problem open URL", testURL);
        LOGGER.debug("Finish");
    }



    /**
     * Test HMAC SHA-1 sign
     */
    @Test
    public void test_signHmacSHA1_toBase64() {
        LOGGER.debug("Start");
        String string_to_sign = "1234:GET:forms/1/entries:1369749344";
        String private_key = props.getProperty(CFG_KEY__TEST_GRAVITY_PRIVATE_KEY);
        LOGGER.debug("try sign string: [{}], private_key: [{}]", string_to_sign, private_key);
        byte[] hash = HmacUtils.hmacSha1(private_key, string_to_sign);
        String signature = java.util.Base64.getEncoder().encodeToString(  hash );
        try {
            signature = URLEncoder.encode( signature, "ASCII");
            LOGGER.info("Signed! signature: [{}], string_to_sign: [{}], private_key: [{}]", signature, string_to_sign, private_key);
            assertTrue("Problem with URLEncode:", true);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem: ", e);
            assertTrue("Problem with URLEncode:", false);
        }
        LOGGER.debug("Finish");
    }


    /**
     * Test HMAC SHA-1 sign
     */
    @Test
    public void test_sign() {
        LOGGER.debug("Start");
        String string_to_sign = "1234:GET:forms/1/entries:1369749344";
        String private_key = props.getProperty(CFG_KEY__TEST_GRAVITY_PRIVATE_KEY);
        String sign = new GravityFormDataSource().signHmacSHA1hash(string_to_sign, private_key);
        LOGGER.info("Got Sign: [{}]", sign);
        assertTrue("Sign not equial!", "uJEnk0EoQ4d3iinjFMBrBzZfH9w%3D".equals(sign));
        LOGGER.debug("Finish");
    }


    /**
     * Test HMAC SHA-1 sign
     *
     * GET
     * http://mydomain.com/gravityformsapi/forms/25?api_key=1234&signature=PueNOtbfUe%2BMfClAOi2lfq%2BHLyo%3D&expires=1369749344
     */
    @Test
    public void test_sign2() {
        LOGGER.debug("Start");
        String string_to_sign = "1234:GET:forms/25:1369749344";
        String private_key = props.getProperty(CFG_KEY__TEST_GRAVITY_PRIVATE_KEY);
        String sign = new GravityFormDataSource().signHmacSHA1hash(string_to_sign, private_key);
        LOGGER.info("Got Sign: [{}]", sign);
        assertTrue("Sign not equial!", "PueNOtbfUe%2BMfClAOi2lfq%2BHLyo%3D".equals(sign));
        LOGGER.debug("Finish");
    }


}
