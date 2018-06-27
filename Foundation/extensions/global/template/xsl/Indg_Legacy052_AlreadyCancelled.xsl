<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="Order">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS052</xsl:attribute>
            <xsl:attribute name="Modifyts">
                <xsl:value-of select="OrderLines/OrderLine/@Modifyts" />
            </xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
            </xsl:attribute>
			<xsl:attribute name="OrderNo">
                <xsl:value-of select="OrderLines/OrderLine/Order/@OrderNo" />
            </xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="LegacyOMSParentOrderNo">
						<xsl:value-of select="OrderLines/OrderLine/Order/@OrderNo" />
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="OrderLines/OrderLine/Order/@EnterpriseCode" />
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="OrderLines/OrderLine/Order/@DocumentType" />
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="OrderLines/OrderLine/Order/@OrderType" />
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="OrderLines/OrderLine">
							<OrderLine>
								<xsl:attribute name="IsProcessed">Y</xsl:attribute>
								<xsl:attribute name="PrimeLineNo">
									<xsl:value-of select="@PrimeLineNo" />
								</xsl:attribute>
								<xsl:attribute name="LegacyOMSCancellationRequestId">
									<xsl:value-of select="../../@LegacyOMSCancellationRequestId" />
								</xsl:attribute>
								<xsl:attribute name="LegacyOMSOrderNo">
									<xsl:value-of select="@CustomerPONo" />
								</xsl:attribute>
								<Item>
									<xsl:attribute name="ItemID">
										<xsl:value-of select="Item/@ItemID" />
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