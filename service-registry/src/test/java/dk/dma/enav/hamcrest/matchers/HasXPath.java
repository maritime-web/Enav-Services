package dk.dma.enav.hamcrest.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Steen on 02-05-2016.
 *
 */
public class HasXPath extends TypeSafeDiagnosingMatcher<String> {
    private final org.hamcrest.xml.HasXPath delegate;
    private HasXPath(Matcher<Node> delegate) {
        this.delegate = (org.hamcrest.xml.HasXPath) delegate;
    }

    @Override
    protected boolean matchesSafely(String item, Description mismatchDescription) {
        Node itemNode = toNode(item);
        return delegate.matchesSafely(itemNode, mismatchDescription);
    }

    private Node toNode(String item) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(item)));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException("Could not parse string to xml Document", e);
        }
    }

    @Override
    public void describeTo(Description description) {
        delegate.describeTo(description);
    }

    @Factory
    public static Matcher<String> hasXPath(String xPath, Matcher<String> valueMatcher) {
        return new HasXPath(org.hamcrest.xml.HasXPath.hasXPath(xPath, valueMatcher));
    }

    @Factory
    public static Matcher<String> hasXPath(String xPath, NamespaceContext namespaceContext, Matcher<String> valueMatcher) {
        return new HasXPath(org.hamcrest.xml.HasXPath.hasXPath(xPath, namespaceContext, valueMatcher));
    }

    @Factory
    public static Matcher<String> hasXPath(String xPath) {
        return new HasXPath(org.hamcrest.xml.HasXPath.hasXPath(xPath));
    }

    @Factory
    public static Matcher<String> hasXPath(String xPath, NamespaceContext namespaceContext) {
        return new HasXPath(org.hamcrest.xml.HasXPath.hasXPath(xPath, namespaceContext));
    }

}
