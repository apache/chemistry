
package org.apache.jackrabbit.cmis.ws.repository;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumPropertiesBase.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumPropertiesBase">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ObjectId"/>
 *     &lt;enumeration value="Uri"/>
 *     &lt;enumeration value="ObjectTypeId"/>
 *     &lt;enumeration value="CreatedBy"/>
 *     &lt;enumeration value="CreationDate"/>
 *     &lt;enumeration value="LastModifiedBy"/>
 *     &lt;enumeration value="LastModificationDate"/>
 *     &lt;enumeration value="ChangeToken"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "enumPropertiesBase")
@XmlEnum
public enum EnumPropertiesBase {

    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("Uri")
    URI("Uri"),
    @XmlEnumValue("ObjectTypeId")
    OBJECT_TYPE_ID("ObjectTypeId"),
    @XmlEnumValue("CreatedBy")
    CREATED_BY("CreatedBy"),
    @XmlEnumValue("CreationDate")
    CREATION_DATE("CreationDate"),
    @XmlEnumValue("LastModifiedBy")
    LAST_MODIFIED_BY("LastModifiedBy"),
    @XmlEnumValue("LastModificationDate")
    LAST_MODIFICATION_DATE("LastModificationDate"),
    @XmlEnumValue("ChangeToken")
    CHANGE_TOKEN("ChangeToken");
    private final String value;

    EnumPropertiesBase(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumPropertiesBase fromValue(String v) {
        for (EnumPropertiesBase c: EnumPropertiesBase.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
