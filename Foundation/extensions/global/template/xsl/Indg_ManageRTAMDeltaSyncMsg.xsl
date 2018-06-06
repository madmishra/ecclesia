<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<xsl:variable name="itemID">
		<xsl:value-of select="InventoryItem/@ItemID" />
		</xsl:variable>
			<xsl:for-each select="AvailabilityChanges/AvailabilityChange">
				<AvailabilityChange>
					<xsl:attribute name="OnhandAvailableQuantity">
						<xsl:value-of select="@OnhandAvailableQuantity" />
					</xsl:attribute>
					<xsl:attribute name="OnhandAvailableDate">
						<xsl:value-of select="@AlertRaisedOn" />
					</xsl:attribute>
					<xsl:attribute name="Node">
						<xsl:value-of select="@Node" />
					</xsl:attribute>
					<Item>
						<xsl:attribute name="ItemID">
							<xsl:value-of select="$itemID" />
						</xsl:attribute>
						<xsl:attribute name="OrganizationCode">Indigo_CA</xsl:attribute>
					</Item>
				</AvailabilityChange>
			</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>