<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="Order">
      <OrderMessage>
         <xsl:attribute name="MessageTypeId">LegacyOMS052</xsl:attribute>
         <xsl:attribute name="Modifyts">
            <xsl:value-of select="/OrderMessage/@Modifyts" />
         </xsl:attribute>
         <xsl:attribute name="LegacyOMSMessageSequenceNumber">
            <xsl:value-of select="/OrderMessage/@LegacyOMSToSterlingMessageSequenceNumber" />
         </xsl:attribute>
         <MessageBody>
            <Order>
               <xsl:attribute name="LegacyOMSParentOrderNo">
                  <xsl:value-of select="@LegacyOMSParentOrderNo" />
               </xsl:attribute>
               <xsl:attribute name="LegacyOMSCancellationRequestId">
                  <xsl:value-of select="@LegacyOMSCancellationRequestId" />
               </xsl:attribute>
               <xsl:attribute name="IsProcessed">Y</xsl:attribute>
               <xsl:attribute name="EnterpriseCode">
                  <xsl:value-of select="@EnterpriseCode" />
               </xsl:attribute>
               <xsl:attribute name="OrderType">
                  <xsl:value-of select="@OrderType" />
               </xsl:attribute>
               <xsl:attribute name="DocumentType">
                  <xsl:value-of select="@DocumentType" />
               </xsl:attribute>
            </Order>
         </MessageBody>
      </OrderMessage>
   </xsl:template>
</xsl:stylesheet>