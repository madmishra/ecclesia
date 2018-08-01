<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">SAP071</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="OrderLine/@Modifyts"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="SterlingOrderNo">
						<xsl:value-of select="OrderLine/DerivedFromOrder/@OrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="ReturnOrderNumber">
						<xsl:value-of select="OrderLine/Order/@OrderNo"/>
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
					<xsl:if test="OrderLine/Order/References/Reference/@Name = 'HUValue'">
						<xsl:attribute name="HUValue">
							<xsl:value-of select="OrderLine/Order/References/Reference/@Value"/>
						</xsl:attribute>
					</xsl:if>
					<OrderLines>
						<xsl:for-each select="OrderLine">
							<OrderLine>
								<xsl:attribute name="Qty">
									<xsl:value-of select="@OrderedQty"/>
								</xsl:attribute>
								<xsl:attribute name="SAPOrderNo">
									<xsl:value-of select="DerivedFromOrderLine/@CustomerLinePONo"/>
								</xsl:attribute>
								<xsl:attribute name="PrimeLineNo">
									<xsl:value-of select="@PrimeLineNo"/>
								</xsl:attribute>
								<xsl:attribute name="ReturnedNodeId">
									<xsl:value-of select="@ShipNode"/>
								</xsl:attribute>
								<xsl:for-each select="References/Reference">
									<xsl:if test="@Name = 'Disposition'">
										<xsl:attribute name="Disposition">
											<xsl:value-of select="@Value"/>
										</xsl:attribute>
									</xsl:if>
								</xsl:for-each>
								<Item>
									<xsl:attribute name="ItemID">
										<xsl:value-of select="Item/@ItemID"/>
									</xsl:attribute>
								</Item>
							</OrderLine>
						</xsl:for-each>	
					</OrderLines>
					<PersonInfoBillTo>
						<xsl:attribute name="AddressLine1">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@AddressLine1"/>
						</xsl:attribute>
						<xsl:attribute name="AddressLine2">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@AddressLine2"/>
						</xsl:attribute>
						<xsl:attribute name="City">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@City"/>
						</xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@Country"/>
						</xsl:attribute>
						<xsl:attribute name="DayPhone">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@DayPhone"/>
						</xsl:attribute>
						<xsl:attribute name="EMailID">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@EMailID"/>
						</xsl:attribute>
						<xsl:attribute name="FirstName">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@FirstName"/>
						</xsl:attribute>
						<xsl:attribute name="LastName">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@LastName"/>
						</xsl:attribute>
						<xsl:attribute name="MiddleName">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@MiddleName"/>
						</xsl:attribute>
						<xsl:attribute name="State">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@State"/>
						</xsl:attribute>
						<xsl:attribute name="ZipCode">
							<xsl:value-of select="OrderLine/Order/PersonInfoBillTo/@ZipCode"/>
						</xsl:attribute>
					</PersonInfoBillTo>
				</Order>
			</MessageBody>
		</OrderMessage>
	</xsl:template>
</xsl:stylesheet>