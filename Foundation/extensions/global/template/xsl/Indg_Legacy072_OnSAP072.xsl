<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderMessage">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS072</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="MessageBody/Order/@SterlingOrderNo"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:choose >
						<xsl:when test="MessageBody/Order/@IsProcessed = 'Y'">
							<xsl:attribute name="IsProcessed">Y</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
						<xsl:attribute name="IsProcessed">N</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:attribute name="ParentLegacyOMSOrderNo">
						<xsl:value-of select="MessageBody/Order/@SterlingOrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="ReturnOrderNumber">
						<xsl:value-of select="MessageBody/Order/@ReturnOrderNumber"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="MessageBody/Order/@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="MessageBody/Order/@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="MessageBody/Order/@EnterpriseCode"/>
					</xsl:attribute>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>