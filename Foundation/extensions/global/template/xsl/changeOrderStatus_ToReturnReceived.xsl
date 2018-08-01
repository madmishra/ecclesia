<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<OrderStatusChange>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="OrderLine/Order/@OrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="OrderLine/Order/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="OrderLine/Order/@DocumentType"/>
			</xsl:attribute>
			<xsl:attribute name="TransactionId">RETURN_RECEIVED.0003.ex</xsl:attribute>
			<OrderLines>
				<xsl:for-each select="OrderLine">
					<OrderLine>
						<xsl:attribute name="BaseDropStatus">1100.200</xsl:attribute>
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="@PrimeLineNo"/>
						</xsl:attribute>
						<xsl:attribute name="SubLineNo">1</xsl:attribute>	
						<xsl:attribute name="Quantity">
							<xsl:value-of select="@OrderedQty"/>
						</xsl:attribute>						
					</OrderLine>
				</xsl:for-each>				
			</OrderLines>
		</OrderStatusChange>
	</xsl:template>
</xsl:stylesheet>