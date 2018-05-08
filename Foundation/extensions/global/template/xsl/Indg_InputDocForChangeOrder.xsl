<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<Order>
			<OrderLines>
				<OrderLine>
					<xsl:for-each select="OrderLine">
						<xsl:if test="OrderStatuses/OrderStatus/@Status=1100">
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="@PrimeLineNo"/>
						</xsl:attribute>
						<xsl:attribute name="Action">CANCEL</xsl:attribute>
						</xsl:if>
					</xsl:for-each>
				</OrderLine>
			</OrderLines>
		</Order>
	</xsl:template>
</xsl:stylesheet>