<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Shipments">
		<Shipment>
			<xsl:attribute name="ShipmentKey">
				<xsl:value-of select="Shipment/@ShipmentKey"/>
			</xsl:attribute>
			<xsl:attribute name="TransactionId">ABANDONMENT_SHIPMENT.0001.ex</xsl:attribute>
			<xsl:attribute name="BaseDropStatus">9000.100</xsl:attribute>
		</Shipment>
	</xsl:template>
</xsl:stylesheet>