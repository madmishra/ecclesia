<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="InboxList">
		<InventoryNodeControl>
			<xsl:attribute name="InventoryPictureCorrect">Y</xsl:attribute>
			<xsl:attribute name="ItemID">
				<xsl:value-of select="Inbox/@DetailDescription"/>
			</xsl:attribute>
			<xsl:attribute name="Node">
				<xsl:value-of select="Inbox/@ShipnodeKey"/>
			</xsl:attribute>
			<xsl:attribute name="NodeControlType">ON_HOLD</xsl:attribute>
			<xsl:attribute name="OrganizationCode">
				<xsl:value-of select="Inbox/@EnterpriseKey"/>
			</xsl:attribute>
			<xsl:attribute name="UnitOfMeasure">EACH</xsl:attribute>
		</InventoryNodeControl>			
	</xsl:template>
</xsl:stylesheet>