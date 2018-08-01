<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">SAP011</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="OrderLine/@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToSAPMessageSequenceNumber">
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="SterlingOrderNo">
						<xsl:value-of select="OrderLine/Order/@OrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="SAPOrderNo">
						<xsl:value-of select="OrderLine/@CustomerLinePONo"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="OrderLine/Order/@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="OrderLine/Order/@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="OrderLine/Order/@EnterpriseCode"/>
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="OrderLine">
							<xsl:if test="@Status != 'Cancelled'">
								<OrderLine>
									<xsl:attribute name="PrimeLineNo">
										<xsl:value-of select="@PrimeLineNo"/>	
									</xsl:attribute>						
									<xsl:attribute name="ShipNode">
										<xsl:value-of select="@ShipNode"/>	
									</xsl:attribute>
									<Item>
										<xsl:attribute name="Item">
											<xsl:value-of select="Item/@ItemID"/>	
										</xsl:attribute>
									</Item>	
									<LinePriceInfo>
										<xsl:choose>
											<xsl:when test="LinePriceInfo/@RetailPrice = '0.00'">
												<xsl:attribute name="RetailPrice">
													<xsl:value-of select="LinePriceInfo/@ListPrice"/>	
												</xsl:attribute>
											</xsl:when>
											<xsl:otherwise>
												<xsl:attribute name="RetailPrice">
													<xsl:value-of select="LinePriceInfo/@RetailPrice"/>	
												</xsl:attribute>
											</xsl:otherwise>	
										</xsl:choose>											
									</LinePriceInfo>									
								</OrderLine>
							</xsl:if>	
						</xsl:for-each>					
					</OrderLines>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>