<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Shipment">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS004</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/@Modifyts" />
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="ShipmentLines/ShipmentLine/@OrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="LegacyOMSOrderNo">
						<xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/@CustomerPONo" />
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="@SellerOrganizationCode" />
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="ShipmentLines/ShipmentLine/@DocumentType" />
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/Order/@OrderType" />
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="ShipmentLines/ShipmentLine">
							<OrderLine>
								<xsl:attribute name="PrimeLineNo">
									<xsl:value-of select="OrderLine/@PrimeLineNo"/>
								</xsl:attribute>
								<xsl:attribute name="Quantity">
									<xsl:value-of select="OrderLine/@OrderedQty"/>
								</xsl:attribute>
								<Item>
									<xsl:attribute name="ItemID">
										<xsl:value-of select="Item/@ItemID"/>
									</xsl:attribute>
								</Item>
							</OrderLine>
						</xsl:for-each>
					</OrderLines>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>