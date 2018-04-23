<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://schemas.microsoft.com/powershell/2004/04" >

<xsl:template match="/UserList">
	<MultiApi>
		<xsl:for-each select="User">
		<API Name="createUserHierarchy">
		<Input>
		<xsl:element name="User">
			
				<xsl:choose>
				<xsl:when test="@ActiveFlag='true'">
				<xsl:attribute name="Activateflag">
					<xsl:text>Y</xsl:text>
				</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
				<xsl:attribute name="Activateflag">
					<xsl:text>N</xsl:text>
				</xsl:attribute>
				</xsl:otherwise>
				</xsl:choose>

				<xsl:attribute name="DisplayUserID">
					<xsl:value-of select="@DisplayUserID"/>
				</xsl:attribute>
				
				<xsl:attribute name="Loginid">
					<xsl:value-of select="@Loginid"/>
				</xsl:attribute>
				
				<xsl:attribute name="Username">
					<xsl:value-of select="@Name"/>
				</xsl:attribute>
		
				<xsl:element name="ContactPersonInfo">
				<xsl:attribute name="AddressLine1">
					<xsl:value-of select="@AddressLine1"/>
				</xsl:attribute>
		
				<xsl:attribute name="City">
					<xsl:value-of select="@City"/>
				</xsl:attribute>
				<xsl:attribute name="Company">
					<xsl:value-of select="@Company"/>
				</xsl:attribute>
				<xsl:attribute name="Country">
					<xsl:value-of select="@Country"/>
				</xsl:attribute>
				<xsl:attribute name="DayFaxNo">
					<xsl:value-of select="@DayFaxNo"/>
				</xsl:attribute>
				<xsl:attribute name="DayPhone">
					<xsl:value-of select="@DayPhone"/>
				</xsl:attribute>
				<xsl:attribute name="Department">
					<xsl:value-of select="@Department"/>
				</xsl:attribute>
				<xsl:attribute name="EMailID">
					<xsl:value-of select="@EMailID"/>
				</xsl:attribute>
				<xsl:attribute name="EveningFaxNo">
					<xsl:value-of select="@EveningFaxNo"/>
				</xsl:attribute>
				<xsl:attribute name="EveningPhone">
					<xsl:value-of select="@EveningPhone"/>
				</xsl:attribute>
				<xsl:attribute name="FirstName">
					<xsl:value-of select="@FirstName"/>
				</xsl:attribute>
				<xsl:attribute name="HttpUrl">
					<xsl:value-of select="@HttpUrl"/>
				</xsl:attribute>
				<xsl:attribute name="IsCommercialAddress">
					<xsl:value-of select="@IsCommercialAddress"/>
				</xsl:attribute>
				<xsl:attribute name="JobTitle">
					<xsl:value-of select="@JobTitle"/>
				</xsl:attribute>
				<xsl:attribute name="LastName">
					<xsl:value-of select="@LastName"/>
				</xsl:attribute>
				<xsl:attribute name="MiddleName">
					<xsl:value-of select="@MiddleName"/>
				</xsl:attribute>
				<xsl:attribute name="MobilePhone">
					<xsl:value-of select="@MobilePhone"/>
				</xsl:attribute>
				<xsl:attribute name="OtherPhone">
					<xsl:value-of select="@OtherPhone"/>
				</xsl:attribute>
				<xsl:attribute name="State">
					<xsl:value-of select="@State"/>
				</xsl:attribute>
				<xsl:attribute name="Title">
					<xsl:value-of select="@JobTitle"/>
				</xsl:attribute>
				<xsl:attribute name="ZipCode">
					<xsl:value-of select="@ZipCode"/>
				</xsl:attribute>
				</xsl:element>
				
			
				</xsl:element>
				
			
				
				

			
		
		</Input>
		</API>
		</xsl:for-each>
	</MultiApi>
</xsl:template>
</xsl:stylesheet>