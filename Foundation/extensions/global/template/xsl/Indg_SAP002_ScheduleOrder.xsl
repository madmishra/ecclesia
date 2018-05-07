<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderMessage">
		<Promise>
			<xsl:for-each select="MessageBody/Order">
				<xsl:attribute name="AllocationRuleID">DEFAULT</xsl:attribute>
				<xsl:attribute name="CheckInventory">Y</xsl:attribute>
				<xsl:attribute name="DocumentType">
					<xsl:value-of select="@DocumentType" />
				</xsl:attribute>
				<xsl:attribute name="EnterpriseCode">
					<xsl:value-of select="@EnterpriseCode" />
				</xsl:attribute>
				<xsl:attribute name="OrderNo">
					<xsl:value-of select="@ParentLegacyOMSOrderNo" />
				</xsl:attribute>
				<xsl:attribute name="ScheduleAndRelease">Y</xsl:attribute>
				<xsl:attribute name="OverrideReleaseDate">Y</xsl:attribute>
				<PromiseLines>
					<xsl:for-each select="OrderLines/OrderLine">
						<PromiseLine>
							<xsl:attribute name="DeliveryDate">
								<xsl:value-of select="/OrderMessage/MessageBody/Order/@OrderDate" />
							</xsl:attribute>
							<xsl:attribute name="PrimeLineNo">
								<xsl:value-of select="@PrimeLineNo" />
							</xsl:attribute>
							<xsl:attribute name="Quantity">
								<xsl:value-of select="@OrderedQty" />
							</xsl:attribute>
							<xsl:attribute name="ShipNode">
								<xsl:value-of select="@ShipNode" />
							</xsl:attribute>
							<xsl:attribute name="SubLineNo">1</xsl:attribute>
						</PromiseLine>
					</xsl:for-each>
				</PromiseLines>
			</xsl:for-each>
		</Promise>
	</xsl:template>
</xsl:stylesheet>