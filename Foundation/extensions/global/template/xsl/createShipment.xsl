<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<Shipment>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="OrderLineList/OrderLine/Order/@EnterpriseCode" />
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:value-of select="OrderLineList/OrderLine/Order/@DocumentType" />
			</xsl:attribute>
			<xsl:attribute name="ExpectedDeliveryDate">
				<xsl:value-of select="OrderLineList/OrderLine/@ReqDeliveryDate" />
			</xsl:attribute>
			<xsl:attribute name="ExpectedShipmentDate">
				<xsl:value-of select="OrderLineList/OrderLine/@ReqShipDate" />
			</xsl:attribute>
			<xsl:attribute name="ShipNode">
				<xsl:value-of select="OrderLineList/OrderLine/@ShipNode" />
			</xsl:attribute>
			<xsl:attribute name="SellerOrganizationCode">
				<xsl:value-of select="OrderLineList/OrderLine/Order/@SellerOrganizationCode" />
			</xsl:attribute>
			<ToAddress>
			<xsl:copy-of select="OrderLineList/OrderLine/Order/PersonInfoShipTo/@*">
			</xsl:copy-of>
			</ToAddress>
			<BillToAddress>
			<xsl:copy-of select="OrderLineList/OrderLine/Order/PersonInfoBillTo/@*">
			</xsl:copy-of>
			</BillToAddress>
			<ShipmentLines>
				<ShipmentLine>		
					<xsl:attribute name="OrderHeaderKey">
						<xsl:value-of select="OrderLineList/OrderLine/@OrderHeaderKey" />
					</xsl:attribute>
					<xsl:attribute name="OrderLineKey">
						<xsl:value-of select="OrderLineList/OrderLine/@OrderLineKey" />
					</xsl:attribute>
					<xsl:attribute name="OrderReleaseKey">
						<xsl:value-of select="OrderLineList/OrderLine/OrderStatuses/OrderStatus/@OrderReleaseKey" />
					</xsl:attribute>
					<xsl:attribute name="OrderNo">
						<xsl:value-of select="OrderLineList/OrderLine/Order/@OrderNo" />
					</xsl:attribute>
					<xsl:attribute name="DocumentType">
						<xsl:value-of select="OrderLineList/OrderLine/Order/@DocumentType" />
					</xsl:attribute>
					<xsl:attribute name="ActualQuantity">
						<xsl:value-of select="OrderLineList/OrderLine/@StatusQuantity" />
					</xsl:attribute>
					<xsl:attribute name="IsHazmat">
						<xsl:value-of select="OrderLineList/OrderLine/ItemDetails/PrimaryInformation/@IsHazmat" />
					</xsl:attribute>
					<xsl:attribute name="ItemDesc">
						<xsl:value-of select="OrderLineList/OrderLine/Item/@ItemShortDesc" />
					</xsl:attribute>
					<xsl:attribute name="ItemID">
						<xsl:value-of select="OrderLineList/OrderLine/Item/@ItemID" />
					</xsl:attribute>
					<xsl:attribute name="PrimeLineNo">
						<xsl:value-of select="OrderLineList/OrderLine/@PrimeLineNo" />
					</xsl:attribute>
					<xsl:attribute name="ProductClass">
						<xsl:value-of select="OrderLineList/OrderLine/Item/@ProductClass" />
					</xsl:attribute>
					<xsl:attribute name="UnitOfMeasure">
						<xsl:value-of select="OrderLineList/OrderLine/Item/@UnitOfMeasure" />
					</xsl:attribute>
					<xsl:attribute name="SubLineNo">
						<xsl:value-of select="OrderLineList/OrderLine/@SubLineNo" />
					</xsl:attribute>
					<xsl:attribute name="Quantity">
						<xsl:value-of select="OrderLineList/OrderLine/@StatusQuantity" />
					</xsl:attribute>
				</ShipmentLine>
			</ShipmentLines>
		</Shipment>
	</xsl:template>
</xsl:stylesheet>