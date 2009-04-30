
package org.apache.jackrabbit.cmis.ws.repository;

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
 *         &lt;element name="folderId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="unfileNonfolderObjects" type="{http://www.cmis.org/2008/05}enumUnfileNonfolderObjects"/>
 *         &lt;element name="continueOnFailure" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "folderId",
    "unfileNonfolderObjects",
    "continueOnFailure"
})
@XmlRootElement(name = "deleteTree")
public class DeleteTree {

    @XmlElement(required = true)
    protected String repositoryId;
    @XmlElement(required = true)
    protected String folderId;
    @XmlElement(required = true)
    protected EnumUnfileNonfolderObjects unfileNonfolderObjects;
    @XmlElementRef(name = "continueOnFailure", namespace = "http://www.cmis.org/2008/05", type = JAXBElement.class)
    protected JAXBElement<Boolean> continueOnFailure;

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
     * Gets the value of the folderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderId() {
        return folderId;
    }

    /**
     * Sets the value of the folderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderId(String value) {
        this.folderId = value;
    }

    /**
     * Gets the value of the unfileNonfolderObjects property.
     * 
     * @return
     *     possible object is
     *     {@link EnumUnfileNonfolderObjects }
     *     
     */
    public EnumUnfileNonfolderObjects getUnfileNonfolderObjects() {
        return unfileNonfolderObjects;
    }

    /**
     * Sets the value of the unfileNonfolderObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumUnfileNonfolderObjects }
     *     
     */
    public void setUnfileNonfolderObjects(EnumUnfileNonfolderObjects value) {
        this.unfileNonfolderObjects = value;
    }

    /**
     * Gets the value of the continueOnFailure property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getContinueOnFailure() {
        return continueOnFailure;
    }

    /**
     * Sets the value of the continueOnFailure property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setContinueOnFailure(JAXBElement<Boolean> value) {
        this.continueOnFailure = ((JAXBElement<Boolean> ) value);
    }

}
