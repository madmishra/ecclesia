<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Shipment">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS064</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="@Modifyts" />
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSSequenceNumber">
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="ShipmentLines/ShipmentLine/@OrderNo" />
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="LegacyOMSOrderNo">
						<xsl:value-of select="@CustomerPONo" />
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="@EnterpriseCode" />
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="@DocumentType" />
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="@OrderType" />
					</xsl:attribute>
					<xsl:attribute name="ParentLegacyOMSOrderNo">
						<xsl:value-of select="ShipmentLines/ShipmentLine/@OrderNo" />
					</xsl:attribute>
					<xsl:attribute name="NewAbandonmentTime">
						<xsl:value-of select="AdditionalDates/AdditionalDate/@ExpectedDate" />
					</xsl:attribute>
					<xsl:attribute name="IsProcessed">
						<xsl:value-of select="@IsProcessed" />
					</xsl:attribute>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>