//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.03 at 01:48:51 AM EDT 
//


package org.oscarehr.hospitalReportManager.xsd;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="reportFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;maxLength value="50"/>
 *     &lt;enumeration value="Text"/>
 *     &lt;enumeration value="Audio File"/>
 *     &lt;enumeration value="Image"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "reportFormat")
@XmlEnum
public enum ReportFormat {

    @XmlEnumValue("Text")
    TEXT("Text"),
    @XmlEnumValue("Audio File")
    AUDIO_FILE("Audio File"),
    @XmlEnumValue("Image")
    IMAGE("Image");
    private final String value;

    ReportFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReportFormat fromValue(String v) {
        for (ReportFormat c: ReportFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}