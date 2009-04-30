
package org.apache.jackrabbit.cmis.ws.repository;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumRelationshipDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumRelationshipDirection">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="source"/>
 *     &lt;enumeration value="target"/>
 *     &lt;enumeration value="both"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "enumRelationshipDirection")
@XmlEnum
public enum EnumRelationshipDirection {

    @XmlEnumValue("source")
    SOURCE("source"),
    @XmlEnumValue("target")
    TARGET("target"),
    @XmlEnumValue("both")
    BOTH("both");
    private final String value;

    EnumRelationshipDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumRelationshipDirection fromValue(String v) {
        for (EnumRelationshipDirection c: EnumRelationshipDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
