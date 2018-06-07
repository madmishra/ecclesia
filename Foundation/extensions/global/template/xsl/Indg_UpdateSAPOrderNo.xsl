<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<Order>
			<xsl:for-each select="OrderMessage/MessageBody/Order">
				<xsl:attribute name="EnterpriseCode">
					<xsl:value-of select="@EnterpriseCode" />
				</xsl:attribute>
				<xsl:attribute name="DocumentType">
					<xsl:value-of select="@DocumentType" />
				</xsl:attribute>
				<xsl:attribute name="OrderNo">
					<xsl:value-of select="@SterlingOrderNo" />
				</xsl:attribute>
				<OrderLines>
					<xsl:for-each select="OrderLines/OrderLine">
						<OrderLine Action="MODIFY">
							<xsl:attribute name="CustomerLinePONo">
								<xsl:value-of select="/OrderMessage/MessageBody/Order/@SAPOrderNo" />
							</xsl:attribute>
							<xsl:attribute name="PrimeLineNo">
								<xsl:value-of select="@PrimeLineNo" />
							</xsl:attribute>
							<xsl:attribute name="SubLineNo">1</xsl:attribute>
							<xsl:attribute name="ShipNode">
								<xsl:value-of select="@ShipNode" />
							</xsl:attribute>
							<Item>
								<xsl:attribute name="ItemID">
									<xsl:value-of select="Item/@ItemID" />
								</xsl:attribute>
							</Item>
						</OrderLine>
					</xsl:for-each>
				</OrderLines>
			</xsl:for-each>
		</Order>
	</xsl:template>
</xsl:stylesheet>