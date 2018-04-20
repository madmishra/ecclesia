<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LegacyOMS004</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="/OrderRelease/OrderLine/OrderStatuses/OrderStatus/@StatusDate"/>
			</xsl:attribute>
			<xsl:attribute name="LegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:for-each select="OrderRelease">
						<xsl:attribute name="EnterpriseCode">
							<xsl:value-of select="@EnterpriseCode"/>
						</xsl:attribute>
						<xsl:attribute name="SAPOrderNo">
							<xsl:value-of select="OrderLine/Extn/@ExtnSAPOrderNo"/>
						</xsl:attribute>
						<xsl:attribute name="LegacyOMSOrderNo">
							<xsl:value-of select="OrderLine/Extn/@ExtnLegacyOMSChildOrderNo"/>
						</xsl:attribute>
						<xsl:attribute name="DocumentType">
							<xsl:value-of select="Order/@DocumentType"/>
						</xsl:attribute>
						<xsl:attribute name="OrderType">
							<xsl:value-of select="@OrderType"/>
						</xsl:attribute>
						<OrderLines>
							<xsl:for-each select="/OrderRelease/OrderLine">
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
					</xsl:for-each>
				</Order>
			</MessageBody>	
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>