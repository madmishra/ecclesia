<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderMessage">
		<Order>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="MessageBody/Order/@LegacyOMSParentOrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="MessageBody/Order/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="MessageBody/Order/@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="OrderType">
				<xsl:value-of select="MessageBody/Order/@OrderType"/>
			</xsl:attribute>
			<OrderLines>
				<xsl:for-each select="MessageBody/Order/OrderLines/OrderLine">
					<OrderLine>
						<xsl:attribute name="Quantity">
							<xsl:value-of select="@CurrentQty"/>
						</xsl:attribute>
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="@PrimeLineNo"/>
						</xsl:attribute>
						<xsl:attribute name="ConditionVariable2">
							<xsl:value-of select="@CancellationReasonCode"/>
						</xsl:attribute>	
						<xsl:attribute name="ShipNode">
							<xsl:value-of select="@ShipNode"/>
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
	</xsl:template>
</xsl:stylesheet>