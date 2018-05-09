<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<Order>
						<xsl:attribute name="OrderHeaderKey">
							<xsl:value-of select="OrderLine/@OrderHeaderKey"/>
						</xsl:attribute>
						<xsl:attribute name="OrderNo">
							<xsl:value-of select="OrderLine/Order/@OrderNo"/>
						</xsl:attribute>
						<xsl:attribute name="EnterpriseCode">
							<xsl:value-of select="OrderLine/Order/@EnterpriseCode"/>
						</xsl:attribute>
						<xsl:attribute name="DocumentType">
							<xsl:value-of select="OrderLine/Order/@DocumentType"/>
						</xsl:attribute>
						
			<OrderLines>
					<xsl:for-each select="OrderLine">
				<OrderLine>
					
						<xsl:if test="OrderStatuses/OrderStatus/@Status=1100">
						
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="@PrimeLineNo"/>
						</xsl:attribute>
						<xsl:attribute name="SubLineNo">
							<xsl:value-of select="@SubLineNo"/>
						</xsl:attribute>
						<xsl:attribute name="Action">CANCEL</xsl:attribute>
						</xsl:if>
					
				</OrderLine>
				   </xsl:for-each>
			</OrderLines>
		</Order>
	</xsl:template>
</xsl:stylesheet>