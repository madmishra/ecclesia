<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderMessage">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS003</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="@Modifyts" />
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="MessageBody/Order/@SterlingOrderNo"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="LegacyOMSOrderNo">
						<xsl:value-of select="MessageBody/Order/@ReleaseNo" />
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="MessageBody/Order/@EnterpriseCode" />
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="MessageBody/Order/@DocumentType" />
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="MessageBody/Order/@OrderType" />
					</xsl:attribute>
					<OrderLines/>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>