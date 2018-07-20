<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="ShipmentLines">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">SAP061</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="ShipmentLine/@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToSAPMessageSequenceNumber">
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="SAPOrderNo">
						<xsl:value-of select="ShipmentLine/OrderLine/@CustomerLinePONo"/>
					</xsl:attribute>
					<xsl:attribute name="SterlingOrderNo">
						<xsl:value-of select="ShipmentLine/@OrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="ShipmentLine/@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="ShipmentLine/Shipment/@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="ShipmentLine/Shipment/@EnterpriseCode"/>
					</xsl:attribute>
					<OrderLines>
						<xsl:if test="ShipmentLine/OrderLine/@Status != 'Cancelled'">
							<xsl:for-each select="ShipmentLine">
								<OrderLine>
									<xsl:attribute name="Qty">
										<xsl:value-of select="@Quantity"/>
									</xsl:attribute>
									<xsl:attribute name="PrimeLineNo">
										<xsl:value-of select="@PrimeLineNo"/>
									</xsl:attribute>
									<xsl:attribute name="ShipNode">
										<xsl:value-of select="OrderLine/@ShipNode"/>
									</xsl:attribute>
									<xsl:attribute name="PickupArea">PickupArea1</xsl:attribute>
									<xsl:attribute name="BinNumber">
										<xsl:value-of select="Shipment/@HoldLocation"/>
									</xsl:attribute>
									<Item>
										<xsl:attribute name="ItemID">
											<xsl:value-of select="@ItemID"/>
										</xsl:attribute>
									</Item>
								</OrderLine>
							</xsl:for-each>	
						</xsl:if>
					</OrderLines>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>