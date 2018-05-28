<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Order">
		<ResourcePool>
			<xsl:attribute name="CapacityOrganizationCode">
				<xsl:value-of select="@EnterpriseCode" />
			</xsl:attribute>
			<xsl:attribute name="ResourcePoolId">
				<xsl:value-of select="concat(OrderLines/OrderLine/@ShipNode,'-','_PICK_RLS_RP')" />
			</xsl:attribute>
			<xsl:attribute name="Node">
				<xsl:value-of select="OrderLines/OrderLine/@ShipNode" />
			</xsl:attribute>
			<xsl:attribute name="ProviderOrganizationCode">
				<xsl:value-of select="@EnterpriseCode" />
			</xsl:attribute>
			<xsl:attribute name="ItemGroupCode">
				<xsl:value-of select="OrderLines/OrderLine/@ItemGroupCode" />
			</xsl:attribute>
		</ResourcePool>
	</xsl:template>
</xsl:stylesheet>