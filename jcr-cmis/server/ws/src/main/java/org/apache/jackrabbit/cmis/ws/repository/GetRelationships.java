
package org.apache.jackrabbit.cmis.ws.repository;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="repositoryId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="direction" type="{http://www.cmis.org/2008/05}enumRelationshipDirection" minOccurs="0"/>
 *         &lt;element name="typeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="includeSubRelationshipTypes" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="filter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="includeAllowableActions" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="includeRelationships" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="maxItems" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="skipCount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "repositoryId",
    "objectId",
    "direction",
    "typeId",
    "includeSubRelationshipTypes",
    "filter",
    "includeAllowableActions",
    "includeRelationships",
    "maxItems",
    "skipCount"
})
@XmlRootElement(name = "getRelationships")
public class GetRelationships {

    @XmlElement(required = true)
    protected String repositoryId;
    @XmlElement(required = true)
    protected String objectId;
    @XmlElementRef(name = "direction", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<EnumRelationshipDirection> direction;
    @XmlElementRef(name = "typeId", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<String> typeId;
    @XmlElementRef(name = "includeSubRelationshipTypes", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<Boolean> includeSubRelationshipTypes;
    @XmlElementRef(name = "filter", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<String> filter;
    @XmlElementRef(name = "includeAllowableActions", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<Boolean> includeAllowableActions;
    @XmlElementRef(name = "includeRelationships", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<Boolean> includeRelationships;
    @XmlElementRef(name = "maxItems", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<BigInteger> maxItems;
    @XmlElementRef(name = "skipCount", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<BigInteger> skipCount;

    /**
     * Gets the value of the repositoryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryId() {
        return repositoryId;
    }

    /**
     * Sets the value of the repositoryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryId(String value) {
        this.repositoryId = value;
    }

    /**
     * Gets the value of the objectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets the value of the objectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectId(String value) {
        this.objectId = value;
    }

    /**
     * Gets the value of the direction property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EnumRelationshipDirection }{@code >}
     *     
     */
    public JAXBElement<EnumRelationshipDirection> getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EnumRelationshipDirection }{@code >}
     *     
     */
    public void setDirection(JAXBElement<EnumRelationshipDirection> value) {
        this.direction = ((JAXBElement<EnumRelationshipDirection> ) value);
    }

    /**
     * Gets the value of the typeId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTypeId(JAXBElement<String> value) {
        this.typeId = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the includeSubRelationshipTypes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIncludeSubRelationshipTypes() {
        return includeSubRelationshipTypes;
    }

    /**
     * Sets the value of the includeSubRelationshipTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIncludeSubRelationshipTypes(JAXBElement<Boolean> value) {
        this.includeSubRelationshipTypes = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFilter(JAXBElement<String> value) {
        this.filter = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the includeAllowableActions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIncludeAllowableActions() {
        return includeAllowableActions;
    }

    /**
     * Sets the value of the includeAllowableActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIncludeAllowableActions(JAXBElement<Boolean> value) {
        this.includeAllowableActions = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the includeRelationships property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIncludeRelationships() {
        return includeRelationships;
    }

    /**
     * Sets the value of the includeRelationships property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIncludeRelationships(JAXBElement<Boolean> value) {
        this.includeRelationships = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the maxItems property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
     *     
     */
    public JAXBElement<BigInteger> getMaxItems() {
        return maxItems;
    }

    /**
     * Sets the value of the maxItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
     *     
     */
    public void setMaxItems(JAXBElement<BigInteger> value) {
        this.maxItems = ((JAXBElement<BigInteger> ) value);
    }

    /**
     * Gets the value of the skipCount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
     *     
     */
    public JAXBElement<BigInteger> getSkipCount() {
        return skipCount;
    }

    /**
     * Sets the value of the skipCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
     *     
     */
    public void setSkipCount(JAXBElement<BigInteger> value) {
        this.skipCount = ((JAXBElement<BigInteger> ) value);
    }

}
