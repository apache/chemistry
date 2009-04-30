
package org.apache.jackrabbit.cmis.ws.repository;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmisPropertyUriDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmisPropertyUriDefinitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.cmis.org/2008/05}cmisPropertyDefinitionType">
 *       &lt;sequence>
 *         &lt;element name="defaultValue" type="{http://www.cmis.org/2008/05}cmisChoiceUriType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmisPropertyUriDefinitionType", propOrder = {
    "defaultValue"
})
public class CmisPropertyUriDefinitionType
    extends CmisPropertyDefinitionType
{

    protected List<CmisChoiceUriType> defaultValue;

    /**
     * Gets the value of the defaultValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the defaultValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDefaultValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CmisChoiceUriType }
     * 
     * 
     */
    public List<CmisChoiceUriType> getDefaultValue() {
        if (defaultValue == null) {
            defaultValue = new ArrayList<CmisChoiceUriType>();
        }
        return this.defaultValue;
    }

}
