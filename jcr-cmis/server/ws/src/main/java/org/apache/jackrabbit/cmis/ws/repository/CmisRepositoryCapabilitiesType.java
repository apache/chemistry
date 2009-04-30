
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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for cmisRepositoryCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmisRepositoryCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="capabilityMultifiling" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="capabilityUnfiling" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="capabilityVersionSpecificFiling" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="capabilityPWCUpdateable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="capabilityPWCSearchable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="capabilityAllVersionsSearchable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="capabilityQuery" type="{http://www.cmis.org/2008/05}enumCapabilityQuery"/>
 *         &lt;element name="capabilityJoin" type="{http://www.cmis.org/2008/05}enumCapabilityJoin"/>
 *         &lt;element name="capabilityFullText" type="{http://www.cmis.org/2008/05}enumCapabilityFullText"/>
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
@XmlType(name = "cmisRepositoryCapabilitiesType", propOrder = {
    "capabilityMultifiling",
    "capabilityUnfiling",
    "capabilityVersionSpecificFiling",
    "capabilityPWCUpdateable",
    "capabilityPWCSearchable",
    "capabilityAllVersionsSearchable",
    "capabilityQuery",
    "capabilityJoin",
    "capabilityFullText",
    "any"
})
public class CmisRepositoryCapabilitiesType {

    protected boolean capabilityMultifiling;
    protected boolean capabilityUnfiling;
    protected boolean capabilityVersionSpecificFiling;
    protected boolean capabilityPWCUpdateable;
    protected boolean capabilityPWCSearchable;
    protected boolean capabilityAllVersionsSearchable;
    @XmlElement(required = true)
    protected EnumCapabilityQuery capabilityQuery;
    @XmlElement(required = true)
    protected EnumCapabilityJoin capabilityJoin;
    @XmlElement(required = true)
    protected EnumCapabilityFullText capabilityFullText;
    @XmlAnyElement
    protected List<Element> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the capabilityMultifiling property.
     * 
     */
    public boolean isCapabilityMultifiling() {
        return capabilityMultifiling;
    }

    /**
     * Sets the value of the capabilityMultifiling property.
     * 
     */
    public void setCapabilityMultifiling(boolean value) {
        this.capabilityMultifiling = value;
    }

    /**
     * Gets the value of the capabilityUnfiling property.
     * 
     */
    public boolean isCapabilityUnfiling() {
        return capabilityUnfiling;
    }

    /**
     * Sets the value of the capabilityUnfiling property.
     * 
     */
    public void setCapabilityUnfiling(boolean value) {
        this.capabilityUnfiling = value;
    }

    /**
     * Gets the value of the capabilityVersionSpecificFiling property.
     * 
     */
    public boolean isCapabilityVersionSpecificFiling() {
        return capabilityVersionSpecificFiling;
    }

    /**
     * Sets the value of the capabilityVersionSpecificFiling property.
     * 
     */
    public void setCapabilityVersionSpecificFiling(boolean value) {
        this.capabilityVersionSpecificFiling = value;
    }

    /**
     * Gets the value of the capabilityPWCUpdateable property.
     * 
     */
    public boolean isCapabilityPWCUpdateable() {
        return capabilityPWCUpdateable;
    }

    /**
     * Sets the value of the capabilityPWCUpdateable property.
     * 
     */
    public void setCapabilityPWCUpdateable(boolean value) {
        this.capabilityPWCUpdateable = value;
    }

    /**
     * Gets the value of the capabilityPWCSearchable property.
     * 
     */
    public boolean isCapabilityPWCSearchable() {
        return capabilityPWCSearchable;
    }

    /**
     * Sets the value of the capabilityPWCSearchable property.
     * 
     */
    public void setCapabilityPWCSearchable(boolean value) {
        this.capabilityPWCSearchable = value;
    }

    /**
     * Gets the value of the capabilityAllVersionsSearchable property.
     * 
     */
    public boolean isCapabilityAllVersionsSearchable() {
        return capabilityAllVersionsSearchable;
    }

    /**
     * Sets the value of the capabilityAllVersionsSearchable property.
     * 
     */
    public void setCapabilityAllVersionsSearchable(boolean value) {
        this.capabilityAllVersionsSearchable = value;
    }

    /**
     * Gets the value of the capabilityQuery property.
     * 
     * @return
     *     possible object is
     *     {@link EnumCapabilityQuery }
     *     
     */
    public EnumCapabilityQuery getCapabilityQuery() {
        return capabilityQuery;
    }

    /**
     * Sets the value of the capabilityQuery property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumCapabilityQuery }
     *     
     */
    public void setCapabilityQuery(EnumCapabilityQuery value) {
        this.capabilityQuery = value;
    }

    /**
     * Gets the value of the capabilityJoin property.
     * 
     * @return
     *     possible object is
     *     {@link EnumCapabilityJoin }
     *     
     */
    public EnumCapabilityJoin getCapabilityJoin() {
        return capabilityJoin;
    }

    /**
     * Sets the value of the capabilityJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumCapabilityJoin }
     *     
     */
    public void setCapabilityJoin(EnumCapabilityJoin value) {
        this.capabilityJoin = value;
    }

    /**
     * Gets the value of the capabilityFullText property.
     * 
     * @return
     *     possible object is
     *     {@link EnumCapabilityFullText }
     *     
     */
    public EnumCapabilityFullText getCapabilityFullText() {
        return capabilityFullText;
    }

    /**
     * Sets the value of the capabilityFullText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumCapabilityFullText }
     *     
     */
    public void setCapabilityFullText(EnumCapabilityFullText value) {
        this.capabilityFullText = value;
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
     * 
     * 
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<Element>();
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
