<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Order">
	<xsl:element name="Order">
	    <xsl:attribute name="EnterpriseCode">
			<xsl:value-of select="@EnterpriseCode"/>
		</xsl:attribute>
		<xsl:attribute name="DocumentType">
			<xsl:value-of select="@DocumentType"/>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="@OrderNo"/>
		</xsl:attribute>
		<xsl:attribute name="Action">
			<xsl:value-of select="@Action"/>
		</xsl:attribute>
		
					<xsl:element name="OrderLines">
					<xsl:for-each select="OrderLines/OrderLine">
						<xsl:element name="OrderLine">
					<xsl:attribute name="ShipNode">
						<xsl:value-of select="@ShipNode"/>
					</xsl:attribute>
					<xsl:attribute name="PrimeLineNo">
						<xsl:value-of select="@PrimeLineNo"/>
					</xsl:attribute>
					</xsl:element>
					<xsl:element name="Item">
					<xsl:attribute name="ItemID">
						<xsl:value-of select="Item/@ItemID"/>
					</xsl:attribute>
					</xsl:element>
					</xsl:for-each>
					</xsl:element>
	</xsl:element>
	</xsl:template>
	</xsl:stylesheet>
					