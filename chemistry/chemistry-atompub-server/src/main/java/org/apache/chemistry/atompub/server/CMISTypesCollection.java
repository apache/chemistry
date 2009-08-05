/*
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
 *
 * Authors:
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.atompub.server;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.chemistry.CMIS;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.Repository;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.AtomPubCMIS;
import org.apache.chemistry.atompub.abdera.PropertiesElement;

/**
 * CMIS Collection for the Types.
 */
public class CMISTypesCollection extends CMISCollection<Type> {

    public CMISTypesCollection(String type, String id, Repository repository) {
        super(type, "types", id, repository);
    }

    /*
     * ----- AbstractCollectionAdapter -----
     */

    @Override
    protected Feed createFeedBase(RequestContext request) {
        Factory factory = request.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        feed.declareNS(CMIS.CMIS_NS, CMIS.CMIS_PREFIX);
        feed.setId(getId(request));
        feed.setTitle(getTitle(request));
        feed.addLink(request.getResolvedUri().toString(), "self");
        feed.addAuthor(getAuthor(request));
        feed.setUpdated(new Date()); // XXX fixed date
        return feed;
    }

    @Override
    public String getId(RequestContext request) {
        // TODO when descendants
        return "urn:x-id:types";
    }

    public String getTitle(RequestContext request) {
        return "Types";
    }

    @Override
    public String getAuthor(RequestContext request) {
        return "system";
    }

    /*
     * ----- AbstractEntityCollectionAdapter -----
     */

    @Override
    public String addEntryDetails(RequestContext request, Entry entry,
            IRI feedIri, Type type) throws ResponseContextException {
        boolean includePropertyDefinitions = "true".equals(request.getParameter("includePropertyDefinitions"));
        Factory factory = request.getAbdera().getFactory();

        entry.setId(getId(type));
        entry.setTitle(getTitle(type));
        entry.setUpdated(getUpdated(type));
        // no authors, feed has one
        String summary = type.getDescription();
        if (summary != null && summary.length() != 0) {
            entry.setSummary(summary);
        }

        String link = getLink(type, feedIri, request);
        entry.addLink(link, "self");
        entry.addLink(link, "edit");
        // alternate is mandated by Atom when there is no atom:content
        entry.addLink(link, "alternate");
        // CMIS links

        // CMIS-specific
        Element te = factory.newElement(AtomPubCMIS.TYPE, entry);
        Element el;
        // note: setText is called in a separate statement as JDK 5 has problems
        // compiling when it's on one line (compiler generics bug)
        el = factory.newElement(CMIS.ID, te);
        el.setText(type.getId());
        el = factory.newElement(CMIS.LOCAL_NAME, te);
        el.setText(type.getLocalName());
        URI localNamespace = type.getLocalNamespace();
        if (localNamespace != null) {
            el = factory.newElement(CMIS.LOCAL_NAMESPACE, te);
            el.setText(localNamespace.toString());
        }
        el = factory.newElement(CMIS.QUERY_NAME, te);
        el.setText(type.getQueryName());
        el = factory.newElement(CMIS.DISPLAY_NAME, te);
        el.setText(type.getDisplayName());
        el = factory.newElement(CMIS.BASE_TYPE_ID, te);
        el.setText(type.getBaseType().getId());
        el = factory.newElement(CMIS.PARENT_ID, te);
        el.setText(type.getParentId());
        el = factory.newElement(CMIS.DESCRIPTION, te);
        el.setText(type.getDescription());
        el = factory.newElement(CMIS.CREATABLE, te);
        el.setText(bool(type.isCreatable()));
        el = factory.newElement(CMIS.FILEABLE, te);
        el.setText(bool(type.isFileable()));
        el = factory.newElement(CMIS.QUERYABLE, te);
        el.setText(bool(type.isQueryable()));
        el = factory.newElement(CMIS.CONTROLLABLE_POLICY, te);
        el.setText(bool(type.isControllablePolicy()));
        el = factory.newElement(CMIS.CONTROLLABLE_ACL, te);
        el.setText(bool(type.isControllableACL()));
        el = factory.newElement(CMIS.FULLTEXT_INDEXED, te);
        el.setText(bool(type.isFulltextIndexed()));
        el = factory.newElement(CMIS.INCLUDED_IN_SUPERTYPE_QUERY, te);
        el.setText(bool(type.isIncludedInSuperTypeQuery()));
        el = factory.newElement(CMIS.VERSIONABLE, te); // docs only
        el.setText(bool(type.isVersionable()));
        el = factory.newElement(CMIS.CONTENT_STREAM_ALLOWED, te); // docs only
        el.setText(type.getContentStreamAllowed().toString()); // TODO null
        // TODO allowedSourceTypes, allowedTargetTypes
        if (includePropertyDefinitions) {
            for (PropertyDefinition pd : type.getPropertyDefinitions()) {
                QName qname;
                switch (pd.getType().ordinal()) {
                case PropertyType.STRING_ORD:
                    qname = CMIS.PROPERTY_STRING_DEFINITION;
                    break;
                case PropertyType.DECIMAL_ORD:
                    qname = CMIS.PROPERTY_DECIMAL_DEFINITION;
                    break;
                case PropertyType.INTEGER_ORD:
                    qname = CMIS.PROPERTY_INTEGER_DEFINITION;
                    break;
                case PropertyType.BOOLEAN_ORD:
                    qname = CMIS.PROPERTY_BOOLEAN_DEFINITION;
                    break;
                case PropertyType.DATETIME_ORD:
                    qname = CMIS.PROPERTY_DATETIME_DEFINITION;
                    break;
                case PropertyType.URI_ORD:
                    qname = CMIS.PROPERTY_URI_DEFINITION;
                    break;
                case PropertyType.ID_ORD:
                    qname = CMIS.PROPERTY_ID_DEFINITION;
                    break;
                case PropertyType.XML_ORD:
                    qname = CMIS.PROPERTY_XML_DEFINITION;
                    break;
                case PropertyType.HTML_ORD:
                    qname = CMIS.PROPERTY_HTML_DEFINITION;
                    break;
                case PropertyType.XHTML_ORD:
                    qname = CMIS.PROPERTY_XHTML_DEFINITION;
                    break;
                default:
                    throw new AssertionError(pd.getType().name());
                }
                Element def = factory.newElement(qname, te);
                el = factory.newElement(CMIS.ID, def);
                el.setText(pd.getId());
                String localName = pd.getLocalName();
                if (localName != null) {
                    el = factory.newElement(CMIS.LOCAL_NAME, def);
                    el.setText(localName);
                }
                URI lns = pd.getLocalNamespace();
                if (lns != null) {
                    el = factory.newElement(CMIS.LOCAL_NAMESPACE, def);
                    el.setText(lns.toString());
                }
                el = factory.newElement(CMIS.QUERY_NAME, def);
                el.setText(pd.getQueryName());
                el = factory.newElement(CMIS.DISPLAY_NAME, def);
                el.setText(pd.getDisplayName());
                el = factory.newElement(CMIS.DESCRIPTION, def);
                el.setText(pd.getDescription());
                el = factory.newElement(CMIS.PROPERTY_TYPE, def);
                el.setText(pd.getType().name());
                el = factory.newElement(CMIS.CARDINALITY, def);
                el.setText(pd.isMultiValued() ? "multi" : "single");
                el = factory.newElement(CMIS.UPDATABILITY, def);
                el.setText(pd.getUpdatability().toString()); // TODO null
                el = factory.newElement(CMIS.INHERITED, def);
                el.setText(pd.isInherited() ? "true" : "false");
                el = factory.newElement(CMIS.REQUIRED, def);
                el.setText(pd.isRequired() ? "true" : "false");
                el = factory.newElement(CMIS.QUERYABLE, def);
                el.setText(pd.isQueryable() ? "true" : "false");
                el = factory.newElement(CMIS.ORDERABLE, def);
                el.setText(pd.isOrderable() ? "true" : "false");
                Serializable defaultValue = pd.getDefaultValue();
                if (defaultValue != null) {
                    Element dv = factory.newElement(CMIS.DEFAULT_VALUE, def);
                    for (String s : PropertiesElement.getStringsForValue(
                            defaultValue, pd)) {
                        el = factory.newElement(CMIS.VALUE, dv);
                        el.setText(s);
                    }
                }
                // TODO choices
                switch (pd.getType().ordinal()) {
                case PropertyType.STRING_ORD:
                    // TODO maxLength
                    break;
                case PropertyType.DECIMAL_ORD:
                    // TODO precision
                    break;
                case PropertyType.INTEGER_ORD:
                    // TODO minValue/maxValue
                    break;
                case PropertyType.BOOLEAN_ORD:
                    break;
                case PropertyType.DATETIME_ORD:
                    break;
                case PropertyType.URI_ORD:
                    break;
                case PropertyType.ID_ORD:
                    break;
                case PropertyType.XML_ORD:
                    // TODO schemaURI
                    break;
                case PropertyType.HTML_ORD:
                    break;
                default:
                    throw new AssertionError(pd.getType().name());
                }
                // end property definition
            }
        }
        // end property definitions
        if ("typesdescendants".equals(getType())) {
            Collection<Type> subTypes = repository.getTypes(type.getId(), 1,
                    includePropertyDefinitions);
            if (!subTypes.isEmpty()) {
                Element che = factory.newElement(AtomPubCMIS.CHILDREN, entry);
                // TODO basic feed info
                // AbstractCollectionAdapter.createFeedBase:
                factory.newID(che).setValue(
                        "urn:x-id:typesdecendants-" + type.getId());
                // che.setTitle(getTitle(request));
                // che.addLink("");
                // che.addLink("", "self");
                // che.addAuthor(getAuthor(request));
                // che.setUpdated(new Date());
                // AbstractEntityCollectionAdapter.addFeedDetails
                // che.setUpdated(new Date());
                for (Type subType : subTypes) {
                    Entry subEntry = factory.newEntry(che);
                    addEntryDetails(request, subEntry, null, subType);
                }
                // end children entry
            }
        }
        return link;
    }

    protected static String bool(boolean bool) {
        return bool ? "true" : "false";
    }

    @Override
    public Iterable<Type> getEntries(RequestContext request)
            throws ResponseContextException {
        return repository.getTypes(id);
    }

    @Override
    public Type postEntry(String title, IRI id, String summary, Date updated,
            List<Person> authors, Content content, RequestContext request)
            throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putEntry(Type type, String title, Date updated,
            List<Person> authors, String summary, Content content,
            RequestContext request) throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext request)
            throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getContent(Type type, RequestContext request)
            throws ResponseContextException {
        return null;
    }

    @Override
    public Type getEntry(String typeId, RequestContext request)
            throws ResponseContextException {
        return repository.getType(typeId);
    }

    @Override
    public String getResourceName(RequestContext request) {
        return request.getTarget().getParameter("typeid");
    }
    @Override
    protected String getLink(Type type, IRI feedIri, RequestContext request) {
        return getTypeLink(type.getId(), request);
    }

    @Override
    public String getName(Type type) {
        throw new UnsupportedOperationException(); // unused
    }

    @Override
    public String getId(Type type) {
        return "urn:x-tid:" + type.getId();
    }

    @Override
    public String getTitle(Type type) {
        return type.getDisplayName();
    }

    @Override
    public Date getUpdated(Type type) {
        // XXX TODO mandatory field
        return new Date();
    }

}
