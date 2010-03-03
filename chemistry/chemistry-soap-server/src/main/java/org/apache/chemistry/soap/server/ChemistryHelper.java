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
package org.apache.chemistry.soap.server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.chemistry.BaseType;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.Type;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.ws.CmisObjectListType;
import org.apache.chemistry.ws.CmisObjectType;
import org.apache.chemistry.ws.CmisPropertiesType;
import org.apache.chemistry.ws.CmisProperty;
import org.apache.chemistry.ws.CmisPropertyBoolean;
import org.apache.chemistry.ws.CmisPropertyDateTime;
import org.apache.chemistry.ws.CmisPropertyDecimal;
import org.apache.chemistry.ws.CmisPropertyDefinitionType;
import org.apache.chemistry.ws.CmisPropertyHtml;
import org.apache.chemistry.ws.CmisPropertyId;
import org.apache.chemistry.ws.CmisPropertyInteger;
import org.apache.chemistry.ws.CmisPropertyString;
import org.apache.chemistry.ws.CmisPropertyUri;
import org.apache.chemistry.ws.CmisRepositoryCapabilitiesType;
import org.apache.chemistry.ws.CmisRepositoryInfoType;
import org.apache.chemistry.ws.CmisTypeContainer;
import org.apache.chemistry.ws.CmisTypeDefinitionListType;
import org.apache.chemistry.ws.CmisTypeDefinitionType;
import org.apache.chemistry.ws.EnumBaseObjectTypeIds;
import org.apache.chemistry.ws.EnumCapabilityJoin;
import org.apache.chemistry.ws.EnumCapabilityQuery;
import org.apache.chemistry.ws.EnumCardinality;
import org.apache.chemistry.ws.EnumPropertyType;
import org.apache.chemistry.ws.EnumUpdatability;
import org.apache.chemistry.ws.ObjectFactory;
import org.apache.chemistry.ws.QueryResponse;

/**
 * Helper for various Chemistry to JAXB conversions.
 */
public class ChemistryHelper {

    private static final ObjectFactory factory = new ObjectFactory();

    private ChemistryHelper() {
        // utility class;
    }

    public static CmisRepositoryInfoType convert(RepositoryInfo cri) {
        CmisRepositoryInfoType ri = factory.createCmisRepositoryInfoType();
        ri.setRepositoryId(cri.getId());
        ri.setRepositoryName(cri.getName());
        ri.setRepositoryDescription(cri.getDescription());
        ri.setVendorName(cri.getVendorName());
        ri.setProductName(cri.getProductName());
        ri.setProductVersion(cri.getProductVersion());
        ri.setCmisVersionSupported(cri.getVersionSupported());
        ri.setRootFolderId(cri.getRootFolderId().getId());

        CmisRepositoryCapabilitiesType cap = factory.createCmisRepositoryCapabilitiesType();
        RepositoryCapabilities ccap = cri.getCapabilities();
        ri.setCapabilities(cap);
        cap.setCapabilityMultifiling(ccap.hasMultifiling());
        cap.setCapabilityUnfiling(ccap.hasUnfiling());
        cap.setCapabilityVersionSpecificFiling(ccap.hasVersionSpecificFiling());
        cap.setCapabilityPWCUpdatable(ccap.isPWCUpdatable());
        cap.setCapabilityPWCSearchable(ccap.isPWCSearchable());
        cap.setCapabilityAllVersionsSearchable(ccap.isAllVersionsSearchable());
        cap.setCapabilityQuery(convert(ccap.getQueryCapability()));
        cap.setCapabilityJoin(convert(ccap.getJoinCapability()));

        return ri;
    }

    public static EnumCapabilityQuery convert(CapabilityQuery query) {
        if (query == null) {
            return null;
        }
        switch (query) {
        case NONE:
            return EnumCapabilityQuery.NONE;
        case METADATA_ONLY:
            return EnumCapabilityQuery.METADATAONLY;
        case FULL_TEXT_ONLY:
            return EnumCapabilityQuery.FULLTEXTONLY;
        case BOTH_COMBINED:
            return EnumCapabilityQuery.BOTHCOMBINED;
        case BOTH_SEPARATE:
            return EnumCapabilityQuery.BOTHSEPARATE;
        default:
            throw new RuntimeException(query.name());
        }
    }

    public static EnumCapabilityJoin convert(CapabilityJoin join) {
        if (join == null) {
            return null;
        }
        switch (join) {
        case NONE:
            return EnumCapabilityJoin.NONE;
        case INNER_ONLY:
            return EnumCapabilityJoin.INNERONLY;
        case INNER_AND_OUTER:
            return EnumCapabilityJoin.INNERANDOUTER;
        default:
            throw new RuntimeException(join.name());
        }
    }

    public static EnumBaseObjectTypeIds convert(BaseType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
        case DOCUMENT:
            return EnumBaseObjectTypeIds.CMIS_DOCUMENT;
        case FOLDER:
            return EnumBaseObjectTypeIds.CMIS_FOLDER;
        case POLICY:
            return EnumBaseObjectTypeIds.CMIS_POLICY;
        case RELATIONSHIP:
            return EnumBaseObjectTypeIds.CMIS_RELATIONSHIP;
        default:
            throw new RuntimeException(type.name());
        }
    }

    public static EnumPropertyType convert(PropertyType type) {
        if (type == null) {
            return null;
        }
        switch (type.ordinal()) {
        case PropertyType.STRING_ORD:
            return EnumPropertyType.STRING;
        case PropertyType.DECIMAL_ORD:
            return EnumPropertyType.DECIMAL;
        case PropertyType.INTEGER_ORD:
            return EnumPropertyType.INTEGER;
        case PropertyType.BOOLEAN_ORD:
            return EnumPropertyType.BOOLEAN;
        case PropertyType.DATETIME_ORD:
            return EnumPropertyType.DATETIME;
        case PropertyType.URI_ORD:
            return EnumPropertyType.URI;
        case PropertyType.ID_ORD:
            return EnumPropertyType.ID;
        case PropertyType.HTML_ORD:
            return EnumPropertyType.HTML;
        default:
            throw new RuntimeException(type.name());
        }
    }

    public static EnumCardinality convertMulti(boolean bool) {
        return bool ? EnumCardinality.MULTI : EnumCardinality.SINGLE;
    }

    public static EnumUpdatability convert(Updatability up) {
        if (up == null) {
            return null;
        }
        switch (up) {
        case READ_ONLY:
            return EnumUpdatability.READONLY;
        case READ_WRITE:
            return EnumUpdatability.READWRITE;
        case WHEN_CHECKED_OUT:
            return EnumUpdatability.WHENCHECKEDOUT;
        case ON_CREATE:
            return EnumUpdatability.ONCREATE;
        default:
            throw new RuntimeException(up.name());
        }
    }

    public static CmisPropertyDefinitionType convert(PropertyDefinition cpd) {
        CmisPropertyDefinitionType pd = factory.createCmisPropertyDefinitionType();
        pd.setId(cpd.getId());
        pd.setLocalName(cpd.getLocalName());
        URI dlns = cpd.getLocalNamespace();
        pd.setLocalNamespace(dlns == null ? null : dlns.toString());
        pd.setDisplayName(cpd.getDisplayName());
        pd.setQueryName(cpd.getQueryName());
        pd.setDescription(cpd.getDescription());
        pd.setPropertyType(convert(cpd.getType()));
        pd.setCardinality(convertMulti(cpd.isMultiValued()));
        pd.setUpdatability(convert(cpd.getUpdatability()));
        pd.setInherited(Boolean.valueOf(cpd.isInherited()));
        pd.setRequired(cpd.isRequired());
        pd.setQueryable(cpd.isQueryable());
        pd.setOrderable(cpd.isOrderable());
        pd.setOpenChoice(Boolean.valueOf(cpd.isOpenChoice()));
        return pd;
    }

    public static CmisTypeDefinitionType convert(Type ct) {
        CmisTypeDefinitionType t = factory.createCmisTypeDefinitionType();
        t.setId(ct.getId());
        t.setLocalName(ct.getLocalName());
        URI tlns = ct.getLocalNamespace();
        t.setLocalNamespace(tlns == null ? null : tlns.toString());
        t.setDisplayName(ct.getDisplayName());
        t.setQueryName(ct.getQueryName());
        t.setDescription(ct.getDescription());
        t.setBaseId(convert(ct.getBaseType()));
        t.setParentId(ct.getParentId());
        t.setCreatable(ct.isCreatable());
        t.setFileable(ct.isFileable());
        t.setQueryable(ct.isQueryable());
        t.setFulltextIndexed(ct.isFulltextIndexed());
        t.setIncludedInSupertypeQuery(ct.isIncludedInSuperTypeQuery());
        t.setControllablePolicy(ct.isControllablePolicy());
        t.setControllableACL(ct.isControllableACL());
        List<CmisPropertyDefinitionType> pdl = t.getPropertyDefinition();
        for (PropertyDefinition cpd : ct.getPropertyDefinitions()) {
            pdl.add(convert(cpd));
        }
        return t;
    }

    public static CmisTypeDefinitionListType convert(ListPage<Type> ctl) {
        CmisTypeDefinitionListType tl = factory.createCmisTypeDefinitionListType();
        for (Type ct : ctl) {
            tl.getTypes().add(convert(ct));
        }
        tl.setHasMoreItems(ctl.getHasMoreItems());
        tl.setNumItems(BigInteger.valueOf(ctl.getNumItems()));
        return tl;
    }

    public static List<CmisTypeContainer> convert(Collection<Type> ctl) {
        List<CmisTypeContainer> list = new ArrayList<CmisTypeContainer>(
                ctl.size());
        // for (Type ct : ctl) {
        // XXX
        // }
        return list;
    }

    public static CmisObjectType convert(ObjectEntry entry) {
        CmisObjectType object = factory.createCmisObjectType();
        CmisPropertiesType properties = factory.createCmisPropertiesType();
        List<CmisProperty> list = properties.getProperty();
        for (Entry<String, Serializable> e : entry.getValues().entrySet()) {
            list.add(getWSCmisProperty(e.getKey(), e.getValue()));
        }
        object.setProperties(properties);
        // object.setAllowableActions(null);
        return object;
    }

    public static QueryResponse convertQuery(ListPage<ObjectEntry> res) {
        QueryResponse response = factory.createQueryResponse();
        CmisObjectListType objects = factory.createCmisObjectListType();
        response.setObjects(objects);
        for (ObjectEntry entry : res) {
            objects.getObjects().add(convert(entry));
        }
        objects.setHasMoreItems(res.getHasMoreItems());
        objects.setNumItems(BigInteger.valueOf(res.getNumItems()));
        return response;
    }

    /**
     * Transforms a Chemistry property into a WS one.
     */
    public static CmisProperty getWSCmisProperty(String key, Serializable value) {
        CmisProperty p;
        PropertyType propertyType = guessType(key, value);
        // boolean multi = false; // TODO
        switch (propertyType.ordinal()) {
        case PropertyType.STRING_ORD:
            p = new CmisPropertyString();
            ((CmisPropertyString) p).getValue().add((String) value);
            break;
        case PropertyType.DECIMAL_ORD:
            p = new CmisPropertyDecimal();
            ((CmisPropertyDecimal) p).getValue().add((BigDecimal) value);
            break;
        case PropertyType.INTEGER_ORD:
            p = new CmisPropertyInteger();
            Long l;
            if (value == null) {
                l = null;
            } else if (value instanceof Long) {
                l = (Long) value;
            } else if (value instanceof Integer) {
                l = Long.valueOf(((Integer) value).longValue());
            } else {
                throw new AssertionError("not a int/long: " + value);
            }
            ((CmisPropertyInteger) p).getValue().add(
                    l == null ? null : BigInteger.valueOf(l.longValue()));
            break;
        case PropertyType.BOOLEAN_ORD:
            p = new CmisPropertyBoolean();
            ((CmisPropertyBoolean) p).getValue().add((Boolean) value);
            break;
        case PropertyType.DATETIME_ORD:
            p = new CmisPropertyDateTime();
            ((CmisPropertyDateTime) p).getValue().add(
                    getXMLGregorianCalendar((Calendar) value));
            break;
        case PropertyType.URI_ORD:
            p = new CmisPropertyUri();
            URI u = (URI) value;
            ((CmisPropertyUri) p).getValue().add(
                    u == null ? null : u.toString());
            break;
        case PropertyType.ID_ORD:
            p = new CmisPropertyId();
            ((CmisPropertyId) p).getValue().add((String) value);
            break;
        case PropertyType.HTML_ORD:
            p = new CmisPropertyHtml();
            // ((CmisPropertyHtml)property).getAny().add(element);
            break;
        default:
            throw new AssertionError();
        }
        p.setPropertyDefinitionId(key);
        return p;

    }

    // TODO XXX we shouldn't guess, values should be typed in ObjectEntry
    protected static PropertyType guessType(String key, Serializable value) {
        for (String n : Arrays.asList( //
                Property.ID, //
                Property.TYPE_ID, //
                Property.BASE_TYPE_ID, //
                Property.VERSION_SERIES_ID, //
                Property.VERSION_SERIES_CHECKED_OUT_ID, //
                Property.PARENT_ID, //
                Property.SOURCE_ID, //
                Property.TARGET_ID)) {
            if (key.toUpperCase().endsWith(n.toUpperCase())) {
                return PropertyType.ID;
            }
        }
        if (value instanceof String) {
            return PropertyType.STRING;
        }
        if (value instanceof BigDecimal) {
            return PropertyType.DECIMAL;
        }
        if (value instanceof Number) {
            return PropertyType.INTEGER;
        }
        if (value instanceof Boolean) {
            return PropertyType.BOOLEAN;
        }
        if (value instanceof Calendar) {
            return PropertyType.DATETIME;
        }
        return PropertyType.STRING;
    }

    protected static DatatypeFactory datatypeFactory;

    protected static XMLGregorianCalendar getXMLGregorianCalendar(
            Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new java.lang.RuntimeException(e);
            }
        }
        return datatypeFactory.newXMLGregorianCalendar((GregorianCalendar) calendar);
    }

}
