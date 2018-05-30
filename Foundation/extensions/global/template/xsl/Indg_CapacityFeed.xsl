<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="ResourcePool">
		<NodeCapacityList>
			<NodeCapacity>
				<xsl:attribute name="Node">
					<xsl:value-of select="ResourcePool/@Node" />
				</xsl:attribute>
				<xsl:attribute name="ResourcePoolId">
					<xsl:value-of select="ResourcePool/@ResourcePoolId" />
				</xsl:attribute>
				<xsl:attribute name="ResourcePoolType">
					<xsl:value-of select="ResourcePool/ResourcePoolAttributeList/ResourcePoolAttribute/@DeliveryMethod" />
				</xsl:attribute>
				<Dates>
					<xsl:for-each select="Slots/Slot">
						<xsl:for-each select="Dates/Date">
							<Date>
								<xsl:attribute name="Date">
									<xsl:value-of select="@AvailableDate" />
								</xsl:attribute>
								<xsl:choose>
									<xsl:when test="@TotalStandardCapacity = '0.00'">
										<xsl:attribute name="IsWorkingDay">N</xsl:attribute>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="IsWorkingDay">Y</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:attribute name="LastUpdatedTime">
									<xsl:value-of select="@AvailableDate" />
								</xsl:attribute>
								<xsl:attribute name="SlotId">
									<xsl:value-of select="/ResourcePool/ResourcePool/@ServiceSlotGroupId" />
								</xsl:attribute>
								<xsl:attribute name="ShiftId">
									<xsl:value-of select="concat(@AvailableDate,'-',/ResourcePool/ResourcePool/@ServiceSlotGroupId)" />
								</xsl:attribute>
								<xsl:attribute name="ShiftStartTime">
									<xsl:value-of select="../../@StartTime" />
								</xsl:attribute>
								<xsl:attribute name="ShiftEndTime">
									<xsl:value-of select="../../@EndTime" />
								</xsl:attribute>
								<xsl:attribute name="AvailableCapacity">
									<xsl:value-of select="@AvailableCapacity" />
								</xsl:attribute>
								<xsl:choose>
									<xsl:when test="@TotalStandardCapacity = '0.00'">
										<xsl:attribute name="ConsumedCapacity">0.00</xsl:attribute>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="ConsumedCapacity">
											<xsl:value-of select="@TotalStandardCapacity - @AvailableCapacity" />
										</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:attribute name="TotalCapacity">
									<xsl:value-of select="@TotalStandardCapacity" />
								</xsl:attribute>
								<xsl:attribute name="CapacityUOM">
									<xsl:value-of select="/ResourcePool/ResourcePool/@CapacityUnitOfMeasure" />
								</xsl:attribute>
							</Date>
						</xsl:for-each>
					</xsl:for-each>
				</Dates>
			</NodeCapacity>
		</NodeCapacityList>
	</xsl:template>
</xsl:stylesheet>