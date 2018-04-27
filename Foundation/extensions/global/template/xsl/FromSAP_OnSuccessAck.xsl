<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<Shipment>
			<xsl:for-each select="OrderMessage/MessageBody/Order">
				<xsl:attribute name="Action">CREATE</xsl:attribute>
				<xsl:attribute name="OrderNo">
					<xsl:value-of select="@SterlingOrderNo" />
				</xsl:attribute>
				<xsl:attribute name="EnterpriseCode">
					<xsl:value-of select="@EnterpriseCode" />
				</xsl:attribute>
				<xsl:attribute name="SellerOrganizationCode">
					<xsl:value-of select="@EnterpriseCode" />
				</xsl:attribute>
				<xsl:attribute name="DocumentType">
					<xsl:value-of select="@DocumentType" />
				</xsl:attribute>
				<xsl:attribute name="ShipNode">
					<xsl:value-of select="@ShipNode" />
				</xsl:attribute>
				<ShipmentLines>
					<ShipmentLine>
						<xsl:attribute name="ItemID">
							<xsl:value-of select="OrderLines/OrderLine/ItemID/@ItemID" />
						</xsl:attribute>
						<xsl:attribute name="OrderNo">
							<xsl:value-of select="@SterlingOrderNo" />
						</xsl:attribute>
						<xsl:attribute name="Quantity">
							<xsl:value-of select="OrderLines/OrderLine/@OrderedQty" />
						</xsl:attribute>
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="OrderLines/OrderLine/@PrimeLineNo" />
						</xsl:attribute>
						<xsl:attribute name="ShipNode">
							<xsl:value-of select="OrderLines/OrderLine/@ShipNode" />
						</xsl:attribute>
						<xsl:attribute name="ReleaseNo">
							<xsl:value-of select="@ReleaseNo" />
						</xsl:attribute>
						<xsl:attribute name="SubLineNo">1</xsl:attribute>
					</ShipmentLine>
				</ShipmentLines>
			</xsl:for-each>
		</Shipment>
	</xsl:template>
</xsl:stylesheet>