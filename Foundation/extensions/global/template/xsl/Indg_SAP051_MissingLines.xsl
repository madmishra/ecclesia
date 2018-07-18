<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Order">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">SAP051</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="@Modifyts"/>
			</xsl:attribute>
			<xsl:attribute name="SterlingToSAPMessageSequenceNumber">
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="SAPOrderNo">
						<xsl:value-of select="OrderLines/OrderLine/@CustomerLinePONo"/>
					</xsl:attribute>
					<xsl:choose>
					<xsl:when test="@SterlingOrderNo">
						<xsl:attribute name="SterlingOrderNo">
					<xsl:value-of select="@SterlingOrderNo"/>
					</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
					<xsl:attribute name="SterlingOrderNo">
					<xsl:value-of select="@OrderNo"/>
					</xsl:attribute>
					</xsl:otherwise>
					</xsl:choose>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="@EnterpriseCode"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="IsFullOrderCancelled">
					<xsl:value-of select="@IsFullOrderCancelled"/>
					</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="OrderLines/OrderLine">
							<OrderLine>
								<xsl:attribute name="CurrentQty">
									<xsl:value-of select="@OrderedQty"/>
								</xsl:attribute>
								<xsl:attribute name="OriginalQty">
									<xsl:value-of select="@OriginalOrderedQty"/>
								</xsl:attribute>
								<xsl:attribute name="PrimeLineNo">
									<xsl:value-of select="@PrimeLineNo"/>
								</xsl:attribute>
								<xsl:attribute name="ShipNode">
									<xsl:value-of select="@ShipNode"/>
								</xsl:attribute>
								<xsl:choose>
									<xsl:when test="@CancellationReasonCode = ''">
										<xsl:attribute name="CancellationReasonCode">03</xsl:attribute>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="CancellationReasonCode">
											<xsl:value-of select="@CancellationReasonCode"/>
										</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
								<Item>
									<xsl:attribute name="ItemID">
										<xsl:value-of select="Item/@ItemID"/>
									</xsl:attribute>
								</Item>
							</OrderLine>
						</xsl:for-each>
					</OrderLines>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>				