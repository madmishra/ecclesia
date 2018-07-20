<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Shipment">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS005</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="ShipmentLines/ShipmentLine/@OrderNo"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="LegacyOMSOrderNo">
						<xsl:value-of select="@CustomerPONo"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="@EnterpriseCode"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="IsFullOrderCancelled">N</xsl:attribute>
					<xsl:attribute name="AbandonmentTime">
						<xsl:value-of select="AdditionalDates/AdditionalDate/@ExpectedDate"/>
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="ShipmentLines/ShipmentLine">
							<xsl:if test="@Quantity != '0.00'">
								<OrderLine>
									<xsl:attribute name="Qty">
										<xsl:value-of select="@Quantity"/>
									</xsl:attribute>
									<xsl:attribute name="PrimeLineNo">
										<xsl:value-of select="@PrimeLineNo"/>
									</xsl:attribute>
									<xsl:attribute name="ShipNode">
										<xsl:value-of select="../../@ShipNode"/>
									</xsl:attribute>
									<Item>
										<xsl:attribute name="ItemID">
											<xsl:value-of select="Item/@ItemID"/>
										</xsl:attribute>
									</Item>
								</OrderLine>
							</xsl:if>
						</xsl:for-each>
					</OrderLines>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>