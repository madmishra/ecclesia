<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="Shipment">
      <OrderMessage>
         <xsl:attribute name="MessageTypeId">LegacyOMS003</xsl:attribute>
         <xsl:attribute name="Modifyts">
            <xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/@Modifyts" />
         </xsl:attribute>
         <xsl:attribute name="LegacyOMSMessageSequenceNumber">
         </xsl:attribute>
         <MessageBody>
            <Order>
               <xsl:attribute name="LegacyOMSOrderNo">
                  <xsl:value-of select="ShipmentLines/ShipmentLine/Extn/@ExtnLegacyOMSChildOrderNo" />
               </xsl:attribute>
               <xsl:attribute name="EnterpriseCode">
                  <xsl:value-of select="@SellerOrganizationCode" />
               </xsl:attribute>
               <xsl:attribute name="DocumentType">
                  <xsl:value-of select="ShipmentLines/ShipmentLine/@DocumentType" />
               </xsl:attribute>
               <xsl:attribute name="OrderType">
                  <xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/@OrderType" />
               </xsl:attribute>
            </Order>
         </MessageBody>
      </OrderMessage>
   </xsl:template>
</xsl:stylesheet>