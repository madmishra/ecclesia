<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Order">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">LEGACYOMS012</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="OrderLines/OrderLine/AdditionalAddresses/AdditionalAddress/@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToLegacyOMSMessageSequenceNumber">
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="@OrderNo"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="ParentLegacyOMSOrderNo">
						<xsl:value-of select="@OrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="@EnterpriseCode"/>
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="OrderLines/OrderLine">
							<xsl:if test="@Status != 'Cancelled'">
								<OrderLine>
									<xsl:attribute name="PrimeLineNo">
										<xsl:value-of select="@PrimeLineNo"/>	
									</xsl:attribute>						
									<Item>
										<xsl:attribute name="Item">
											<xsl:value-of select="Item/@ItemID"/>	
										</xsl:attribute>
									</Item>	
									<AdditionalAddresses>
										<AdditionalAddress>
											<xsl:attribute name="AddressType">
												<xsl:value-of select="AdditionalAddresses/AdditionalAddress/@AddressType"/>	
											</xsl:attribute>
											<PersonInfo>
												<xsl:attribute name="AddressID">
													<xsl:value-of select="AdditionalAddresses/AdditionalAddress/PersonInfo/@AddressID"/>	
												</xsl:attribute>
											</PersonInfo>
										</AdditionalAddress>
									</AdditionalAddresses>									
								</OrderLine>
							</xsl:if>	
						</xsl:for-each>					
					</OrderLines>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>