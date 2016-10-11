package fr.jp.pdf;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Hashtable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main class. Parse PDF Forms
 * Author: dctm.programmer@gmail.com
 * Date: 2016.10.06
 */
public class FormParser {
  public static final String VERSION = "16.10.11.3";
  private static final Logger LOGGER = LoggerFactory.getLogger(FormParser.class);
  public static final String PARAM_KEY__GET_LIST = "-LIST_FIELDS";
  private static final String MSG_HELP = "usage:\n> java -cp $CLASSPATH fr.jp.pdf.FormParser -LIST_FIELDS pdf_file\n";

  public static void main(String[] args) {
    LOGGER.debug("Start, v{}", VERSION);
    FormParser formParser = new FormParser();

    try {
      String mode = formParser.getMode(args);
      switch (mode) {
        case PARAM_KEY__GET_LIST:
          int idx_file_name = (args[0].equals(PARAM_KEY__GET_LIST)) ? 1 : 0;
          formParser.printFields(args[idx_file_name]);
          break;
        default:
          printHelp();
      }
    } catch (Exception e) {
      LOGGER.error("Problem: ", e);
    }
    LOGGER.debug("Finish");
  }

  private String getMode(String[] args) {
    LOGGER.debug("Start with {}", Arrays.asList(args));
    String mode = "UNKNOWN";
    if (args.length > 0) {
      if ((Arrays.binarySearch(args, PARAM_KEY__GET_LIST) >= 0) && (args.length == 2)) {
        mode = PARAM_KEY__GET_LIST;
      } else {
        LOGGER.warn("Invoke with unknown params: {}", Arrays.asList(args));
      }
    } else {
      LOGGER.warn("Invoke with empty arguments! Don't run.");
    }
    LOGGER.debug("Finish, return: [{}]", mode);
    return mode;
  }

  private void printFields(String file_name) throws IOException {
    int i;
    LOGGER.debug("Start for [{}]", file_name);
    LOGGER.trace("Try open PDF file: [{}]", file_name);

    URL fileURL = super.getClass().getClassLoader().getResource(file_name);
    if (fileURL == null) throw new IOException("NOT FOUND File \"" + file_name + "\"");
    File file = new File(fileURL.getFile());

    PDDocument doc = null;
    try {
      doc = PDDocument.load(file);
      if (doc == null) throw new IOException("Problem with load PDDocument!");
      LOGGER.trace("PDDocument open succ");

      PDDocumentCatalog documentCatalog = doc.getDocumentCatalog();
      LOGGER.trace("PDDocumentCatalog getted: [{}]", documentCatalog);
      PDAcroForm acroForm = documentCatalog.getAcroForm();
      LOGGER.trace("PDAcroForm getted: [{}]", acroForm);
      if (acroForm == null) throw new IOException("The PDF Doc \"" + file_name + "\" hasn't a FORM FIELDS!");
      i = 0;
      for (PDField field : acroForm.getFields()) {
        LOGGER.info("{}. Get PDField: [{}]", Integer.valueOf(++i), field);
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

  /**
   * parse key=value array as single String
   *
   * @param key_values_string - key=value array as single String
   * @return -
   */
  public static Hashtable<String, String> prepareData(String key_values_string) throws ParseException {
    LOGGER.debug("Invoke with [{}]", key_values_string);
    Hashtable<String, String> result = new Hashtable<>();
    String[] key_values_arr = key_values_string.split(";");
    LOGGER.trace("Found the {} pair key=value", key_values_arr.length);
    for(String key_value: key_values_arr) {
      String[] key_value_pair_arr = key_value.split("=");
      if (key_value_pair_arr.length != 2) {
        throw new ParseException("Wrong format \"key=value\" pair: " + key_value, 0);
      }
      if (result.containsKey( key_value_pair_arr[0])) {
        throw new ParseException("Found duplicate Key: " + key_value_pair_arr[0], 0);
      }
      result.put(key_value_pair_arr[0], key_value_pair_arr[1]);
    }
    return result;
  }

  private static void printHelp() {
    LOGGER.debug("Invoke");
    System.out.println(MSG_HELP);
  }
}