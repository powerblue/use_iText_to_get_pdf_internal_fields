package fr.jp.data.adapters;

import fr.jp.pdf.FormParser;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * Created on 10/22/2016.
 */
public class GravityFormDataSource {
    public static final String VERSION = "16.10.22.1";
    private static final Logger LOGGER = LoggerFactory.getLogger(GravityFormDataSource.class);

    public static final String CFG_FILE = "App.properties";
    public static final String CFG_KEY__DS_URL = "datasource.url";
    public static final String CFG_KEY__GRAVITY_API_KEY = "gravity.api_key";
    public static final String CFG_KEY__GRAVITY_ROUTE = "gravity.route";
    public static final String CFG_KEY__GRAVITY_PRIVATE_KEY = "gravity.private_key";

    private Properties props;

    public GravityFormDataSource() {
        props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(CFG_FILE));
            LOGGER.debug("Application properties loaded succ: [{}]",getClass().getClassLoader().getResource(CFG_FILE));
        } catch (IOException e) {
            LOGGER.error("Problem init Instance:", e);
        }
        LOGGER.debug("new Instance created. v{}", VERSION);
    }


    /**
     * Create HmacSHA1 hash and return as Base64 URLEncoded string
     * @param string_to_sign - String with data for sign
     * @return hash in Base64 encoding
     */
    public String signHmacSHA1hash(String string_to_sign) {
        LOGGER.trace("Invoke with data: [{}]", string_to_sign);
        String private_key = props.getProperty(CFG_KEY__GRAVITY_PRIVATE_KEY);
        return signHmacSHA1hash(string_to_sign, private_key);
    }

    public String signHmacSHA1hash(String string_to_sign, String private_key) {
        LOGGER.trace("Invoke for sign data: [{}], private_key: [{}]", string_to_sign, private_key);
        byte[] hash = HmacUtils.hmacSha1(private_key, string_to_sign);
        String signature = java.util.Base64.getEncoder().encodeToString(  hash );
        try {
            signature = URLEncoder.encode( signature, "ASCII");
            LOGGER.trace("Signed! signature: [{}], string_to_sign: [{}], private_key: [{}]", signature, string_to_sign, private_key);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem: ", e);
        }
        LOGGER.debug("Return sign: [{}]", signature);
        return signature;
    }
}
