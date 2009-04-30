
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
 * <p>Java class for cmisPropertyDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmisPropertyDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="displayName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="propertyType" type="{http://www.cmis.org/2008/05}enumPropertyType"/>
 *         &lt;element name="cardinality" type="{http://www.cmis.org/2008/05}enumCardinality"/>
 *         &lt;element name="updateability" type="{http://www.cmis.org/2008/05}enumUpdateability"/>
 *         &lt;element name="inherited" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="required" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="queryable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="orderable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceBoolean"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceDateTime"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceDecimal"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceHtml"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceId"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceInteger"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceString"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceUri"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}choiceXml"/>
 *         &lt;/choice>
 *         &lt;element name="openChoice" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
@XmlType(name = "cmisPropertyDefinitionType", propOrder = {
    "name",
    "id",
    "displayName",
    "description",
    "propertyType",
    "cardinality",
    "updateability",
    "inherited",
    "required",
    "queryable",
    "orderable",
    "choice",
    "openChoice",
    "any"
})
@XmlSeeAlso({
    CmisPropertyHtmlDefinitionType.class,
    CmisPropertyIntegerDefinitionType.class,
    CmisPropertyBooleanDefinitionType.class,
    CmisPropertyIdDefinitionType.class,
    CmisPropertyStringDefinitionType.class,
    CmisPropertyDecimalDefinitionType.class,
    CmisPropertyDateTimeDefinitionType.class,
    CmisPropertyUriDefinitionType.class,
    CmisPropertyXmlDefinitionType.class
})
public class CmisPropertyDefinitionType {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String displayName;
    protected String description;
    @XmlElement(required = true)
    protected EnumPropertyType propertyType;
    @XmlElement(required = true)
    protected EnumCardinality cardinality;
    @XmlElement(required = true)
    protected EnumUpdateability updateability;
    protected Boolean inherited;
    protected boolean required;
    protected boolean queryable;
    protected boolean orderable;
    @XmlElements({
        @XmlElement(name = "choiceDateTime", type = CmisChoiceDateTimeType.class),
        @XmlElement(name = "choiceString", type = CmisChoiceStringType.class),
        @XmlElement(name = "choiceId", type = CmisChoiceIdType.class),
        @XmlElement(name = "choiceBoolean", type = CmisChoiceBooleanType.class),
        @XmlElement(name = "choiceInteger", type = CmisChoiceIntegerType.class),
        @XmlElement(name = "choiceDecimal", type = CmisChoiceDecimalType.class),
        @XmlElement(name = "choiceXml", type = CmisChoiceXmlType.class),
        @XmlElement(name = "choiceHtml", type = CmisChoiceHtmlType.class),
        @XmlElement(name = "choiceUri", type = CmisChoiceUriType.class)
    })
    protected List<CmisChoiceType> choice;
    protected Boolean openChoice;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
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
     * Gets the value of the propertyType property.
     * 
     * @return
     *     possible object is
     *     {@link EnumPropertyType }
     *     
     */
    public EnumPropertyType getPropertyType() {
        return propertyType;
    }

    /**
     * Sets the value of the propertyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumPropertyType }
     *     
     */
    public void setPropertyType(EnumPropertyType value) {
        this.propertyType = value;
    }

    /**
     * Gets the value of the cardinality property.
     * 
     * @return
     *     possible object is
     *     {@link EnumCardinality }
     *     
     */
    public EnumCardinality getCardinality() {
        return cardinality;
    }

    /**
     * Sets the value of the cardinality property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumCardinality }
     *     
     */
    public void setCardinality(EnumCardinality value) {
        this.cardinality = value;
    }

    /**
     * Gets the value of the updateability property.
     * 
     * @return
     *     possible object is
     *     {@link EnumUpdateability }
     *     
     */
    public EnumUpdateability getUpdateability() {
        return updateability;
    }

    /**
     * Sets the value of the updateability property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumUpdateability }
     *     
     */
    public void setUpdateability(EnumUpdateability value) {
        this.updateability = value;
    }

    /**
     * Gets the value of the inherited property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInherited() {
        return inherited;
    }

    /**
     * Sets the value of the inherited property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInherited(Boolean value) {
        this.inherited = value;
    }

    /**
     * Gets the value of the required property.
     * 
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the value of the required property.
     * 
     */
    public void setRequired(boolean value) {
        this.required = value;
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
     * Gets the value of the orderable property.
     * 
     */
    public boolean isOrderable() {
        return orderable;
    }

    /**
     * Sets the value of the orderable property.
     * 
     */
    public void setOrderable(boolean value) {
        this.orderable = value;
    }

    /**
     * Gets the value of the choice property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the choice property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChoice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CmisChoiceDateTimeType }
     * {@link CmisChoiceStringType }
     * {@link CmisChoiceIdType }
     * {@link CmisChoiceBooleanType }
     * {@link CmisChoiceIntegerType }
     * {@link CmisChoiceDecimalType }
     * {@link CmisChoiceXmlType }
     * {@link CmisChoiceHtmlType }
     * {@link CmisChoiceUriType }
     * 
     * 
     */
    public List<CmisChoiceType> getChoice() {
        if (choice == null) {
            choice = new ArrayList<CmisChoiceType>();
        }
        return this.choice;
    }

    /**
     * Gets the value of the openChoice property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOpenChoice() {
        return openChoice;
    }

    /**
     * Sets the value of the openChoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOpenChoice(Boolean value) {
        this.openChoice = value;
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
