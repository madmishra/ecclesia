<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<xsl:element name="Order">
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="OrderLine/Order/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="OrderLine/Order/@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="OrderLine/Order/@OrderNo"/>
			</xsl:attribute>
			<xsl:element name="OrderLines">
				<xsl:for-each select="OrderLine">
					<xsl:element name="OrderLine">
						<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"/>
						</xsl:attribute>
						<xsl:attribute name="Action">MODIFY</xsl:attribute>
						<xsl:attribute name="OrderedQty">0</xsl:attribute>
						<xsl:attribute name="ModificationReasonCode">INDG_CANCEL_ORDER</xsl:attribute>
						<xsl:attribute name="ModificationReasonText">Item not available</xsl:attribute>
						<xsl:attribute name="OrderLinekey">
							<xsl:value-of select="@OrderLinekey"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
					