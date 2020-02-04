/**
 * SCServiceServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.5  Built on : May 06, 2017 (03:45:26 BST)
 */
package com.chinatowercom.scservice;


/**
 *  SCServiceServiceMessageReceiverInOut message receiver
 */
public class SCServiceServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {
    public void invokeBusinessLogic(
        org.apache.axis2.context.MessageContext msgContext,
        org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault {
        try {
            // get the implementation class for the Web Service
            Object obj = getTheImplementationObject(msgContext);

            SCServiceServiceSkeletonInterface skel = (SCServiceServiceSkeletonInterface) obj;

            //Out Envelop
            org.apache.axiom.soap.SOAPEnvelope envelope = null;

            //Find the axisOperation that has been set by the Dispatch phase.
            org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext()
                                                                      .getAxisOperation();

            if (op == null) {
                throw new org.apache.axis2.AxisFault(
                    "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }

            String methodName;

            if ((op.getName() != null) &&
                    ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(
                            op.getName().getLocalPart())) != null)) {
                if ("invoke".equals(methodName)) {
                    com.chinatowercom.scservice.InvokeResponse invokeResponse7 = null;
                    com.chinatowercom.scservice.Invoke wrappedParam = (com.chinatowercom.scservice.Invoke) fromOM(msgContext.getEnvelope()
                                                                                                                            .getBody()
                                                                                                                            .getFirstElement(),
                            com.chinatowercom.scservice.Invoke.class);

                    invokeResponse7 = skel.invoke(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            invokeResponse7, false,
                            new javax.xml.namespace.QName(
                                "http://SCService.kongtrolink.framework.com",
                                "invokeResponse"));
                } else {
                    throw new RuntimeException("method not found");
                }

                newMsgContext.setEnvelope(envelope);
            }
        } catch (Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    //
    private org.apache.axiom.om.OMElement toOM(
            com.chinatowercom.scservice.Invoke param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(com.chinatowercom.scservice.Invoke.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        com.chinatowercom.scservice.InvokeResponse param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(com.chinatowercom.scservice.InvokeResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        com.chinatowercom.scservice.InvokeResponse param,
        boolean optimizeContent, javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    com.chinatowercom.scservice.InvokeResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private com.chinatowercom.scservice.InvokeResponse wrapinvoke() {
        com.chinatowercom.scservice.InvokeResponse wrappedElement = new com.chinatowercom.scservice.InvokeResponse();

        return wrappedElement;
    }

    /**
     *  get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private Object fromOM(org.apache.axiom.om.OMElement param,
        Class type) throws org.apache.axis2.AxisFault {
        try {
            if (com.chinatowercom.scservice.Invoke.class.equals(type)) {
                return com.chinatowercom.scservice.Invoke.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (com.chinatowercom.scservice.InvokeResponse.class.equals(type)) {
                return com.chinatowercom.scservice.InvokeResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
        } catch (Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

        return null;
    }

    private org.apache.axis2.AxisFault createAxisFault(Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();

        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }
} //end of class
