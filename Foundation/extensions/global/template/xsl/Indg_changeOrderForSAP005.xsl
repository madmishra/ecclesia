<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderMessage">
		<Order>
			<xsl:attribute name="Action">MODIFY</xsl:attribute>
			<xsl:attribute name="CustomerLinePONo">
				<xsl:value-of select="MessageBody/Order/@SAPOrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="MessageBody/Order/@SterlingOrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="MessageBody/Order/@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="OrderType">
				<xsl:value-of select="MessageBody/Order/@OrderType"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="MessageBody/Order/@EnterpriseCode"/>
			</xsl:attribute>
			<OrderLines>
				<xsl:for-each select="MessageBody/Order/OrderLines/OrderLine">
					<OrderLine>
						<xsl:attribute name="Action">MODIFY</xsl:attribute>
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="@PrimeLineNo"/>	
						</xsl:attribute>
						<xsl:attribute name="SubLineNo">1</xsl:attribute>
						<xsl:attribute name="ShipNode">
							<xsl:value-of select="@ShipNode"/>
						</xsl:attribute>	
						<Item>
							<xsl:attribute name="ItemID">
								<xsl:value-of select="Item/@ItemID"/>
							</xsl:attribute>						
							<xsl:attribute name="CostCurrency">
								<xsl:value-of select="@CostCurrency"/>
							</xsl:attribute>
							<xsl:attribute name="UnitCost">
								<xsl:value-of select="@UnitCost"/>
							</xsl:attribute>
						</Item>						
					</OrderLine>
				</xsl:for-each>				
			</OrderLines>
		</Order>
	</xsl:template>
</xsl:stylesheet>