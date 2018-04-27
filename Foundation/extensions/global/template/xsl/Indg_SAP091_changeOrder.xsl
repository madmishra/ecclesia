<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="Order">
<xsl:choose>
	<xsl:when test="OrderLines/OrderLine/@OrderLineKey">
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

				<xsl:for-each select="OrderLines/OrderLine">
					<xsl:element name="OrderLines">
					<xsl:element name="OrderLine">
					<xsl:attribute name="Action">MODIFY</xsl:attribute>
					<xsl:attribute name="OrderedQty">
						<xsl:value-of select="@OrderedQty"/>
					</xsl:attribute>
					<xsl:attribute name="ModificationReasonCode">INDG_CANCEL_ORDER</xsl:attribute>
					<xsl:attribute name="ModificationReasonText">Item not available</xsl:attribute>
					<xsl:attribute name="OrderLinekey">
					   <xsl:value-of select="@OrderLinekey"/>
					</xsl:attribute>
					</xsl:element>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
	</xsl:when>
	<xsl:otherwise>
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
					<xsl:attribute name="OrderedQty">
					<xsl:value-of select="@OrderedQty"/>
						</xsl:attribute>
					<xsl:attribute name="ModificationReasonCode">
						<xsl:value-of select="@ModificationReasonCode"/>
					</xsl:attribute>
					<xsl:attribute name="ModificationReasonText">
						<xsl:value-of select="@ModificationReasonText"/>
					</xsl:attribute>
					</xsl:element>
	</xsl:otherwise>
	</xsl:choose>
				
	</xsl:template>
	</xsl:stylesheet>
					