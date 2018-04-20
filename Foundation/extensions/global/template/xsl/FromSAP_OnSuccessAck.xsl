<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">SAP002</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="/OrderRelease/OrderLine/OrderStatuses/OrderStatus/@StatusDate"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:for-each select="OrderRelease/Order">				
						<xsl:attribute name="OrderDate">
							<xsl:value-of select="/OrderRelease/@OrderDate"/>
						</xsl:attribute>
						<xsl:attribute name="SAPOrderNo">
							<xsl:value-of select="OrderRelease/OrderLine/Extn/@ExtnSAPOrderNo"/>
						</xsl:attribute>
						<xsl:attribute name="EnterpriseCode">
							<xsl:value-of select="@EnterpriseCode"/>
						</xsl:attribute>
						<xsl:attribute name="ReleaseNo">
							<xsl:value-of select="OrderRelease/OrderLine/Extn/@ExtnLegacyOMSChildOrderNo"/>
						</xsl:attribute>
						<xsl:attribute name="DocumentType">
							<xsl:value-of select="@DocumentType"/>
						</xsl:attribute>
						<xsl:attribute name="OrderType">
							<xsl:value-of select="/OrderRelease/@OrderType"/>
						</xsl:attribute>
						<OrderLines>
							<xsl:for-each select="/OrderRelease/OrderLine">
								<OrderLine>
									<xsl:attribute name="OrderedQty">
										<xsl:value-of select="OrderStatuses/OrderStatus/@TotalQuantity"/>
									</xsl:attribute>
									<xsl:attribute name="PrimeLineNo">
										<xsl:value-of select="@PrimeLineNo"/>
									</xsl:attribute>
									<xsl:attribute name="ShipNode">
										<xsl:value-of select="/OrderRelease/@ShipNode"/>
									</xsl:attribute>
									<Item>
										<xsl:attribute name="ItemID">
											<xsl:value-of select="Item/@ItemID"/>
										</xsl:attribute>
									</Item>
								</OrderLine>
							</xsl:for-each>
						</OrderLines>
					</xsl:for-each>
				</Order>
			</MessageBody>	
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>