<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="Order">
      <xsl:element name="Order">
         <xsl:attribute name="EnterpriseCode">
            <xsl:value-of select="@EnterpriseCode" />
         </xsl:attribute>
         <xsl:attribute name="DocumentType">
            <xsl:value-of select="@DocumentType" />
         </xsl:attribute>
         <xsl:attribute name="OrderNo">
            <xsl:value-of select="@LegacyOMSParentOrderNo" />
         </xsl:attribute>
         <xsl:element name="OrderLines">
            <xsl:for-each select="OrderLines/OrderLine">
               <xsl:element name="OrderLine">
                  <xsl:attribute name="PrimeLineNo">
                     <xsl:value-of select="@PrimeLineNo" />
                  </xsl:attribute>
                  <xsl:attribute name="OrderedQty">
                     <xsl:value-of select="@CurrentQty" />
                  </xsl:attribute>
                  <xsl:attribute name="ModificationReasonCode">
                     <xsl:value-of select="@CancellationReasonCode" />
                  </xsl:attribute>
                  <xsl:attribute name="ModificationReasonText">
                     <xsl:value-of select="@CancellationText" />
                  </xsl:attribute>
                  <xsl:attribute name="SubLineNo">1</xsl:attribute>
                  <xsl:attribute name="Action">MODIFY</xsl:attribute>
               </xsl:element>
            </xsl:for-each>
         </xsl:element>
      </xsl:element>
   </xsl:template>
</xsl:stylesheet>