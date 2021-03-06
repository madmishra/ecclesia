<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS008</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="OrderLine/@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="OrderLine/Order/@OrderNo"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="LegacyOMSOrderNo">
						<xsl:value-of select="OrderLine/@CustomerPONo"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="OrderLine/Order/@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="OrderLine/Order/@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="OrderLine/Order/@EnterpriseCode"/>
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="OrderLine">
							<xsl:if test="@Status != 'Cancelled'">
								<OrderLine>
									<xsl:attribute name="PrimeLineNo">
										<xsl:value-of select="@PrimeLineNo"/>	
									</xsl:attribute>						
									<xsl:attribute name="CostCurrency">
										<xsl:value-of select="Item/@CostCurrency"/>
									</xsl:attribute>
									<xsl:attribute name="UnitCost">
										<xsl:value-of select="Item/@UnitCost"/>
									</xsl:attribute>					
								</OrderLine>
							</xsl:if>	
						</xsl:for-each>					
					</OrderLines>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>