<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="OrderLineList">
		<OrderMessage>
			<xsl:attribute name="MessageTypeId">SAP001</xsl:attribute>
			<xsl:attribute name="Modifyts">
				<xsl:value-of select="/OrderLine/Order/@Modifyts"/>
			</xsl:attribute>
			<MessageBody>
				<Order>
					<xsl:attribute name="Currency">
						<xsl:value-of select="OrderLine/Item/@CostCurrency"/>
					</xsl:attribute>
					<xsl:attribute name="CustReqDeliveryDate">
						<xsl:value-of select="OrderLine/@ReqDeliveryDate"/>
					</xsl:attribute>
					<xsl:attribute name="CustReqShipDate">
						<xsl:value-of select="OrderLine/@ReqShipDate"/>
					</xsl:attribute>
					<xsl:attribute name="CustReqShipDate">
						<xsl:value-of select="OrderLine/@ReqShipDate"/>
					</xsl:attribute>
					<xsl:attribute name="CustomerRewardsNo">
						<xsl:value-of select="OrderLine/Order/@CustomerRewardsNo"/>
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="OrderLine/Order/@DocumentType"/>
					</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
						<xsl:value-of select="OrderLine/Order/@EnterpriseCode"/>
					</xsl:attribute>
					<xsl:attribute name="LegacyOMSOrderNo">
						<xsl:value-of select="OrderLine/EXtn/@ExtnLegacyOMSChildOrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="OrderDate">
						<xsl:value-of select="OrderLine/Order/@OrderDate"/>
					</xsl:attribute>
					<xsl:attribute name="OrderType">
						<xsl:value-of select="OrderLine/Order/@OrderType"/>
					</xsl:attribute>
					<xsl:attribute name="ParentLegacyOMSOrderNo">
						<xsl:value-of select="OrderLine/Order/@OrderNo"/>
					</xsl:attribute>
					<xsl:attribute name="ReleaseNo">ReleaseNo</xsl:attribute>
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
				<OrderLines>
					<xsl:for-each select="/OrderLineList/OrderLine">
						<xsl:if test="@Status!='Cancelled'">
							<OrderLine>
								<xsl:attribute name="OrderedQty">
									<xsl:value-of select="@OrderedQty"/>
								</xsl:attribute>
								<xsl:attribute name="PrimeLineNo">
									<xsl:value-of select="@PrimeLineNo"/>
								</xsl:attribute>
								<xsl:attribute name="ShipNode">
									<xsl:value-of select="@ShipNode"/>
								</xsl:attribute>
								<Item>
									<xsl:attribute name="ItemID">
										<xsl:value-of select="Item/@ItemID"/>
									</xsl:attribute>
								</Item>
								<LinePriceInfo>
									<xsl:attribute name="RetailPrice">
										<xsl:value-of select="LinePriceInfo/@RetailPrice"/>
									</xsl:attribute>
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