<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderMessage">
		<ShipmentLine>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="MessageBody/Order/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="MessageBody/Order/@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="MessageBody/Order/@LegacyOMSParentOrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="CustomerPoNo">
				<xsl:value-of select="MessageBody/Order/@LegacyOMSOrderNo"/>
			</xsl:attribute>
			<OrderLine>
				<xsl:attribute name="ShipNode">
					<xsl:value-of select="MessageBody/Order/OrderLines/OrderLine/@ShipNode"/>
				</xsl:attribute>
			</OrderLine>
		</ShipmentLine>
	</xsl:template>
</xsl:stylesheet>