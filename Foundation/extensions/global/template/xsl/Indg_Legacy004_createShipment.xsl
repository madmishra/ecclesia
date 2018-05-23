<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Shipment">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS004</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="LegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="@SellerOrganizationCode"/>
					</xsl:attribute>
					<xsl:attribute name="SAPOrderNo">
						<xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/Extn/@ExtnSAPOrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="LegacyOMSOrderNo">
						<xsl:value-of select="ShipmentLines/ShipmentLine/OrderLine/Extn/@ExtnLegacyOMSChildOrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="ShipmentLines/ShipmentLine/@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="@OrderType"/>
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="ShipmentLines/ShipmentLine/OrderLine">
								<OrderLine>
									<xsl:attribute name="PrimeLineNo">
										<xsl:value-of select="@PrimeLineNo"/>
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