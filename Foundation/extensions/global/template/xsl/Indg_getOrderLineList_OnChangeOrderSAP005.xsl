<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Order">
		<OrderLine>					
			<xsl:attribute name="ShipNode">
				<xsl:value-of select="@ShipNode"/>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="@EnterpriseCode"/>
			</xsl:attribute>	
			<Order>
				<xsl:attribute name="OrderNo">
					<xsl:value-of select="@OrderNo"/>
				</xsl:attribute>
			</Order>			
		</OrderLine>
	</xsl:template>
</xsl:stylesheet>