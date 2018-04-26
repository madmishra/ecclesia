<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<OrderLine>
			<Extn>
				<xsl:attribute name="ExtnLegacyOMSChildOrderNo">
					<xsl:value-of select="OrderMessage/MessageBody/Order/@ReleaseNo" />
				</xsl:attribute>
				<xsl:attribute name="ExtnReasonCode">
					<xsl:value-of select="OrderMessage/MessageBody/Order/@HeaderErrorCode" />
				</xsl:attribute>
				<xsl:attribute name="ExtnReasonText">
					<xsl:value-of select="OrderMessage/MessageBody/Order/@HeaderErrorDescription" />
				</xsl:attribute>
			</Extn>
		</OrderLine>
	</xsl:template>
</xsl:stylesheet>