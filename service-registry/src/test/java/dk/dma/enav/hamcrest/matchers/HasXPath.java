/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
public final class HasXPath extends TypeSafeDiagnosingMatcher<String> {
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
