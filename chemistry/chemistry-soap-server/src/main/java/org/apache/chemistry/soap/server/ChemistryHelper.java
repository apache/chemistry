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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.chemistry.AllowableAction;
import org.apache.chemistry.BaseType;
import org.apache.chemistry.CapabilityJoin;
import org.apache.chemistry.CapabilityQuery;
import org.apache.chemistry.ConstraintViolationException;
import org.apache.chemistry.ContentAlreadyExistsException;
import org.apache.chemistry.ContentStream;
import org.apache.chemistry.FilterNotValidException;
import org.apache.chemistry.ListPage;
import org.apache.chemistry.NameConstraintViolationException;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectNotFoundException;
import org.apache.chemistry.PermissionDeniedException;
import org.apache.chemistry.Property;
import org.apache.chemistry.PropertyDefinition;
import org.apache.chemistry.PropertyType;
import org.apache.chemistry.RelationshipDirection;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryCapabilities;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.StorageException;
import org.apache.chemistry.StreamNotSupportedException;
import org.apache.chemistry.Tree;
import org.apache.chemistry.Type;
import org.apache.chemistry.Updatability;
import org.apache.chemistry.UpdateConflictException;
import org.apache.chemistry.VersioningException;
import org.apache.chemistry.VersioningState;
import org.apache.chemistry.impl.simple.SimpleContentStream;
import org.apache.chemistry.ws.CmisAllowableActionsType;
import org.apache.chemistry.ws.CmisContentStreamType;
import org.apache.chemistry.ws.CmisException;
import org.apache.chemistry.ws.CmisFaultType;
import org.apache.chemistry.ws.CmisObjectInFolderContainerType;
import org.apache.chemistry.ws.CmisObjectInFolderListType;
import org.apache.chemistry.ws.CmisObjectInFolderType;
import org.apache.chemistry.ws.CmisObjectListType;
import org.apache.chemistry.ws.CmisObjectParentsType;
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
import org.apache.chemistry.ws.EnumIncludeRelationships;
import org.apache.chemistry.ws.EnumPropertyType;
import org.apache.chemistry.ws.EnumServiceException;
import org.apache.chemistry.ws.EnumUpdatability;
import org.apache.chemistry.ws.EnumVersioningState;
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

    public static CmisException convert(Exception e) {
        CmisFaultType fault = factory.createCmisFaultType();
        if (e instanceof ConstraintViolationException) {
            fault.setType(EnumServiceException.CONSTRAINT);
        } else if (e instanceof ContentAlreadyExistsException) {
            fault.setType(EnumServiceException.CONTENT_ALREADY_EXISTS);
        } else if (e instanceof FilterNotValidException) {
            fault.setType(EnumServiceException.FILTER_NOT_VALID);
        } else if (e instanceof IllegalArgumentException) {
            fault.setType(EnumServiceException.INVALID_ARGUMENT);
        } else if (e instanceof NameConstraintViolationException) {
            fault.setType(EnumServiceException.NAME_CONSTRAINT_VIOLATION);
        } else if (e instanceof UnsupportedOperationException) {
            fault.setType(EnumServiceException.NOT_SUPPORTED);
        } else if (e instanceof ObjectNotFoundException) {
            fault.setType(EnumServiceException.OBJECT_NOT_FOUND);
        } else if (e instanceof PermissionDeniedException) {
            fault.setType(EnumServiceException.PERMISSION_DENIED);
        } else if (e instanceof StorageException) {
            fault.setType(EnumServiceException.STORAGE);
        } else if (e instanceof StreamNotSupportedException) {
            fault.setType(EnumServiceException.STREAM_NOT_SUPPORTED);
        } else if (e instanceof UpdateConflictException) {
            fault.setType(EnumServiceException.UPDATE_CONFLICT);
        } else if (e instanceof VersioningException) {
            fault.setType(EnumServiceException.VERSIONING);
        } else {
            fault.setType(EnumServiceException.RUNTIME);
        }
        fault.setCode(BigInteger.ZERO);
        fault.setMessage(e.getMessage());
        return new CmisException(e.getMessage(), fault, e);
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

    public static RelationshipDirection convert(
            EnumIncludeRelationships includeRelationships) {
        if (includeRelationships == null) {
            return null;
        }
        switch (includeRelationships) {
        case NONE:
            return null;
        case SOURCE:
            return RelationshipDirection.SOURCE;
        case TARGET:
            return RelationshipDirection.TARGET;
        case BOTH:
            return RelationshipDirection.EITHER;
        default:
            throw new RuntimeException(includeRelationships.name());
        }
    }

    public static VersioningState convert(EnumVersioningState versioningState) {
        if (versioningState == null) {
            return null;
        }
        switch (versioningState) {
        case NONE:
            return null;
        case CHECKEDOUT:
            return VersioningState.CHECKED_OUT;
        case MINOR:
            return VersioningState.MINOR;
        case MAJOR:
            return VersioningState.MAJOR;
        default:
            throw new RuntimeException(versioningState.name());
        }
    }

    public static CmisAllowableActionsType convert(Set<QName> set) {
        if (set == null) {
            return null;
        }
        CmisAllowableActionsType aa = factory.createCmisAllowableActionsType();
        aa.setCanDeleteObject(Boolean.valueOf(set.contains(AllowableAction.CAN_DELETE_OBJECT)));
        aa.setCanUpdateProperties(Boolean.valueOf(set.contains(AllowableAction.CAN_UPDATE_PROPERTIES)));
        aa.setCanGetFolderTree(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_FOLDER_TREE)));
        aa.setCanGetProperties(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_PROPERTIES)));
        aa.setCanGetObjectRelationships(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_OBJECT_RELATIONSHIPS)));
        aa.setCanGetObjectParents(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_OBJECT_PARENTS)));
        aa.setCanGetFolderParent(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_FOLDER_PARENT)));
        aa.setCanGetDescendants(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_DESCENDANTS)));
        aa.setCanMoveObject(Boolean.valueOf(set.contains(AllowableAction.CAN_MOVE_OBJECT)));
        aa.setCanDeleteContentStream(Boolean.valueOf(set.contains(AllowableAction.CAN_DELETE_CONTENT_STREAM)));
        aa.setCanCheckOut(Boolean.valueOf(set.contains(AllowableAction.CAN_CHECK_OUT)));
        aa.setCanCancelCheckOut(Boolean.valueOf(set.contains(AllowableAction.CAN_CANCEL_CHECK_OUT)));
        aa.setCanCheckIn(Boolean.valueOf(set.contains(AllowableAction.CAN_CHECK_IN)));
        aa.setCanSetContentStream(Boolean.valueOf(set.contains(AllowableAction.CAN_SET_CONTENT_STREAM)));
        aa.setCanGetAllVersions(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_ALL_VERSIONS)));
        aa.setCanAddObjectToFolder(Boolean.valueOf(set.contains(AllowableAction.CAN_ADD_OBJECT_TO_FOLDER)));
        aa.setCanRemoveObjectFromFolder(Boolean.valueOf(set.contains(AllowableAction.CAN_REMOVE_OBJECT_FROM_FOLDER)));
        aa.setCanGetContentStream(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_CONTENT_STREAM)));
        aa.setCanApplyPolicy(Boolean.valueOf(set.contains(AllowableAction.CAN_APPLY_POLICY)));
        aa.setCanGetAppliedPolicies(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_APPLIED_POLICIES)));
        aa.setCanRemovePolicy(Boolean.valueOf(set.contains(AllowableAction.CAN_REMOVE_POLICY)));
        aa.setCanGetChildren(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_CHILDREN)));
        aa.setCanCreateDocument(Boolean.valueOf(set.contains(AllowableAction.CAN_CREATE_DOCUMENT)));
        aa.setCanCreateFolder(Boolean.valueOf(set.contains(AllowableAction.CAN_CREATE_FOLDER)));
        aa.setCanCreateRelationship(Boolean.valueOf(set.contains(AllowableAction.CAN_CREATE_RELATIONSHIP)));
        aa.setCanDeleteTree(Boolean.valueOf(set.contains(AllowableAction.CAN_DELETE_TREE)));
        aa.setCanGetRenditions(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_RENDITIONS)));
        aa.setCanGetACL(Boolean.valueOf(set.contains(AllowableAction.CAN_GET_ACL)));
        aa.setCanApplyACL(Boolean.valueOf(set.contains(AllowableAction.CAN_APPLY_ACL)));
        return aa;
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
        if (ctl == null) {
            return null;
        }
        CmisTypeDefinitionListType tl = factory.createCmisTypeDefinitionListType();
        for (Type ct : ctl) {
            tl.getTypes().add(convert(ct));
        }
        tl.setHasMoreItems(ctl.getHasMoreItems());
        tl.setNumItems(BigInteger.valueOf(ctl.getNumItems()));
        return tl;
    }

    public static List<CmisTypeContainer> convert(Collection<Type> ctl) {
        if (ctl == null) {
            return null;
        }
        List<CmisTypeContainer> list = new ArrayList<CmisTypeContainer>(
                ctl.size());
        // for (Type ct : ctl) {
        // XXX
        // }
        return list;
    }

    public static CmisObjectType convert(ObjectEntry entry) {
        CmisObjectType object = factory.createCmisObjectType();
        object.setProperties(convertProperties(entry));
        // object.setAllowableActions(null);
        return object;
    }

    public static ContentStream convert(CmisContentStreamType contentStream) {
        if (contentStream == null) {
            return null;
        }
        DataHandler dataHandler = contentStream.getStream();
        InputStream stream;
        if (dataHandler == null) {
            stream = null;
        } else {
            try {
                // if (dh instanceof StreamingDataHandler) {
                // stream = ((StreamingDataHandler) dh).readOnce();
                // } else {
                stream = contentStream.getStream().getInputStream();
            } catch (IOException e) {
                throw new RuntimeException("Could not get the stream: "
                        + e.getMessage(), e);
            }
        }
        String mimeType = contentStream.getMimeType();
        String filename = contentStream.getFilename();
        try {
            return new SimpleContentStream(stream, mimeType, filename);
        } catch (IOException e) {
            throw new RuntimeException("Could not get the stream: "
                    + e.getMessage(), e);
        }
    }

    public static CmisObjectInFolderType convertInFolder(ObjectEntry entry) {
        CmisObjectInFolderType object = factory.createCmisObjectInFolderType();
        object.getObject().setProperties(convertProperties(entry));
        object.setPathSegment(entry.getPathSegment());
        return object;
    }

    public static List<CmisObjectParentsType> convertParents(
            Collection<ObjectEntry> entries) {
        List<CmisObjectParentsType> list = new ArrayList<CmisObjectParentsType>(
                entries.size());
        for (ObjectEntry entry : entries) {
            list.add(convertParent(entry));
        }
        return list;
    }

    public static CmisObjectParentsType convertParent(ObjectEntry entry) {
        CmisObjectParentsType object = factory.createCmisObjectParentsType();
        object.setObject(convert(entry));
        object.setRelativePathSegment(entry.getPathSegment());
        return object;
    }

    public static CmisPropertiesType convertProperties(ObjectEntry entry) {
        CmisPropertiesType properties = factory.createCmisPropertiesType();
        List<CmisProperty> list = properties.getProperty();
        for (Entry<String, Serializable> e : entry.getValues().entrySet()) {
            list.add(getWSCmisProperty(e.getKey(), e.getValue()));
        }
        return properties;
    }

    public static Map<String, Serializable> convert(
            CmisPropertiesType properties, Repository repository) {
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        List<CmisProperty> list = properties.getProperty();
        for (CmisProperty prop : list) {
            Serializable value = convert(prop, repository);
            map.put(prop.getPropertyDefinitionId(), value);
        }
        return map;
    }

    public static CmisObjectListType convert(ListPage<ObjectEntry> entries) {
        CmisObjectListType list = factory.createCmisObjectListType();
        for (ObjectEntry entry : entries) {
            list.getObjects().add(convert(entry));
        }
        list.setHasMoreItems(entries.getHasMoreItems());
        list.setNumItems(BigInteger.valueOf(entries.getNumItems()));
        return list;
    }

    public static CmisObjectInFolderListType convertInFolder(
            ListPage<ObjectEntry> entries) {
        CmisObjectInFolderListType list = factory.createCmisObjectInFolderListType();
        for (ObjectEntry entry : entries) {
            list.getObjects().add(convertInFolder(entry));
        }
        list.setHasMoreItems(entries.getHasMoreItems());
        list.setNumItems(BigInteger.valueOf(entries.getNumItems()));
        return list;
    }

    public static List<CmisObjectInFolderContainerType> convertForest(
            Tree<ObjectEntry> tree) {
        List<CmisObjectInFolderContainerType> list = new ArrayList<CmisObjectInFolderContainerType>(
                tree.getChildren().size());
        for (Tree<ObjectEntry> s : tree.getChildren()) {
            list.add(convert(s));
        }
        return list;
    }

    public static CmisObjectInFolderContainerType convert(Tree<ObjectEntry> s) {
        CmisObjectInFolderContainerType object = factory.createCmisObjectInFolderContainerType();
        object.setObjectInFolder(convertInFolder(s.getNode()));
        object.getChildren().addAll(convertForest(s));
        return object;
    }

    public static QueryResponse convertQuery(ListPage<ObjectEntry> entries) {
        QueryResponse response = factory.createQueryResponse();
        CmisObjectListType objects = factory.createCmisObjectListType();
        response.setObjects(objects);
        for (ObjectEntry entry : entries) {
            objects.getObjects().add(convert(entry));
        }
        objects.setHasMoreItems(entries.getHasMoreItems());
        objects.setNumItems(BigInteger.valueOf(entries.getNumItems()));
        return response;
    }

    public static CmisContentStreamType convert(final ContentStream cs) {
        if (cs == null) {
            return null;
        }
        CmisContentStreamType s = factory.createCmisContentStreamType();
        s.setMimeType(cs.getMimeType());
        s.setLength(BigInteger.valueOf(cs.getLength()));
        s.setFilename(cs.getFileName());
        s.setStream(new DataHandler(new DataSource() {
            public InputStream getInputStream() throws IOException {
                return cs.getStream();
            }

            public OutputStream getOutputStream() {
                return null;
            }

            public String getContentType() {
                return cs.getMimeType();
            }

            public String getName() {
                return cs.getFileName();
            }
        }));
        return s;
    }

    public static Serializable convert(CmisProperty prop, Repository repository) {
        PropertyDefinition pd = repository.getPropertyDefinition(prop.getPropertyDefinitionId());
        boolean multi = pd.isMultiValued();
        if (prop instanceof CmisPropertyBoolean) {
            List<Boolean> value = ((CmisPropertyBoolean) prop).getValue();
            return multi ? value.toArray() : value.get(0);
        } else if (prop instanceof CmisPropertyDateTime) {
            List<XMLGregorianCalendar> value = ((CmisPropertyDateTime) prop).getValue();
            return multi ? value.toArray() : convert(value.get(0));
        } else if (prop instanceof CmisPropertyDecimal) {
            List<BigDecimal> value = ((CmisPropertyDecimal) prop).getValue();
            return multi ? value.toArray() : value.get(0);
        } else if (prop instanceof CmisPropertyHtml) {
            List<String> value = ((CmisPropertyHtml) prop).getValue();
            return multi ? value.toArray() : value.get(0);
        } else if (prop instanceof CmisPropertyId) {
            List<String> value = ((CmisPropertyId) prop).getValue();
            return multi ? value.toArray() : value.get(0);
        } else if (prop instanceof CmisPropertyInteger) {
            List<BigInteger> value = ((CmisPropertyInteger) prop).getValue();
            return multi ? convert(value) : convert(value.get(0));
        } else if (prop instanceof CmisPropertyString) {
            List<String> value = ((CmisPropertyString) prop).getValue();
            return multi ? value.toArray() : value.get(0);
        } else if (prop instanceof CmisPropertyUri) {
            List<String> value = ((CmisPropertyUri) prop).getValue();
            return multi ? value.toArray() : value.get(0);
        } else {
            throw new RuntimeException(prop.getClass().getName());
        }
    }

    public static Calendar convert(XMLGregorianCalendar value) {
        if (value == null) {
            return null;
        }
        return value.toGregorianCalendar();
    }

    public static Calendar[] convert(List<XMLGregorianCalendar> values) {
        if (values == null) {
            return null;
        }
        List<Calendar> list = new ArrayList<Calendar>(values.size());
        for (XMLGregorianCalendar value : values) {
            list.add(convert(value));
        }
        return (Calendar[]) list.toArray();
    }

    public static Long convert(BigInteger value) {
        if (value == null) {
            return null;
        }
        return Long.valueOf(value.longValue());
    }

    public static Long[] convert(List<BigInteger> values) {
        if (values == null) {
            return null;
        }
        List<Long> list = new ArrayList<Long>(values.size());
        for (BigInteger value : values) {
            list.add(convert(value));
        }
        return (Long[]) list.toArray();
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
