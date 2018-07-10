<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS072</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="OrderLine/@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="OrderLine/DerivedFromOrder/@OrderNo"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="IsProcessed">Y</xsl:attribute>
					<xsl:attribute name="ParentLegacyOMSOrderNo">
						<xsl:value-of select="OrderLine/DerivedFromOrderLine/@CustomerPONo"/>
					</xsl:attribute>
					<xsl:attribute name="ReturnOrderNumber">
						<xsl:value-of select="OrderLine/Order/@OrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="OrderLine/Order/@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="OrderLine/Order/@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="OrderLine/Order/@EnterpriseCode"/>
					</xsl:attribute>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>