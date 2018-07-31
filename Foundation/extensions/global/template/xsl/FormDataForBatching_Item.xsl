<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="ShipNode">
		<Items>
				<xsl:for-each select="Item">
					<Item>
						<xsl:attribute name="AdjustmentType">ABSOLUTE</xsl:attribute>
						<xsl:attribute name="ItemID">
							<xsl:value-of select="@ItemID"/>
						</xsl:attribute>
						<xsl:attribute name="OrganizationCode">
							<xsl:value-of select="@InventoryOrganizationCode"/>
						</xsl:attribute>
						<xsl:attribute name="Quantity">
							<xsl:value-of select="SupplyDetails/@Quantity"/>
						</xsl:attribute>
							<xsl:attribute name="ReasonCode">INITIAL</xsl:attribute>
						<xsl:attribute name="ShipNode">
							<xsl:value-of select="../@ShipNode"/>
						</xsl:attribute>
						<xsl:attribute name="SupplyType">
							<xsl:value-of select="SupplyDetails/@SupplyType"/>
						</xsl:attribute>
						<xsl:attribute name="UnitOfMeasure">
							<xsl:value-of select="@UnitOfMeasure"/>
						</xsl:attribute>
						<xsl:attribute name="ValidateItem">N</xsl:attribute>
					</Item>
				</xsl:for-each>
		</Items>				
	</xsl:template>
</xsl:stylesheet>