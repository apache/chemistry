
package org.apache.jackrabbit.cmis.ws.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for cmisTypeDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmisTypeDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="typeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="queryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="displayName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="baseType" type="{http://www.cmis.org/2008/05}enumObjectType"/>
 *         &lt;element name="baseTypeQueryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parentId" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creatable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="fileable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="queryable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="controllable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="includedInSupertypeQuery" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyBooleanDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyDateTimeDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyDecimalDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyHtmlDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyIdDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyIntegerDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyStringDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyUriDefinition"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyXmlDefinition"/>
 *         &lt;/choice>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.cmis.org/2008/05}cmisUndefinedAttribute"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmisTypeDefinitionType", propOrder = {
    "typeId",
    "queryName",
    "displayName",
    "baseType",
    "baseTypeQueryName",
    "parentId",
    "description",
    "creatable",
    "fileable",
    "queryable",
    "controllable",
    "includedInSupertypeQuery",
    "propertyDefinition",
    "any"
})
@XmlSeeAlso({
    CmisTypeFolderDefinitionType.class,
    CmisTypeRelationshipDefinitionType.class,
    CmisTypeDocumentDefinitionType.class,
    CmisTypePolicyDefinitionType.class
})
public class CmisTypeDefinitionType {

    @XmlElement(required = true)
    protected String typeId;
    @XmlElement(required = true)
    protected String queryName;
    @XmlElement(required = true)
    protected String displayName;
    @XmlElement(required = true)
    protected EnumObjectType baseType;
    @XmlElement(required = true)
    protected String baseTypeQueryName;
    protected Object parentId;
    protected String description;
    protected boolean creatable;
    protected boolean fileable;
    protected boolean queryable;
    protected boolean controllable;
    @XmlElement(defaultValue = "true")
    protected boolean includedInSupertypeQuery;
    @XmlElements({
        @XmlElement(name = "propertyUriDefinition", type = CmisPropertyUriDefinitionType.class),
        @XmlElement(name = "propertyDecimalDefinition", type = CmisPropertyDecimalDefinitionType.class),
        @XmlElement(name = "propertyIntegerDefinition", type = CmisPropertyIntegerDefinitionType.class),
        @XmlElement(name = "propertyXmlDefinition", type = CmisPropertyXmlDefinitionType.class),
        @XmlElement(name = "propertyStringDefinition", type = CmisPropertyStringDefinitionType.class),
        @XmlElement(name = "propertyHtmlDefinition", type = CmisPropertyHtmlDefinitionType.class),
        @XmlElement(name = "propertyIdDefinition", type = CmisPropertyIdDefinitionType.class),
        @XmlElement(name = "propertyDateTimeDefinition", type = CmisPropertyDateTimeDefinitionType.class),
        @XmlElement(name = "propertyBooleanDefinition", type = CmisPropertyBooleanDefinitionType.class)
    })
    protected List<CmisPropertyDefinitionType> propertyDefinition;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the typeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeId(String value) {
        this.typeId = value;
    }

    /**
     * Gets the value of the queryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Sets the value of the queryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryName(String value) {
        this.queryName = value;
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the baseType property.
     * 
     * @return
     *     possible object is
     *     {@link EnumObjectType }
     *     
     */
    public EnumObjectType getBaseType() {
        return baseType;
    }

    /**
     * Sets the value of the baseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumObjectType }
     *     
     */
    public void setBaseType(EnumObjectType value) {
        this.baseType = value;
    }

    /**
     * Gets the value of the baseTypeQueryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseTypeQueryName() {
        return baseTypeQueryName;
    }

    /**
     * Sets the value of the baseTypeQueryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseTypeQueryName(String value) {
        this.baseTypeQueryName = value;
    }

    /**
     * Gets the value of the parentId property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setParentId(Object value) {
        this.parentId = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the creatable property.
     * 
     */
    public boolean isCreatable() {
        return creatable;
    }

    /**
     * Sets the value of the creatable property.
     * 
     */
    public void setCreatable(boolean value) {
        this.creatable = value;
    }

    /**
     * Gets the value of the fileable property.
     * 
     */
    public boolean isFileable() {
        return fileable;
    }

    /**
     * Sets the value of the fileable property.
     * 
     */
    public void setFileable(boolean value) {
        this.fileable = value;
    }

    /**
     * Gets the value of the queryable property.
     * 
     */
    public boolean isQueryable() {
        return queryable;
    }

    /**
     * Sets the value of the queryable property.
     * 
     */
    public void setQueryable(boolean value) {
        this.queryable = value;
    }

    /**
     * Gets the value of the controllable property.
     * 
     */
    public boolean isControllable() {
        return controllable;
    }

    /**
     * Sets the value of the controllable property.
     * 
     */
    public void setControllable(boolean value) {
        this.controllable = value;
    }

    /**
     * Gets the value of the includedInSupertypeQuery property.
     * 
     */
    public boolean isIncludedInSupertypeQuery() {
        return includedInSupertypeQuery;
    }

    /**
     * Sets the value of the includedInSupertypeQuery property.
     * 
     */
    public void setIncludedInSupertypeQuery(boolean value) {
        this.includedInSupertypeQuery = value;
    }

    /**
     * Gets the value of the propertyDefinition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propertyDefinition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertyDefinition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CmisPropertyUriDefinitionType }
     * {@link CmisPropertyDecimalDefinitionType }
     * {@link CmisPropertyIntegerDefinitionType }
     * {@link CmisPropertyXmlDefinitionType }
     * {@link CmisPropertyStringDefinitionType }
     * {@link CmisPropertyHtmlDefinitionType }
     * {@link CmisPropertyIdDefinitionType }
     * {@link CmisPropertyDateTimeDefinitionType }
     * {@link CmisPropertyBooleanDefinitionType }
     * 
     * 
     */
    public List<CmisPropertyDefinitionType> getPropertyDefinition() {
        if (propertyDefinition == null) {
            propertyDefinition = new ArrayList<CmisPropertyDefinitionType>();
        }
        return this.propertyDefinition;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
