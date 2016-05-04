package dk.dma.enav.hamcrest.matchers;

import org.hamcrest.Matcher;

/**
 * Created by Steen on 02-05-2016.
 *
 */
public class EnavMatchers {
    public static org.hamcrest.Matcher<String> hasXPath(java.lang.String xPath) {
        return HasXPath.hasXPath(xPath);
    }

    public static org.hamcrest.Matcher<String> hasXPath(java.lang.String xPath, Matcher<String> valueMather) {
        return HasXPath.hasXPath(xPath, valueMather);
    }

}
