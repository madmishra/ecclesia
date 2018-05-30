<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="ResourcePool">
		<ResourcePool>
			<xsl:attribute name="CapacityOrganizationCode">
				<xsl:value-of select="@CapacityOrganizationCode" />
			</xsl:attribute>
			<xsl:attribute name="ResourcePoolId">
				<xsl:value-of select="@ResourcePoolId" />
			</xsl:attribute>
			<xsl:attribute name="Node">
				<xsl:value-of select="@Node" />
			</xsl:attribute>
			<xsl:attribute name="ProviderOrganizationCode">
				<xsl:value-of select="@ProviderOrganizationCode" />
			</xsl:attribute>
			<xsl:attribute name="ItemGroupCode">
				<xsl:value-of select="@ItemGroupCode" />
			</xsl:attribute>
		</ResourcePool>
	</xsl:template>
</xsl:stylesheet>