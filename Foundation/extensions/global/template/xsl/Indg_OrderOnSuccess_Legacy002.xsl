<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/Order">
      <OrderMessage>
         <xsl:attribute name="MessageTypeId">LEGACYOMS002</xsl:attribute>
         <xsl:attribute name="Modifyts">
            <xsl:value-of select="@Modifyts" />
         </xsl:attribute>
         <xsl:attribute name="SAPMessageSequenceNumber" />
         <MessageBody>
            <Order>
               <xsl:attribute name="ParentLegacyOMSOrderNo">
                  <xsl:value-of select="@OrderNo" />
               </xsl:attribute>
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