
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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for cmisPropertiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmisPropertiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyBoolean"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyId"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyInteger"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyDateTime"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyDecimal"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyHtml"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyString"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyUri"/>
 *           &lt;element ref="{http://www.cmis.org/2008/05}propertyXml"/>
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
@XmlType(name = "cmisPropertiesType", propOrder = {
    "property",
    "any"
})
public class CmisPropertiesType {

    @XmlElements({
        @XmlElement(name = "propertyHtml", type = CmisPropertyHtml.class, nillable = true),
        @XmlElement(name = "propertyUri", type = CmisPropertyUri.class, nillable = true),
        @XmlElement(name = "propertyString", type = CmisPropertyString.class, nillable = true),
        @XmlElement(name = "propertyInteger", type = CmisPropertyInteger.class, nillable = true),
        @XmlElement(name = "propertyId", type = CmisPropertyId.class, nillable = true),
        @XmlElement(name = "propertyDecimal", type = CmisPropertyDecimal.class, nillable = true),
        @XmlElement(name = "propertyDateTime", type = CmisPropertyDateTime.class, nillable = true),
        @XmlElement(name = "propertyBoolean", type = CmisPropertyBoolean.class, nillable = true),
        @XmlElement(name = "propertyXml", type = CmisPropertyXml.class, nillable = true)
    })
    protected List<CmisProperty> property;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the property property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CmisPropertyHtml }
     * {@link CmisPropertyUri }
     * {@link CmisPropertyString }
     * {@link CmisPropertyInteger }
     * {@link CmisPropertyId }
     * {@link CmisPropertyDecimal }
     * {@link CmisPropertyDateTime }
     * {@link CmisPropertyBoolean }
     * {@link CmisPropertyXml }
     * 
     * 
     */
    public List<CmisProperty> getProperty() {
        if (property == null) {
            property = new ArrayList<CmisProperty>();
        }
        return this.property;
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
