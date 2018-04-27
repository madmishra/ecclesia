<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Order">
		<xsl:choose>
			<xsl:when test="@HeaderErrorCode">
				<xsl:element name="OrderLine">
					<xsl:element name="Order">
						<xsl:attribute name="EnterpriseCode">
							<xsl:value-of select="@EnterpriseCode"/>
						</xsl:attribute>
						<xsl:attribute name="DocumentType">
							<xsl:value-of select="@DocumentType"/>
						</xsl:attribute>
						<xsl:attribute name="OrderType">
							<xsl:value-of select="@OrderType"/>
						</xsl:attribute>
						<xsl:attribute name="OrderNo">
							<xsl:value-of select="@ParentOrderNo"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="OrderLines">
					<xsl:element name="OrderLine">
						<xsl:attribute name="ShipNode">
							<xsl:value-of select="OrderLine/@ShipNode"/>
						</xsl:attribute>
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="OrderLine/@PrimeLineNo"/>
						</xsl:attribute>
						<xsl:element name="Item">
							<xsl:attribute name="ItemID">
								<xsl:value-of select="OrderLine/Item/@ItemID"/>
							</xsl:attribute>
						</xsl:element>
						<xsl:element name="Order">
							<xsl:attribute name="OrderNo">
								<xsl:value-of select="parent::Order/attribute::ParentOrderNo"/>
							</xsl:attribute>
							<xsl:attribute name="EnterpriseCode">
								<xsl:value-of select="parent::Order/attribute::EnterpriseCode"/>
							</xsl:attribute>
							<xsl:attribute name="DocumentType">
								<xsl:value-of select="parent::Order/attribute::DocumentType"/>
							</xsl:attribute>
							<xsl:attribute name="OrderType">
								<xsl:value-of select="parent::Order/attribute::OrderType"/>
							</xsl:attribute>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
					