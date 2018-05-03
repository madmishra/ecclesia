<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/OrderRelease">
      <OrderMessage>
         <xsl:attribute name="MessageTypeId">LegacyOMS003</xsl:attribute>
         <xsl:attribute name="Modifyts">
            <xsl:value-of select="OrderLine/OrderStatuses/OrderStatus/@StatusDate" />
         </xsl:attribute>
         <xsl:attribute name="LegacyOMSMessageSequenceNumber">
            <xsl:value-of select="@LegacyOMSMessageSequenceNumber" />
         </xsl:attribute>
         <MessageBody>
            <Order>
               <xsl:attribute name="LegacyOMSOrderNo">
                  <xsl:value-of select="OrderLine/Extn/@ExtnLegacyOMSChildOrderNo" />
               </xsl:attribute>
               <xsl:attribute name="EnterpriseCode">
                  <xsl:value-of select="@EnterpriseCode" />
               </xsl:attribute>
               <xsl:attribute name="DocumentType">
                  <xsl:value-of select="Order/@DocumentType" />
               </xsl:attribute>
               <xsl:attribute name="OrderType">
                  <xsl:value-of select="@OrderType" />
               </xsl:attribute>
            </Order>
         </MessageBody>
      </OrderMessage>
   </xsl:template>
</xsl:stylesheet>