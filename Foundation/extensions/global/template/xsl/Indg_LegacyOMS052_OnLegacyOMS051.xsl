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
                <xsl:value-of select="@OrderNo" />
            </xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="LegacyOMSParentOrderNo">
						<xsl:value-of select="@OrderNo" />
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
					<OrderLines>
						<xsl:for-each select="OrderLines/OrderLine">
							<OrderLine>
								<xsl:attribute name="IsProcessed">
									<xsl:value-of select="@IsProcessed" />
								</xsl:attribute>
								<xsl:attribute name="PrimeLineNo">
									<xsl:value-of select="@PrimeLineNo" />
								</xsl:attribute>
								<xsl:choose>
								<xsl:when test="@LegacyOMSCancellationRequestId">
									<xsl:attribute name="LegacyOMSCancellationRequestId">
										<xsl:value-of select="@LegacyOMSCancellationRequestId"/>
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="LegacyOMSCancellationRequestId">
										<xsl:value-of select="@ConditionVariable1"/>
									</xsl:attribute>
								</xsl:otherwise>
								</xsl:choose>
								<xsl:attribute name="LegacyOMSOrderNo">
									<xsl:value-of select="@LegacyOMSOrderNo" />
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