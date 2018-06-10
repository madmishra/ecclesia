<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<xsl:variable name="itemID">
		<xsl:value-of select="InventoryItem/@ItemID" />
		</xsl:variable>
		<AvailabilityChanges>
			<xsl:for-each select="InventoryItem/AvailabilityChanges/AvailabilityChange">
			<xsl:if test = "@Node != '' " >
				<AvailabilityChange>
					<xsl:attribute name="OnhandAvailableQuantity">
						<xsl:value-of select="@OnhandAvailableQuantity" />
					</xsl:attribute>
					<xsl:attribute name="OnhandAvailableDate">
						<xsl:value-of select="@OnhandAvailableDate" />
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
				</xsl:if>
			</xsl:for-each>
			
		</AvailabilityChanges>
	</xsl:template>
</xsl:stylesheet>