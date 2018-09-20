/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * Gesture config entity class to write to xml
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@XmlRootElement(name = "gesture-configuration")
public class GestureConfiguration {

    private static JAXBContext jaxbContext = null;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(GestureConfiguration.class);
        } catch (javax.xml.bind.JAXBException excp) {
            excp.printStackTrace();
        }
    }
    private Map<String, Boolean> gesturesMap = new HashMap<String, Boolean>();

    @XmlElement
    public Map<String, Boolean> getGesturesMap() {
        return gesturesMap;
    }

    public void setGesturesMap(Map<String, Boolean> gesturesMap) {
        this.gesturesMap = gesturesMap;
    }

    public static GestureConfiguration decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        GestureConfiguration configQuestionDialog = (GestureConfiguration) unmarshaller.unmarshal(r);
        return configQuestionDialog;
    }

    public void encode(Writer w) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

}
