<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderMessage">
		<Order>	
			<xsl:attribute name="Action">MODIFY</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="MessageBody/Order/@ReturnOrderNumber"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="MessageBody/Order/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="MessageBody/Order/@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="OrderType">
				<xsl:value-of select="MessageBody/Order/@OrderType"/>
			</xsl:attribute>
			<xsl:attribute name="CustomerPONo">
				<xsl:value-of select="MessageBody/Order/@SAPReturnOrderNo"/>
			</xsl:attribute>
		</Order>
	</xsl:template>
</xsl:stylesheet>