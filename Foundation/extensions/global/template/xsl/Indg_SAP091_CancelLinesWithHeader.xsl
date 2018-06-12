<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Order">
		<xsl:element name="Order">
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="@EnterpriseCode"/>
			</xsl:attribute>
			
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="@SterlingOrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="CancellationType">SAP</xsl:attribute>
			<xsl:choose>
			<xsl:when test="@HeaderErrorCode !='' " >
			<xsl:attribute name="ModificationReasonCode">
			<xsl:value-of select="@HeaderErrorCode"/>
			</xsl:attribute>
			<xsl:attribute name="ModificationReasonText">
			<xsl:value-of select="@HeaderErrorDescription"/>
			</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
			<xsl:attribute name="ModificationReasonCode">SAP091_CANCEL_LINE</xsl:attribute>
			<xsl:attribute name="ModificationReasonText">SAP091 Order Line Cancellation</xsl:attribute>
			</xsl:otherwise>
			</xsl:choose>
			
			
			<xsl:element name="OrderLines">
				<xsl:for-each select="OrderLines/OrderLine">
					<xsl:element name="OrderLine">
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="@PrimeLineNo"/>
						</xsl:attribute>
						<xsl:attribute name="ShipNode">
							<xsl:value-of select="@ShipNode"/>
						</xsl:attribute>
						<xsl:attribute name="SubLineNo">1</xsl:attribute>
						<xsl:attribute name="Action">CANCEL</xsl:attribute>	
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
					