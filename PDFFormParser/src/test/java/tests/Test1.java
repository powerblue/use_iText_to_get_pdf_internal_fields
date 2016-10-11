package tests;

import fr.jp.pdf.FormParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Date: 10/10/16
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test1 {
  public String VERSION = "16.10.11.1";
  private static final Logger LOGGER = LoggerFactory.getLogger(Test1.class);
  private static final String TEST_PROPERTIES_FILE = "Test.properties";
  private static final String CFG_KEY__TEST_PDF = "pdf.processing.file";
  private static final String CFG_KEY__TEST_FILL_VALUES = "fill.values";

  private Properties props;


  public Test1() {
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
  public void test_PDDocumentFormList() {
    LOGGER.debug("Start");
    String file_name = props.getProperty(CFG_KEY__TEST_PDF);

    LOGGER.debug("Start for [{}]", file_name);
    LOGGER.debug("Try open PDF file: [{}]", file_name);

    URL fileURL = super.getClass().getClassLoader().getResource(file_name);
    assertNotNull("NOT FOUND File \"" + file_name + "\"", fileURL);
    File file = new File(fileURL.getFile());

    PDDocument doc = null;
    try {
      doc = PDDocument.load(file);
      assertNotNull("Problem with load PDDocument!", doc);
      LOGGER.debug("PDDocument open succ");

      PDDocumentCatalog documentCatalog = doc.getDocumentCatalog();
      LOGGER.debug("PDDocumentCatalog getted: [{}]", documentCatalog);
      PDAcroForm acroForm = documentCatalog.getAcroForm();
      LOGGER.debug("PDAcroForm getted: [{}]", acroForm);
      assertNotNull("The PDF Doc \"" + file_name + "\" hasn't a FORM FIELDS!", acroForm);
      int i = 0;
      for (PDField field : acroForm.getFields()) {
        LOGGER.info("{}. Get PDField: [{}]", ++i, field);
      }
    } catch (IOException e) {
      LOGGER.error("Problem: ", e);
    } finally {
      if (doc != null)
        try {
          doc.close();
          LOGGER.trace("PDDocument closed");
        } catch (IOException e) {
          LOGGER.error("Can't close PDDocument:", e);
        }
    }

    LOGGER.debug("Finish processing PDF doc [{}]", file_name);
  }


  @Test
  public void test_PDDocumentFormListFieldsDetails() {
    LOGGER.debug("Start");
    String file_name = props.getProperty(CFG_KEY__TEST_PDF);

    LOGGER.debug("Start for [{}]", file_name);
    LOGGER.debug("Try open PDF file: [{}]", file_name);

    URL fileURL = super.getClass().getClassLoader().getResource(file_name);
    assertNotNull("NOT FOUND File \"" + file_name + "\"", fileURL);
    File file = new File(fileURL.getFile());

    PDDocument doc = null;
    try {
      doc = PDDocument.load(file);
      assertNotNull("Problem with load PDDocument!", doc);
      LOGGER.debug("PDDocument open succ");

      PDDocumentCatalog documentCatalog = doc.getDocumentCatalog();
      LOGGER.debug("PDDocumentCatalog getted: [{}]", documentCatalog);
      PDAcroForm acroForm = documentCatalog.getAcroForm();
      LOGGER.debug("PDAcroForm getted: [{}]", acroForm);
      assertNotNull("The PDF Doc \"" + file_name + "\" hasn't a FORM FIELDS!", acroForm);
      int i = 0;
      for (PDField field : acroForm.getFields()) {
        LOGGER.info("{}. Get PDField: [{}]", ++i, field);
        LOGGER.info("    getActions:            [{}]", field.getActions());
        LOGGER.info("    getAlternateFieldName: [{}]", field.getAlternateFieldName());
        LOGGER.info("    getCOSObject:          [{}]", field.getCOSObject());
        LOGGER.info("    getFieldType:          [{}]", field.getFieldType());
        LOGGER.info("    getFullyQualifiedName: [{}]", field.getFullyQualifiedName());
        LOGGER.info("    getMappingName:        [{}]", field.getMappingName());
        LOGGER.info("    getPartialName:        [{}]", field.getPartialName());
      }
    } catch (IOException e) {
      LOGGER.error("Problem: ", e);
    } finally {
      if (doc != null)
        try {
          doc.close();
          LOGGER.trace("PDDocument closed");
        } catch (IOException e) {
          LOGGER.error("Can't close PDDocument:", e);
        }
    }

    LOGGER.debug("Finish processing PDF doc [{}]", file_name);
  }


  @Test
  public void test_PDDocumentFormFill() {
    LOGGER.debug("Start");
    String file_name = props.getProperty(CFG_KEY__TEST_PDF);
    LOGGER.debug("Start for [{}]", file_name);
    LOGGER.debug("Try open PDF file: [{}]", file_name);

    URL fileURL = super.getClass().getClassLoader().getResource(file_name);
    assertNotNull("NOT FOUND File \"" + file_name + "\"", fileURL);
    File file = new File(fileURL.getFile());

    PDDocument doc = null;
    try {
      Hashtable<String, String> fill_values = FormParser.prepareData( props.getProperty(CFG_KEY__TEST_FILL_VALUES));

      doc = PDDocument.load(file);
      assertNotNull("Problem with load PDDocument!", doc);
      LOGGER.debug("PDDocument open succ");

      PDDocumentCatalog documentCatalog = doc.getDocumentCatalog();
      LOGGER.debug("PDDocumentCatalog getted: [{}]", documentCatalog);
      PDAcroForm acroForm = documentCatalog.getAcroForm();
      LOGGER.debug("PDAcroForm getted: [{}]", acroForm);
      assertNotNull("The PDF Doc \"" + file_name + "\" hasn't a FORM FIELDS!", acroForm);
      int i = 0;
      for (PDField field : acroForm.getFields()) {
        String field_name = field.getPartialName();
        if (fill_values.containsKey( field_name)) {
          field.setValue( fill_values.get( field_name));
          LOGGER.info("set [{}={}]", field_name, fill_values.get( field_name));
        }
        LOGGER.debug("{}. Get PDField: [{}]", ++i, field);
      }
      String result_file_name = "_" + file_name;
      doc.save(result_file_name);
      LOGGER.info("PDF doc save: [{}]", result_file_name);
    } catch (IOException|ParseException e) {
      LOGGER.error("Problem: ", e);
    } finally {
      if (doc != null)
        try {
          doc.close();
          LOGGER.trace("PDDocument closed");
        } catch (IOException e) {
          LOGGER.error("Can't close PDDocument:", e);
        }
    }

    LOGGER.debug("Finish processing PDF doc [{}]", file_name);
  }



}
